package clases;

/**
 * Clase que representa un documento en el sistema de impresión.
 *
 * <p>Cada documento pertenece a un usuario y puede encontrarse en dos estados:
 * pendiente (creado pero no enviado a la cola) o en cola (enviado a imprimir).
 * El documento almacena su nombre, tamaño en páginas y tipo de archivo.</p>
 */
public class Documento {

    /** Nombre identificador del documento. */
    private String nombre;

    /** Tamaño del documento en número de páginas. */
    private int tamaño;

    /** Tipo del documento (ej: PDF, DOCX, TXT, JPG). */
    private String tipo;

    /** Indica si este documento ya fue enviado a la cola de impresión. */
    private boolean enCola;

    /**
     * Constructor que crea un nuevo documento con estado "no en cola".
     *
     * @param nombre Nombre del documento.
     * @param tamaño Número de páginas del documento.
     * @param tipo   Tipo de archivo (PDF, DOCX, etc.).
     */
    public Documento(String nombre, int tamaño, String tipo) {
        this.nombre  = nombre;
        this.tamaño  = tamaño;
        this.tipo    = tipo;
        this.enCola  = false;
    }

    /**
     * Retorna el nombre del documento.
     * @return El nombre del documento.
     */
    public String getNombre() { return nombre; }

    /**
     * Retorna el tamaño del documento en páginas.
     * @return Número de páginas.
     */
    public int getTamaño() { return tamaño; }

    /**
     * Retorna el tipo de archivo del documento.
     * @return El tipo (PDF, DOCX, TXT, etc.).
     */
    public String getTipo() { return tipo; }

    /**
     * Indica si el documento ya fue enviado a la cola de impresión.
     * @return true si está en cola, false si aún está pendiente.
     */
    public boolean isEnCola() { return enCola; }

    /**
     * Marca el documento como enviado a la cola de impresión.
     */
    public void marcarEnCola() { this.enCola = true; }

    /**
     * Retorna una representación textual del documento para mostrar en la UI.
     * @return String con nombre, tipo y tamaño.
     */
    @Override
    public String toString() {
        return nombre + " [" + tipo + ", " + tamaño + " pág.]" + (enCola ? " ✓en cola" : "");
    }
}
