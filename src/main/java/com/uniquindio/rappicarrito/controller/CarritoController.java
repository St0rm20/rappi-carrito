package com.uniquindio.rappicarrito.controller;

import com.uniquindio.rappicarrito.services.adapter.ACarritoService;
import com.uniquindio.rappicarrito.services.adapter.ADetalleProductoService;
import com.uniquindio.rappicarrito.view_model.CarritoViewModel;
import com.uniquindio.rappicarrito.view_model.DetalleProductoViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class CarritoController implements Initializable {

    private static final int ID_CARRITO = 1; // Siempre usamos el carrito 1

    @FXML private ListView<DetalleProductoViewModel> itemsListView;
    @FXML private Label totalLabel;
    @FXML private Button pagarButton;

    // Inyectamos ambos adapters: Carrito (General) y Detalle (Específico por item)
    private final ACarritoService carritoAdapter;
    private final ADetalleProductoService detalleAdapter;
    private final ApplicationContext springContext;

    public CarritoController(ACarritoService carritoAdapter,
                             ADetalleProductoService detalleAdapter,
                             ApplicationContext springContext) { // <--- Inyectar
        this.carritoAdapter = carritoAdapter;
        this.detalleAdapter = detalleAdapter;
        this.springContext = springContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configurar cómo se ve cada celda de la lista
        configurarLista();

        // 2. Cargar datos iniciales
        cargarCarrito();
    }


    private void cargarCarrito() {
        // Usamos el adapter para traer todo el ViewModel calculado
        CarritoViewModel carritoVM = carritoAdapter.obtenerCarrito(ID_CARRITO);

        if (carritoVM != null) {
            // Llenar la lista
            itemsListView.setItems(FXCollections.observableArrayList(carritoVM.getItems()));
            // Actualizar el Total (Funcionalidad: CalcularTotal)
            totalLabel.setText(carritoVM.getTotalFormateado());
        } else {
            totalLabel.setText("$ 0.00");
        }
    }

    // --- LÓGICA DE RENDERIZADO DE FILAS (La parte visual compleja) ---
    private void configurarLista() {
        itemsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DetalleProductoViewModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Creamos el diseño de la fila dinámicamente
                    HBox row = createRow(item);
                    setGraphic(row);
                }
            }
        });
    }

    private HBox createRow(DetalleProductoViewModel item) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        // 1. Imagen Pequeña
        ImageView img = new ImageView();
        img.setFitHeight(60);
        img.setFitWidth(60);
        img.setPreserveRatio(true);
        try {
            img.setImage(new Image(item.getProducto().getImagenUrl(), true));
        } catch (Exception e) { /* Placeholder */ }

        // 2. Información (Nombre y Precio Unitario)
        VBox infoBox = new VBox(5);
        Label lblNombre = new Label(item.getProducto().getNombre());
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblUnitario = new Label("Unitario: " + item.getProducto().getPrecioFormateado());
        lblUnitario.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");
        infoBox.getChildren().addAll(lblNombre, lblUnitario);

        // Spacer para empujar lo siguiente a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 3. Controles de Cantidad (+ / -) -> Funcionalidad: Modificar Cantidad
        HBox qtyBox = new HBox(5);
        qtyBox.setAlignment(Pos.CENTER);

        Button btnMenos = new Button("-");
        btnMenos.setOnAction(e -> modificarCantidad(item, item.getCantidad() - 1));

        Label lblQty = new Label(String.valueOf(item.getCantidad()));
        lblQty.setStyle("-fx-font-weight: bold; -fx-padding: 0 10;");

        Button btnMas = new Button("+");
        btnMas.setOnAction(e -> modificarCantidad(item, item.getCantidad() + 1));

        qtyBox.getChildren().addAll(btnMenos, lblQty, btnMas);

        // 4. Subtotal por Item -> Funcionalidad: Calcular Subtotal
        Label lblSubtotal = new Label(item.getSubtotalFormateado());
        lblSubtotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-font-size: 14px;");
        lblSubtotal.setPrefWidth(100);
        lblSubtotal.setAlignment(Pos.CENTER_RIGHT);

        // 5. Botón Eliminar -> Funcionalidad: Eliminar del Carrito
        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc3545; -fx-font-weight: bold; -fx-cursor: hand;");
        btnEliminar.setOnAction(e -> eliminarItem(item));

        row.getChildren().addAll(img, infoBox, spacer, qtyBox, lblSubtotal, btnEliminar);
        return row;
    }

    // --- ACCIONES DE LA LISTA ---

    private void modificarCantidad(DetalleProductoViewModel item, int nuevaCantidad) {
        if (nuevaCantidad < 1) {
            // Si baja a 0, preguntamos si quiere borrar
            eliminarItem(item);
            return;
        }

        try {
            // Llamamos al Adapter para actualizar
            detalleAdapter.actualizarCantidad(item.getId(), item.getProducto().getId().intValue(), nuevaCantidad);
            // Recargamos toda la lista para actualizar totales generales
            cargarCarrito();
        } catch (Exception e) {
            mostrarAlerta("Error al actualizar cantidad: " + e.getMessage());
        }
    }

    private void eliminarItem(DetalleProductoViewModel item) {
        try {
            // Funcionalidad: Eliminar Item
            carritoAdapter.eliminarDetalle(item.getId(), ID_CARRITO);
            cargarCarrito(); // Refrescar UI
        } catch (Exception e) {
            mostrarAlerta("Error al eliminar producto.");
        }
    }

    // --- ACCIONES GLOBALES ---

    @FXML
    public void handleVaciarCarrito() {
        try {
            // Funcionalidad: Vaciar Carrito
            carritoAdapter.vaciarCarrito(ID_CARRITO);
            cargarCarrito();
            mostrarAlerta("El carrito ha sido vaciado.");
        } catch (Exception e) {
            mostrarAlerta("Error al vaciar el carrito.");
        }
    }

    @FXML
    public void handlePagar() {
        // Solo mensaje visual
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pago");
        alert.setHeaderText("Procesando compra...");
        alert.setContentText("¡Gracias por tu compra! (Simulación finalizada)");
        alert.showAndWait();
    }

    @FXML
    public void handleVolverALista() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/productos.fxml"));
            loader.setControllerFactory(springContext::getBean); // Usar Spring
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) itemsListView.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}