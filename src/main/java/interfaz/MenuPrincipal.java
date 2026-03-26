package interfaz;

import clases.Documento;
import clases.NodoPrioridad;
import clases.Simulador;
import clases.Usuario;
import estructura.MonticuloBinario;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la simulación del sistema de cola de impresión.
 *
 * <p>Implementa la interfaz gráfica completa del simulador, organizada en
 * tres paneles principales:</p>
 * <ul>
 *   <li>Panel izquierdo: gestión de usuarios y sus documentos.</li>
 *   <li>Panel central: cola de impresión (vista lista y vista árbol).</li>
 *   <li>Panel derecho: controles del SO y reloj de simulación.</li>
 * </ul>
 */
public class MenuPrincipal extends JFrame {

    // ── Modelo ─────────────────────────────────────────────────────────────
    /** Instancia central del simulador (modelo de la aplicación). */
    private Simulador simulador;

    /** Timer de Swing para actualizar el reloj cada segundo. */
    private Timer timerReloj;

    // ── Panel izquierdo: Usuarios ───────────────────────────────────────────
    private JList<String> listaUsuarios;
    private DefaultListModel<String> modeloUsuarios;
    private JList<String> listaDocumentos;
    private DefaultListModel<String> modeloDocumentos;

    // ── Panel central: Cola ─────────────────────────────────────────────────
    private JList<String> listaCola;
    private DefaultListModel<String> modeloCola;
    private DibujoArbol panelArbol;
    private JTabbedPane tabsCola;

    // ── Panel derecho: Controles ────────────────────────────────────────────
    private JLabel lblReloj;
    private JTextArea txtLog;

    /**
     * Constructor que inicializa el simulador y construye la interfaz gráfica.
     */
    public MenuPrincipal() {
        simulador = new Simulador();
        initUI();
        iniciarReloj();
    }

    /**
     * Construye todos los componentes de la interfaz y los organiza en la ventana.
     */
    private void initUI() {
        setTitle("PrintOS — Sistema de Cola de Impresión con Prioridad");
        setSize(1300, 800);
        setMinimumSize(new Dimension(1100, 650));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Confirmar cierre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int r = JOptionPane.showConfirmDialog(MenuPrincipal.this,
                    "¿Desea salir de la simulación?", "Confirmar salida",
                    JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) System.exit(0);
            }
        });

        // Tema oscuro
        getContentPane().setBackground(new Color(15, 23, 42));
        setLayout(new BorderLayout(8, 8));

        // ── BARRA SUPERIOR ──────────────────────────────────────────────────
        JPanel barraTop = crearBarraSuperior();
        add(barraTop, BorderLayout.NORTH);

        // ── PANEL PRINCIPAL (3 columnas) ────────────────────────────────────
        JPanel panelMain = new JPanel(new GridLayout(1, 3, 8, 0));
        panelMain.setBackground(new Color(15, 23, 42));
        panelMain.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        panelMain.add(crearPanelUsuarios());
        panelMain.add(crearPanelCola());
        panelMain.add(crearPanelControles());

        add(panelMain, BorderLayout.CENTER);
    }

    // =========================================================================
    // CONSTRUCCIÓN DE PANELES
    // =========================================================================

    /**
     * Crea la barra superior con título, reloj y botones de carga/guardado.
     */
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(30, 41, 59));
        barra.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        // Título
        JLabel lblTitulo = new JLabel("PrintOS — Cola de Impresión con Prioridad");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        barra.add(lblTitulo, BorderLayout.WEST);

        // Reloj
        lblReloj = new JLabel("⏱ t = 0");
        lblReloj.setForeground(new Color(244, 162, 97));
        lblReloj.setFont(new Font("Monospaced", Font.BOLD, 16));
        barra.add(lblReloj, BorderLayout.CENTER);

        // Botón cargar CSV
        JButton btnCSV = makeBtn("📂 Cargar CSV usuarios", new Color(69, 123, 157));
        btnCSV.addActionListener(e -> accionCargarCSV());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnCSV);
        barra.add(btnPanel, BorderLayout.EAST);

        return barra;
    }

    /**
     * Crea el panel izquierdo de gestión de usuarios y documentos.
     */
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(15, 23, 42));

        // ── Sección usuarios ────────────────────────────────────────────────
        JPanel secUsuarios = new JPanel(new BorderLayout(0, 6));
        secUsuarios.setBackground(new Color(17, 24, 39));
        secUsuarios.setBorder(crearBorde("Usuarios registrados"));

        modeloUsuarios = new DefaultListModel<>();
        listaUsuarios  = new JList<>(modeloUsuarios);
        estilizarLista(listaUsuarios);
        listaUsuarios.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarDocumentosDelUsuario();
        });
        secUsuarios.add(new JScrollPane(listaUsuarios), BorderLayout.CENTER);

        // Botones usuarios
        JPanel botonesU = new JPanel(new GridLayout(1, 2, 4, 0));
        botonesU.setOpaque(false);
        JButton btnAgregar = makeBtn("+ Usuario", new Color(42, 157, 143));
        JButton btnEliminar = makeBtn("− Usuario", new Color(230, 57, 70));
        btnAgregar.addActionListener(e -> accionAgregarUsuario());
        btnEliminar.addActionListener(e -> accionEliminarUsuario());
        botonesU.add(btnAgregar);
        botonesU.add(btnEliminar);
        secUsuarios.add(botonesU, BorderLayout.SOUTH);

        // ── Sección documentos ───────────────────────────────────────────────
        JPanel secDocs = new JPanel(new BorderLayout(0, 6));
        secDocs.setBackground(new Color(17, 24, 39));
        secDocs.setBorder(crearBorde("Documentos del usuario"));

        modeloDocumentos = new DefaultListModel<>();
        listaDocumentos  = new JList<>(modeloDocumentos);
        estilizarLista(listaDocumentos);
        secDocs.add(new JScrollPane(listaDocumentos), BorderLayout.CENTER);

        // Botones documentos
        JPanel botonesD = new JPanel(new GridLayout(2, 2, 4, 4));
        botonesD.setOpaque(false);
        JButton btnCrearDoc   = makeBtn("+ Documento",    new Color(82, 183, 136));
        JButton btnElimDoc    = makeBtn("− Documento",    new Color(230, 57, 70));
        JButton btnImprimir   = makeBtn("⚡ Imprimir",    new Color(244, 162, 97));
        JButton btnPrioritario= makeBtn("★ Prioritario",  new Color(233, 196, 106));
        btnCrearDoc.addActionListener(e -> accionCrearDocumento());
        btnElimDoc.addActionListener(e -> accionEliminarDocumento());
        btnImprimir.addActionListener(e -> accionMandarImprimir(false));
        btnPrioritario.addActionListener(e -> accionMandarImprimir(true));
        botonesD.add(btnCrearDoc);
        botonesD.add(btnElimDoc);
        botonesD.add(btnImprimir);
        botonesD.add(btnPrioritario);
        secDocs.add(botonesD, BorderLayout.SOUTH);

        // Dividir verticalmente
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, secUsuarios, secDocs);
        split.setDividerLocation(260);
        split.setBackground(new Color(15, 23, 42));
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel central con las dos vistas de la cola (lista y árbol).
     */
    private JPanel crearPanelCola() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 23, 42));

        tabsCola = new JTabbedPane();
        tabsCola.setBackground(new Color(17, 24, 39));
        tabsCola.setForeground(Color.WHITE);
        tabsCola.setFont(new Font("SansSerif", Font.BOLD, 12));

        // Tab 1: Vista como lista
        JPanel tabLista = new JPanel(new BorderLayout(0, 6));
        tabLista.setBackground(new Color(17, 24, 39));
        tabLista.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        modeloCola = new DefaultListModel<>();
        listaCola  = new JList<>(modeloCola);
        estilizarLista(listaCola);
        listaCola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tabLista.add(new JScrollPane(listaCola), BorderLayout.CENTER);

        // Botón eliminar de cola
        JButton btnElimCola = makeBtn("✕ Eliminar de cola", new Color(230, 57, 70));
        btnElimCola.addActionListener(e -> accionEliminarDeCola());
        tabLista.add(btnElimCola, BorderLayout.SOUTH);
        tabsCola.addTab("📋 Lista", tabLista);

        // Tab 2: Vista como árbol
        JPanel tabArbol = new JPanel(new BorderLayout());
        tabArbol.setBackground(new Color(17, 24, 39));
        panelArbol = new DibujoArbol();
        JScrollPane scrollArbol = new JScrollPane(panelArbol);
        scrollArbol.getViewport().setBackground(new Color(15, 23, 42));
        tabArbol.add(scrollArbol, BorderLayout.CENTER);
        tabsCola.addTab("🌳 Árbol", tabArbol);

        // Actualizar árbol al cambiar tab
        tabsCola.addChangeListener(e -> {
            if (tabsCola.getSelectedIndex() == 1) actualizarArbol();
        });

        panel.add(tabsCola, BorderLayout.CENTER);

        // Leyenda de colores
        JPanel leyenda = crearLeyendaColores();
        panel.add(leyenda, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea el panel derecho con controles del SO, log de eventos y reloj.
     */
    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(15, 23, 42));

        // ── Controles SO ────────────────────────────────────────────────────
        JPanel secSO = new JPanel(new GridLayout(3, 1, 0, 6));
        secSO.setBackground(new Color(17, 24, 39));
        secSO.setBorder(crearBorde("Control del Sistema Operativo"));
        secSO.setBorder(BorderFactory.createCompoundBorder(
            crearBorde("Control del Sistema Operativo"),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JButton btnLiberar  = makeBtn("🖨 Liberar impresora (eliminar_min)", new Color(42, 157, 143));
        JButton btnAvanzar  = makeBtn("⏩ Avanzar reloj",                    new Color(69, 123, 157));
        JButton btnLimpiar  = makeBtn("🗑 Limpiar log",                       new Color(100, 116, 139));

        btnLiberar.addActionListener(e -> accionLiberarImpresora());
        btnAvanzar.addActionListener(e -> { simulador.avanzarReloj(); actualizarReloj(); printLog("Reloj avanzado manualmente → t=" + simulador.getReloj()); });
        btnLimpiar.addActionListener(e -> txtLog.setText(""));

        secSO.add(btnLiberar);
        secSO.add(btnAvanzar);
        secSO.add(btnLimpiar);

        // ── Log de eventos ───────────────────────────────────────────────────
        JPanel secLog = new JPanel(new BorderLayout());
        secLog.setBackground(new Color(17, 24, 39));
        secLog.setBorder(crearBorde("Log de eventos"));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(new Color(10, 16, 30));
        txtLog.setForeground(new Color(226, 232, 240));
        txtLog.setMargin(new Insets(6, 8, 6, 8));
        txtLog.setText("Sistema iniciado. Bienvenido a PrintOS.\n");
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.getViewport().setBackground(new Color(10, 16, 30));
        secLog.add(scrollLog, BorderLayout.CENTER);

        // ── Información de prioridades ───────────────────────────────────────
        JPanel secInfo = new JPanel(new GridLayout(4, 1, 0, 2));
        secInfo.setBackground(new Color(17, 24, 39));
        secInfo.setBorder(crearBorde("Tabla de prioridades"));
        secInfo.setBorder(BorderFactory.createCompoundBorder(
            crearBorde("Tabla de prioridades"),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        addInfoLabel(secInfo, "prioridad_alta   → descuento de 20 en t", new Color(230, 57, 70));
        addInfoLabel(secInfo, "prioridad_media  → descuento de 10 en t", new Color(244, 162, 97));
        addInfoLabel(secInfo, "prioridad_baja   → descuento de  5 en t", new Color(82, 183, 136));
        addInfoLabel(secInfo, "sin prioridad    → t = reloj actual",     new Color(148, 163, 184));

        // Organizar verticalmente
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(new Color(15, 23, 42));
        contenedor.add(secSO);
        contenedor.add(Box.createVerticalStrut(8));
        contenedor.add(secInfo);

        panel.add(contenedor, BorderLayout.NORTH);
        panel.add(secLog, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea la leyenda de colores de los niveles del árbol.
     */
    private JPanel crearLeyendaColores() {
        JPanel ley = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        ley.setBackground(new Color(17, 24, 39));
        ley.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JLabel titulo = new JLabel("Niveles: ");
        titulo.setForeground(new Color(100, 116, 139));
        titulo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ley.add(titulo);
        String[] niveles = {"Raíz", "Nivel 1", "Nivel 2", "Nivel 3"};
        Color[] colores  = {new Color(230, 57, 70), new Color(69, 123, 157),
                             new Color(42, 157, 143), new Color(244, 162, 97)};
        for (int i = 0; i < niveles.length; i++) {
            JLabel dot = new JLabel("●");
            dot.setForeground(colores[i]);
            JLabel txt = new JLabel(niveles[i]);
            txt.setForeground(new Color(148, 163, 184));
            txt.setFont(new Font("SansSerif", Font.PLAIN, 11));
            ley.add(dot);
            ley.add(txt);
        }
        return ley;
    }

    // =========================================================================
    // ACCIONES DE BOTONES
    // =========================================================================

    /** Carga usuarios desde un CSV seleccionado con JFileChooser. */
    private void accionCargarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo CSV de usuarios");
        fc.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv", "txt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String resultado = simulador.cargarUsuariosDesdeCSV(fc.getSelectedFile().getPath());
            printLog(resultado);
            actualizarListaUsuarios();
        }
    }

    /** Abre diálogo para agregar un nuevo usuario manualmente. */
    private void accionAgregarUsuario() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del usuario:", "Agregar usuario", JOptionPane.PLAIN_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) return;
        nombre = nombre.trim();

        String[] tipos = {"prioridad_alta", "prioridad_media", "prioridad_baja"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Tipo de prioridad:",
            "Agregar usuario", JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[1]);
        if (tipo == null) return;

        if (simulador.registrarUsuario(nombre, tipo)) {
            printLog("Usuario registrado: " + nombre + " (" + tipo + ")");
            actualizarListaUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "El usuario '" + nombre + "' ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Elimina el usuario seleccionado en la lista. */
    private void accionEliminarUsuario() {
        String sel = listaUsuarios.getSelectedValue();
        if (sel == null) { showError("Seleccione un usuario de la lista."); return; }
        String nombre = sel.split(" \\(")[0];
        int r = JOptionPane.showConfirmDialog(this,
            "¿Eliminar usuario '" + nombre + "' y sus documentos pendientes?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        if (simulador.eliminarUsuario(nombre)) {
            printLog("Usuario eliminado: " + nombre);
            actualizarListaUsuarios();
            modeloDocumentos.clear();
        } else {
            showError("No se encontró el usuario.");
        }
    }

    /** Abre diálogo para crear un documento para el usuario seleccionado. */
    private void accionCrearDocumento() {
        String sel = listaUsuarios.getSelectedValue();
        if (sel == null) { showError("Seleccione un usuario primero."); return; }
        String nombreUsuario = sel.split(" \\(")[0];

        String nombre = JOptionPane.showInputDialog(this, "Nombre del documento:", "Crear documento", JOptionPane.PLAIN_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) return;

        String tamStr = JOptionPane.showInputDialog(this, "Número de páginas:", "Crear documento", JOptionPane.PLAIN_MESSAGE);
        if (tamStr == null) return;
        int tamaño;
        try {
            tamaño = Integer.parseInt(tamStr.trim());
            if (tamaño <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("El número de páginas debe ser un entero positivo.");
            return;
        }

        String[] tipos = {"PDF", "DOCX", "TXT", "JPG", "XLSX", "PPT"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Tipo de archivo:",
            "Crear documento", JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == null) return;

        if (simulador.crearDocumento(nombreUsuario, nombre.trim(), tamaño, tipo)) {
            printLog("Documento creado: " + nombre + " para " + nombreUsuario);
            actualizarDocumentosDelUsuario();
        } else {
            showError("No se pudo crear el documento.");
        }
    }

    /** Elimina el documento seleccionado (solo si no está en cola). */
    private void accionEliminarDocumento() {
        String selU = listaUsuarios.getSelectedValue();
        String selD = listaDocumentos.getSelectedValue();
        if (selU == null || selD == null) { showError("Seleccione usuario y documento."); return; }
        String nombreUsuario = selU.split(" \\(")[0];
        String nombreDoc     = selD.split(" \\[")[0];

        if (simulador.eliminarDocumentoUsuario(nombreUsuario, nombreDoc)) {
            printLog("Documento eliminado: " + nombreDoc);
            actualizarDocumentosDelUsuario();
        } else {
            showError("No se pudo eliminar. El documento puede estar en la cola.");
        }
    }

    /** Envía el documento seleccionado a la cola de impresión. */
    private void accionMandarImprimir(boolean prioritario) {
        String selU = listaUsuarios.getSelectedValue();
        String selD = listaDocumentos.getSelectedValue();
        if (selU == null || selD == null) { showError("Seleccione usuario y documento."); return; }
        String nombreUsuario = selU.split(" \\(")[0];
        String nombreDoc     = selD.split(" \\[")[0];

        String resultado = simulador.mandarAImprimir(nombreUsuario, nombreDoc, prioritario);
        printLog(resultado);
        actualizarDocumentosDelUsuario();
        actualizarCola();
    }

    /** Libera la impresora: extrae e imprime el documento con mayor prioridad. */
    private void accionLiberarImpresora() {
        NodoPrioridad impreso = simulador.liberarImpresora();
        if (impreso == null) {
            printLog("⚠ La cola de impresión está vacía.");
        } else {
            printLog("🖨 IMPRESO: " + impreso.getDocumento().getNombre()
                     + " [t=" + impreso.getEtiquetaTiempo() + "] — "
                     + impreso.getDocumento().getTamaño() + " pág.");
        }
        actualizarReloj();
        actualizarCola();
        actualizarDocumentosDelUsuario();
    }

    /** Elimina de la cola el documento seleccionado en la lista de cola. */
    private void accionEliminarDeCola() {
        String sel = listaCola.getSelectedValue();
        if (sel == null) { showError("Seleccione un documento de la cola."); return; }
        // Extraer nombre del documento del string "[t=X] nombre (tipo, Y pág.)"
        String nombreDoc = sel.replaceAll("^\\[t=-?\\d+\\] ", "").split(" \\(")[0];
        String resultado = simulador.eliminarDeCola(nombreDoc);
        printLog(resultado);
        actualizarCola();
    }

    // =========================================================================
    // ACTUALIZACIÓN DE VISTAS
    // =========================================================================

    /** Recarga la lista de usuarios en el JList correspondiente. */
    private void actualizarListaUsuarios() {
        modeloUsuarios.clear();
        for (Usuario u : simulador.getTablaUsuarios().getTodosLosUsuarios()) {
            modeloUsuarios.addElement(u.toString());
        }
    }

    /** Actualiza la lista de documentos del usuario actualmente seleccionado. */
    private void actualizarDocumentosDelUsuario() {
        modeloDocumentos.clear();
        String sel = listaUsuarios.getSelectedValue();
        if (sel == null) return;
        String nombre = sel.split(" \\(")[0];
        Usuario u = simulador.getTablaUsuarios().buscar(nombre);
        if (u == null) return;
        for (Documento d : u.getDocumentos()) {
            modeloDocumentos.addElement(d.toString());
        }
    }

    /** Actualiza la vista de la cola (lista y árbol si está visible). */
    private void actualizarCola() {
        modeloCola.clear();
        MonticuloBinario cola = simulador.getColaImpresion();
        NodoPrioridad[] heap  = cola.getArreglo();
        for (int i = 0; i < cola.getTamano(); i++) {
            modeloCola.addElement(heap[i].toString());
        }
        if (tabsCola.getSelectedIndex() == 1) actualizarArbol();
    }

    /** Actualiza el panel de dibujo del árbol. */
    private void actualizarArbol() {
        panelArbol.setMonticulo(simulador.getColaImpresion());
    }

    /** Actualiza la etiqueta del reloj en la barra superior. */
    private void actualizarReloj() {
        lblReloj.setText("⏱ t = " + simulador.getReloj());
    }

    /** Inicia el Timer que actualiza el reloj visualmente cada 500ms. */
    private void iniciarReloj() {
        timerReloj = new Timer(500, (ActionEvent e) -> actualizarReloj());
        timerReloj.start();
    }

    // =========================================================================
    // UTILIDADES DE UI
    // =========================================================================

    /** Agrega un mensaje al área de log con scroll automático. */
    private void printLog(String msg) {
        txtLog.append("▶ " + msg + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    /** Muestra un diálogo de error. */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error — PrintOS", JOptionPane.ERROR_MESSAGE);
    }

    /** Crea un botón con estilo consistente. */
    private JButton makeBtn(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Crea un borde titulado con estilo oscuro. */
    private TitledBorder crearBorde(String titulo) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(30, 41, 59)),
            "  " + titulo + "  ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(148, 163, 184)
        );
    }

    /** Aplica estilo oscuro a un JList. */
    private void estilizarLista(JList<String> lista) {
        lista.setBackground(new Color(10, 16, 30));
        lista.setForeground(new Color(226, 232, 240));
        lista.setSelectionBackground(new Color(30, 58, 95));
        lista.setSelectionForeground(Color.WHITE);
        lista.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lista.setFixedCellHeight(28);
    }

    /** Agrega una etiqueta informativa con color al panel dado. */
    private void addInfoLabel(JPanel panel, String texto, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(color);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(lbl);
    }

    /**
     * Método main — punto de entrada de la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
