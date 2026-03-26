package estructura;

import clases.Usuario;

/**
 * Implementación propia de una Tabla de Dispersión (Hash Table) para usuarios.
 *
 * <p>Permite acceso cercano a O(1) para buscar usuarios por nombre,
 * lo que es fundamental para localizar datos del propietario de un documento
 * que se desea eliminar de la cola, sin necesidad de recorrer el montículo.</p>
 *
 * <p>La función de dispersión aplica el método de división sobre el valor
 * hash del nombre del usuario. Las colisiones se resuelven mediante
 * <b>encadenamiento separado</b> (listas enlazadas en cada celda).</p>
 *
 * <p>Complejidad promedio: O(1) para insertar, buscar y eliminar.</p>
 */
public class HashTableUsuarios {

    /** Tamaño de la tabla (número primo para reducir colisiones). */
    private static final int TAMAÑO_TABLA = 101;

    /** Arreglo de cabeceras de listas enlazadas (encadenamiento). */
    private NodoHash[] tabla;

    /** Número total de usuarios almacenados. */
    private int totalUsuarios;

    /**
     * Nodo interno de la lista enlazada para resolución de colisiones.
     * Cada nodo guarda el objeto Usuario completo y el siguiente nodo de la cadena.
     */
    private static class NodoHash {
        Usuario usuario;
        NodoHash siguiente;

        /**
         * Constructor del nodo hash.
         * @param u El usuario a almacenar.
         */
        NodoHash(Usuario u) {
            this.usuario   = u;
            this.siguiente = null;
        }
    }

    /**
     * Constructor que inicializa la tabla vacía.
     */
    public HashTableUsuarios() {
        tabla         = new NodoHash[TAMAÑO_TABLA];
        totalUsuarios = 0;
    }

    /**
     * Función de dispersión: calcula el índice en la tabla para una clave dada.
     * Usa el valor hash del String con operación módulo sobre el tamaño de la tabla.
     *
     * @param clave El nombre del usuario (clave de búsqueda).
     * @return Índice entre 0 y TAMAÑO_TABLA - 1.
     */
    private int funcionHash(String clave) {
        return Math.abs(clave.hashCode()) % TAMAÑO_TABLA;
    }

    /**
     * Inserta un usuario en la tabla de dispersión.
     * Si ya existe un usuario con el mismo nombre, no lo duplica.
     *
     * @param usuario El objeto Usuario a insertar.
     * @return true si se insertó, false si ya existía.
     */
    public boolean insertar(Usuario usuario) {
        if (buscar(usuario.getNombre()) != null) return false; // ya existe

        int indice     = funcionHash(usuario.getNombre());
        NodoHash nuevo = new NodoHash(usuario);

        // Insertar al inicio de la cadena (O(1))
        nuevo.siguiente = tabla[indice];
        tabla[indice]   = nuevo;
        totalUsuarios++;
        return true;
    }

    /**
     * Busca y retorna un usuario por su nombre.
     * Complejidad promedio O(1), O(n) en el peor caso (muchas colisiones).
     *
     * @param nombre El nombre del usuario a buscar.
     * @return El objeto Usuario encontrado, o null si no existe.
     */
    public Usuario buscar(String nombre) {
        int indice    = funcionHash(nombre);
        NodoHash aux  = tabla[indice];
        while (aux != null) {
            if (aux.usuario.getNombre().equals(nombre)) return aux.usuario;
            aux = aux.siguiente;
        }
        return null;
    }

    /**
     * Elimina un usuario de la tabla por su nombre.
     * No elimina sus documentos que ya están en la cola de impresión.
     *
     * @param nombre El nombre del usuario a eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    public boolean eliminar(String nombre) {
        int indice   = funcionHash(nombre);
        NodoHash aux = tabla[indice];

        if (aux == null) return false;

        // Caso: es el primer nodo de la cadena
        if (aux.usuario.getNombre().equals(nombre)) {
            tabla[indice] = aux.siguiente;
            totalUsuarios--;
            return true;
        }

        // Caso: está en la mitad/final de la cadena
        while (aux.siguiente != null) {
            if (aux.siguiente.usuario.getNombre().equals(nombre)) {
                aux.siguiente = aux.siguiente.siguiente;
                totalUsuarios--;
                return true;
            }
            aux = aux.siguiente;
        }
        return false;
    }

    /**
     * Retorna todos los usuarios almacenados en la tabla como arreglo.
     * Útil para mostrar la lista de usuarios en la interfaz gráfica.
     *
     * @return Arreglo con todos los objetos Usuario.
     */
    public Usuario[] getTodosLosUsuarios() {
        Usuario[] result = new Usuario[totalUsuarios];
        int idx = 0;
        for (int i = 0; i < TAMAÑO_TABLA; i++) {
            NodoHash aux = tabla[i];
            while (aux != null) {
                result[idx++] = aux.usuario;
                aux = aux.siguiente;
            }
        }
        return result;
    }

    /**
     * Retorna el número total de usuarios registrados.
     * @return Cantidad de usuarios en la tabla.
     */
    public int getTotalUsuarios() { return totalUsuarios; }

    /**
     * Verifica si la tabla está vacía.
     * @return true si no hay usuarios registrados.
     */
    public boolean estaVacia() { return totalUsuarios == 0; }
}
