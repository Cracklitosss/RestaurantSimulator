package com.simulador.application;

import com.simulador.domain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimuladorService {
    private final RestauranteMonitor monitor;
    private final Recepcionista recepcionista;
    private final List<Mesero> meseros;
    private final List<Cocinero> cocineros;
    private final ExecutorService executorService;
    private boolean simulacionActiva;

    public SimuladorService() {
        this.monitor = new RestauranteMonitor(
            Constants.RESTAURANT_CAPACITY,
            Constants.WAITERS_COUNT,
            Constants.COOKS_COUNT
        );
        this.recepcionista = new Recepcionista(monitor);
        this.meseros = new ArrayList<>();
        this.cocineros = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
        this.simulacionActiva = false;
    }

    public void iniciarSimulacion() {
        if (simulacionActiva) return;
        simulacionActiva = true;

        // Iniciar recepcionista
        executorService.submit(recepcionista);

        // Iniciar meseros
        for (int i = 0; i < Constants.WAITERS_COUNT; i++) {
            Mesero mesero = new Mesero(i, monitor);
            meseros.add(mesero);
            executorService.submit(mesero);
        }

        // Iniciar cocineros
        for (int i = 0; i < Constants.COOKS_COUNT; i++) {
            Cocinero cocinero = new Cocinero(i, monitor);
            cocineros.add(cocinero);
            executorService.submit(cocinero);
        }
    }

    public void detenerSimulacion() {
        if (!simulacionActiva) return;
        simulacionActiva = false;

        // Detener recepcionista
        recepcionista.detener();

        // Detener threads
        executorService.shutdownNow();
    }

    // Getters para acceder a los componentes
    public RestauranteMonitor getMonitor() {
        return monitor;
    }

    public List<Mesero> getMeseros() {
        return new ArrayList<>(meseros);
    }

    public List<Cocinero> getCocineros() {
        return new ArrayList<>(cocineros);
    }

    public boolean isSimulacionActiva() {
        return simulacionActiva;
    }

    // Métodos para obtener estadísticas
    public int getClientesEnRestaurante() {
        return monitor.getClientesActuales();
    }

    public int getOrdenesEnEspera() {
        return monitor.getOrdenesEnEspera();
    }

    public int getOrdenesPorCocinar() {
        return monitor.getOrdenesPorCocinar();
    }

    public int getOrdenesListas() {
        return monitor.getOrdenesListas();
    }

    public int getClientesRecibidos() {
        return recepcionista.getClientesRecibidos();
    }

    public int getClientesEnEspera() {
        return monitor.getClientesEnEspera();
    }

    public List<Comensal> getComensalesActuales() {
        return monitor.getComensalesActuales();
    }

    public String getEstadisticasDetalladas() {
        return monitor.getEstadisticasDetalladas();
    }

    public int getOrdenesSiendoCocinadas() {
        return monitor.getOrdenesSiendoCocinadas();
    }
}
