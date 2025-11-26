package com.uniquindio.rappicarrito.controller;

import com.uniquindio.rappicarrito.services.adapter.ACarritoService;
import com.uniquindio.rappicarrito.services.adapter.ADetalleProductoService;
import com.uniquindio.rappicarrito.services.adapter.AProductoService;
import com.uniquindio.rappicarrito.services.adapter.AUserService;
import com.uniquindio.rappicarrito.view_model.CarritoViewModel;
import com.uniquindio.rappicarrito.view_model.ProductosViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class ProductosController implements Initializable {

    // --- Constantes de Regla de Negocio ---
    private static final int ID_CARRITO = 1; // SIEMPRE usamos el carrito 1
    private static final int ITEMS_PER_PAGE = 5;

    // --- Componentes Tienda ---
    @FXML private Pagination pagination;
    @FXML private GridPane productosGrid;
    @FXML private Button carritoButton;

    // --- Filtros y Dirección ---
    @FXML private TextField filtroNombreField;
    @FXML private TextField filtroPrecioMinField;
    @FXML private TextField filtroPrecioMaxField;
    @FXML private TextField direccionField;

    // --- Componentes del Modal ---
    @FXML private StackPane modalOverlay;
    @FXML private ImageView modalImagen;
    @FXML private Label modalIdLabel;
    @FXML private Label modalNombre;
    @FXML private Label modalDescripcion;
    @FXML private Label modalPrecio;
    @FXML private Label modalStock;
    @FXML private Spinner<Integer> cantidadSpinner;
    @FXML private Label subtotalLabel;

    // --- Datos ---
    private List<ProductosViewModel> allProducts = new ArrayList<>();
    private List<ProductosViewModel> filteredProducts = new ArrayList<>();
    private ProductosViewModel productoSeleccionado;

    // --- Adapters (Servicios) ---
    private final AProductoService productoAdapter;
    private final ACarritoService carritoAdapter;
    private final AUserService usuarioAdapter;
    private final ADetalleProductoService detalleAdapter; // Para operaciones específicas de items

    private final ApplicationContext springContext;

    public ProductosController(AProductoService productoAdapter,
                               ACarritoService carritoAdapter,
                               AUserService usuarioAdapter,
                               ADetalleProductoService detalleAdapter,
                               ApplicationContext springContext) { // <--- Inyección aquí
        this.productoAdapter = productoAdapter;
        this.carritoAdapter = carritoAdapter;
        this.usuarioAdapter = usuarioAdapter;
        this.detalleAdapter = detalleAdapter;
        this.springContext = springContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cargar productos y dirección
        cargarDatos();
        cargarDireccionUsuario();

        // 2. Cargar estado inicial del carrito #1
        actualizarResumenCarrito();

        // 3. Configurar filtros y UI
        filteredProducts.addAll(allProducts);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        cantidadSpinner.setValueFactory(valueFactory);
        cantidadSpinner.valueProperty().addListener((obs, oldV, newV) -> actualizarSubtotal());

        setupPagination();
    }

    private void cargarDatos() {
        allProducts = productoAdapter.listarProductos();
    }

    // ==========================================================
    // 1. DIRECCIÓN (Usuario ID 1)
    // ==========================================================
    private void cargarDireccionUsuario() {
        String dir = usuarioAdapter.obtenerDireccionActual();
        if (dir != null) direccionField.setText(dir);
    }

    @FXML
    public void handleGuardarDireccion() {
        String direccion = direccionField.getText();
        if (direccion == null || direccion.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Ingresa una dirección válida.");
            return;
        }
        try {
            usuarioAdapter.guardarDireccion(direccion);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Dirección guardada.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Fallo al guardar dirección.");
        }
    }

    // ==========================================================
    // 2. CARRITO (Siempre ID 1)
    // ==========================================================

    @FXML
    public void handleAnadirAlCarrito() {
        if (productoSeleccionado != null) {
            int cantidad = cantidadSpinner.getValue();

            try {
                // Opción Rápida: Usamos un bucle porque tu servicio 'anadirProductoAgain' suma de 1 en 1
                // Esto asegura que si el usuario pide 5, se sumen 5 al carrito #1.
                for (int i = 0; i < cantidad; i++) {
                    carritoAdapter.agregarProducto(productoSeleccionado.getId().intValue(), ID_CARRITO);
                }

                actualizarResumenCarrito(); // Refrescar botón del header
                cerrarModal();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Carrito",
                        "Se añadieron " + cantidad + " unidad(es) al carrito.");

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo añadir: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleVaciarCarrito() {
        try {
            carritoAdapter.vaciarCarrito(ID_CARRITO);
            actualizarResumenCarrito();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Carrito", "Carrito vaciado correctamente.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void handleIrAlCarrito() {
        try {
            // 1. Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/carrito.fxml"));

            // 2. ¡CRUCIAL! Decirle a JavaFX que use Spring para crear el controlador.
            // Esto permite que CarritoController reciba sus servicios (@Autowired o constructor)
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            // 3. Obtener el Stage (ventana) actual usando el botón del carrito como referencia
            Stage stage = (Stage) carritoButton.getScene().getWindow();

            // 4. Cambiar la escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Rappi Carrito - Tu Pedido"); // Opcional: Cambiar título
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la vista del carrito: " + e.getMessage());
        }
    }

    private void actualizarResumenCarrito() {
        // Obtenemos el Carrito #1 para pintar el botón del header
        CarritoViewModel carritoVM = carritoAdapter.obtenerCarrito(ID_CARRITO);

        if (carritoVM != null) {
            carritoButton.setText("🛒 " + carritoVM.getTotalFormateado() + " (" + carritoVM.getCantidadTotalItems() + ")");
        } else {
            // Si el carrito no existe aún (null), mostramos vacío
            carritoButton.setText("🛒 $ 0.00 (0)");
        }
    }

    // ==========================================================
    // 3. FILTROS Y PAGINACIÓN
    // ==========================================================

    @FXML
    public void handleFiltrar() {
        String texto = filtroNombreField.getText().toLowerCase();
        Double min = parseDoubleSafe(filtroPrecioMinField.getText(), 0.0);
        Double max = parseDoubleSafe(filtroPrecioMaxField.getText(), Double.MAX_VALUE);

        filteredProducts = allProducts.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(texto) ||
                        (p.getEtiquetas() != null && p.getEtiquetas().stream().anyMatch(t -> t.toLowerCase().contains(texto))))
                .filter(p -> p.getPrecioValor() >= min && p.getPrecioValor() <= max)
                .collect(Collectors.toList());

        setupPagination();
    }

    @FXML
    public void handleLimpiarFiltros() {
        filtroNombreField.clear();
        filtroPrecioMinField.clear();
        filtroPrecioMaxField.clear();
        handleFiltrar();
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(5);

        // Limpiamos listeners viejos para evitar duplicación
        pagination.currentPageIndexProperty().removeListener((obs, oldIndex, newIndex) -> fillGrid(newIndex.intValue()));
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> fillGrid(newIndex.intValue()));

        fillGrid(0);
    }

    private void fillGrid(int pageIndex) {
        productosGrid.getChildren().clear();
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredProducts.size());
        if (fromIndex >= filteredProducts.size()) return;

        List<ProductosViewModel> pageItems = filteredProducts.subList(fromIndex, toIndex);
        int col = 0; int row = 0;

        for (ProductosViewModel prod : pageItems) {
            VBox card = createProductCard(prod);
            productosGrid.add(card, col, row);
            col++;
            if (col == 5) { col = 0; row++; }
        }
    }

    private VBox createProductCard(ProductosViewModel prod) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.CENTER);
        card.setSpacing(5);
        card.setPrefHeight(260);
        card.setStyle("-fx-cursor: hand;");
        card.setOnMouseClicked(e -> abrirModalProducto(prod));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(110);
        imageView.setFitWidth(110);
        imageView.setPreserveRatio(true);
        try {
            imageView.setImage(new Image(prod.getImagenUrl(), true));
        } catch (Exception e) { /* Placeholder */ }

        Label nameLabel = new Label(prod.getNombre());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        Label idLabel = new Label("ID: " + prod.getId());
        idLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");

        Label priceLabel = new Label(prod.getPrecioFormateado());
        priceLabel.getStyleClass().add("price-label");

        Button btnVer = new Button("Ver");
        btnVer.getStyleClass().add("button-primary");
        btnVer.setOnAction(e -> abrirModalProducto(prod));

        card.getChildren().addAll(imageView, nameLabel, idLabel, priceLabel, btnVer);
        return card;
    }

    // ==========================================================
    // 4. MODAL
    // ==========================================================

    private void abrirModalProducto(ProductosViewModel prod) {
        this.productoSeleccionado = prod;

        modalIdLabel.setText("ID Producto: " + prod.getId());
        modalNombre.setText(prod.getNombre());
        modalDescripcion.setText(prod.getDescripcion() != null ? prod.getDescripcion() : "Sin descripción.");
        modalPrecio.setText(prod.getPrecioFormateado());
        modalStock.setText(prod.getStock() != null ? prod.getStock().toString() : "0");

        try {
            modalImagen.setImage(new Image(prod.getImagenUrl(), true));
        } catch (Exception e) { modalImagen.setImage(null); }

        cantidadSpinner.getValueFactory().setValue(1);
        actualizarSubtotal();
        modalOverlay.setVisible(true);
    }

    @FXML public void cerrarModal() { modalOverlay.setVisible(false); }

    private void actualizarSubtotal() {
        if (productoSeleccionado != null) {
            int cantidad = cantidadSpinner.getValue();
            double subtotal = productoSeleccionado.getPrecioValor() * cantidad;
            subtotalLabel.setText("Subtotal: $ " + String.format("%.2f", subtotal));
        }
    }

    // Utilidades
    private Double parseDoubleSafe(String value, Double defaultValue) {
        try { return Double.parseDouble(value); } catch(Exception e) { return defaultValue; }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ==========================================================
    // MÉTODOS FALTANTES (Controles de UI)
    // ==========================================================

    @FXML
    public void handleIncrementarCantidad() {
        // Incrementa el valor del spinner en 1 paso
        cantidadSpinner.increment();
    }

    @FXML
    public void handleDecrementarCantidad() {
        // Decrementa el valor del spinner en 1 paso
        cantidadSpinner.decrement();
    }

}