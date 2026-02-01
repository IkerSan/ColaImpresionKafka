package cuatrovientos.dam.psp.kafka.colaimpresion.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cuatrovientos.dam.psp.kafka.colaimpresion.util.KafkaConfig;

public class ColorPrinterApp {
    //Impresoras a color
    public static void main(String[] args) {
        int numPrinters = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numPrinters);

        System.out.println("--- Iniciando " + numPrinters + " Impresoras a Color ---");

// Creamos un hilo por impresora
        for (int i = 1; i <= numPrinters; i++) {
            PrinterConsumer printer = new PrinterConsumer(
                KafkaConfig.TOPIC_COLOR_QUEUE, 
                KafkaConfig.GROUP_ID_COLOR_PRINTERS, 
                "Pr_Color_" + i, 
                KafkaConfig.STORAGE_COLOR
            );
            executor.submit(printer);
        }
        
        // No cerramos el executor porque queremos que sigan corriendo
    }
}
