package com.simulador.domain;

import com.simulador.application.RestauranteMonitor;

public class Mesero implements Runnable {
    private final int id;
    private EstadoMesero estado;
    private Orden ordenActual;
    private RestauranteMonitor monitor;

    public enum EstadoMesero {
        DISPONIBLE,
        TOMANDO_ORDEN,
        LLEVANDO_ORDEN_A_COCINA,
        ESPERANDO_ORDEN,
        SIRVIENDO_ORDEN
    }

    public Mesero(int id, RestauranteMonitor monitor) {
        this.id = id;
        this.monitor = monitor;
        this.estado = EstadoMesero.DISPONIBLE;
    }

    @Override
    public void run() {
        while (true) {
            try {
                switch (estado) {
                    case DISPONIBLE -> esperarNuevaOrden();
                    case TOMANDO_ORDEN -> tomarOrden();
                    case LLEVANDO_ORDEN_A_COCINA -> llevarOrdenACocina();
                    case ESPERANDO_ORDEN -> esperarOrdenLista();
                    case SIRVIENDO_ORDEN -> servirOrden();
                }
                Thread.sleep(Constants.SERVING_TIME * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void esperarNuevaOrden() {
        try {
            ordenActual = monitor.obtenerNuevaOrden();
            if (ordenActual != null) {
                ordenActual.setMesero(this);
                estado = EstadoMesero.TOMANDO_ORDEN;
                System.out.println("Mesero " + id + " atendiendo orden " + ordenActual.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void tomarOrden() {
        try {
            Thread.sleep(Constants.SERVING_TIME * 500L); // Mitad del tiempo de servicio
            estado = EstadoMesero.LLEVANDO_ORDEN_A_COCINA;
            System.out.println("Mesero " + id + " tomó orden " + ordenActual.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void llevarOrdenACocina() {
        monitor.entregarOrdenACocina(ordenActual);
        estado = EstadoMesero.ESPERANDO_ORDEN;
        System.out.println("Mesero " + id + " llevó orden " + ordenActual.getId() + " a cocina");
    }

    private void esperarOrdenLista() {
        if (ordenActual.getEstado() == Orden.EstadoOrden.LISTA) {
            estado = EstadoMesero.SIRVIENDO_ORDEN;
            System.out.println("Mesero " + id + " recogiendo orden " + ordenActual.getId());
        }
    }

    private void servirOrden() {
        try {
            Thread.sleep(Constants.SERVING_TIME * 500L);
            monitor.entregarOrdenAComensal(ordenActual);
            estado = EstadoMesero.DISPONIBLE;
            System.out.println("Mesero " + id + " entregó orden " + ordenActual.getId());
            ordenActual = null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getId() { return id; }
    public EstadoMesero getEstado() { return estado; }
    public void setEstado(EstadoMesero estado) { this.estado = estado; }
    public Orden getOrdenActual() { return ordenActual; }
    public void setOrdenActual(Orden orden) { this.ordenActual = orden; }
}
