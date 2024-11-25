package com.simulador.ui;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.simulador.application.SimuladorService;
import com.simulador.domain.*;
import com.simulador.ui.entities.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Rectangle;

public class GameApp extends GameApplication {
    private SimuladorService simuladorService;
    private List<Entity> mesas;
    private List<MeseroEntity> meserosEntities;
    private List<CocineroEntity> cocinerosEntities;
    private Text statsText;
    private Button btnIniciar;
    private Button btnDetener;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(800);
        settings.setTitle("Simulador de Restaurante");
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        simuladorService = new SimuladorService();
        mesas = new ArrayList<>();
        meserosEntities = new ArrayList<>();
        cocinerosEntities = new ArrayList<>();

        // Crear mesas
        for (int i = 0; i < Constants.RESTAURANT_CAPACITY; i++) {
            Entity mesa = FXGL.entityBuilder()
                    .at(100 + (i % 5) * 150, 100 + (i / 5) * 150)
                    .view(new Rectangle(40, 40, Color.BROWN))
                    .buildAndAttach();
            mesas.add(mesa);
        }

        // Crear meseros
        for (int i = 0; i < Constants.WAITERS_COUNT; i++) {
            MeseroEntity meseroEntity = new MeseroEntity(
                new Mesero(i, simuladorService.getMonitor()),
                800,
                100 + i * 100
            );
            meserosEntities.add(meseroEntity);
            FXGL.getGameWorld().addEntity(meseroEntity);
        }

        // Crear cocineros
        for (int i = 0; i < Constants.COOKS_COUNT; i++) {
            CocineroEntity cocineroEntity = new CocineroEntity(
                new Cocinero(i, simuladorService.getMonitor()),
                1000,
                100 + i * 100
            );
            cocinerosEntities.add(cocineroEntity);
            FXGL.getGameWorld().addEntity(cocineroEntity);
        }

        // Crear panel de estadísticas
        statsText = new Text();
        statsText.setTranslateX(50);
        statsText.setTranslateY(700);
        FXGL.getGameScene().addUINode(statsText);

        // Crear botones de control
        crearBotonesControl();

        // Iniciar actualización de estadísticas
        FXGL.run(() -> actualizarEstadisticas(), Duration.seconds(1));
    }

    private void crearBotonesControl() {
        btnIniciar = new Button("Iniciar Simulación");
        btnIniciar.setTranslateX(50);
        btnIniciar.setTranslateY(50);
        btnIniciar.setOnAction(e -> {
            simuladorService.iniciarSimulacion();
            btnIniciar.setDisable(true);
            btnDetener.setDisable(false);
        });

        btnDetener = new Button("Detener Simulación");
        btnDetener.setTranslateX(200);
        btnDetener.setTranslateY(50);
        btnDetener.setDisable(true);
        btnDetener.setOnAction(e -> {
            simuladorService.detenerSimulacion();
            btnIniciar.setDisable(false);
            btnDetener.setDisable(true);
        });

        FXGL.getGameScene().addUINodes(btnIniciar, btnDetener);
    }

    private void actualizarEstadisticas() {
        String stats = String.format("""
                Clientes en restaurante: %d
                Órdenes en espera: %d
                Órdenes por cocinar: %d
                Órdenes listas: %d
                Total clientes recibidos: %d
                """,
                simuladorService.getClientesEnRestaurante(),
                simuladorService.getOrdenesEnEspera(),
                simuladorService.getOrdenesPorCocinar(),
                simuladorService.getOrdenesListas(),
                simuladorService.getClientesRecibidos());
        
        statsText.setText(stats);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
