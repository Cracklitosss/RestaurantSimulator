package com.simulador.application;

import com.simulador.domain.*;
import java.util.concurrent.*;
import java.util.Queue;
import java.util.LinkedList;

public class RestauranteMonitor {
    private final int capacidad;
    private int clientesActuales;
    private final Queue<Orden> ordenesEnEspera;
    private final Queue<Orden> ordenesPorCocinar;
    private final Queue<Orden> ordenesListas;
    private final Semaphore mesasDisponibles;
    private final Semaphore meserosDisponibles;
    private final Semaphore cocinerosDisponibles;

    public RestauranteMonitor(int capacidad, int numMeseros, int numCocineros) {
        this.capacidad = capacidad;
        this.clientesActuales = 0;
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
            return false;
        }
        mesasDisponibles.acquire();
        clientesActuales++;
        return true;
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
        meserosDisponibles.release();
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
        return ordenesPorCocinar.poll();
    }

    public synchronized void marcarOrdenComoLista(Orden orden) {
        orden.setEstado(Orden.EstadoOrden.LISTA);
        ordenesListas.offer(orden);
        cocinerosDisponibles.release();
        notifyAll();
    }

    // Métodos para Comensales
    public synchronized void agregarNuevaOrden(Orden orden) {
        ordenesEnEspera.offer(orden);
        notifyAll();
    }

    public synchronized void finalizarVisita(Comensal comensal) {
        clientesActuales--;
        mesasDisponibles.release();
        notifyAll();
    }

    // Getters para estado del restaurante
    public synchronized int getClientesActuales() {
        return clientesActuales;
    }

    public synchronized int getOrdenesEnEspera() {
        return ordenesEnEspera.size();
    }

    public synchronized int getOrdenesPorCocinar() {
        return ordenesPorCocinar.size();
    }

    public synchronized int getOrdenesListas() {
        return ordenesListas.size();
    }
}
