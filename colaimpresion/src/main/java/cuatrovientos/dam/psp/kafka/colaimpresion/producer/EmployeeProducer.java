package cuatrovientos.dam.psp.kafka.colaimpresion.producer;

import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cuatrovientos.dam.psp.kafka.colaimpresion.model.PrintJob;
import cuatrovientos.dam.psp.kafka.colaimpresion.util.KafkaConfig;

/**
 * Productor que simula a un empleado enviando documentos.
 * Permite la entrada interactiva de trabajos de impresión.
 */
public class EmployeeProducer {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeProducer.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Properties props = KafkaConfig.getProducerProps();

        // Creamos el productor
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props);
             Scanner scanner = new Scanner(System.in)) {

            logger.info("Sistema de Envío de Documentos Iniciado");
            
            while (true) {
                System.out.println("\n--- Nuevo Documento ---");
                System.out.print("Nombre del empleado (o 'salir'): ");
                String sender = scanner.nextLine();
                if ("salir".equalsIgnoreCase(sender)) break;

                System.out.print("Título del documento: ");
                String titulo = scanner.nextLine();

                System.out.print("Contenido del documento: ");
                String documento = scanner.nextLine();

                System.out.print("Tipo (B/N o Color): ");
                String tipo = scanner.nextLine();
                
                // Normalizar tipo
                if (!tipo.equalsIgnoreCase("Color") && !tipo.equalsIgnoreCase("B/N")) {
                	System.out.println("ADVERTENCIA: Tipo desconicido, se usará B/N por defecto.");
                	tipo = "B/N";
                }

                PrintJob job = new PrintJob(titulo, documento, tipo, sender);
                String jsonJob = gson.toJson(job);

                // Usamos el sender como Key para garantizar orden si fuera necesario, 
                // o UUID si queremos balanceo 
                String key = sender; 
                
                ProducerRecord<String, String> record = new ProducerRecord<>(
                        KafkaConfig.TOPIC_PRINT_JOBS,
                        key,
                        jsonJob
                );

                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        logger.error("Error enviando mensaje", exception);
                    } else {
                        logger.info("Documento enviado correctamente a topic {} partition @ offset {}", metadata.topic(), metadata.offset());
                    }
                });
            }

        } catch (Exception e) {
            logger.error("Error en el productor", e);
        }
    }
}
