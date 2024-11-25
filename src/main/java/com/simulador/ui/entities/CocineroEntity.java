package com.simulador.ui.entities;

import com.almasb.fxgl.entity.Entity;
import com.simulador.domain.Cocinero;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CocineroEntity extends Entity {
    private Cocinero cocinero;

    public CocineroEntity(Cocinero cocinero, double x, double y) {
        this.cocinero = cocinero;
        this.setPosition(x, y);
        this.getViewComponent().addChild(new Rectangle(30, 30, Color.RED));
    }

    public Cocinero getCocinero() {
        return cocinero;
    }
}
