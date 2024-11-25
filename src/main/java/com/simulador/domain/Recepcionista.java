package com.simulador.domain;

import com.simulador.application.RestauranteMonitor;
import com.simulador.infrastructure.PoissonGenerator;

public class Recepcionista implements Runnable {
    private final RestauranteMonitor monitor;
    private final PoissonGenerator poissonGenerator;
    private int clientesRecibidos;
    private boolean activo;

    public Recepcionista(RestauranteMonitor monitor) {
        this.monitor = monitor;
        this.poissonGenerator = new PoissonGenerator(1.0 / Constants.POISSON_MEAN_TIME);
        this.clientesRecibidos = 0;
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                // Generar grupo de clientes según distribución de Poisson
                int grupoClientes = poissonGenerator.nextPoisson();
            
                grupoClientes = Math.min(grupoClientes, 4);
                
                for (int i = 0; i < grupoClientes; i++) {
                    Comensal nuevoComensal = new Comensal(++clientesRecibidos);
                    boolean ingreso = monitor.intentarIngresarComensal(nuevoComensal);
                    
                    if (ingreso) {
                        System.out.println("Comensal " + nuevoComensal.getId() + 
                                        " ha ingresado al restaurante (Grupo: " + grupoClientes + ")");
                        nuevoComensal.setEstado(Comensal.EstadoComensal.ESPERANDO_ORDEN);
                        
                        Orden nuevaOrden = new Orden(nuevoComensal.getId(), nuevoComensal);
                        nuevoComensal.setOrden(nuevaOrden);
                        monitor.agregarNuevaOrden(nuevaOrden);
                    } else {
                        System.out.println("Comensal " + nuevoComensal.getId() + 
                                        " no pudo ingresar - restaurante lleno");
                        clientesRecibidos--;
                        break;
                    }
                }

                // Esperar un tiempo aleatorio Poisson
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

    public void detener() {
        this.activo = false;
    }

    public int getClientesRecibidos() {
        return clientesRecibidos;
    }
}
