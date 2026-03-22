/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Cliente
 */
public class Documento {
    private String nombre;
    private int tamaño;
    private String tipo; // ej: "PDF", "Docx"

    public Documento(String nombre, int tamaño, String tipo) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.tipo = tipo;
    }

    // Getters
    public String getNombre() { return nombre; }
    public int getTamaño() { return tamaño; }
}