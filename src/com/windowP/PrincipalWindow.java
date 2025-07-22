package com.windowP;

import com.components.PanelRedondeado;
import com.components.SimpleSlideAnimation;
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
import java.util.List;

public class PrincipalWindow extends JFrame {
    private int idUsuario;
    private GestorRegistro gestorRegistro;
    private JPanel ParentPanel;
    private JLabel jLabel1;
    private PanelRedondeado panelRedondeado1;
    private PanelRedondeado panelRedondeado2;
    private JPanel Draggle;
    private JPanel ExitBtn;
    private JLabel ExitBtnt;
    private JLabel CrearTareaBtnt;
    private JLabel DeshacerBtnt;
    private ListaTareas listaTareas;
    private int xMouse, yMouse;

    public PrincipalWindow(int idUsuario, GestorRegistro gestorRegistro) {
        this.idUsuario = idUsuario;
        this.gestorRegistro = gestorRegistro;
        this.listaTareas = new ListaTareas();
        initComponents();
        cargarTareas();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        JPanel jPanel1 = new JPanel();
        jPanel1.setBackground(new Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Panel superior verde oscuro
        // Panel superior verde con botón de salir
        Draggle = new JPanel();
        Draggle.setBackground(new Color(0, 102, 102));
        Draggle.setLayout(new BorderLayout());
        
        // Botón de salir
        ExitBtn = new JPanel();
        ExitBtn.setBackground(new Color(0, 102, 102));
        ExitBtnt = new JLabel("X");
        ExitBtnt.setFont(new Font("Segoe UI", Font.BOLD, 24));
        ExitBtnt.setForeground(Color.WHITE);
        ExitBtnt.setHorizontalAlignment(SwingConstants.CENTER);
        ExitBtnt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ExitBtnt.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        ExitBtnt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                System.exit(0);
            }
            @Override
            public void mouseEntered(MouseEvent evt) {
                //ExitBtnt.setBackground(new Color(0, 85, 85)); // Verde más oscuro
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                //ExitBtnt.setBackground(new Color(0, 102, 102)); // Verde normal
            }
        });
        ExitBtn.add(ExitBtnt);
        
        // Panel para el botón de salir (alineado a la derecha)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(0, 102, 102));
        rightPanel.add(ExitBtn);
        Draggle.add(rightPanel, BorderLayout.EAST);
        
        // Configurar el arrastre de la ventana
        Draggle.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                setLocation(x - xMouse, y - yMouse);
            }
        });
        Draggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                xMouse = evt.getX();
                yMouse = evt.getY();
            }
        });
        
        jPanel1.add(Draggle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 80));

        ParentPanel = new JPanel();
        ParentPanel.setBackground(new Color(255, 255, 255));
        //ParentPanel.setLayout(new BoxLayout(ParentPanel, BoxLayout.Y_AXIS));
        //ParentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        ParentPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        JScrollPane scrollPane = new JScrollPane(ParentPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jPanel1.add(scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 720, 360));

        // Mensaje de bienvenida
        jLabel1 = new JLabel();
        jLabel1.setFont(new Font("Roboto SemiBold", Font.BOLD, 24));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Bienvenido, " + gestorRegistro.obtenerNombreUsuario(idUsuario));
        Draggle.add(jLabel1, BorderLayout.CENTER);

        panelRedondeado1 = new PanelRedondeado();
        panelRedondeado1.setBackground(new Color(0, 153, 153));
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
                    JOptionPane.showMessageDialog(PrincipalWindow.this, "Tarea creada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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

        GroupLayout panelRedondeado1Layout = new GroupLayout(panelRedondeado1);
        panelRedondeado1.setLayout(panelRedondeado1Layout);
        panelRedondeado1Layout.setHorizontalGroup(
            panelRedondeado1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(CrearTareaBtnt, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );
        panelRedondeado1Layout.setVerticalGroup(
            panelRedondeado1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, panelRedondeado1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(CrearTareaBtnt, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
        );
        jPanel1.add(panelRedondeado1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 470, 160, 34));

        panelRedondeado2 = new PanelRedondeado();
        panelRedondeado2.setBackground(new Color(0, 153, 153));
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

        GroupLayout panelRedondeado2Layout = new GroupLayout(panelRedondeado2);
        panelRedondeado2.setLayout(panelRedondeado2Layout);
        panelRedondeado2Layout.setHorizontalGroup(
            panelRedondeado2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(DeshacerBtnt, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );
        panelRedondeado2Layout.setVerticalGroup(
            panelRedondeado2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, panelRedondeado2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(DeshacerBtnt, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
        );
        jPanel1.add(panelRedondeado2, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 470, 160, 34));

        Draggle = new JPanel();
        Draggle.setBackground(new Color(255, 255, 255));
        Draggle.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            private int xMouse, yMouse;
            public void mouseDragged(MouseEvent evt) {
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                PrincipalWindow.this.setLocation(x - xMouse, y - yMouse);
            }
            public void mousePressed(MouseEvent evt) {
                xMouse = evt.getX();
                yMouse = evt.getY();
            }
        });

        ExitBtn = new JPanel();
        ExitBtn.setBackground(new Color(0, 102, 102));
        ExitBtnt = new JLabel();
        ExitBtnt.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        ExitBtnt.setForeground(new Color(204, 204, 204));
        ExitBtnt.setHorizontalAlignment(SwingConstants.CENTER);
        ExitBtnt.setText("X");
        ExitBtnt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ExitBtnt.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                System.exit(0);
            }
            public void mouseEntered(MouseEvent evt) {
                ExitBtnt.setForeground(Color.BLACK);
            }
            public void mouseExited(MouseEvent evt) {
                ExitBtnt.setForeground(Color.LIGHT_GRAY);
            }
        });

        GroupLayout ExitBtnLayout = new GroupLayout(ExitBtn);
        ExitBtn.setLayout(ExitBtnLayout);
        ExitBtnLayout.setHorizontalGroup(
            ExitBtnLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(ExitBtnLayout.createSequentialGroup()
                    .addComponent(ExitBtnt, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        ExitBtnLayout.setVerticalGroup(
            ExitBtnLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(ExitBtnt, GroupLayout.PREFERRED_SIZE, 35, Short.MAX_VALUE)
        );

        GroupLayout DraggleLayout = new GroupLayout(Draggle);
        Draggle.setLayout(DraggleLayout);
        DraggleLayout.setHorizontalGroup(
            DraggleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(DraggleLayout.createSequentialGroup()
                    .addComponent(ExitBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 769, Short.MAX_VALUE))
        );
        DraggleLayout.setVerticalGroup(
            DraggleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(DraggleLayout.createSequentialGroup()
                    .addComponent(ExitBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 5, Short.MAX_VALUE))
        );

        jPanel1.add(Draggle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 40));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 800, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 520, GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }

    private void cargarTareas() {
        ParentPanel.removeAll();
        List<Tarea> tareas = gestorRegistro.buscarTareasPorUsuario(idUsuario);
        for (Tarea tarea : tareas) {
            NodoTareas nodo = listaTareas.agregarTarea(tarea.getIdTarea(), idUsuario, tarea.getNombre(), tarea.getDescripcion());
            PanelTareaFactory.agregarTarea(ParentPanel, listaTareas, nodo, gestorRegistro);
        }
        ParentPanel.revalidate();
        ParentPanel.repaint();
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