// ...existing code...
package edu.espe.proyectoresenasbackend.service;

import java.util.Map;

/**
 * Interfaz para controlar el simulador de chat (start/stop/status)
 */
public interface ChatSimulatorService {

    Map<String, Object> startSimulation();

    Map<String, Object> stopSimulation();

    Map<String, Object> getSimulationStatus();
}

