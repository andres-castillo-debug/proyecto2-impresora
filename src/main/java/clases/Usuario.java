/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Cliente
 */


public class Usuario {
    private String nombre;
    private String tipoPrioridad; // alta, media, baja

    public Usuario(String nombre, String tipoPrioridad) {
        this.nombre = nombre;
        this.tipoPrioridad = tipoPrioridad;
    }

    public String getNombre() { return nombre; }
    public String getTipoPrioridad() { return tipoPrioridad; }
}
