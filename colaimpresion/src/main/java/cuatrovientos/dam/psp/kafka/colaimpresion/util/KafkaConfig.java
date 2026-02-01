package cuatrovientos.dam.psp.kafka.colaimpresion.util;

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Configuración centralizada de constantes para Kafka
 * Contiene nombres de topics, grupos de consumo y directorios
 */
public class KafkaConfig {
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    
    // Nombres de los Topics
    public static final String TOPIC_PRINT_JOBS = "print-jobs-input";
    public static final String TOPIC_COLOR_QUEUE = "print-queue-color";
    public static final String TOPIC_BW_QUEUE = "print-queue-bw";
    
    // IDs de Grupo
    public static final String GROUP_ID_PROCESSOR = "job-processor-group";
    public static final String GROUP_ID_COLOR_PRINTERS = "color-printers-group";
    public static final String GROUP_ID_BW_PRINTERS = "bw-printers-group";
    
    // Rutas de almacenamiento
    public static final String STORAGE_ORIGINALS = "storage/originals";
    public static final String STORAGE_COLOR = "storage/prints/color";
    public static final String STORAGE_BW = "storage/prints/bw";

    public static Properties getProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public static Properties getConsumerProps(String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }
}
