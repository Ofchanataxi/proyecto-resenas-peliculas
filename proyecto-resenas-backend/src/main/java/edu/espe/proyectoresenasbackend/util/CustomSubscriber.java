package edu.espe.proyectoresenasbackend.util;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.ArrayList;
import java.util.List;

public class CustomSubscriber implements Subscriber<Integer> {

    private final int batchSize;
    private Subscription subscription;
    private int received = 0;
    private int processedInBatch = 0;

    // Lista para capturar los logs
    private final List<String> logs = new ArrayList<>();

    public CustomSubscriber(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        addLog("onSubscribe: Subscripcion iniciada. Solicitando: " + batchSize);
        subscription.request(batchSize);
    }

    @Override
    public void onNext(Integer integer) {
        received++;
        processedInBatch++;

        try {
            Thread.sleep(500); // 500ms de pausa por cada elemento
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        addLog("onNext: Calificación recibida = " + integer + " | Total procesados = " + received);

        if(processedInBatch == batchSize) {
            processedInBatch = 0;
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            addLog("--- Lote procesado. Solicitando " + batchSize + " más ---");
            subscription.request(batchSize);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        addLog("onError: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        addLog("onComplete: Flujo de calificaciones terminado.");
    }

    // Método auxiliar para guardar y (opcionalmente) imprimir
    private void addLog(String message) {
        logs.add(message);
        System.out.println(message); // Mantenemos el sysout por si acaso
    }

    // Getter para recuperar los logs al final
    public List<String> getLogs() {
        return logs;
    }
}