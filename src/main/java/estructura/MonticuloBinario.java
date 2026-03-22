/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructura;

/**
 *
 * @author Cliente
 */

import clases.NodoPrioridad;

public class MonticuloBinario {
    private NodoPrioridad[] heap;
    private int tamano;

    public MonticuloBinario(int capacidad) {
        this.heap = new NodoPrioridad[capacidad];
        this.tamano = 0;
    }

    public void insertar(NodoPrioridad nuevo) {
        if (tamano >= heap.length) return;
        heap[tamano] = nuevo;
        siftUp(tamano);
        tamano++;
    }

    private void siftUp(int i) {
        while (i > 0 && heap[i].getEtiquetaTiempo() < heap[(i - 1) / 2].getEtiquetaTiempo()) {
            intercambiar(i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    public NodoPrioridad eliminarMin() {
        if (tamano == 0) return null;
        NodoPrioridad min = heap[0];
        heap[0] = heap[tamano - 1];
        tamano--;
        siftDown(0);
        return min;
    }

    private void siftDown(int i) {
        int menor = i;
        int izq = 2 * i + 1;
        int der = 2 * i + 2;

        if (izq < tamano && heap[izq].getEtiquetaTiempo() < heap[menor].getEtiquetaTiempo()) menor = izq;
        if (der < tamano && heap[der].getEtiquetaTiempo() < heap[menor].getEtiquetaTiempo()) menor = der;

        if (menor != i) {
            intercambiar(i, menor);
            siftDown(menor);
        }
    }

    private void intercambiar(int i, int j) {
        NodoPrioridad temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public NodoPrioridad[] getArreglo() { return heap; }
    public int getTamano() { return tamano; }
}