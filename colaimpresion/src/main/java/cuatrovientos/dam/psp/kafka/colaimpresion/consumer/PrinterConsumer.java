package cuatrovientos.dam.psp.kafka.colaimpresion.consumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cuatrovientos.dam.psp.kafka.colaimpresion.model.PrintPage;
import cuatrovientos.dam.psp.kafka.colaimpresion.util.KafkaConfig;

/**
 * Consumidor que simula una impresora física.
 * Escucha en un topic específico (Color o B/N) e "imprime" los trabajos generado archivos de texto.
 */
public class PrinterConsumer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PrinterConsumer.class);
    private static final Gson gson = new Gson();
    
    private final String topic;
    private final String groupId;
    private final String printerName;
    private final String outputDir;

    public PrinterConsumer(String topic, String groupId, String printerName, String outputDir) {
        this.topic = topic;
        this.groupId = groupId;
        this.printerName = printerName;
        this.outputDir = outputDir;
    }

    @Override
    public void run() {
        Properties props = KafkaConfig.getConsumerProps(groupId);
        // Kafka por defecto crea 1 partición, así que partimos el topic en 3

        // Creamos el consumidor
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            logger.info("Impresora {} iniciada. Escuchando en topic: {}", printerName, topic);

            // Asegurar directorio
            Files.createDirectories(Paths.get(outputDir));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    processPrintPage(record.value());
                }
            }
        } catch (Exception e) {
            logger.error("Error en la impresora {}", printerName, e);
        }
    }

    // Procesamos la página
    private void processPrintPage(String jsonPage) {
        try {
            PrintPage page = gson.fromJson(jsonPage, PrintPage.class);
            
            // Simular tiempo de impresión
            Thread.sleep(500); 

            String fileName = String.format("%s_Page%d-%d_%s.txt", 
                page.getTitulo().replaceAll("[^a-zA-Z0-9.-]", "_"), 
                page.getPageNumber(), 
                page.getTotalPages(), 
                UUID.randomUUID().toString()
            );
            
            File file = new File(outputDir, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("--- IMPRESORA: " + printerName + " ---\n");
                writer.write("Título: " + page.getTitulo() + "\n");
                writer.write("Pág: " + page.getPageNumber() + " de " + page.getTotalPages() + "\n");
                writer.write("Contenido:\n");
                writer.write(page.getContent());
                writer.write("\n------------------------------\n");
            }

            logger.info("[{}] Página impresa: {} (Archivo: {})", printerName, page, file.getName());

        } catch (Exception e) {
            logger.error("Error procesando página en {}", printerName, e);
        }
    }
}
