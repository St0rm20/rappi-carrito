package com.uniquindio.rappicarrito;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RappiCarritoApplication extends Application {

    private ConfigurableApplicationContext context;

    // 1. Iniciamos Spring antes que la ventana
    @Override
    public void init() {
        context = new SpringApplicationBuilder(RappiCarritoApplication.class)
                .run(); // Arranca Spring
    }

    // 2. Iniciamos la ventana (Aquí está la simplificación)
    @Override
    public void start(Stage stage) throws Exception {
        // Cargas el FXML directamente aquí
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/productos.fxml"));

        // ESTA ES LA ÚNICA LÍNEA OBLIGATORIA:
        // Le dice a JavaFX: "Usa Spring para crear los controladores"
        loader.setControllerFactory(context::getBean);

        // Configuración estándar de JavaFX
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("RappiCarrito");
        stage.show();
    }

    // 3. Cerramos Spring al cerrar la ventana
    @Override
    public void stop() {
        context.close();
    }

    // 4. El main clásico
    public static void main(String[] args) {
        launch(args);
    }
}