package com.simulador.domain;

public class Orden {
    private final int id;
    private final Comensal comensal;
    private EstadoOrden estado;
    private Mesero mesero;
    private Cocinero cocinero;

    public enum EstadoOrden {
        TOMADA,
        EN_COCINA,
        LISTA,
        ENTREGADA,
        FINALIZADA
    }

    public Orden(int id, Comensal comensal) {
        this.id = id;
        this.comensal = comensal;
        this.estado = EstadoOrden.TOMADA;
    }

    public int getId() { return id; }
    public Comensal getComensal() { return comensal; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public Mesero getMesero() { return mesero; }
    public void setMesero(Mesero mesero) { this.mesero = mesero; }
    public Cocinero getCocinero() { return cocinero; }
    public void setCocinero(Cocinero cocinero) { this.cocinero = cocinero; }
}
