package com.simulador.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RestauranteGameFactory implements EntityFactory {

    @Spawns("mesa")
    public Entity newMesa(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(EntityType.MESA)
                .viewWithBBox(new Rectangle(40, 40, Color.BROWN))
                .build();
    }

    @Spawns("mesero")
    public Entity newMesero(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(EntityType.MESERO)
                .viewWithBBox(new Rectangle(30, 30, Color.BLUE))
                .build();
    }

    @Spawns("cocinero")
    public Entity newCocinero(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(EntityType.COCINERO)
                .viewWithBBox(new Rectangle(30, 30, Color.RED))
                .build();
    }
}
