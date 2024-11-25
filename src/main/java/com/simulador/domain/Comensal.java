package com.simulador.domain;

public class Comensal {
    private final int id;
    private EstadoComensal estado;
    private Orden orden;

    public enum EstadoComensal {
        ESPERANDO_MESA,
        ESPERANDO_ORDEN,
        ORDENANDO,
        COMIENDO,
        FINALIZADO
    }

    public Comensal(int id) {
        this.id = id;
        this.estado = EstadoComensal.ESPERANDO_MESA;
    }
    
    public int getId() { return id; }
    public EstadoComensal getEstado() { return estado; }
    public void setEstado(EstadoComensal estado) { this.estado = estado; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
}
