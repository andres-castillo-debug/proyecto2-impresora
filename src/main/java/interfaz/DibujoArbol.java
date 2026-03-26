
package interfaz;

import clases.NodoPrioridad;
import estructura.MonticuloBinario;
import javax.swing.JPanel;
import java.awt.*;

/**
 * Panel que dibuja el Montículo Binario como árbol binario visual.
 *
 */
public class DibujoArbol extends JPanel {

    /** El montículo binario cuya estructura se visualizará. */
    private MonticuloBinario monticulo;

    /** Radio de cada nodo dibujado en píxeles. */
    private static final int RADIO = 26;

    /** Paleta de colores por nivel del árbol. */
    private static final Color[] COLORES_NIVEL = {
        new Color(230, 57, 70),   // Nivel 0 (raíz) — rojo
        new Color(69, 123, 157),  // Nivel 1 — azul
        new Color(42, 157, 143),  // Nivel 2 — teal
        new Color(244, 162, 97),  // Nivel 3 — naranja
        new Color(106, 76, 147),  // Nivel 4 — morado
        new Color(82, 183, 136),  // Nivel 5 — verde
    };

    /**
     * Constructor que inicializa el panel con fondo oscuro.
     */
    public DibujoArbol() {
        setBackground(new Color(15, 23, 42));
        setPreferredSize(new Dimension(700, 400));
    }

    /**
     * Actualiza el montículo a visualizar y redibuja el panel.
     *
     * @param m El montículo binario a dibujar.
     */
    public void setMonticulo(MonticuloBinario m) {
        this.monticulo = m;
        repaint();
    }

    /**
     * Dibuja el árbol completo sobre el panel.
     * Si el montículo está vacío, muestra un mensaje informativo.
     *
     * @param g El contexto gráfico de Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (monticulo == null || monticulo.getTamano() == 0) {
            g2.setColor(new Color(100, 116, 139));
            g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
            String msg = "La cola de impresión está vacía";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            return;
        }

        int n       = monticulo.getTamano();
        int anchoPanel = getWidth();

        // Calcular posiciones X e Y de cada nodo
        int[] posX = new int[n];
        int[] posY = new int[n];

        // Nivel 0 (raíz) en el centro superior
        posX[0] = anchoPanel / 2;
        posY[0] = 50;

        // Para cada nodo, calcular la posición de sus hijos
        for (int i = 0; i < n; i++) {
            int nivel  = (int)(Math.log(i + 1) / Math.log(2));
            int nodosPorNivel = (int) Math.pow(2, nivel);
            int espacioX = anchoPanel / (nodosPorNivel + 1);

            int posEnNivel = i - (nodosPorNivel - 1); // posición dentro de su nivel
            posX[i] = espacioX * (posEnNivel + 1);
            posY[i] = 50 + nivel * 80;

            // Calcular posición de hijos
            int izq = 2 * i + 1;
            int der = 2 * i + 2;
            int sigNivel = nivel + 1;
            int sigNodosPorNivel = (int) Math.pow(2, sigNivel);
            int sigEspacioX = anchoPanel / (sigNodosPorNivel + 1);

            if (izq < n) {
                int posIzqEnNivel = izq - ((int) Math.pow(2, sigNivel) - 1);
                posX[izq] = sigEspacioX * (posIzqEnNivel + 1);
                posY[izq]  = 50 + sigNivel * 80;
            }
            if (der < n) {
                int posDerEnNivel = der - ((int) Math.pow(2, sigNivel) - 1);
                posX[der] = sigEspacioX * (posDerEnNivel + 1);
                posY[der]  = 50 + sigNivel * 80;
            }
        }

        NodoPrioridad[] heap = monticulo.getArreglo();

        // 1. Dibujar aristas primero (para que queden detrás de los nodos)
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < n; i++) {
            int izq = 2 * i + 1;
            int der = 2 * i + 2;
            g2.setColor(new Color(51, 65, 85));
            if (izq < n) g2.drawLine(posX[i], posY[i], posX[izq], posY[izq]);
            if (der < n) g2.drawLine(posX[i], posY[i], posX[der], posY[der]);
        }

        // 2. Dibujar nodos encima de las aristas
        for (int i = 0; i < n; i++) {
            int nivel = (int)(Math.log(i + 1) / Math.log(2));
            Color colorNodo = COLORES_NIVEL[Math.min(nivel, COLORES_NIVEL.length - 1)];

            // Sombra del nodo
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(posX[i] - RADIO + 2, posY[i] - RADIO + 2, RADIO * 2, RADIO * 2);

            // Relleno del nodo
            g2.setColor(colorNodo);
            g2.fillOval(posX[i] - RADIO, posY[i] - RADIO, RADIO * 2, RADIO * 2);

            // Borde del nodo
            g2.setColor(colorNodo.brighter());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(posX[i] - RADIO, posY[i] - RADIO, RADIO * 2, RADIO * 2);

            // Texto: etiqueta de tiempo
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            String etiqueta = "t=" + heap[i].getEtiquetaTiempo();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(etiqueta, posX[i] - fm.stringWidth(etiqueta) / 2, posY[i] + 4);

            // Texto: nombre del documento (debajo del nodo)
            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String nombre = heap[i].getDocumento().getNombre();
            if (nombre.length() > 10) nombre = nombre.substring(0, 9) + "…";
            fm = g2.getFontMetrics();
            g2.drawString(nombre, posX[i] - fm.stringWidth(nombre) / 2, posY[i] + RADIO + 14);
        }
    }
}
