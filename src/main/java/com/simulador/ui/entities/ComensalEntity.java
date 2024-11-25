package com.simulador.ui.entities;

import com.almasb.fxgl.entity.Entity;
import com.simulador.domain.Comensal;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ComensalEntity extends Entity {
    private Comensal comensal;

    public ComensalEntity(Comensal comensal, double x, double y) {
        this.comensal = comensal;
        this.setPosition(x, y);
        this.getViewComponent().addChild(new Circle(15, Color.GREEN));
    }

    public Comensal getComensal() {
        return comensal;
    }
}
