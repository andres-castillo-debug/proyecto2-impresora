package clases;

import estructura.MonticuloBinario;
import estructura.HashTableUsuarios;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase central del sistema de simulación de cola de impresión.
 *
 * <p>Integra el {@link MonticuloBinario} (cola de prioridad) con la
 * {@link HashTableUsuarios} (acceso O(1) a usuarios) para gestionar
 * el flujo completo de documentos desde su creación hasta su impresión.</p>
 *
 * <p>El reloj interno mide el tiempo transcurrido desde el inicio de la
 * simulación. Cada vez que se envía un documento a imprimir, la etiqueta
 * de tiempo puede ser modificada según la prioridad del usuario.</p>
 */
public class Simulador {

    /** Cola de impresión implementada como Montículo Binario (Min-Heap). */
    private MonticuloBinario colaImpresion;

    /** Tabla de dispersión para acceso O(1) a usuarios registrados. */
    private HashTableUsuarios tablaUsuarios;

    /** Reloj de la simulación: tiempo transcurrido en unidades enteras. */
    private int reloj;

    /** Capacidad máxima de la cola de impresión. */
    private static final int CAPACIDAD_COLA = 200;

    /**
     * Constructor que inicializa el simulador con estructuras vacías y reloj en 0.
     */
    public Simulador() {
        this.colaImpresion = new MonticuloBinario(CAPACIDAD_COLA);
        this.tablaUsuarios = new HashTableUsuarios();
        this.reloj         = 0;
    }

    // =========================================================================
    // GESTIÓN DE USUARIOS
    // =========================================================================

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param nombre        Nombre del usuario.
     * @param tipoPrioridad Tipo de prioridad (prioridad_alta, prioridad_media, prioridad_baja).
     * @return true si se registró, false si el usuario ya existía.
     */
    public boolean registrarUsuario(String nombre, String tipoPrioridad) {
        Usuario nuevo = new Usuario(nombre, tipoPrioridad);
        return tablaUsuarios.insertar(nuevo);
    }

    /**
     * Elimina un usuario del sistema.
     * Sus documentos creados (no en cola) se eliminan con él.
     * Los documentos ya encolados permanecen en la cola.
     *
     * @param nombre Nombre del usuario a eliminar.
     * @return true si se eliminó, false si no existía.
     */
    public boolean eliminarUsuario(String nombre) {
        return tablaUsuarios.eliminar(nombre);
    }

    /**
     * Carga usuarios desde un archivo CSV con formato: usuario,tipo
     *
     * @param rutaArchivo Ruta absoluta del archivo CSV.
     * @return Mensaje de resultado con cantidad de usuarios cargados.
     */
    public String cargarUsuariosDesdeCSV(String rutaArchivo) {
        int cargados = 0;
        int errores  = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length != 2) { errores++; continue; }

                String nombre = partes[0].trim();
                String tipo   = partes[1].trim().toLowerCase();

                // Saltar cabecera
                if (primeraLinea && (nombre.equalsIgnoreCase("usuario") || nombre.equalsIgnoreCase("nombre"))) {
                    primeraLinea = false;
                    continue;
                }
                primeraLinea = false;

                // Validar tipo de prioridad
                if (!tipo.equals("prioridad_alta") && !tipo.equals("prioridad_media") && !tipo.equals("prioridad_baja")) {
                    errores++;
                    continue;
                }

                if (registrarUsuario(nombre, tipo)) cargados++;
            }

        } catch (IOException e) {
            return "Error al leer el archivo: " + e.getMessage();
        }

        return "Carga completada: " + cargados + " usuario(s) registrado(s)" +
               (errores > 0 ? ", " + errores + " línea(s) ignorada(s)." : ".");
    }

    // =========================================================================
    // GESTIÓN DE DOCUMENTOS
    // =========================================================================

    /**
     * Crea un documento y lo agrega a la lista del usuario indicado.
     *
     * @param nombreUsuario Nombre del usuario propietario.
     * @param nombreDoc     Nombre del documento.
     * @param tamaño        Tamaño en páginas.
     * @param tipo          Tipo de archivo (PDF, DOCX, etc.).
     * @return true si se creó correctamente, false si el usuario no existe.
     */
    public boolean crearDocumento(String nombreUsuario, String nombreDoc, int tamaño, String tipo) {
        Usuario u = tablaUsuarios.buscar(nombreUsuario);
        if (u == null) return false;
        Documento doc = new Documento(nombreDoc, tamaño, tipo);
        return u.agregarDocumento(doc);
    }

    /**
     * Elimina un documento de la lista de un usuario (solo si no está en cola).
     *
     * @param nombreUsuario Nombre del usuario propietario.
     * @param nombreDoc     Nombre del documento a eliminar.
     * @return true si se eliminó, false si no existe o ya está en cola.
     */
    public boolean eliminarDocumentoUsuario(String nombreUsuario, String nombreDoc) {
        Usuario u = tablaUsuarios.buscar(nombreUsuario);
        if (u == null) return false;
        return u.eliminarDocumento(nombreDoc);
    }

    // =========================================================================
    // COLA DE IMPRESIÓN
    // =========================================================================

    /**
     * Envía un documento de un usuario a la cola de impresión.
     *
     * <p>Si es prioritario, la etiqueta de tiempo se reduce según el tipo de usuario:
     * alta: -20, media: -10, baja: -5. Esto hace que el nodo suba en el Min-Heap.</p>
     *
     * @param nombreUsuario Nombre del usuario propietario del documento.
     * @param nombreDoc     Nombre del documento a enviar.
     * @param esPrioritario Si true, aplica el descuento de prioridad al tiempo.
     * @return Mensaje de resultado o error.
     */
    public String mandarAImprimir(String nombreUsuario, String nombreDoc, boolean esPrioritario) {
        Usuario u = tablaUsuarios.buscar(nombreUsuario);
        if (u == null) return "Error: usuario '" + nombreUsuario + "' no encontrado.";

        Documento doc = u.buscarDocumento(nombreDoc);
        if (doc == null) return "Error: documento '" + nombreDoc + "' no encontrado.";
        if (doc.isEnCola()) return "El documento ya está en la cola de impresión.";

        int etiqueta = this.reloj;

        if (esPrioritario) {
            String tipo = u.getTipoPrioridad();
            if (tipo.contains("alta"))  etiqueta -= 20;
            else if (tipo.contains("media")) etiqueta -= 10;
            else etiqueta -= 5;
        }

        NodoPrioridad nodo = new NodoPrioridad(doc, etiqueta);
        if (!colaImpresion.insertar(nodo)) {
            return "Error: la cola de impresión está llena.";
        }

        doc.marcarEnCola();
        avanzarReloj();
        return "Documento '" + nombreDoc + "' encolado con etiqueta t=" + etiqueta + ".";
    }

    /**
     * Libera la impresora: extrae e imprime el documento de mayor prioridad (eliminar_min).
     *
     * @return El nodo impreso, o null si la cola está vacía.
     */
    public NodoPrioridad liberarImpresora() {
        NodoPrioridad atendido = colaImpresion.eliminarMin();
        if (atendido != null) avanzarReloj();
        return atendido;
    }

    /**
     * Elimina un documento de la cola de impresión sin imprimirlo.
     * Modifica su etiqueta a Integer.MIN_VALUE, lo sube al tope y lo extrae.
     *
     * @param nombreDoc Nombre del documento a eliminar de la cola.
     * @return Mensaje de resultado.
     */
    public String eliminarDeCola(String nombreDoc) {
        NodoPrioridad eliminado = colaImpresion.eliminarPorNombre(nombreDoc);
        if (eliminado == null) return "Documento '" + nombreDoc + "' no encontrado en la cola.";
        avanzarReloj();
        return "Documento '" + nombreDoc + "' eliminado de la cola (no impreso).";
    }

    /**
     * Incrementa el reloj de la simulación en una unidad.
     */
    public void avanzarReloj() { this.reloj++; }

    // =========================================================================
    // GETTERS
    // =========================================================================

    /**
     * Retorna la cola de impresión (Montículo Binario).
     * @return La instancia del MonticuloBinario.
     */
    public MonticuloBinario getColaImpresion() { return colaImpresion; }

    /**
     * Retorna la tabla de usuarios (HashTable).
     * @return La instancia de HashTableUsuarios.
     */
    public HashTableUsuarios getTablaUsuarios() { return tablaUsuarios; }

    /**
     * Retorna el valor actual del reloj de la simulación.
     * @return El tiempo transcurrido en unidades.
     */
    public int getReloj() { return reloj; }
}
