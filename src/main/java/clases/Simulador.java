/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Cliente
 */


import estructuras.MonticuloBinario;
import estructuras.HashTableUsuarios;
import estructuras.ListaDocumentos;

public class Simulador {
    private MonticuloBinario colaImpresion;
    private HashTableUsuarios tablaUsuarios;
    private int reloj;

    public Simulador() {
        // Inicializamos con una capacidad razonable, ej. 100 documentos
        this.colaImpresion = new MonticuloBinario(100);
        this.tablaUsuarios = new HashTableUsuarios();
        this.reloj = 0;
    }

    /**
     * Incrementa el tiempo de la simulación.
     */
    public void avanzarReloj() {
        this.reloj++;
    }

    /**
     * Agrega un usuario al sistema (usualmente desde el CSV).
     */
    public void registrarUsuario(String nombre, String tipo) {
        Usuario nuevo = new Usuario(nombre, tipo);
        tablaUsuarios.insertar(nuevo);
    }

    /**
     * Lógica para enviar un documento a la cola de impresión.
     * Aquí se aplica la prioridad sobre la etiqueta de tiempo.
     */
    public void mandarAImprimir(String nombreUsuario, Documento doc, boolean esPrioritario) {
        Usuario u = tablaUsuarios.buscar(nombreUsuario);
        if (u == null) return;

        int etiqueta = this.reloj;

        if (esPrioritario) {
            // Mientras más pequeño el número, más prioridad en el Min-Heap.
            // Restamos un valor según el tipo de usuario.
            String tipo = u.getTipoPrioridad().toLowerCase();
            if (tipo.contains("alta")) etiqueta -= 20;
            else if (tipo.contains("media")) etiqueta -= 10;
            else etiqueta -= 5;
        }

        // Creamos el nodo y lo insertamos en el Montículo
        NodoPrioridad nodo = new NodoPrioridad(doc, etiqueta);
        colaImpresion.insertar(nodo);
        
        // El reloj avanza un paso por cada acción de envío
        avanzarReloj();
    }

    /**
     * Simula la "Liberación de impresora" (eliminar_min).
     */
    public Documento liberarImpresora() {
        NodoPrioridad atendido = colaImpresion.eliminarMin();
        return (atendido != null) ? atendido.getDocumento() : null;
    }

    // Getters para la interfaz gráfica
    public MonticuloBinario getColaImpresion() { return colaImpresion; }
    public HashTableUsuarios getTablaUsuarios() { return tablaUsuarios; }
    public int getReloj() { return reloj; }
}