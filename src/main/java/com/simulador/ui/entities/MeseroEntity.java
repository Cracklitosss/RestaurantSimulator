package com.simulador.ui.entities;

import com.almasb.fxgl.entity.Entity;
import com.simulador.domain.Mesero;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MeseroEntity extends Entity {
    private Mesero mesero;

    public MeseroEntity(Mesero mesero, double x, double y) {
        this.mesero = mesero;
        this.setPosition(x, y);
        this.getViewComponent().addChild(new Rectangle(30, 30, Color.BLUE));
    }

    public Mesero getMesero() {
        return mesero;
    }
}
