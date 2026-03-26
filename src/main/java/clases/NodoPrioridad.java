package clases;


public class NodoPrioridad {

    /** El documento asociado a este nodo de la cola. */
    private Documento documento;

    /**
     * Etiqueta de tiempo que determina la prioridad en el Min-Heap.
     * Menor valor = mayor prioridad = se imprime antes.
     * Puede ser negativa si se aplicó un descuento de prioridad alta.
     */
    private int etiquetaTiempo;

    /**
     * Constructor que crea un nodo de prioridad para la cola de impresión.
     *
     * @param documento     El documento a encolar.
     * @param etiquetaTiempo El valor de tiempo/prioridad (menor = más urgente).
     */
    public NodoPrioridad(Documento documento, int etiquetaTiempo) {
        this.documento     = documento;
        this.etiquetaTiempo = etiquetaTiempo;
    }

    /**
     * Retorna el documento asociado a este nodo.
     * @return El documento.
     */
    public Documento getDocumento() { return documento; }

    /**
     * Retorna la etiqueta de tiempo de este nodo.
     * @return El valor de la etiqueta (menor = mayor prioridad).
     */
    public int getEtiquetaTiempo() { return etiquetaTiempo; }

   
    public void setEtiquetaTiempo(int etiqueta) { this.etiquetaTiempo = etiqueta; }

    /**
     * Representación textual del nodo para la vista de cola como lista.
     * @return String con nombre del documento y etiqueta de tiempo.
     */
    @Override
    public String toString() {
        return "[t=" + etiquetaTiempo + "] " + documento.getNombre()
               + " (" + documento.getTipo() + ", " + documento.getTamaño() + " pág.)";
    }
}
