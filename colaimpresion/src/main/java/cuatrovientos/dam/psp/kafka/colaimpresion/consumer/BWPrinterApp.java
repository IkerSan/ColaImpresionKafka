package cuatrovientos.dam.psp.kafka.colaimpresion.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cuatrovientos.dam.psp.kafka.colaimpresion.util.KafkaConfig;

public class BWPrinterApp {
    public static void main(String[] args) {
        int numPrinters = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numPrinters);

        System.out.println("--- Iniciando " + numPrinters + " Impresoras a B/N ---");

        for (int i = 1; i <= numPrinters; i++) {
            PrinterConsumer printer = new PrinterConsumer(
                KafkaConfig.TOPIC_BW_QUEUE, 
                KafkaConfig.GROUP_ID_BW_PRINTERS, 
                "Pr_BN_" + i, 
                KafkaConfig.STORAGE_BW
            );
            executor.submit(printer);
        }
        
        // No cerramos el executor porque queremos que sigan corriendo
    }
}
