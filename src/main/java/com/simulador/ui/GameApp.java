package com.simulador.ui;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.simulador.application.RestauranteMonitor;
import com.simulador.domain.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class GameApp extends GameApplication {
    private RestauranteMonitor monitor;
    private Recepcionista recepcionista;
    private List<Entity> mesas = new ArrayList<>();
    private List<Entity> meserosEntities = new ArrayList<>();
    private List<Entity> cocinerosEntities = new ArrayList<>();
    private Text statsText;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(800);
        settings.setTitle("Simulador de Restaurante");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        monitor = new RestauranteMonitor(Constants.RESTAURANT_CAPACITY, 
                                       Constants.WAITERS_COUNT, 
                                       Constants.COOKS_COUNT);
        recepcionista = new Recepcionista(monitor);

        // Crear mesas
        for (int i = 0; i < Constants.RESTAURANT_CAPACITY; i++) {
            Entity mesa = crearMesa(100 + (i % 5) * 150, 100 + (i / 5) * 150);
            mesas.add(mesa);
        }

        // Crear meseros
        for (int i = 0; i < Constants.WAITERS_COUNT; i++) {
            Entity meseroEntity = crearMesero(800, 100 + i * 100);
            meserosEntities.add(meseroEntity);
            new Thread(new Mesero(i, monitor)).start();
        }

        // Crear cocineros
        for (int i = 0; i < Constants.COOKS_COUNT; i++) {
            Entity cocineroEntity = crearCocinero(1000, 100 + i * 100);
            cocinerosEntities.add(cocineroEntity);
            new Thread(new Cocinero(i, monitor)).start();
        }

        // Iniciar recepcionista
        new Thread(recepcionista).start();

        // Crear panel de estadísticas
        statsText = new Text();
        statsText.setTranslateX(50);
        statsText.setTranslateY(700);
        FXGL.getGameScene().addUINode(statsText);

        // Iniciar actualización de estadísticas
        FXGL.run(() -> actualizarEstadisticas(), Duration.seconds(1));
    }

    private Entity crearMesa(double x, double y) {
        return FXGL.entityBuilder()
                .at(x, y)
                .view(new Rectangle(40, 40, Color.BROWN))
                .buildAndAttach();
    }

    private Entity crearMesero(double x, double y) {
        return FXGL.entityBuilder()
                .at(x, y)
                .view(new Rectangle(30, 30, Color.BLUE))
                .buildAndAttach();
    }

    private Entity crearCocinero(double x, double y) {
        return FXGL.entityBuilder()
                .at(x, y)
                .view(new Rectangle(30, 30, Color.RED))
                .buildAndAttach();
    }

    private void actualizarEstadisticas() {
        String stats = String.format("""
                Clientes en restaurante: %d
                Órdenes en espera: %d
                Órdenes por cocinar: %d
                Órdenes listas: %d
                """,
                monitor.getClientesActuales(),
                monitor.getOrdenesEnEspera(),
                monitor.getOrdenesPorCocinar(),
                monitor.getOrdenesListas());
        
        statsText.setText(stats);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
