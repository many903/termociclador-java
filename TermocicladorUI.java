import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TermocicladorUI extends JFrame {
    private JPanel contentPane;
    private JTextArea datosArea;
    private JTextArea cicloTextArea;
    private Map<String, JTextField> entradas;
    private String archivoActual;
    private Object puertoSerie;
    private String currentLang = "es";
    private boolean serialRunning = false;
    private SwingWorker<Void, String> serialWorker;

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
        es.put("menuAbrirPuerto", "Abrir Puerto");
        es.put("menuEjecutar", "Ejecutar");
        es.put("menuIdioma", "Idioma");
        es.put("idioma_es", "Español");
        es.put("idioma_en", "English");
        es.put("idioma_zh", "中文");
        es.put("btnNuevo", "Nuevo");
        es.put("btnAbrirArchivo", "Abrir Archivo");
        es.put("btnAbrirPuerto", "Abrir Puerto");
        es.put("btnEjecutar", "Ejecutar");
        es.put("btnVerificarConexion", "Verificar Conexión");
        es.put("datosAEnviar", "Datos a Enviar:");
        es.put("cicloLabel", "Ciclo:");
        es.put("msgCamposLimpiados", "Campos limpiados. Ahora puede ingresar nuevos datos.");
        es.put("msgDatosGuardados", "Datos guardados exitosamente.");
        es.put("msgErrorGuardar", "Error al guardar el archivo: ");
        es.put("msgDatosCargados", "Datos cargados exitosamente.");
        es.put("msgErrorCargar", "Error al cargar el archivo: ");
        es.put("msgPuertoConectado", "Conectado a ");
        es.put("msgErrorPuerto", "No se pudo abrir el puerto seleccionado.");
        es.put("msgNumeroCiclosMayor", "El número de ciclos no puede ser mayor a 100.");
        es.put("msgValorNumerico", "Ingrese un valor numérico válido para la vuelta.");
        es.put("msgArchivoAjustado", "El archivo contenía más de 100 ciclos. Se ha ajustado a 100.");
        es.put("titleError", "Error");
        es.put("titleAviso", "Aviso");
        es.put("archivoPrefix", "Archivo: ");
        es.put("msgSeleccionePuerto", "Seleccione el puerto:");
        es.put("tempInicial", "Temperatura Inicial");
        es.put("tempMax", "Temperatura Máxima");
        es.put("tempMed", "Temperatura Media");
        es.put("tempMin", "Temperatura Mínima");
        es.put("time1", "Tiempo 1");
        es.put("time2", "Tiempo 2");
        es.put("time3", "Tiempo 3");
        es.put("time4", "Tiempo 4");
        es.put("numCiclos", "Número de Ciclos");

        Map<String, String> en = new HashMap<>();
        en.put("title", "Thermocycler Interface");
        en.put("menuArchivo", "File");
        en.put("menuNuevo", "New");
        en.put("menuAbrir", "Open");
        en.put("menuAbrirPuerto", "Open Port");
        en.put("menuEjecutar", "Run");
        en.put("menuIdioma", "Language");
        en.put("idioma_es", "Español");
        en.put("idioma_en", "English");
        en.put("idioma_zh", "中文");
        en.put("btnNuevo", "New");
        en.put("btnAbrirArchivo", "Open File");
        en.put("btnAbrirPuerto", "Open Port");
        en.put("btnEjecutar", "Run");
        en.put("btnVerificarConexion", "Verify Connection");
        en.put("datosAEnviar", "Data to Send:");
        en.put("cicloLabel", "Cycle:");
        en.put("msgCamposLimpiados", "Fields cleared. You can enter new data now.");
        en.put("msgDatosGuardados", "Data saved successfully.");
        en.put("msgErrorGuardar", "Error saving file: ");
        en.put("msgDatosCargados", "Data loaded successfully.");
        en.put("msgErrorCargar", "Error loading file: ");
        en.put("msgPuertoConectado", "Connected to ");
        en.put("msgErrorPuerto", "Could not open the selected port.");
        en.put("msgNumeroCiclosMayor", "The number of cycles cannot be greater than 100.");
        en.put("msgValorNumerico", "Enter a valid numeric value for the cycle.");
        en.put("msgArchivoAjustado", "The file contained more than 100 cycles. It has been adjusted to 100.");
        en.put("titleError", "Error");
        en.put("titleAviso", "Warning");
        en.put("archivoPrefix", "File: ");
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

        Map<String, String> zh = new HashMap<>();
        zh.put("title", "\u70ED\u5FAA\u73AF\u4EEA\u754C\u9762");
        zh.put("menuArchivo", "\u6587\u4EF6");
        zh.put("menuNuevo", "\u65B0\u5EFA");
        zh.put("menuAbrir", "\u6253\u5F00");
        zh.put("menuAbrirPuerto", "\u6253\u5F00\u7AEF\u53E3");
        zh.put("menuEjecutar", "\u8FD0\u884C");
        zh.put("menuIdioma", "\u8BED\u8A00");
        zh.put("idioma_es", "Espa\u00F1ol");
        zh.put("idioma_en", "English");
        zh.put("idioma_zh", "\u4E2D\u6587");
        zh.put("btnNuevo", "\u65B0\u5EFA");
        zh.put("btnAbrirArchivo", "\u6253\u5F00\u6587\u4EF6");
        zh.put("btnAbrirPuerto", "\u6253\u5F00\u7AEF\u53E3");
        zh.put("btnEjecutar", "\u8FD0\u884C");
        zh.put("btnVerificarConexion", "\u786E\u8BA4\u8FDE\u63A5");
        zh.put("datosAEnviar", "\u8981\u53D1\u9001\u7684\u6570\u636E:");
        zh.put("cicloLabel", "\u5FAA\u73AF:");
        zh.put("msgCamposLimpiados", "\u5B57\u6BB5\u5DF2\u6E05\u9664\u3002\u73B0\u5728\u53EF\u4EE5\u8F93\u5165\u65B0\u6570\u636E\u3002");
        zh.put("msgDatosGuardados", "\u6570\u636E\u5DF2\u6210\u529F\u4FDD\u5B58\u3002");
        zh.put("msgErrorGuardar", "\u4FDD\u5B58\u6587\u4EF6\u51FA\u9519: ");
        zh.put("msgDatosCargados", "\u6570\u636E\u5DF2\u6210\u529F\u52A0\u8F7D\u3002");
        zh.put("msgErrorCargar", "\u52A0\u8F7D\u6587\u4EF6\u51FA\u9519: ");
        zh.put("msgPuertoConectado", "\u5DF2\u8FDE\u63A5\u5230 ");
        zh.put("msgErrorPuerto", "\u65E0\u6CD5\u6253\u5F00\u6240\u9009\u7AEF\u53E3\u3002");
        zh.put("msgNumeroCiclosMayor", "\u5FAA\u73AF\u6B21\u6570\u4E0D\u80FD\u5927\u4E8E100\u3002");
        zh.put("msgValorNumerico", "\u8BF7\u8F93\u5165\u6709\u6548\u7684\u6570\u5B57\u503C\u4F5C\u4E3A\u5FAA\u73AF\u6B21\u6570\u3002");
        zh.put("msgArchivoAjustado", "\u6587\u4EF6\u5305\u542B\u8D85\u8FC7100\u4E2A\u5FAA\u73AF\u3002\u5DF2\u8C03\u6574\u4E3A100\u3002");
        zh.put("titleError", "\u9519\u8BEF");
        zh.put("titleAviso", "\u8B66\u544A");
        zh.put("archivoPrefix", "\u6587\u4EF6: ");
        zh.put("msgSeleccionePuerto", "\u8BF7\u9009\u62E9\u7AEF\u53E3:");
        zh.put("tempInicial", "\u521D\u59CB\u6E29\u5EA6");
        zh.put("tempMax", "\u6700\u9AD8\u6E29\u5EA6");
        zh.put("tempMed", "\u5E73\u5747\u6E29\u5EA6");
        zh.put("tempMin", "\u6700\u4F4E\u6E29\u5EA6");
        zh.put("time1", "\u65F6\u95F41");
        zh.put("time2", "\u65F6\u95F42");
        zh.put("time3", "\u65F6\u95F43");
        zh.put("time4", "\u65F6\u95F44");
        zh.put("numCiclos", "\u5FAA\u73AF\u6B21\u6570");

        TRADUCCIONES.put("es", es);
        TRADUCCIONES.put("en", en);
        TRADUCCIONES.put("zh", zh);
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
                JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public TermocicladorUI() {
        inicializarComponentes();
        crearMenu();
        crearBotones();
    }

    private void inicializarComponentes() {
        setTitle(traducir("title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 400);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        entradas = new HashMap<>();

        for (String clave : ETIQUETAS_KEYS) {
            JLabel etiqueta = new JLabel(traducir(clave));
            gbc.gridx = 0;
            panelCentral.add(etiqueta, gbc);

            JTextField campo = new JTextField();
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            panelCentral.add(campo, gbc);
            gbc.weightx = 0;

            if ("numCiclos".equals(clave)) {
                AbstractDocument doc = (AbstractDocument) campo.getDocument();
                doc.setDocumentFilter(new FiltroNumeroCiclos());
            }

            entradas.put(clave, campo);
            gbc.gridy++;
        }

        JLabel lblCiclo = new JLabel(traducir("cicloLabel"));
        gbc.gridx = 0;
        panelCentral.add(lblCiclo, gbc);
        cicloTextArea = new JTextArea(5, 30);
        JScrollPane scrollCiclo = new JScrollPane(cicloTextArea);
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelCentral.add(scrollCiclo, gbc);
        gbc.weighty = 0;

        contentPane.add(panelCentral, BorderLayout.CENTER);

        datosArea = new JTextArea(traducir("datosAEnviar"));
        datosArea.setEditable(false);
        datosArea.setLineWrap(true);
        datosArea.setWrapStyleWord(true);
        JScrollPane scrollDatos = new JScrollPane(datosArea);
        scrollDatos.setPreferredSize(new Dimension(100, 80));
        contentPane.add(scrollDatos, BorderLayout.SOUTH);

        archivoActual = null;
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
        JMenuItem miZh = new JMenuItem(traducir("idioma_zh"));
        miZh.addActionListener(e -> setLanguage("zh"));
        menuIdioma.add(miEs);
        menuIdioma.add(miEn);
        menuIdioma.add(miZh);
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
        
        JButton btnAbrirPuerto = new JButton(traducir("btnAbrirPuerto"));
        btnAbrirPuerto.addActionListener(e -> abrirPuerto());
        panelBotones.add(btnAbrirPuerto);
        
        JButton btnEjecutar = new JButton(traducir("btnEjecutar"));
        btnEjecutar.addActionListener(e -> play());
        panelBotones.add(btnEjecutar);

        JButton btnVerificar = new JButton(traducir("btnVerificarConexion"));
        btnVerificar.addActionListener(e -> verificarConexion());
        panelBotones.add(btnVerificar);
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

        actualizarNombreArchivo();
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
        actualizarNombreArchivo();
        JOptionPane.showMessageDialog(this, traducir("msgCamposLimpiados"));
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = fileChooser.getSelectedFile().getAbsolutePath();
            cargarDatos();
            actualizarNombreArchivo();
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
                writer.println(clave + ": " + entradas.get(clave).getText());
            }
            writer.println("ciclo: " + cicloTextArea.getText());
            
            JOptionPane.showMessageDialog(this, traducir("msgDatosGuardados"));
            actualizarNombreArchivo();
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
        String[] opciones = {"COM1", "COM2", "COM3", "COM4"};
        String puertoSeleccionado = (String) JOptionPane.showInputDialog(
            this, traducir("msgSeleccionePuerto"), traducir("menuAbrirPuerto"),
            JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        
        if (puertoSeleccionado != null) {
            tryOpenPortWithRetries(puertoSeleccionado, 3);
        }
    }

    private void tryOpenPortWithRetries(String portName, int maxAttempts) {
        new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() {
                for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                    publish("Intento " + attempt + " de " + maxAttempts + " en " + portName);
                    try {
                        Thread.sleep(200);
                        boolean success = attempt == 2; // Simulación de éxito en segundo intento
                        if (success) {
                            publish("Conectado exitosamente a " + portName);
                            return true;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
                return false;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String mensaje : chunks) {
                    datosArea.append("\n" + mensaje);
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(TermocicladorUI.this, 
                            "Conectado a " + portName, 
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(TermocicladorUI.this, 
                            "No se pudo conectar al puerto después de " + maxAttempts + " intentos",
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

    private void play() {
        try {
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
        if (puertoSerie == null) {
            System.out.println("Simulación - Enviado: " + comando);
        } else {
            System.out.println("Enviado por serie: " + comando);
        }
    }

    private void verificarConexion() {
        if (puertoSerie == null) {
            JOptionPane.showMessageDialog(this, 
                "No hay puerto abierto. Abra un puerto primero.", 
                traducir("titleAviso"), 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
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
                            "Conexión verificada correctamente", 
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(TermocicladorUI.this, 
                            "Error al verificar la conexión", 
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

    private void actualizarNombreArchivo() {
        if (archivoActual != null) {
            String nombreArchivo = new File(archivoActual).getName();
            datosArea.setText(traducir("archivoPrefix") + nombreArchivo);
        } else {
            datosArea.setText(traducir("datosAEnviar"));
        }
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