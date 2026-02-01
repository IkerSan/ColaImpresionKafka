package cuatrovientos.dam.psp.kafka.colaimpresion.model;

/**
 * Modelo de datos que representa una página individual de un documento.
 * Generado por el JobProcessor y consumido por las impresoras.
 */
public class PrintPage {
    private String titulo;
    private int pageNumber;
    private int totalPages;
    private String content; 
    private String originalSender; 

    public PrintPage() {}

    public PrintPage(String titulo, int pageNumber, int totalPages, String content, String originalSender) {
        this.titulo = titulo;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.content = content;
        this.originalSender = originalSender;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }

    @Override
    public String toString() {
        return "PrintPage{" +
                "titulo='" + titulo + '\'' +
                ", page=" + pageNumber +
                "/" + totalPages +
                '}';
    }
}
