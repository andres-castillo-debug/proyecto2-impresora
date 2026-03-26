
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
