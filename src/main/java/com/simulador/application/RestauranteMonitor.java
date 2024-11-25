package com.simulador.application;

import com.simulador.domain.*;
import java.util.concurrent.*;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class RestauranteMonitor {
    private final int capacidad;
    private int clientesActuales;
    private int clientesEnEspera;
    private final Queue<Orden> ordenesEnEspera;
    private final Queue<Orden> ordenesPorCocinar;
    private final Queue<Orden> ordenesListas;
    private final Semaphore mesasDisponibles;
    private final Semaphore meserosDisponibles;
    private final Semaphore cocinerosDisponibles;

    public RestauranteMonitor(int capacidad, int numMeseros, int numCocineros) {
        this.capacidad = capacidad;
        this.clientesActuales = 0;
        this.clientesEnEspera = 0;
        this.ordenesEnEspera = new LinkedList<>();
        this.ordenesPorCocinar = new LinkedList<>();
        this.ordenesListas = new LinkedList<>();
        this.mesasDisponibles = new Semaphore(capacidad, true);
        this.meserosDisponibles = new Semaphore(numMeseros, true);
        this.cocinerosDisponibles = new Semaphore(numCocineros, true);
    }

    // Métodos para el Recepcionista
    public synchronized boolean intentarIngresarComensal(Comensal comensal) throws InterruptedException {
        if (clientesActuales >= capacidad) {
            clientesEnEspera++;
            System.out.println("Comensal " + comensal.getId() + 
                             " en espera. Restaurante lleno (" + clientesActuales + "/" + capacidad + ")");
            return false;
        }
        mesasDisponibles.acquire();
        clientesActuales++;
        System.out.println("Comensal " + comensal.getId() + 
                          " ingresó al restaurante (" + clientesActuales + "/" + capacidad + ")");
        return true;
    }

    public synchronized void finalizarEspera() {
        if (clientesEnEspera > 0) {
            clientesEnEspera--;
        }
    }

    public synchronized int getClientesEnEspera() {
        return clientesEnEspera;
    }

    // Métodos para Meseros
    public synchronized Orden obtenerNuevaOrden() throws InterruptedException {
        while (ordenesEnEspera.isEmpty()) {
            wait();
        }
        meserosDisponibles.acquire();
        return ordenesEnEspera.poll();
    }

    public synchronized void entregarOrdenACocina(Orden orden) {
        orden.setEstado(Orden.EstadoOrden.EN_COCINA);
        ordenesPorCocinar.offer(orden);
        System.out.println("Orden " + orden.getId() + " agregada a la cola de cocina. Total ordenes por cocinar: " + ordenesPorCocinar.size());
        notifyAll();
    }

    public synchronized void entregarOrdenAComensal(Orden orden) {
        orden.setEstado(Orden.EstadoOrden.ENTREGADA);
        meserosDisponibles.release();
        notifyAll();
    }

    // Métodos para Cocineros
    public synchronized Orden obtenerOrdenParaCocinar() throws InterruptedException {
        while (ordenesPorCocinar.isEmpty()) {
            wait();
        }
        cocinerosDisponibles.acquire();
        Orden orden = ordenesPorCocinar.poll();
        System.out.println("Cocinero tomó orden " + orden.getId() + " para cocinar. Quedan por cocinar: " + ordenesPorCocinar.size());
        return orden;
    }

    public synchronized void marcarOrdenComoLista(Orden orden) {
        orden.setEstado(Orden.EstadoOrden.LISTA);
        ordenesListas.offer(orden);
        cocinerosDisponibles.release();
        System.out.println("Orden " + orden.getId() + " marcada como lista");
        notifyAll();  // Notificar a los meseros que hay una orden lista
    }

    public synchronized Orden obtenerOrdenLista() throws InterruptedException {
        while (ordenesListas.isEmpty()) {
            wait();
        }
        return ordenesListas.poll();
    }

    // Método para verificar si una orden específica está lista
    public synchronized boolean isOrdenLista(Orden orden) {
        return orden.getEstado() == Orden.EstadoOrden.LISTA;
    }

    // Métodos para Comensales
    public synchronized void agregarNuevaOrden(Orden orden) {
        ordenesEnEspera.offer(orden);
        notifyAll();
    }

    public synchronized void finalizarVisita(Comensal comensal) {
        clientesActuales--;
        mesasDisponibles.release();
        System.out.println("Comensal " + comensal.getId() + " ha salido del restaurante. " +
                          "Clientes actuales: " + clientesActuales);
    }

    // Getters para estado del restaurante
    public synchronized int getClientesActuales() {
        return clientesActuales;
    }

    public synchronized int getOrdenesEnEspera() {
        return ordenesEnEspera.size();
    }

    public synchronized int getOrdenesPorCocinar() {
        int ordenesSiendoCocinadas = Constants.COOKS_COUNT - cocinerosDisponibles.availablePermits();
        return ordenesPorCocinar.size() + ordenesSiendoCocinadas;
    }

    public synchronized int getOrdenesListas() {
        return ordenesListas.size();
    }

    public synchronized int getOrdenesSiendoCocinadas() {
        return Constants.COOKS_COUNT - cocinerosDisponibles.availablePermits();
    }

    public synchronized List<Comensal> getComensalesActuales() {
        List<Comensal> comensales = new ArrayList<>();
        // Recolectar comensales de todas las órdenes
        for (Orden orden : ordenesEnEspera) {
            comensales.add(orden.getComensal());
        }
        for (Orden orden : ordenesPorCocinar) {
            comensales.add(orden.getComensal());
        }
        for (Orden orden : ordenesListas) {
            comensales.add(orden.getComensal());
        }
        return comensales;
    }

    public synchronized String getEstadisticasDetalladas() {
        int cocinerosOcupados = Constants.COOKS_COUNT - cocinerosDisponibles.availablePermits();
        return String.format("""
            Órdenes esperando mesero: %d
            Órdenes en cola de cocina: %d
            Órdenes siendo cocinadas: %d
            """,
            ordenesEnEspera.size(),
            ordenesPorCocinar.size(),
            cocinerosOcupados
        );
    }
}
