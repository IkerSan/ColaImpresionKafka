package cuatrovientos.dam.psp.kafka.colaimpresion.processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cuatrovientos.dam.psp.kafka.colaimpresion.model.PrintJob;
import cuatrovientos.dam.psp.kafka.colaimpresion.model.PrintPage;
import cuatrovientos.dam.psp.kafka.colaimpresion.util.KafkaConfig;

/**
 * Procesador central del sistema.
 * Consume trabajos brutos, los archiva y los divide en páginas para distribuir a las colas de impresión.
 */
public class JobProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JobProcessor.class);
    private static final Gson gson = new Gson();
    // Executor para tareas asíncronas
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        // Configuración
        Properties consumerProps = KafkaConfig.getConsumerProps(KafkaConfig.GROUP_ID_PROCESSOR);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Properties producerProps = KafkaConfig.getProducerProps();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
             KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

            consumer.subscribe(Collections.singletonList(KafkaConfig.TOPIC_PRINT_JOBS));
            logger.info("JobProcessor iniciado. Esperando trabajos...");

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String jsonValue = record.value();
                    logger.info("Recibido trabajo: {}", jsonValue);

                    // Paralelismo sin bloqueo
                    executor.submit(() -> saveOriginalJob(jsonValue));
                    executor.submit(() -> processAndRouteJob(jsonValue, producer));
                }
            }
        } catch (Exception e) {
            logger.error("Error en JobProcessor", e);
        } finally {
            executor.shutdown();
        }
    }

    private static void saveOriginalJob(String jsonJob) {
        try {
            PrintJob job = gson.fromJson(jsonJob, PrintJob.class);
            String senderDir = KafkaConfig.STORAGE_ORIGINALS + "/" + job.getSender();
            Files.createDirectories(Paths.get(senderDir));

            String fileName = UUID.randomUUID().toString() + ".json";
            File file = new File(senderDir, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonJob);
            }
            logger.info("Archivo original guardado en: {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error al guardar archivo original", e);
        }
    }

    private static void processAndRouteJob(String jsonJob, KafkaProducer<String, String> producer) {
        try {
            PrintJob job = gson.fromJson(jsonJob, PrintJob.class);
            String text = job.getDocumento();
            
            // Dividir en páginas de 400 caracteres
            List<String> pagesContent = splitText(text, 400);
            int totalPages = pagesContent.size();

            String targetTopic = job.getTipo().equalsIgnoreCase("Color") ? 
                                 KafkaConfig.TOPIC_COLOR_QUEUE : KafkaConfig.TOPIC_BW_QUEUE;

            for (int i = 0; i < totalPages; i++) {
                PrintPage page = new PrintPage(
                    job.getTitulo(),
                    i + 1,
                    totalPages,
                    pagesContent.get(i),
                    job.getSender()
                );

                String pageJson = gson.toJson(page);
                // Usamos titulo como key para mantener orden de páginas si se usa particionado
                ProducerRecord<String, String> record = new ProducerRecord<>(targetTopic, job.getTitulo(), pageJson);
                
                producer.send(record);
                logger.info("Enviada página {}/{} al topic {}", (i+1), totalPages, targetTopic);
            }

        } catch (Exception e) {
            logger.error("Error procesando trabajo", e);
        }
    }

    private static List<String> splitText(String text, int size) {
        List<String> ret = new ArrayList<>((text.length() + size - 1) / size);
        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }
}
