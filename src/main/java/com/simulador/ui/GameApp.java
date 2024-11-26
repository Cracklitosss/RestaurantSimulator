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
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class GameApp extends GameApplication {
    private SimuladorService simuladorService;
    private List<Entity> mesas;
    private List<MeseroEntity> meserosEntities;
    private List<CocineroEntity> cocinerosEntities;
    private Text statsText;
    private Button btnIniciar;
    private Button btnDetener;
    private Map<Integer, ComensalEntity> comensalesEntities;

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
        comensalesEntities = new HashMap<>();

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

        // Agregar un observer para nuevos comensales
        FXGL.run(() -> actualizarComensales(), Duration.seconds(0.5));
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
                Clientes en espera: %d
                Órdenes esperando mesero: %d
                Órdenes en cola de cocina: %d
                Órdenes siendo cocinadas: %d
                Órdenes listas: %d
                Total clientes recibidos: %d
                """,
                simuladorService.getClientesEnRestaurante(),
                simuladorService.getClientesEnEspera(),
                simuladorService.getOrdenesEnEspera(),
                simuladorService.getOrdenesPorCocinar(),
                simuladorService.getOrdenesSiendoCocinadas(),
                simuladorService.getOrdenesListas(),
                simuladorService.getClientesRecibidos());
        
        statsText.setText(stats);
    }

    private void actualizarComensales() {
        List<Comensal> comensalesActuales = simuladorService.getComensalesActuales();
        
        // Remover comensales que ya no están en el restaurante
        Iterator<Map.Entry<Integer, ComensalEntity>> it = comensalesEntities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ComensalEntity> entry = it.next();
            if (!comensalesActuales.contains(entry.getValue().getComensal())) {
                FXGL.getGameWorld().removeEntity(entry.getValue());
                it.remove();
                System.out.println("Removida visualización del comensal " + entry.getKey());
            }
        }
        
        // Agregar nuevos comensales solo en mesas disponibles
        for (Comensal comensal : comensalesActuales) {
            if (!comensalesEntities.containsKey(comensal.getId())) {
                // Encontrar una mesa libre
                int mesaLibre = encontrarMesaLibre();
                if (mesaLibre >= 0) {
                    double x = 100 + (mesaLibre % 5) * 150;
                    double y = 100 + (mesaLibre / 5) * 150;
                    
                    ComensalEntity comensalEntity = new ComensalEntity(comensal, x, y);
                    comensalesEntities.put(comensal.getId(), comensalEntity);
                    FXGL.getGameWorld().addEntity(comensalEntity);
                }
            }
            
            // Cambiar el color de la mesa si la orden ha sido entregada
            ComensalEntity comensalEntity = comensalesEntities.get(comensal.getId());
            if (comensal.getOrden() != null && comensal.getOrden().getEstado() == Orden.EstadoOrden.ENTREGADA) {
                int mesaIndex = (int)((comensalEntity.getX() - 100) / 150) + 
                               ((int)((comensalEntity.getY() - 100) / 150) * 5);
                if (mesaIndex >= 0 && mesaIndex < mesas.size()) {
                    mesas.get(mesaIndex).getViewComponent().clearChildren();
                    mesas.get(mesaIndex).getViewComponent().addChild(new Rectangle(40, 40, Color.YELLOW)); // Cambia a amarillo
                }
                
                // Mover mesero hacia la mesa
                MeseroEntity meseroEntity = findAvailableMesero();
                if (meseroEntity != null) {
                    meseroEntity.moveTo(comensalEntity.getX(), comensalEntity.getY());
                }
            }
        }
    }

    private MeseroEntity findAvailableMesero() {
        for (MeseroEntity meseroEntity : meserosEntities) {
            if (meseroEntity.getMesero().getEstado() == Mesero.EstadoMesero.DISPONIBLE) {
                return meseroEntity;
            }
        }
        return null;
    }

    private int encontrarMesaLibre() {
        boolean[] mesasOcupadas = new boolean[Constants.RESTAURANT_CAPACITY];
        
        // Marcar mesas ocupadas
        for (ComensalEntity entity : comensalesEntities.values()) {
            int mesaIndex = (int)((entity.getX() - 100) / 150) + 
                           ((int)((entity.getY() - 100) / 150) * 5);
            if (mesaIndex >= 0 && mesaIndex < mesasOcupadas.length) {
                mesasOcupadas[mesaIndex] = true;
            }
        }
        
        // Encontrar primera mesa libre
        for (int i = 0; i < mesasOcupadas.length; i++) {
            if (!mesasOcupadas[i]) {
                return i;
            }
        }
        
        return -1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
