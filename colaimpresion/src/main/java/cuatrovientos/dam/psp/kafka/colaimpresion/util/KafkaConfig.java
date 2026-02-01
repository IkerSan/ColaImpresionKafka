package cuatrovientos.dam.psp.kafka.colaimpresion.util;

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
}
