
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fazecast.jSerialComm.*;

public class TermocicladorUI extends JFrame {
    private JPanel contentPane;
    private JTextArea datosArea;
    private JTextArea cicloTextArea;
    private Map<String, JTextField> entradas;
    private String archivoActual;
    private SerialPort puertoSerie;
    private String currentLang = "es";
    private boolean serialRunning = false;
    private SwingWorker<Void, String> serialWorker;
    private String puertoSeleccionado;

    // Variables para la gráfica
    private List<Double> temperaturas;
    private List<Long> tiempos;
    private JPanel panelGrafica;
    private final int MAX_PUNTOS = 50;
    private long tiempoInicio;

    // Configuración del puerto serial
    private static final int BAUD_RATE = 9600;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = 1;
    private static final int PARITY = SerialPort.NO_PARITY;

    private static final Map<String, Map<String, String>> TRADUCCIONES = new HashMap<>();
    
    private static final String[] ETIQUETAS_KEYS = {
        "tempInicial", "tempMax", "tempMed", "tempMin",
        "time1", "time2", "time3", "time4", "numCiclos"
    };
    
    static {
        Map<String, String> es = new HashMap<>();
        es.put("title", "Interfaz Termociclador");
        es.put("menuArchivo", "Archivo");
        es.put("menuNuevo", "Nuevo");
        es.put("menuAbrir", "Abrir");
        es.put("menuGuardar", "Guardar");
        es.put("menuAbrirPuerto", "Abrir Puerto");
        es.put("menuEjecutar", "Ejecutar");
        es.put("menuIdioma", "Idioma");
        es.put("idioma_es", "Espanol");
        es.put("idioma_en", "English");
        es.put("btnNuevo", "Nuevo");
        es.put("btnAbrirArchivo", "Abrir Archivo");
        es.put("btnGuardarArchivo", "Guardar Archivo");
        es.put("btnAbrirPuerto", "Abrir Puerto");
        es.put("btnEjecutar", "Ejecutar");
        es.put("btnVerificarConexion", "Verificar Conexion");
        es.put("datosAEnviar", "Datos a Enviar:");
        es.put("cicloLabel", "Ciclo:");
        es.put("graficaLabel", "Grafica en Tiempo Real");
        es.put("ejeX", "Tiempo");
        es.put("ejeY", "Temperatura");
        es.put("msgCamposLimpiados", "Campos limpiados. Ahora puede ingresar nuevos datos.");
        es.put("msgDatosGuardados", "Datos guardados exitosamente.");
        es.put("msgErrorGuardar", "Error al guardar el archivo: ");
        es.put("msgDatosCargados", "Datos cargados exitosamente.");
        es.put("msgErrorCargar", "Error al cargar el archivo: ");
        es.put("msgPuertoConectado", "Conectado a ");
        es.put("msgErrorPuerto", "No se pudo abrir el puerto seleccionado.");
        es.put("msgNumeroCiclosMayor", "El numero de ciclos no puede ser mayor a 100.");
        es.put("msgValorNumerico", "Ingrese un valor numerico valido para el ciclo.");
        es.put("msgArchivoAjustado", "El archivo contenia mas de 100 ciclos. Se ha ajustado a 100.");
        es.put("titleError", "Error");
        es.put("titleAviso", "Aviso");
        es.put("archivoPrefix", "Archivo: ");
        es.put("puertoPrefix", "Puerto: ");
        es.put("msgSeleccionePuerto", "Seleccione el puerto:");
        es.put("tempInicial", "Temperatura Inicial");
        es.put("tempMax", "Temperatura Maxima");
        es.put("tempMed", "Temperatura Media");
        es.put("tempMin", "Temperatura Minima");
        es.put("time1", "Tiempo 1");
        es.put("time2", "Tiempo 2");
        es.put("time3", "Tiempo 3");
        es.put("time4", "Tiempo 4");
        es.put("numCiclos", "Numero de Ciclos");

        Map<String, String> en = new HashMap<>();
        en.put("title", "Thermocycler Interface");
        en.put("menuArchivo", "File");
        en.put("menuNuevo", "New");
        en.put("menuAbrir", "Open");
        en.put("menuGuardar", "Save");
        en.put("menuAbrirPuerto", "Open Port");
        en.put("menuEjecutar", "Run");
        en.put("menuIdioma", "Language");
        en.put("idioma_es", "Spanish");
        en.put("idioma_en", "English");
        en.put("btnNuevo", "New");
        en.put("btnAbrirArchivo", "Open File");
        en.put("btnGuardarArchivo", "Save File");
        en.put("btnAbrirPuerto", "Open Port");
        en.put("btnEjecutar", "Run");
        en.put("btnVerificarConexion", "Verify Connection");
        en.put("datosAEnviar", "Data to Send:");
        en.put("cicloLabel", "Cycle:");
        en.put("graficaLabel", "Real Time Graph");
        en.put("ejeX", "Time");
        en.put("ejeY", "Temperature");
        en.put("msgCamposLimpiados", "Fields cleared. You can now enter new data.");
        en.put("msgDatosGuardados", "Data saved successfully.");
        en.put("msgErrorGuardar", "Error saving file: ");
        en.put("msgDatosCargados", "Data loaded successfully.");
        en.put("msgErrorCargar", "Error loading file: ");
        en.put("msgPuertoConectado", "Connected to ");
        en.put("msgErrorPuerto", "Could not open the selected port.");
        en.put("msgNumeroCiclosMayor", "The number of cycles cannot be greater than 100.");
        en.put("msgValorNumerico", "Please enter a valid numeric value for the cycle.");
        en.put("msgArchivoAjustado", "The file contained more than 100 cycles. It has been adjusted to 100.");
        en.put("titleError", "Error");
        en.put("titleAviso", "Warning");
        en.put("archivoPrefix", "File: ");
        en.put("puertoPrefix", "Port: ");
        en.put("msgSeleccionePuerto", "Select port:");
        en.put("tempInicial", "Initial Temperature");
        en.put("tempMax", "Maximum Temperature");
        en.put("tempMed", "Average Temperature");
        en.put("tempMin", "Minimum Temperature");
        en.put("time1", "Time 1");
        en.put("time2", "Time 2");
        en.put("time3", "Time 3");
        en.put("time4", "Time 4");
        en.put("numCiclos", "Number of Cycles");

        TRADUCCIONES.put("es", es);
        TRADUCCIONES.put("en", en);
    }

    private String traducir(String key) {
        Map<String, String> traduccion = TRADUCCIONES.get(currentLang);
        if (traduccion != null && traduccion.containsKey(key)) {
            return traduccion.get(key);
        }
        return key;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                TermocicladorUI frame = new TermocicladorUI();
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al iniciar la aplicacion: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public TermocicladorUI() {
        temperaturas = new ArrayList<>();
        tiempos = new ArrayList<>();
        tiempoInicio = System.currentTimeMillis();
        
        inicializarComponentes();
        crearMenu();
        crearBotones();
        actualizarEstado();
    }

    private void inicializarComponentes() {
        setTitle(traducir("title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 700);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cerrarPuerto();
            }
        });
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BorderLayout());
        splitPane.setLeftComponent(panelControles);
        
        JPanel panelEntradas = new JPanel();
        panelEntradas.setLayout(new GridBagLayout());
        JScrollPane scrollEntradas = new JScrollPane(panelEntradas);
        panelControles.add(scrollEntradas, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        entradas = new HashMap<>();

        for (String clave : ETIQUETAS_KEYS) {
            JLabel etiqueta = new JLabel(traducir(clave));
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            panelEntradas.add(etiqueta, gbc);

            JTextField campo = new JTextField();
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            panelEntradas.add(campo, gbc);

            if ("numCiclos".equals(clave)) {
                AbstractDocument doc = (AbstractDocument) campo.getDocument();
                doc.setDocumentFilter(new FiltroNumeroCiclos());
            }

            entradas.put(clave, campo);
            gbc.gridy++;
        }

        JLabel lblCiclo = new JLabel(traducir("cicloLabel"));
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panelEntradas.add(lblCiclo, gbc);
        
        cicloTextArea = new JTextArea(3, 20);
        JScrollPane scrollCiclo = new JScrollPane(cicloTextArea);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        panelEntradas.add(scrollCiclo, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());
        splitPane.setRightComponent(panelInferior);
        
        JLabel tituloGrafica = new JLabel(traducir("graficaLabel"), JLabel.CENTER);
        tituloGrafica.setFont(new Font("Arial", Font.BOLD, 14));
        panelInferior.add(tituloGrafica, BorderLayout.NORTH);
        
        panelGrafica = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafica(g);
            }
        };
        panelGrafica.setBackground(Color.WHITE);
        panelGrafica.setPreferredSize(new Dimension(600, 300));
        panelInferior.add(panelGrafica, BorderLayout.CENTER);

        datosArea = new JTextArea(traducir("datosAEnviar"));
        datosArea.setEditable(false);
        datosArea.setLineWrap(true);
        datosArea.setWrapStyleWord(true);
        JScrollPane scrollDatos = new JScrollPane(datosArea);
        scrollDatos.setPreferredSize(new Dimension(100, 80));
        contentPane.add(scrollDatos, BorderLayout.SOUTH);

        archivoActual = null;
        puertoSeleccionado = null;
    }

    private void dibujarGrafica(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = panelGrafica.getWidth();
        int height = panelGrafica.getHeight();
        
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        
        if (temperaturas.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("Esperando datos del dispositivo...", width/2 - 100, height/2);
            return;
        }
        
        int margin = 60;
        int graphWidth = width - 2 * margin;
        int graphHeight = height - 2 * margin;
        
        double minTemp = Double.MAX_VALUE;
        double maxTemp = -Double.MAX_VALUE;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        
        for (int i = 0; i < temperaturas.size(); i++) {
            double temp = temperaturas.get(i);
            long time = tiempos.get(i);
            
            if (temp < minTemp) minTemp = temp;
            if (temp > maxTemp) maxTemp = temp;
            if (time < minTime) minTime = time;
            if (time > maxTime) maxTime = time;
        }
        
        if (minTemp == maxTemp) {
            minTemp -= 1;
            maxTemp += 1;
        }
        if (minTime == maxTime) {
            minTime -= 1;
            maxTime += 1;
        }
        
        g2.setColor(Color.BLACK);
        g2.drawLine(margin, margin, margin, margin + graphHeight);
        g2.drawLine(margin, margin + graphHeight, margin + graphWidth, margin + graphHeight);
        
        g2.drawString(traducir("ejeY"), margin - 40, margin - 10);
        g2.drawString(traducir("ejeX"), margin + graphWidth - 20, margin + graphHeight + 15);
        
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 5; i++) {
            int y = margin + (graphHeight * i / 5);
            g2.drawLine(margin, y, margin + graphWidth, y);
        }
        
        g2.setColor(Color.BLUE);
        for (int i = 1; i < temperaturas.size(); i++) {
            int x1 = margin + (int)((tiempos.get(i-1) - minTime) * graphWidth / (maxTime - minTime));
            int y1 = margin + graphHeight - (int)((temperaturas.get(i-1) - minTemp) * graphHeight / (maxTemp - minTemp));
            int x2 = margin + (int)((tiempos.get(i) - minTime) * graphWidth / (maxTime - minTime));
            int y2 = margin + graphHeight - (int)((temperaturas.get(i) - minTemp) * graphHeight / (maxTemp - minTemp));
            
            g2.drawLine(x1, y1, x2, y2);
        }
        
        g2.setColor(Color.RED);
        for (int i = 0; i < temperaturas.size(); i++) {
            int x = margin + (int)((tiempos.get(i) - minTime) * graphWidth / (maxTime - minTime));
            int y = margin + graphHeight - (int)((temperaturas.get(i) - minTemp) * graphHeight / (maxTemp - minTemp));
            
            g2.fillOval(x - 2, y - 2, 4, 4);
        }
        
        g2.setColor(Color.BLACK);
        g2.drawString(String.format("%.1f", maxTemp), margin - 40, margin - 5);
        g2.drawString(String.format("%.1f", minTemp), margin - 40, margin + graphHeight + 5);
        
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Baud Rate: " + BAUD_RATE, width - 150, 20);
    }

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu menuArchivo = new JMenu(traducir("menuArchivo"));
        menuBar.add(menuArchivo);
        
        JMenuItem menuNuevo = new JMenuItem(traducir("menuNuevo"));
        menuNuevo.addActionListener(e -> archivoNuevo());
        menuArchivo.add(menuNuevo);
        
        JMenuItem menuAbrir = new JMenuItem(traducir("menuAbrir"));
        menuAbrir.addActionListener(e -> abrirArchivo());
        menuArchivo.add(menuAbrir);
        
        JMenuItem menuGuardar = new JMenuItem(traducir("menuGuardar"));
        menuGuardar.addActionListener(e -> guardarDatos());
        menuArchivo.add(menuGuardar);
        
        menuArchivo.addSeparator();
        
        JMenuItem menuAbrirPuerto = new JMenuItem(traducir("menuAbrirPuerto"));
        menuAbrirPuerto.addActionListener(e -> abrirPuerto());
        menuBar.add(menuAbrirPuerto);
        
        JMenuItem menuEjecutar = new JMenuItem(traducir("menuEjecutar"));
        menuEjecutar.addActionListener(e -> play());
        menuBar.add(menuEjecutar);

        JMenu menuIdioma = new JMenu(traducir("menuIdioma"));
        JMenuItem miEs = new JMenuItem(traducir("idioma_es"));
        miEs.addActionListener(e -> setLanguage("es"));
        JMenuItem miEn = new JMenuItem(traducir("idioma_en"));
        miEn.addActionListener(e -> setLanguage("en"));
        menuIdioma.add(miEs);
        menuIdioma.add(miEn);
        menuBar.add(menuIdioma);
    }

    private void crearBotones() {
        JPanel panelBotones = new JPanel();
        contentPane.add(panelBotones, BorderLayout.NORTH);
        
        JButton btnNuevo = new JButton(traducir("btnNuevo"));
        btnNuevo.addActionListener(e -> archivoNuevo());
        panelBotones.add(btnNuevo);
        
        JButton btnAbrir = new JButton(traducir("btnAbrirArchivo"));
        btnAbrir.addActionListener(e -> abrirArchivo());
        panelBotones.add(btnAbrir);
        
        JButton btnGuardar = new JButton(traducir("btnGuardarArchivo"));
        btnGuardar.addActionListener(e -> guardarDatos());
        panelBotones.add(btnGuardar);
        
        JButton btnAbrirPuerto = new JButton(traducir("btnAbrirPuerto"));
        btnAbrirPuerto.addActionListener(e -> abrirPuerto());
        panelBotones.add(btnAbrirPuerto);
        
        JButton btnEjecutar = new JButton(traducir("btnEjecutar"));
        btnEjecutar.addActionListener(e -> play());
        panelBotones.add(btnEjecutar);

        JButton btnVerificar = new JButton(traducir("btnVerificarConexion"));
        btnVerificar.addActionListener(e -> verificarConexion());
        panelBotones.add(btnVerificar);
        
        JButton btnLimpiarGrafica = new JButton("Limpiar Grafica");
        btnLimpiarGrafica.addActionListener(e -> limpiarGrafica());
        panelBotones.add(btnLimpiarGrafica);
        
        JButton btnCerrarPuerto = new JButton("Cerrar Puerto");
        btnCerrarPuerto.addActionListener(e -> cerrarPuerto());
        panelBotones.add(btnCerrarPuerto);
    }

    private void limpiarGrafica() {
        temperaturas.clear();
        tiempos.clear();
        tiempoInicio = System.currentTimeMillis();
        panelGrafica.repaint();
        datosArea.append("\nGrafica limpiada");
    }

    private void cerrarPuerto() {
        serialRunning = false;
        if (serialWorker != null) {
            serialWorker.cancel(true);
        }
        if (puertoSerie != null && puertoSerie.isOpen()) {
            puertoSerie.closePort();
            datosArea.append("\nPuerto cerrado: " + puertoSeleccionado);
            puertoSeleccionado = null;
            actualizarEstado();
        }
    }

    private void setLanguage(String lang) {
        Map<String, String> valores = new HashMap<>();
        if (entradas != null) {
            for (String clave : ETIQUETAS_KEYS) {
                JTextField campo = entradas.get(clave);
                if (campo != null) {
                    valores.put(clave, campo.getText());
                }
            }
        }
        String ciclo = (cicloTextArea != null) ? cicloTextArea.getText() : "";

        currentLang = lang;

        getContentPane().removeAll();
        setJMenuBar(null);
        inicializarComponentes();
        crearMenu();
        crearBotones();

        if (entradas != null) {
            for (String clave : ETIQUETAS_KEYS) {
                JTextField campo = entradas.get(clave);
                if (campo != null && valores.containsKey(clave)) {
                    campo.setText(valores.get(clave));
                }
            }
        }
        if (cicloTextArea != null) {
            cicloTextArea.setText(ciclo);
        }

        actualizarEstado();
        revalidate();
        repaint();
    }

    private void archivoNuevo() {
        for (String clave : ETIQUETAS_KEYS) {
            JTextField campo = entradas.get(clave);
            if (campo != null) {
                campo.setText("");
            }
        }
        if (cicloTextArea != null) {
            cicloTextArea.setText("");
        }
        archivoActual = null;
        actualizarEstado();
        JOptionPane.showMessageDialog(this, traducir("msgCamposLimpiados"));
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = fileChooser.getSelectedFile().getAbsolutePath();
            cargarDatos();
            actualizarEstado();
        }
    }

    private void guardarDatos() {
        if (archivoActual == null) {
            JFileChooser fileChooser = new JFileChooser();
            int resultado = fileChooser.showSaveDialog(this);
            
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivoActual = fileChooser.getSelectedFile().getAbsolutePath();
                if (!archivoActual.endsWith(".txt")) {
                    archivoActual += ".txt";
                }
            } else {
                return;
            }
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoActual))) {
            for (String clave : ETIQUETAS_KEYS) {
                JTextField campo = entradas.get(clave);
                if (campo != null) {
                    writer.println(clave + ": " + campo.getText());
                }
            }
            writer.println("ciclo: " + cicloTextArea.getText());
            
            JOptionPane.showMessageDialog(this, traducir("msgDatosGuardados"));
            actualizarEstado();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, traducir("msgErrorGuardar") + e.getMessage(),
                    traducir("titleError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatos() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoActual))) {
            String linea;
            Map<String, String> datos = new HashMap<>();
            
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(": ", 2);
                if (partes.length == 2) {
                    datos.put(partes[0], partes[1]);
                }
            }
            
            for (String clave : ETIQUETAS_KEYS) {
                if (datos.containsKey(clave)) {
                    JTextField campo = entradas.get(clave);
                    if (campo != null) {
                        campo.setText(datos.get(clave));
                    }
                }
            }
            
            if (datos.containsKey("ciclo")) {
                cicloTextArea.setText(datos.get("ciclo"));
            }

            if (datos.containsKey("numCiclos")) {
                String valorCiclos = datos.get("numCiclos");
                if (valorCiclos != null && valorCiclos.matches("\\d+")) {
                    try {
                        int ciclos = Integer.parseInt(valorCiclos);
                        if (ciclos > 100) {
                            JTextField campo = entradas.get("numCiclos");
                            if (campo != null) campo.setText("100");
                            JOptionPane.showMessageDialog(this, 
                                traducir("msgArchivoAjustado"), 
                                traducir("titleAviso"), 
                                JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        // Ignorar error de formato
                    }
                }
            }
            
            JOptionPane.showMessageDialog(this, traducir("msgDatosCargados"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, traducir("msgErrorCargar") + e.getMessage(),
                    traducir("titleError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirPuerto() {
        SerialPort[] puertosDisponibles = SerialPort.getCommPorts();
        String[] opciones = new String[puertosDisponibles.length];
        
        for (int i = 0; i < puertosDisponibles.length; i++) {
            opciones[i] = puertosDisponibles[i].getSystemPortName();
        }
        
        if (opciones.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "No se encontraron puertos seriales disponibles",
                traducir("titleError"), 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String seleccionActual = puertoSeleccionado != null ? puertoSeleccionado : opciones[0];
        
        String nuevoPuerto = (String) JOptionPane.showInputDialog(
            this, traducir("msgSeleccionePuerto"), traducir("menuAbrirPuerto"),
            JOptionPane.QUESTION_MESSAGE, null, opciones, seleccionActual);
        
        if (nuevoPuerto != null) {
            this.puertoSeleccionado = nuevoPuerto;
            conectarPuerto(nuevoPuerto);
            actualizarEstado();
        }
    }

    private void conectarPuerto(String nombrePuerto) {
        new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    publish("Conectando a " + nombrePuerto + " a " + BAUD_RATE + " baudios...");
                    
                    puertoSerie = SerialPort.getCommPort(nombrePuerto);
                    puertoSerie.setBaudRate(BAUD_RATE);
                    puertoSerie.setNumDataBits(DATA_BITS);
                    puertoSerie.setNumStopBits(STOP_BITS);
                    puertoSerie.setParity(PARITY);
                    puertoSerie.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
                    
                    boolean abierto = puertoSerie.openPort();
                    if (!abierto) {
                        publish("Error: No se pudo abrir el puerto " + nombrePuerto);
                        return false;
                    }
                    
                    tiempoInicio = System.currentTimeMillis();
                    publish("Conexion establecida con " + nombrePuerto + " a " + BAUD_RATE + " baudios");
                    publish("Configuracion: " + DATA_BITS + " bits de datos, " + STOP_BITS + " bit de parada, Sin paridad");
                    publish("Listo para recibir datos del dispositivo...");
                    
                    serialRunning = true;
                    new Thread(() -> leerDatosSerial()).start();
                    
                    return true;
                } catch (Exception e) {
                    publish("Error al conectar: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void process(java.util.List<String> mensajes) {
                for (String mensaje : mensajes) {
                    datosArea.append("\n" + mensaje);
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(TermocicladorUI.this, 
                            traducir("msgPuertoConectado") + nombrePuerto + "\nBaud Rate: " + BAUD_RATE, 
                            "Conexion Exitosa", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(TermocicladorUI.this, 
                            traducir("msgErrorPuerto"),
                            traducir("titleError"), 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TermocicladorUI.this, 
                        "Error: " + e.getMessage(),
                        traducir("titleError"), 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void leerDatosSerial() {
        try {
            InputStream inputStream = puertoSerie.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            while (serialRunning && puertoSerie != null && puertoSerie.isOpen()) {
                if (puertoSerie.bytesAvailable() > 0) {
                    String linea = reader.readLine();
                    if (linea != null) {
                        String lineaFinal = linea.trim();
                        SwingUtilities.invokeLater(() -> recibirDatoSerial(lineaFinal));
                    }
                }
                Thread.sleep(10);
            }
        } catch (Exception e) {
            if (serialRunning) {
                SwingUtilities.invokeLater(() -> {
                    datosArea.append("\nError en lectura serial: " + e.getMessage());
                });
            }
        }
    }

    public void procesarDatoTemperatura(double temperatura) {
        long tiempoActual = System.currentTimeMillis() - tiempoInicio;
        
        temperaturas.add(temperatura);
        tiempos.add(tiempoActual);
        
        if (temperaturas.size() > MAX_PUNTOS) {
            temperaturas.remove(0);
            tiempos.remove(0);
        }
        
        SwingUtilities.invokeLater(() -> {
            panelGrafica.repaint();
            datosArea.append("\nTemperatura: " + String.format("%.2f", temperatura) + "°C");
        });
    }

    public void recibirDatoSerial(String dato) {
        try {
            double temperatura = Double.parseDouble(dato.trim());
            procesarDatoTemperatura(temperatura);
        } catch (NumberFormatException e) {
            datosArea.append("\n" + dato);
            
            if (dato.trim().equalsIgnoreCase("terminado")) {
                JOptionPane.showMessageDialog(this, 
                    "Proceso terminado en el dispositivo", 
                    "Proceso Completado", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void play() {
        try {
            if (puertoSeleccionado == null) {
                JOptionPane.showMessageDialog(this, 
                    "Primero debe seleccionar un puerto", 
                    traducir("titleAviso"), 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            StringBuilder csv = new StringBuilder();
            for (String clave : ETIQUETAS_KEYS) {
                if (csv.length() > 0) {
                    csv.append(",");
                }
                JTextField campo = entradas.get(clave);
                if (campo != null) {
                    csv.append(escapeForCsv(campo.getText()));
                }
            }
            
            String comando = csv.toString();
            enviarDatos(comando);
            datosArea.append("\nENVIADO: " + comando);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                traducir("msgValorNumerico"), 
                traducir("titleError"), 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapeForCsv(String texto) {
        if (texto == null) return "";
        String resultado = texto.replace("\"", "\"\"");
        if (resultado.contains(",") || resultado.contains("\n") || resultado.contains("\"")) {
            return "\"" + resultado + "\"";
        }
        return resultado;
    }

    private void enviarDatos(String comando) {
        if (puertoSeleccionado == null) {
            System.out.println("Simulacion - Enviado: " + comando);
        } else {
            try {
                if (puertoSerie != null && puertoSerie.isOpen()) {
                    OutputStream outputStream = puertoSerie.getOutputStream();
                    String comandoConSalto = comando + "\n";
                    outputStream.write(comandoConSalto.getBytes());
                    outputStream.flush();
                    System.out.println("Enviado por puerto " + puertoSeleccionado + " a " + BAUD_RATE + " baudios: " + comando);
                } else {
                    datosArea.append("\nError: Puerto no disponible para enviar datos");
                }
            } catch (Exception e) {
                datosArea.append("\nError al enviar datos: " + e.getMessage());
            }
        }
    }

    private void verificarConexion() {
        if (puertoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "No hay puerto seleccionado. Abra un puerto primero.", 
                traducir("titleAviso"), 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    enviarDatos("listo");
                    Thread.sleep(1000);
                    return true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(TermocicladorUI.this, 
                            "Comando de verificacion enviado a " + puertoSeleccionado + " a " + BAUD_RATE + " baudios", 
                            "Verificacion Enviada", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(TermocicladorUI.this, 
                            "Error al verificar la conexion con " + puertoSeleccionado, 
                            traducir("titleError"), 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TermocicladorUI.this, 
                        "Error inesperado: " + e.getMessage(),
                        traducir("titleError"), 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void actualizarEstado() {
        StringBuilder estado = new StringBuilder();
        estado.append(traducir("datosAEnviar"));
        
        if (archivoActual != null) {
            String nombreArchivo = new File(archivoActual).getName();
            estado.append("\n").append(traducir("archivoPrefix")).append(nombreArchivo);
        }
        
        if (puertoSeleccionado != null) {
            estado.append("\n").append(traducir("puertoPrefix")).append(puertoSeleccionado);
            estado.append(" (").append(BAUD_RATE).append(" baudios)");
            
            if (puertoSerie != null && puertoSerie.isOpen()) {
                estado.append(" - CONECTADO");
            } else {
                estado.append(" - DESCONECTADO");
            }
        }
        
        datosArea.setText(estado.toString());
    }

    private static class FiltroNumeroCiclos extends DocumentFilter {
        private boolean isValid(String texto) {
            if (texto == null || texto.isEmpty()) return true;
            if (!texto.matches("\\d*")) return false;
            try {
                if (texto.isEmpty()) return true;
                int valor = Integer.parseInt(texto);
                return valor <= 100;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            try {
                sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            } catch (BadLocationException e) {
                return;
            }
            sb.insert(offset, string);
            if (isValid(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            try {
                sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            } catch (BadLocationException e) {
                return;
            }
            sb.replace(offset, offset + length, text == null ? "" : text);
            if (isValid(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}