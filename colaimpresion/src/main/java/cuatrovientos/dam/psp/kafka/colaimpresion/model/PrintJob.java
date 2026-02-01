package cuatrovientos.dam.psp.kafka.colaimpresion.model;

public class PrintJob {
    private String titulo;
    private String documento;
    private String tipo; // "B/N" or "Color"
    private String sender;
    
    public PrintJob() {}

    public PrintJob(String titulo, String documento, String tipo, String sender) {
        this.titulo = titulo;
        this.documento = documento;
        this.tipo = tipo;
        this.sender = sender;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "PrintJob{" +
                "titulo='" + titulo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
