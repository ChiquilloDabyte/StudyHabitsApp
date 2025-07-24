package com.windowP;

import com.components.PanelRedondeado;
import com.components.PanelTarea;
import com.components.PanelTareaFactory;
import com.database.GestorRegistro;
import com.implementation.ListaTareas;
import com.implementation.NodoTareas;
import com.implementation.Tarea;
import com.components.WrapLayout;
import com.components.Toast;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class PrincipalWindow extends JFrame {
    private int idUsuario;
    private GestorRegistro gestorRegistro;
    private JPanel ParentPanel;
    private JLabel jLabel1;
    private PanelRedondeado panelRedondeado1;
    private PanelRedondeado panelRedondeado2;
    private PanelRedondeado buscarBtn;
    private JTextField buscarTareaField;
    private JPanel headerPanel;
    private JPanel buttonPanel;
    private JScrollPane scrollPane;
    private JLabel CrearTareaBtnt;
    private JLabel DeshacerBtnt;
    private JLabel BuscarLabel;
    private ListaTareas listaTareas;

    public PrincipalWindow(int idUsuario, GestorRegistro gestorRegistro) {
        this.idUsuario = idUsuario;
        this.gestorRegistro = gestorRegistro;
        this.listaTareas = new ListaTareas();
        initComponents();
        cargarTareas();
        setLocationRelativeTo(null);
        
        // Add shutdown hook for proper database cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            gestorRegistro.cerrarConexion();
            GestorRegistro.shutdown();
        }));
        
        // Add component listener for responsive behavior
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLayoutForSize();
            }
        });
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("StudyHabits - Task Manager");
        setResizable(true); // Make window resizable
        setMinimumSize(new Dimension(800, 600)); // Set minimum size
        setPreferredSize(new Dimension(1000, 700)); // Set preferred size

        // Create main container with BorderLayout for responsiveness
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(255, 255, 255));

        // Header Panel
        createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Central content area with tasks
        createContentPanel();
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // Bottom button panel
        createButtonPanel();
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Search panel on the right
        JPanel searchPanel = createSearchPanel();
        mainContainer.add(searchPanel, BorderLayout.EAST);

        setContentPane(mainContainer);
        pack();
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel1 = new JLabel();
        jLabel1.setFont(new Font("Roboto SemiBold", Font.BOLD, 24));
        jLabel1.setForeground(new Color(0, 102, 102));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Bienvenido, " + gestorRegistro.obtenerNombreUsuario(idUsuario));
        
        // Add connection pool stats
        JLabel statsLabel = new JLabel(gestorRegistro.getConnectionPoolStats());
        statsLabel.setFont(new Font("Roboto", Font.PLAIN, 10));
        statsLabel.setForeground(new Color(128, 128, 128));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(255, 255, 255));
        titlePanel.add(jLabel1, BorderLayout.CENTER);
        titlePanel.add(statsLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
    }

    private void createContentPanel() {
        ParentPanel = new JPanel();
        ParentPanel.setBackground(new Color(255, 255, 255));
        ParentPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        
        scrollPane = new JScrollPane(ParentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
    }

    private void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Create Task Button
        panelRedondeado1 = new PanelRedondeado();
        panelRedondeado1.setBackground(new Color(0, 153, 153));
        panelRedondeado1.setPreferredSize(new Dimension(160, 40));
        
        CrearTareaBtnt = new JLabel();
        CrearTareaBtnt.setFont(new Font("Roboto Medium", Font.BOLD, 18));
        CrearTareaBtnt.setForeground(Color.WHITE);
        CrearTareaBtnt.setHorizontalAlignment(SwingConstants.CENTER);
        CrearTareaBtnt.setText("Crear Tarea");
        CrearTareaBtnt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        CrearTareaBtnt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                PanelTarea panel = PanelTareaFactory.agregarTarea(ParentPanel, listaTareas, null, gestorRegistro);
                if (panel != null) {
                    Toast.mostrar(PrincipalWindow.this, "Tarea creada exitosamente");
                }
            }
            @Override
            public void mouseEntered(MouseEvent evt) {
                panelRedondeado1.setBackground(new Color(0, 102, 102));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                panelRedondeado1.setBackground(new Color(0, 153, 153));
            }
        });

        panelRedondeado1.setLayout(new BorderLayout());
        panelRedondeado1.add(CrearTareaBtnt, BorderLayout.CENTER);

        // Undo Button
        panelRedondeado2 = new PanelRedondeado();
        panelRedondeado2.setBackground(new Color(0, 153, 153));
        panelRedondeado2.setPreferredSize(new Dimension(160, 40));
        
        DeshacerBtnt = new JLabel();
        DeshacerBtnt.setFont(new Font("Roboto Medium", Font.BOLD, 18));
        DeshacerBtnt.setForeground(Color.WHITE);
        DeshacerBtnt.setHorizontalAlignment(SwingConstants.CENTER);
        DeshacerBtnt.setText("Deshacer");
        DeshacerBtnt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        DeshacerBtnt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                boolean seDeshacer = gestorRegistro.deshacerUltimaAccion(PrincipalWindow.this);
                if (seDeshacer) {
                    Toast.mostrar(PrincipalWindow.this, "Acción deshecha");
                }
                actualizarTareas();
            }
            @Override
            public void mouseEntered(MouseEvent evt) {
                panelRedondeado2.setBackground(new Color(0, 102, 102));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                panelRedondeado2.setBackground(new Color(0, 153, 153));
            }
        });

        panelRedondeado2.setLayout(new BorderLayout());
        panelRedondeado2.add(DeshacerBtnt, BorderLayout.CENTER);

        buttonPanel.add(panelRedondeado1);
        buttonPanel.add(panelRedondeado2);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(255, 255, 255));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        searchPanel.setPreferredSize(new Dimension(200, 0));

        BuscarLabel = new JLabel("Buscar Tareas");
        BuscarLabel.setFont(new Font("Roboto Medium", Font.BOLD, 14));
        BuscarLabel.setForeground(new Color(0, 102, 102));
        
        buscarTareaField = new JTextField();
        buscarTareaField.setFont(new Font("Roboto", Font.PLAIN, 14));
        buscarTareaField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 153), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        buscarBtn = new PanelRedondeado();
        buscarBtn.setBackground(new Color(0, 153, 153));
        buscarBtn.setPreferredSize(new Dimension(0, 35));
        
        JLabel buscarText = new JLabel("Buscar");
        buscarText.setFont(new Font("Roboto Medium", Font.BOLD, 14));
        buscarText.setForeground(Color.WHITE);
        buscarText.setHorizontalAlignment(SwingConstants.CENTER);
        buscarText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buscarBtn.setLayout(new BorderLayout());
        buscarBtn.add(buscarText, BorderLayout.CENTER);

        JPanel searchInputPanel = new JPanel(new BorderLayout(0, 10));
        searchInputPanel.setBackground(new Color(255, 255, 255));
        searchInputPanel.add(BuscarLabel, BorderLayout.NORTH);
        searchInputPanel.add(buscarTareaField, BorderLayout.CENTER);
        searchInputPanel.add(buscarBtn, BorderLayout.SOUTH);

        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        return searchPanel;
    }

    private void adjustLayoutForSize() {
        Dimension size = getSize();
        
        // Adjust search panel visibility based on window width
        Component searchPanel = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.EAST);
        if (searchPanel != null) {
            searchPanel.setVisible(size.width > 1000);
        }
        
        // Adjust button layout based on window width
        if (size.width < 900) {
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        } else {
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        }
        
        revalidate();
        repaint();
    }

    private void cargarTareas() {
        long startTime = System.nanoTime();
        ParentPanel.removeAll();
        
        List<Tarea> tareas = gestorRegistro.buscarTareasPorUsuario(idUsuario);
        for (Tarea tarea : tareas) {
            NodoTareas nodo = listaTareas.agregarTarea(tarea.getIdTarea(), idUsuario, tarea.getNombre(), tarea.getDescripcion());
            PanelTareaFactory.agregarTarea(ParentPanel, listaTareas, nodo, gestorRegistro);
        }
        
        ParentPanel.revalidate();
        ParentPanel.repaint();
        
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("UI task loading completed in %.2f ms%n", timeMs);
    }

    private void agregarPanelTarea(Tarea tarea) {
        NodoTareas nodo = listaTareas.agregarTarea(tarea.getIdTarea(), idUsuario, tarea.getNombre(), tarea.getDescripcion());
        PanelTarea panelTarea = PanelTareaFactory.agregarTarea(ParentPanel, listaTareas, nodo, gestorRegistro);
        if (panelTarea != null) {
            ParentPanel.add(panelTarea);
            ParentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            ParentPanel.revalidate();
            ParentPanel.repaint();
        }
    }

    public void actualizarTareas() {
        cargarTareas();
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PrincipalWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            GestorRegistro gestor = new GestorRegistro();
            new PrincipalWindow(1, gestor).setVisible(true);
        });
    }
}
