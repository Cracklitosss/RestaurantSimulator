package com.simulador.domain;

import com.simulador.application.RestauranteMonitor;
import com.simulador.infrastructure.PoissonGenerator;
import java.util.Queue;
import java.util.LinkedList;

public class Recepcionista implements Runnable {
    private final RestauranteMonitor monitor;
    private final PoissonGenerator poissonGenerator;
    private final Queue<Comensal> colaEspera;
    private int clientesRecibidos;
    private boolean activo;

    public Recepcionista(RestauranteMonitor monitor) {
        this.monitor = monitor;
        this.poissonGenerator = new PoissonGenerator(1.0 / Constants.POISSON_MEAN_TIME);
        this.colaEspera = new LinkedList<>();
        this.clientesRecibidos = 0;
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                // 1. Intentar ingresar clientes en espera primero
                atenderColaEspera();

                // 2. Generar nuevos clientes según distribución Poisson
                int grupoClientes = poissonGenerator.nextPoisson();
                grupoClientes = Math.min(grupoClientes, 4); // Limitar tamaño del grupo
                
                for (int i = 0; i < grupoClientes; i++) {
                    Comensal nuevoComensal = new Comensal(++clientesRecibidos);
                    
                    if (intentarIngresarComensal(nuevoComensal)) {
                        System.out.println("Comensal " + nuevoComensal.getId() + 
                                        " ha ingresado al restaurante (Grupo: " + grupoClientes + ")");
                    } else {
                        System.out.println("Comensal " + nuevoComensal.getId() + 
                                        " en cola de espera");
                        colaEspera.offer(nuevoComensal);
                    }
                }

                // 3. Esperar tiempo aleatorio entre llegadas
                double tiempoEspera = poissonGenerator.nextBoundedInterArrivalTime(
                    Constants.POISSON_MEAN_TIME * 0.5,
                    Constants.POISSON_MEAN_TIME * 1.5
                );
                
                Thread.sleep((long)(tiempoEspera * 1000));
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                detener();
            }
        }
    }

    private void atenderColaEspera() {
        while (!colaEspera.isEmpty()) {
            Comensal comensal = colaEspera.peek();
            try {
                if (intentarIngresarComensal(comensal)) {
                    colaEspera.poll(); // Remover de la cola solo si ingresó
                    System.out.println("Comensal " + comensal.getId() + 
                                    " ingresó desde la cola de espera");
                } else {
                    break; // Si no hay espacio, dejar de intentar
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean intentarIngresarComensal(Comensal comensal) throws InterruptedException {
        boolean ingreso = monitor.intentarIngresarComensal(comensal);
        if (ingreso) {
            comensal.setEstado(Comensal.EstadoComensal.ESPERANDO_ORDEN);
            Orden nuevaOrden = new Orden(comensal.getId(), comensal);
            comensal.setOrden(nuevaOrden);
            monitor.agregarNuevaOrden(nuevaOrden);
            Comensal comensalConMonitor = new Comensal(comensal.getId(), monitor);
            comensalConMonitor.setOrden(nuevaOrden);
            comensalConMonitor.setEstado(Comensal.EstadoComensal.ESPERANDO_ORDEN);
            new Thread(comensalConMonitor).start();
            return true;
        }
        return false;
    }

    public void detener() {
        this.activo = false;
    }

    public int getClientesRecibidos() {
        return clientesRecibidos;
    }

    public int getClientesEnEspera() {
        return colaEspera.size();
    }
}
