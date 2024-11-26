package com.simulador.ui.entities;

import com.almasb.fxgl.entity.Entity;
import com.simulador.domain.Mesero;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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

    public void moveTo(double x, double y) {
        Node view = this.getViewComponent().getChildren().get(0);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), view);
        transition.setToX(x - this.getX());
        transition.setToY(y - this.getY());
        transition.setOnFinished(e -> this.setPosition(x, y)); // Update entity position after animation
        transition.play();
    }
}
