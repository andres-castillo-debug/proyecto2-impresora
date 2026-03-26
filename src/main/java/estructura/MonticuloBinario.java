package estructura;

import clases.NodoPrioridad;

/**
 * Implementación propia del TDA Montículo Binario (Min-Heap) sin librerías externas.
 *
 * <p>Estructura de datos utilizada como cola de prioridad para el sistema de impresión.
 * Se implementa mediante un arreglo de {@link NodoPrioridad}, aprovechando que
 * los árboles binarios completos pueden representarse sin punteros.</p>
 *
 * <p>Propiedades del Min-Heap mantenidas en todo momento:</p>
 * <ul>
 *   <li><b>Propiedad de forma:</b> árbol binario completo (lleno de izq. a der.).</li>
 *   <li><b>Propiedad de orden:</b> cada nodo tiene etiqueta ≤ que sus hijos.</li>
 * </ul>
 *
 * <p>Relaciones padre-hijo en el arreglo (índice base 0):</p>
 * <ul>
 *   <li>Padre del nodo i: {@code (i-1)/2}</li>
 *   <li>Hijo izquierdo de i: {@code 2*i+1}</li>
 *   <li>Hijo derecho de i: {@code 2*i+2}</li>
 * </ul>
 *
 * <p>Complejidad: insertar O(log n), eliminarMin O(log n).</p>
 */
public class MonticuloBinario {

    /** Arreglo interno que representa el árbol binario completo. */
    private NodoPrioridad[] heap;

    /** Número actual de elementos en el montículo. */
    private int tamano;

    /** Capacidad máxima del montículo. */
    private int capacidad;

    /**
     * Constructor que inicializa el montículo con la capacidad indicada.
     *
     * @param capacidad Número máximo de elementos que puede contener.
     */
    public MonticuloBinario(int capacidad) {
        this.capacidad = capacidad;
        this.heap      = new NodoPrioridad[capacidad];
        this.tamano    = 0;
    }

    /**
     * Inserta un nuevo nodo en el montículo (primitiva insertar).
     *
     * <p>El nodo se agrega al final del arreglo (mantiene propiedad de forma)
     * y luego sube hasta su posición correcta (mantiene propiedad de orden).
     * Operación O(log n).</p>
     *
     * @param nuevo El nodo de prioridad a insertar.
     * @return true si se insertó correctamente, false si el heap está lleno.
     */
    public boolean insertar(NodoPrioridad nuevo) {
        if (tamano >= capacidad) return false;
        heap[tamano] = nuevo;
        subirNodo(tamano);
        tamano++;
        return true;
    }

    /**
     * Elimina y retorna el nodo con menor etiqueta de tiempo (primitiva eliminar_min).
     *
     * <p>El mínimo siempre está en heap[0]. Se reemplaza por el último elemento,
     * se reduce el tamaño y se baja el nuevo nodo hasta su posición correcta.
     * Operación O(log n).</p>
     *
     * @return El nodo con menor etiqueta (mayor prioridad), o null si el heap está vacío.
     */
    public NodoPrioridad eliminarMin() {
        if (tamano == 0) return null;
        NodoPrioridad min = heap[0];
        heap[0] = heap[tamano - 1];
        heap[tamano - 1] = null;
        tamano--;
        if (tamano > 0) bajarNodo(0);
        return min;
    }

    /**
     * Elimina un documento específico de la cola por nombre.
     *
     * <p>Como el montículo no soporta búsqueda directa, el proceso consiste en:</p>
     * <ol>
     *   <li>Localizar el nodo por nombre del documento (única excepción a no recorrer).</li>
     *   <li>Asignarle la etiqueta Integer.MIN_VALUE para que suba al tope.</li>
     *   <li>Llamar a eliminarMin() para extraerlo sin imprimirlo.</li>
     * </ol>
     *
     * @param nombreDoc Nombre del documento a eliminar de la cola.
     * @return El nodo eliminado, o null si no se encontró.
     */
    public NodoPrioridad eliminarPorNombre(String nombreDoc) {
        int indice = -1;
        for (int i = 0; i < tamano; i++) {
            if (heap[i].getDocumento().getNombre().equals(nombreDoc)) {
                indice = i;
                break;
            }
        }
        if (indice == -1) return null;

        // Asignar la mayor prioridad posible para subirlo al tope
        heap[indice].setEtiquetaTiempo(Integer.MIN_VALUE);
        subirNodo(indice);

        // Ahora está en heap[0], eliminarlo sin imprimirlo
        return eliminarMin();
    }

    /**
     * Retorna (sin eliminar) el nodo con mayor prioridad (menor etiqueta).
     *
     * @return El nodo en la cima del heap, o null si está vacío.
     */
    public NodoPrioridad verMin() {
        return (tamano > 0) ? heap[0] : null;
    }

    /**
     * Sube el nodo en la posición i hasta cumplir la propiedad de orden del heap.
     * Compara con su padre y sube mientras sea menor.
     *
     * @param i Índice del nodo a subir.
     */
    private void subirNodo(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (heap[i].getEtiquetaTiempo() < heap[padre].getEtiquetaTiempo()) {
                intercambiar(i, padre);
                i = padre;
            } else {
                break;
            }
        }
    }

    /**
     * Baja el nodo en la posición i hasta cumplir la propiedad de orden del heap.
     * Compara con sus hijos y baja hacia el hijo de menor etiqueta.
     *
     * @param i Índice del nodo a bajar.
     */
    private void bajarNodo(int i) {
        int menor = i;
        int izq   = 2 * i + 1;
        int der   = 2 * i + 2;

        if (izq < tamano && heap[izq].getEtiquetaTiempo() < heap[menor].getEtiquetaTiempo())
            menor = izq;
        if (der < tamano && heap[der].getEtiquetaTiempo() < heap[menor].getEtiquetaTiempo())
            menor = der;

        if (menor != i) {
            intercambiar(i, menor);
            bajarNodo(menor);
        }
    }

    /**
     * Intercambia dos nodos en el arreglo.
     *
     * @param i Índice del primer nodo.
     * @param j Índice del segundo nodo.
     */
    private void intercambiar(int i, int j) {
        NodoPrioridad temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    /**
     * Retorna el arreglo interno del heap para visualización y dibujo del árbol.
     * @return El arreglo de NodoPrioridad (puede contener nulls al final).
     */
    public NodoPrioridad[] getArreglo() { return heap; }

    /**
     * Retorna el número actual de elementos en el montículo.
     * @return El tamaño del heap.
     */
    public int getTamano() { return tamano; }

    /**
     * Verifica si el montículo está vacío.
     * @return true si no hay elementos, false en caso contrario.
     */
    public boolean estaVacio() { return tamano == 0; }

    /**
     * Verifica si el montículo está lleno.
     * @return true si alcanzó la capacidad máxima.
     */
    public boolean estaLleno() { return tamano >= capacidad; }
}
