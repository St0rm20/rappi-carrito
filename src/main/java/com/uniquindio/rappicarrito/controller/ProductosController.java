package com.uniquindio.rappicarrito.controller;


import com.uniquindio.rappicarrito.model.Producto;
import com.uniquindio.rappicarrito.services.def.ProductoService;
import com.uniquindio.rappicarrito.services.impl.ProductoServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


@Controller
public class ProductosController implements Initializable {

    // --- FXML Components ---
    @FXML private Pagination pagination;
    @FXML private GridPane productosGrid;
    @FXML private TextField filtroNombreField;
    @FXML private TextField filtroPrecioMinField;
    @FXML private TextField filtroPrecioMaxField;

    // Dialog Components
    @FXML private VBox dialogContainer;
    @FXML private Label productoNombreLabel;
    @FXML private Label precioUnitarioLabel;
    @FXML private Label subtotalLabel;
    @FXML private Spinner<Integer> cantidadSpinner;
    @FXML private Button anadirCarritoButton;

    private List<Producto> allProducts = new ArrayList<>(); // Todos los productos (simulando DB)
    private List<Producto> filteredProducts = new ArrayList<>(); // Productos filtrados actualmente
    private static final int ITEMS_PER_PAGE = 5;
    private Producto productoSeleccionado;
    private final ProductoService productoService;

    public ProductosController(ProductoService productoService) {
        this.productoService = productoService;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cargar datos simulados
        cargarDatosDummy();

        // 2. Inicializar lista filtrada con todo
        filteredProducts.addAll(allProducts);

        // 3. Configurar el Spinner del dialog
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        cantidadSpinner.setValueFactory(valueFactory);

        // Listener para actualizar subtotal cuando cambia el spinner
        cantidadSpinner.valueProperty().addListener((obs, oldValue, newValue) -> actualizarSubtotal());

        // 4. Configurar Paginación
        setupPagination();
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(5);

        // Usamos un listener en el índice de la página para repintar el Grid
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            fillGrid(newIndex.intValue());
        });

        // Cargar la primera página
        fillGrid(0);
    }

    private void fillGrid(int pageIndex) {
        productosGrid.getChildren().clear();

        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredProducts.size());

        if (fromIndex >= filteredProducts.size()) return;

        List<Producto> pageItems = filteredProducts.subList(fromIndex, toIndex);

        int col = 0;
        int row = 0;

        for (Producto prod : pageItems) {
            VBox card = createProductCard(prod);
            productosGrid.add(card, col, row);

            col++;
            if (col == 5) { // 5 columnas como definiste en el FXML
                col = 0;
                row++;
            }
        }
    }

    // Método clave: Crea la "Tarjeta" visual del producto
    private VBox createProductCard(Producto prod) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card"); // Clase CSS definida arriba
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setPrefHeight(250);

        // Imagen (Placeholder si es null)
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        try {
            // Usa una imagen por defecto si la URL falla o es nula
            String url = (prod.getImagenUrl() != null && !prod.getImagenUrl().isEmpty())
                    ? prod.getImagenUrl()
                    : "https://via.placeholder.com/150";
            imageView.setImage(new Image(url, true));
        } catch (Exception e) {
            // Manejo de error de imagen
        }

        // Nombre
        Label nameLabel = new Label(prod.getNombre());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        // Precio
        Label priceLabel = new Label("$ " + String.format("%.2f", prod.getPrecio()));
        priceLabel.getStyleClass().add("price-label");

        // Botón "Agregar" rápido
        Button btnAdd = new Button("Agregar");
        btnAdd.getStyleClass().add("button-primary");
        btnAdd.setOnAction(e -> abrirDialogoProducto(prod));

        card.getChildren().addAll(imageView, nameLabel, priceLabel, btnAdd);
        return card;
    }

    // --- Lógica de Filtrado ---

    @FXML
    public void handleFiltrar() {
        String texto = filtroNombreField.getText().toLowerCase();
        Double min = parseDoubleSafe(filtroPrecioMinField.getText(), 0.0);
        Double max = parseDoubleSafe(filtroPrecioMaxField.getText(), Double.MAX_VALUE);

        filteredProducts = allProducts.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(texto) ||
                        (p.getEtiquetas() != null && p.getEtiquetas().stream().anyMatch(t -> t.toLowerCase().contains(texto))))
                .filter(p -> p.getPrecio() >= min && p.getPrecio() <= max)
                .collect(Collectors.toList());

        setupPagination(); // Recalcula páginas y vuelve a la página 0
    }

    @FXML
    public void handleLimpiarFiltros() {
        filtroNombreField.clear();
        filtroPrecioMinField.clear();
        filtroPrecioMaxField.clear();
        handleFiltrar();
    }

    // --- Lógica del Dialog (Carrito) ---

    private void abrirDialogoProducto(Producto prod) {
        this.productoSeleccionado = prod;
        dialogContainer.setVisible(true);
        dialogContainer.setManaged(true);

        productoNombreLabel.setText(prod.getNombre());
        precioUnitarioLabel.setText("$ " + prod.getPrecio());
        cantidadSpinner.getValueFactory().setValue(1);
        actualizarSubtotal();
    }

    private void actualizarSubtotal() {
        if (productoSeleccionado != null) {
            int cantidad = cantidadSpinner.getValue();
            double subtotal = productoSeleccionado.getPrecio() * cantidad;
            subtotalLabel.setText("$ " + String.format("%.2f", subtotal));
        }
    }

    @FXML
    public void handleIncrementarCantidad() {
        cantidadSpinner.increment();
    }

    @FXML
    public void handleDecrementarCantidad() {
        cantidadSpinner.decrement();
    }

    @FXML
    public void handleAnadirAlCarrito() {
        if (productoSeleccionado != null) {
            System.out.println("Añadido al carrito: " + productoSeleccionado.getNombre() + " Cantidad: " + cantidadSpinner.getValue());

            // Ocultar dialog
            dialogContainer.setVisible(false);
            dialogContainer.setManaged(false);

            // Aquí agregarías la lógica para guardar en tu lista real de carrito

            // Feedback visual simple
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("Producto añadido al carrito correctamente.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleIrAlCarrito() {
        System.out.println("Navegando al carrito...");
    }

    // --- Utilidades ---

    private Double parseDoubleSafe(String value, Double defaultValue) {
        try {
            if (value == null || value.trim().isEmpty()) return defaultValue;
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void cargarDatosDummy() {
//        // Generar 50 productos de prueba
//        for (int i = 1; i <= 50; i++) {
//            Producto p = new Producto();
//            p.setId((long) i);
//            p.setNombre("Producto Rappi " + i);
//            p.setPrecio(10000.0 + (i * 500));
//            p.setDescripcion("Descripción del producto " + i);
//            // p.setImagenUrl("url_real_aqui");
//            p.setEstado(true);
//            allProducts.add(p);
//        }

        allProducts = productoService.listarProductos();
    }
}