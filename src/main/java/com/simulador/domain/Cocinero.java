package com.simulador.domain;

import com.simulador.application.RestauranteMonitor;

public class Cocinero implements Runnable {
    private final int id;
    private EstadoCocinero estado;
    private Orden ordenActual;
    private RestauranteMonitor monitor;

    public enum EstadoCocinero {
        DISPONIBLE,
        COCINANDO,
        ORDEN_LISTA
    }

    public Cocinero(int id, RestauranteMonitor monitor) {
        this.id = id;
        this.monitor = monitor;
        this.estado = EstadoCocinero.DISPONIBLE;
    }

    @Override
    public void run() {
        while (true) {
            try {
                switch (estado) {
                    case DISPONIBLE -> esperarNuevaOrden();
                    case COCINANDO -> cocinarOrden();
                    case ORDEN_LISTA -> notificarOrdenLista();
                }
                Thread.sleep(Constants.COOKING_TIME * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void esperarNuevaOrden() {
        try {
            ordenActual = monitor.obtenerOrdenParaCocinar();
            if (ordenActual != null) {
                ordenActual.setCocinero(this);
                estado = EstadoCocinero.COCINANDO;
                System.out.println("Cocinero " + id + " comenzó a preparar orden " + ordenActual.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void cocinarOrden() {
        try {
            System.out.println("Cocinero " + id + " cocinando orden " + ordenActual.getId());
            Thread.sleep(Constants.COOKING_TIME * 1000L);
            estado = EstadoCocinero.ORDEN_LISTA;
            System.out.println("Cocinero " + id + " terminó de preparar orden " + ordenActual.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void notificarOrdenLista() {
        monitor.marcarOrdenComoLista(ordenActual);
        estado = EstadoCocinero.DISPONIBLE;
        System.out.println("Cocinero " + id + " notificó que orden " + ordenActual.getId() + " está lista");
        ordenActual = null;
    }

    public int getId() { return id; }
    public EstadoCocinero getEstado() { return estado; }
    public void setEstado(EstadoCocinero estado) { this.estado = estado; }
    public Orden getOrdenActual() { return ordenActual; }
    public void setOrdenActual(Orden orden) { this.ordenActual = orden; }
}
