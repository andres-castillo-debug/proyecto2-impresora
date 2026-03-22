/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Cliente
 */
public class NodoPrioridad {
    String nombreDocumento;
    int etiquetaTiempo; // El valor por el cual se ordena el Min-Heap

    public NodoPrioridad(String nombre, int etiqueta) {
        this.nombreDocumento = nombre;
        this.etiquetaTiempo = etiqueta;
    }
}
