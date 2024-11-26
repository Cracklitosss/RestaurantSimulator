package com.simulador.domain;

public class Constants {
    public static final int RESTAURANT_CAPACITY = 20;
    public static final int WAITERS_COUNT = (int)(RESTAURANT_CAPACITY * 0.10);
    public static final int COOKS_COUNT = (int)(RESTAURANT_CAPACITY * 0.15);
    public static final double POISSON_MEAN_TIME = 2.0;
    
    public static final int COOKING_TIME = 8;
    public static final int EATING_TIME = 15;
    public static final int SERVING_TIME = 3;
}