# Sistema de Colas de Impresión con Kafka

Este proyecto implementa una simulación de sistema de colas de impresión distribuido utilizando Apache Kafka.

## 🏗 Arquitectura

El sistema consta de los siguientes componentes desacoplados que se comunican a través de Topics de Kafka:

1.  **EmployeeProducer (Productor)**:
    - Simula la estación de trabajo de un empleado.
    - Envía trabajos de impresión en formato JSON al topic `print-jobs-input`.
    - Format del mensaje: `{ "titulo": "...", "documento": "...", "tipo": "B/N"| "Color", "sender": "..." }`.

2.  **JobProcessor (Procesador Central)**:
    - Consume mensajes de `print-jobs-input`.
    - Realiza dos tareas en paralelo para maximizar eficiencia:
      1.  **Archivado**: Guarda el JSON original en `storage/originals/{sender}/`.
      2.  **Procesamiento**: Divide el contenido del documento en páginas de 400 caracteres.
    - Enruta las páginas generadas a la cola correspondiente: `print-queue-color` o `print-queue-bw`.

3.  **PrinterConsumers (Impresoras)**:
    - Simulan impresoras físicas consumiendo de las colas.
    - **ColorPrinterApp**: Lanza 2 hilos (impresoras) consumiendo de `print-queue-color`.
    - **BWPrinterApp**: Lanza 3 hilos (impresoras) consumiendo de `print-queue-bw`.
    - Los documentos "impresos" se guardan en `storage/prints/color` o `storage/prints/bw`.

### Topics Implementados

- `print-jobs-input`: Entrada de trabajos crudos.
- `print-queue-color`: Cola de páginas para impresión a color.
- `print-queue-bw`: Cola de páginas para impresión en blanco y negro.

---

## 🚀 Puesta en Marcha (Entorno de Desarrollo)

### Prerrequisitos

- Java 11 o superior.
- Apache Kafka y Zookeeper instalados y corriendo localmente (puerto 9092).
- Maven.

### Paso 1: Iniciar Kafka (Windows)

Abre una terminal en tu directorio de Kafka y ejecuta:

1.  **Zookeeper**:
    ```powershell
    .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
    ```
2.  **Kafka Server**:
    ```powershell
    .\bin\windows\kafka-server-start.bat .\config\server.properties
    ```

### Paso 2: Ejecutar los Componentes

Es necesario ejecutar los componentes en terminales separadas para ver el funcionamiento en paralelo.

1.  **Compilar el proyecto**:

    ```powershell
    cd colaimpresion
    mvn clean package
    ```

2.  **Iniciar Procesador (JobProcessor)**:

    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.processor.JobProcessor"
    ```

3.  **Iniciar Impresoras Color**:

    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.consumer.ColorPrinterApp"
    ```

4.  **Iniciar Impresoras B/N**:

    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.consumer.BWPrinterApp"
    ```

5.  **Enviar Trabajos (Empleado)**:
    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.producer.EmployeeProducer"
    ```
    Sigue las instrucciones en pantalla para enviar documentos.

---

## 🧹 Reinicio del Sistema y Limpieza

Para el **Implantador** o **Mantenedor**:

### Limpiar Topics (Resetear Colas)

Si se desea vaciar las colas de impresión, se pueden borrar y recrear los topics:

```powershell
# Borrar topics
.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --delete --topic print-jobs-input
.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --delete --topic print-queue-color
.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --delete --topic print-queue-bw
```

(Kafka los recreará automáticamente con la configuración por defecto al recibir nuevos mensajes, o se pueden crear manualmente si se requieren particiones específicas).

### Limpiar Archivos Generados

Para reiniciar el estado del almacenamiento local, borrar la carpeta `storage` en el directorio raíz de la ejecución:

```powershell
Remove-Item -Recurse -Force storage
```

---

## 📋 Log de Cambios (Commits Realizados)

1.  **feat(project)**: Inicialización del proyecto Maven, dependencias y modelos.
2.  **feat(core)**: Implementación del Productor y Procesador con lógica paralela.
3.  **feat(consumers)**: Implementación de las aplicaciones de impresión (Color y B/N).
