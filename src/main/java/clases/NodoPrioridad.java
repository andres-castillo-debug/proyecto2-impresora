package clases;

/**
 * Clase que representa un nodo dentro de la cola de prioridad (Montículo Binario).
 *
 * <p>Cada nodo contiene la información del documento enviado a imprimir
 * y una etiqueta de tiempo que determina su posición en el Min-Heap.
 * A menor etiqueta de tiempo, mayor prioridad en la cola.</p>
 *
 * <p>Importante: el nodo NO guarda información sobre el propietario del documento,
 * lo cual es intencional según el enunciado. La relación usuario-documento
 * se gestiona a través de la {@link estructura.HashTableUsuarios}.</p>
 */
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

    /**
     * Modifica la etiqueta de tiempo de este nodo.
     * Se usa en la operación de eliminación de un documento de la cola,
     * donde se le asigna Integer.MIN_VALUE para subirlo al tope del heap.
     *
     * @param etiqueta El nuevo valor de la etiqueta.
     */
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
