
package clases;

/**
 * Clase que representa un usuario en el sistema de simulación de impresión.
 *
 * <p>Cada usuario tiene un nombre único, un tipo de prioridad que determina
 * cuán rápido se atienden sus documentos prioritarios, y una lista interna
 * de documentos creados (implementada como arreglo sin librerías).</p>
 *
 * <p>Los tipos de prioridad válidos son:
 * <ul>
 *   <li>{@code prioridad_alta} — descuento de 20 unidades de tiempo</li>
 *   <li>{@code prioridad_media} — descuento de 10 unidades de tiempo</li>
 *   <li>{@code prioridad_baja} — descuento de 5 unidades de tiempo</li>
 * </ul>
 * </p>
 */
public class Usuario {

    /** Nombre/identificador único del usuario. */
    private String nombre;

    /** Tipo de prioridad del usuario (prioridad_alta, prioridad_media, prioridad_baja). */
    private String tipoPrioridad;

    /** Arreglo de documentos del usuario (sin librerías). */
    private Documento[] documentos;

    /** Número actual de documentos almacenados. */
    private int numDocumentos;

    /** Capacidad máxima de documentos por usuario. */
    private static final int MAX_DOCS = 50;

    /**
     * Constructor que inicializa el usuario con nombre y tipo de prioridad.
     *
     * @param nombre        Nombre identificador del usuario.
     * @param tipoPrioridad Tipo de prioridad (prioridad_alta, prioridad_media, prioridad_baja).
     */
    public Usuario(String nombre, String tipoPrioridad) {
        this.nombre        = nombre;
        this.tipoPrioridad = tipoPrioridad.toLowerCase().trim();
        this.documentos    = new Documento[MAX_DOCS];
        this.numDocumentos = 0;
    }

    /**
     * Agrega un nuevo documento a la lista del usuario si no supera la capacidad.
     *
     * @param doc El documento a agregar.
     * @return true si se agregó correctamente, false si la lista está llena.
     */
    public boolean agregarDocumento(Documento doc) {
        if (numDocumentos >= MAX_DOCS) return false;
        documentos[numDocumentos++] = doc;
        return true;
    }

    /**
     * Elimina un documento de la lista del usuario por nombre,
     * solo si aún no ha sido enviado a la cola de impresión.
     *
     * @param nombreDoc Nombre del documento a eliminar.
     * @return true si se eliminó, false si no existe o ya está en cola.
     */
    public boolean eliminarDocumento(String nombreDoc) {
        for (int i = 0; i < numDocumentos; i++) {
            if (documentos[i].getNombre().equals(nombreDoc)) {
                if (documentos[i].isEnCola()) return false; // ya en cola, no se puede eliminar
                // Compactar el arreglo
                for (int j = i; j < numDocumentos - 1; j++) {
                    documentos[j] = documentos[j + 1];
                }
                documentos[--numDocumentos] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * Busca y retorna un documento del usuario por su nombre.
     *
     * @param nombreDoc Nombre del documento a buscar.
     * @return El documento encontrado, o null si no existe.
     */
    public Documento buscarDocumento(String nombreDoc) {
        for (int i = 0; i < numDocumentos; i++) {
            if (documentos[i].getNombre().equals(nombreDoc)) return documentos[i];
        }
        return null;
    }

    /**
     * Retorna un arreglo con los documentos actuales del usuario.
     * Solo incluye los documentos existentes (sin nulls al final).
     *
     * @return Arreglo de documentos del usuario.
     */
    public Documento[] getDocumentos() {
        Documento[] result = new Documento[numDocumentos];
        for (int i = 0; i < numDocumentos; i++) result[i] = documentos[i];
        return result;
    }

    /**
     * Retorna el nombre del usuario.
     * @return El nombre del usuario.
     */
    public String getNombre() { return nombre; }

    /**
     * Retorna el tipo de prioridad del usuario.
     * @return El tipo de prioridad.
     */
    public String getTipoPrioridad() { return tipoPrioridad; }

    /**
     * Retorna el número de documentos actuales del usuario.
     * @return Cantidad de documentos.
     */
    public int getNumDocumentos() { return numDocumentos; }

    /**
     * Representación textual del usuario para mostrar en la UI.
     * @return String con nombre y tipo de prioridad.
     */
    @Override
    public String toString() {
        return nombre + " (" + tipoPrioridad + ")";
    }
}
