# Sistema de Colas de Impresión con Kafka

Simulación de un sistema de impresión distribuido usando Apache Kafka. Los empleados envían trabajos, un procesador central los clasifica y divide, y las impresoras (consumidores) los procesan.

## Arquitectura

1.  **EmployeeProducer**: Empleados enviando trabajos (JSON) a `print-jobs-input`.
2.  **JobProcessor**: Consume trabajos, archiva originales y divide documentos en páginas. Envíando a `print-queue-color` o `print-queue-bw`.
3.  **PrinterConsumers**: Impresoras consumiendo de las colas de color o B/N.

## Requisitos Previos (Iniciar Kafka)

Para facilitar el inicio de Kafka (limpieza, formateo y arranque), hemos creado un script automático.

1.  Abre una terminal de PowerShell en la carpeta del proyecto.
2.  Ejecuta el script:
    ```powershell
    .\iniciarKafka.ps1
    ```
3.  Espera a que arranque y **no cierres esa ventana**.

**Nota**: Este script borra los datos antiguos para asegurar un inicio limpio cada vez.

## Ejecución

### Opción A: Terminal (Recomendado)

Abrir 4 terminales en la carpeta del proyecto `colaimpresion`:

1.  **Terminal 1 (Procesador):**
    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.processor.JobProcessor"
    ```
2.  **Terminal 2 (Impresoras Color):**
    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.consumer.ColorPrinterApp"
    ```
3.  **Terminal 3 (Impresoras B/N):**
    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.consumer.BWPrinterApp"
    ```
4.  **Terminal 4 (Empleado - Productor):**
    ```powershell
    mvn exec:java -Dexec.mainClass="cuatrovientos.dam.psp.kafka.colaimpresion.producer.EmployeeProducer"
    ```

### Opción B: Guía Detallada para Eclipse IDE

**Paso 0: Iniciar Kafka (Externo)**
Eclipse solo ejecuta el código Java. **Kafka debe estar corriendo** en terminales externas antes de empezar (ver sección "Paso 1" arriba).

**Paso 1: Localizar y Arrancar los programas**
En el **Project Explorer** de Eclipse, navega por `colaimpresion > src/main/java`. Tienes que arrancar estos 4 archivos (haz **Clic Derecho** en cada uno -> **Run As** -> **Java Application**):

1.  `...processor.JobProcessor` (El "Cerebro", clasifica los trabajos).
2.  `...consumer.ColorPrinterApp` (Las impresoras de Color).
3.  `...consumer.BWPrinterApp` (Las impresoras de B/N).
4.  `...producer.EmployeeProducer` (El Empleado, desde donde escribes).

**IMPORTANTE**: Arranca los 4 **uno detrás de otro**. No esperes a que terminen (nunca terminan, están "escuchando").

**Paso 2: Gestionar las Consolas (El truco)**
Al arrancar 4 programas, Eclipse abre 4 consolas, pero se amontonan una encima de otra y solo ves la última.

1.  Busca la pestaña **Console** (normalmente abajo).
2.  En la barra de herramientas de la consola, busca el icono de un **pequeño monitor** ("Display Selected Console") junto al botón rojo de Stop.
3.  Haz clic en la flechita de ese monitor para ver la lista de programas corriendo.

**Paso 3: Realizar la Prueba**

1.  Usa el icono del monitor para cambiar a la consola de **`EmployeeProducer`**.
2.  **Haz clic dentro del área blanca de la consola** para asegurarte de que puedes escribir.
3.  Escribe los datos que te pide (Nombre, Título, Contenido, Tipo: Color) y pulsa Enter.
4.  Vuelve al icono del monitor y cambia a la consola de **`JobProcessor`** o **`ColorPrinterApp`** para ver cómo mágicamente ha llegado y se ha procesado tu mensaje.

## Limpieza

Para reiniciar (borrar topics y archivos):

- Borrar carpeta `storage`.

## Troubleshooting

### Error: 'mvn' no se reconoce...

Si recibes este error en la terminal, significa que Maven no está configurado en las variables de entorno de tu sistema (PATH).

**Solución Rápida**: Utiliza la **Opción B: Eclipse IDE**, ya que Eclipse trae su propio Maven integrado y no requiere configuración extra.

### Error: "Shutdown broker because all log dirs... have failed"

Si Kafka se cierra solo con este error, es porque los logs antiguos están corruptos o entran en conflicto.

**Solución:**

1.  Borra la carpeta temporal: Ve al explorador de archivos y borra `C:\tmp\kraft-combined-logs`.
2.  Vuelve a ejecutar el **Paso A: Formatear el almacenamiento** (generar UUID y formatear).
3.  Arranca de nuevo con el **Paso B**.
