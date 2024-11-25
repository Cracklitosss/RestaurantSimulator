package com.simulador.infrastructure;

import java.util.Random;

public class PoissonGenerator {
    private final double lambda;
    private final Random random;

    public PoissonGenerator(double lambda) {
        this.lambda = lambda;
        this.random = new Random();
    }

    public int nextPoisson() {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }

    public double nextInterArrivalTime() {
        return -Math.log(1.0 - random.nextDouble()) / lambda;
    }

    public double nextBoundedInterArrivalTime(double minTime, double maxTime) {
        double time = nextInterArrivalTime();
        return Math.min(Math.max(time, minTime), maxTime);
    }
}
