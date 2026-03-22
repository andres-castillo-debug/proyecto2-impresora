/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructura;

/**
 *
 * @author Cliente
 */
public class HashTableUsuarios {
    private NodoHash[] tabla;
    private int tamañoArray = 100;

    private class NodoHash {
        String nombreUsuario;
        String tipo; // prioridad_alta, etc.
        NodoHash siguiente;

        public NodoHash(String n, String t) {
            this.nombreUsuario = n;
            this.tipo = t;
        }
    }

    public HashTableUsuarios() {
        tabla = new NodoHash[tamañoArray];
    }

    // Función Hash simple
    private int funcionHash(String key) {
        return Math.abs(key.hashCode()) % tamañoArray;
    }

    public void insertar(String nombre, String tipo) {
        int indice = funcionHash(nombre);
        NodoHash nuevo = new NodoHash(nombre, tipo);
        if (tabla[indice] == null) {
            tabla[indice] = nuevo;
        } else {
            // Insertar al inicio de la lista enlazada (colisión)
            nuevo.siguiente = tabla[indice];
            tabla[indice] = nuevo;
        }
    }
    
    public String buscarTipo(String nombre) {
        int indice = funcionHash(nombre);
        NodoHash aux = tabla[indice];
        while (aux != null) {
            if (aux.nombreUsuario.equals(nombre)) return aux.tipo;
            aux = aux.siguiente;
        }
        return null;
    }
}
