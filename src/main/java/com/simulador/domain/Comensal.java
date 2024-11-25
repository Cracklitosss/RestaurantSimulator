package com.simulador.domain;

import com.simulador.application.RestauranteMonitor;

public class Comensal implements Runnable {
    private final int id;
    private EstadoComensal estado;
    private Orden orden;
    private final RestauranteMonitor monitor;

    public enum EstadoComensal {
        ESPERANDO_MESA,
        ESPERANDO_ORDEN,
        COMIENDO,
        FINALIZADO
    }

    public Comensal(int id) {
        this.id = id;
        this.estado = EstadoComensal.ESPERANDO_MESA;
        this.monitor = null;
    }

    public Comensal(int id, RestauranteMonitor monitor) {
        this.id = id;
        this.monitor = monitor;
        this.estado = EstadoComensal.ESPERANDO_MESA;
    }

    @Override
    public void run() {
        try {
            // Esperar a que la orden sea entregada
            while (orden == null || orden.getEstado() != Orden.EstadoOrden.ENTREGADA) {
                Thread.sleep(500);
            }
            
            // Comer
            estado = EstadoComensal.COMIENDO;
            System.out.println("Comensal " + id + " está comiendo");
            Thread.sleep(Constants.EATING_TIME * 1000L);
            
            // Finalizar y salir del restaurante
            estado = EstadoComensal.FINALIZADO;
            orden.setEstado(Orden.EstadoOrden.FINALIZADA);
            monitor.finalizarVisita(this);
            System.out.println("Comensal " + id + " ha terminado y se retira del restaurante");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Getters y setters
    public int getId() { return id; }
    public EstadoComensal getEstado() { return estado; }
    public void setEstado(EstadoComensal estado) { this.estado = estado; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
}
