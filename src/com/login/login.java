package com.login;

import com.database.GestorRegistro;
import com.windowP.PrincipalWindow;
import com.components.PanelRedondeado;
import com.components.SimpleSlideAnimation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class login extends JFrame {
    private GestorRegistro gestorRegistro;
    private int xMouse, yMouse;
    private CardLayout layout;
//s
    public login() {
        gestorRegistro = new GestorRegistro();
        initComponents();
        layout = new CardLayout();
        FlipFrame.setLayout(layout);
        FlipFrame.add(SingUpFrame, "singup");
        FlipFrame.add(SingInFrame, "singin");
        SingUpFrame.setName("singup");
        SingInFrame.setName("singin");
        layout.show(FlipFrame, "singin");
        
        // Configurar orden de tabulación para inicio de sesión
        Username.setNextFocusableComponent(Pass);
        Pass.setNextFocusableComponent(EntrarBtnt);
        
        // Configurar orden de tabulación para registro
        EntryUser.setNextFocusableComponent(EntryMail);
        EntryMail.setNextFocusableComponent(EntryPass);
        EntryPass.setNextFocusableComponent(SendRest);
        
        // Agregar key listeners para la tecla Enter
        Username.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    Pass.requestFocusInWindow();
                }
            }
        });
        
        Pass.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    EntrarBtnt.requestFocusInWindow();
                }
            }
        });
        
        EntrarBtnt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    EntrarBtnt.dispatchEvent(new MouseEvent(EntrarBtnt, MouseEvent.MOUSE_CLICKED, 
                        System.currentTimeMillis(), 0, 0, 0, 1, false));
                }
            }
        });
        
        EntryUser.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    EntryMail.requestFocusInWindow();
                }
            }
        });
        
        EntryMail.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    EntryPass.requestFocusInWindow();
                }
            }
        });
        
        EntryPass.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    SendRest.requestFocusInWindow();
                }
            }
        });
        
        SendRest.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    SendRest.dispatchEvent(new MouseEvent(SendRest, MouseEvent.MOUSE_CLICKED, 
                        System.currentTimeMillis(), 0, 0, 0, 1, false));
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setUndecorated(true);
        setResizable(false);

        jPanel2 = new JPanel();
        FlipFrame = new JPanel();
        SingInFrame = new JPanel();
        IniciarSesion = new JLabel();
        Usuario = new JLabel();
        UsernameSpace = new PanelRedondeado();
        Username = new JTextField();
        Contrasena = new JLabel();
        EntrarBtn = new PanelRedondeado();
        EntrarBtnt = new JLabel();
        goSignup = new JLabel();
        jLabel1 = new JLabel();
        jPanel1 = new JPanel();
        PassSpace = new PanelRedondeado();
        Pass = new JPasswordField();
        jLabel2 = new JLabel();
        SingUpFrame = new JPanel();
        RegistrarText = new JLabel();
        SendRes = new PanelRedondeado();
        SendRest = new JLabel();
        goSingIn = new JLabel();
        MailText = new JLabel();
        MailUnderline = new JSeparator();
        EntryMail = new JTextField();
        PassText = new JLabel();
        PassUnderline = new JSeparator();
        UserText = new JLabel();
        UserUnderline = new JSeparator();
        EntryUser = new JTextField();
        EntryPass = new JPasswordField();
        Logo = new JLabel();
        TextLogo = new JLabel();
        CityLogo = new JLabel();
        Draggle = new JPanel();
        ExitBtn = new JPanel();
        ExitBtnt = new JLabel();

        jPanel2.setBackground(new Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        FlipFrame.setLayout(new CardLayout());

        SingInFrame.setBackground(new Color(255, 255, 255));

        IniciarSesion.setFont(new Font("Roboto SemiBold", Font.BOLD, 36));
        IniciarSesion.setForeground(new Color(0, 102, 102));
        IniciarSesion.setHorizontalAlignment(SwingConstants.CENTER);
        IniciarSesion.setText("INICIAR SESIÓN");

        Usuario.setFont(new Font("Roboto SemiBold", Font.BOLD, 24));
        Usuario.setForeground(new Color(0, 102, 102));
        Usuario.setHorizontalAlignment(SwingConstants.CENTER);
        Usuario.setText("Usuario");

        UsernameSpace.setBackground(new Color(195, 221, 211));
        Username.setBackground(new Color(195, 221, 211));
        Username.setFont(new Font("Roboto SemiBold", Font.BOLD, 18));
        Username.setForeground(new Color(153, 153, 153));
        Username.setHorizontalAlignment(JTextField.CENTER);
        Username.setText("Ingrese su usuario");
        Username.setBorder(null);
        Username.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (Username.getText().equals("Ingrese su usuario")) {
                    Username.setText("");
                    Username.setForeground(new Color(0, 102, 102));
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (Username.getText().isEmpty()) {
                    Username.setText("Ingrese su usuario");
                    Username.setForeground(Color.GRAY);
                }
            }
        });
        
        Username.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    Pass.requestFocus();
                }
            }
        });

        GroupLayout UsernameSpaceLayout = new GroupLayout(UsernameSpace);
        UsernameSpace.setLayout(UsernameSpaceLayout);
        UsernameSpaceLayout.setHorizontalGroup(
            UsernameSpaceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, UsernameSpaceLayout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Username, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        UsernameSpaceLayout.setVerticalGroup(
            UsernameSpaceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(UsernameSpaceLayout.createSequentialGroup()
                    .addComponent(Username, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );

        Contrasena.setFont(new Font("Roboto SemiBold", Font.BOLD, 24));
        Contrasena.setForeground(new Color(0, 102, 102));
        Contrasena.setHorizontalAlignment(SwingConstants.CENTER);
        Contrasena.setText("Contraseña");

        EntrarBtn.setBackground(new Color(0, 153, 153));
        EntrarBtnt.setFont(new Font("Roboto Medium", Font.BOLD, 18));
        EntrarBtnt.setForeground(Color.WHITE);
        EntrarBtnt.setHorizontalAlignment(SwingConstants.CENTER);
        EntrarBtnt.setText("ENTRAR");
        EntrarBtnt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        EntrarBtnt.setFocusable(true);
        EntrarBtnt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                EntrarBtn.setBackground(new Color(0, 102, 102));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                EntrarBtn.setBackground(new Color(0, 153, 153));
            }
        });
        EntrarBtnt.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                String correo = Username.getText().trim();
                String contrasena = new String(Pass.getPassword()).trim();
                if (correo.isEmpty() || correo.equals("Ingrese su usuario") || contrasena.isEmpty() || contrasena.equals("********")) {
                    JOptionPane.showMessageDialog(login.this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int idUsuario = gestorRegistro.validarCredenciales(correo, contrasena);
                if (idUsuario != -1) {
                    JOptionPane.showMessageDialog(login.this, "Inicio de sesión exitoso.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    PrincipalWindow principal = new PrincipalWindow(idUsuario, gestorRegistro);
                    principal.setVisible(true);
                    login.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(login.this, "Correo o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            public void mouseEntered(MouseEvent evt) {
                EntrarBtn.setBackground(new Color(0, 102, 102));
            }
            public void mouseExited(MouseEvent evt) {
                EntrarBtn.setBackground(new Color(0, 153, 153));
            }
        });

        GroupLayout EntrarBtnLayout = new GroupLayout(EntrarBtn);
        EntrarBtn.setLayout(EntrarBtnLayout);
        EntrarBtnLayout.setHorizontalGroup(
            EntrarBtnLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(EntrarBtnt, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );
        EntrarBtnLayout.setVerticalGroup(
            EntrarBtnLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, EntrarBtnLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(EntrarBtnt, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
        );

        goSignup.setFont(new Font("Roboto Medium", Font.PLAIN, 12));
        goSignup.setForeground(new Color(0, 102, 102));
        goSignup.setHorizontalAlignment(SwingConstants.LEFT);
        goSignup.setText(" Ir a Registrarte >");
        goSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goSignup.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                SimpleSlideAnimation.slide(FlipFrame, layout, "singup", "left");
            }
            public void mouseEntered(MouseEvent evt) {
                goSignup.setFont(new Font("Roboto Medium", Font.BOLD, 12));
            }
            public void mouseExited(MouseEvent evt) {
                goSignup.setFont(new Font("Roboto Medium", Font.PLAIN, 12));
            }
        });

        jLabel1.setForeground(new Color(102, 102, 102));
        jLabel1.setText("¿No tienes una cuenta?");

        jPanel1.setBackground(new Color(255, 255, 255));
        PassSpace.setBackground(new Color(195, 221, 211));
        Pass.setBackground(new Color(195, 221, 211));
        Pass.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        Pass.setForeground(new Color(153, 153, 153));
        Pass.setHorizontalAlignment(JTextField.CENTER);
        Pass.setText("********");
        Pass.setBorder(null);
        Pass.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(Pass.getPassword()).equals("********")) {
                    Pass.setText("");
                    Pass.setForeground(new Color(0, 102, 102));
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (String.valueOf(Pass.getPassword()).isEmpty()) {
                    Pass.setText("********");
                    Pass.setForeground(Color.GRAY);
                }
            }
        });
        
        Pass.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    EntrarBtnt.requestFocus();
                }
            }
        });

        GroupLayout PassSpaceLayout = new GroupLayout(PassSpace);
        PassSpace.setLayout(PassSpaceLayout);
        PassSpaceLayout.setHorizontalGroup(
            PassSpaceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(PassSpaceLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Pass, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addContainerGap())
        );
        PassSpaceLayout.setVerticalGroup(
            PassSpaceLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(Pass, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
        );

        jLabel2.setFont(new Font("Roboto SemiCondensed Light", Font.PLAIN, 12));
        jLabel2.setForeground(new Color(0, 153, 153));
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Olvidaste tu contraseña?");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addGap(189, 189, 189))
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(133, 133, 133)
                    .addComponent(PassSpace, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(137, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(PassSpace, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel2)
                    .addContainerGap())
        );

        GroupLayout SingInFrameLayout = new GroupLayout(SingInFrame);
        SingInFrame.setLayout(SingInFrameLayout);
        SingInFrameLayout.setHorizontalGroup(
            SingInFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(SingInFrameLayout.createSequentialGroup()
                    .addComponent(IniciarSesion, GroupLayout.PREFERRED_SIZE, 520, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(SingInFrameLayout.createSequentialGroup()
                    .addGap(134, 134, 134)
                    .addComponent(UsernameSpace, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(SingInFrameLayout.createSequentialGroup()
                    .addGroup(SingInFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(Contrasena, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(SingInFrameLayout.createSequentialGroup()
                            .addGroup(SingInFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(SingInFrameLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(Usuario, GroupLayout.PREFERRED_SIZE, 490, GroupLayout.PREFERRED_SIZE))
                                .addGroup(SingInFrameLayout.createSequentialGroup()
                                    .addGap(178, 178, 178)
                                    .addComponent(EntrarBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGroup(SingInFrameLayout.createSequentialGroup()
                                    .addGap(164, 164, 164)
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(goSignup)))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, SingInFrameLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        SingInFrameLayout.setVerticalGroup(
            SingInFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(SingInFrameLayout.createSequentialGroup()
                    .addGap(23, 23, 23)
                    .addComponent(IniciarSesion)
                    .addGap(48, 48, 48)
                    .addComponent(Usuario, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(UsernameSpace, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(13, 13, 13)
                    .addComponent(Contrasena, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                    .addComponent(EntrarBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(SingInFrameLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(goSignup)
                        .addComponent(jLabel1))
                    .addContainerGap())
        );

        FlipFrame.add(SingInFrame, "singin");

        SingUpFrame.setBackground(new Color(255, 255, 255));
        SingUpFrame.setMinimumSize(new Dimension(20, 20));

        RegistrarText.setFont(new Font("Roboto SemiBold", Font.BOLD, 36));
        RegistrarText.setForeground(new Color(0, 102, 102));
        RegistrarText.setHorizontalAlignment(SwingConstants.CENTER);
        RegistrarText.setText("REGISTRARSE");

        SendRes.setBackground(new Color(0, 153, 153));
        SendRest.setFont(new Font("Roboto Medium", Font.BOLD, 18));
        SendRest.setForeground(Color.WHITE);
        SendRest.setHorizontalAlignment(SwingConstants.CENTER);
        SendRest.setText("Enviar");
        SendRest.setCursor(new Cursor(Cursor.HAND_CURSOR));
        SendRest.setFocusable(true);
        SendRest.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                SendRes.setBackground(new Color(0, 102, 102));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                SendRes.setBackground(new Color(0, 153, 153));
            }
        });
        SendRest.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                String nombre = EntryUser.getText().trim();
                String correo = EntryMail.getText().trim();
                String contrasena = new String(EntryPass.getPassword()).trim();

                if (nombre.isEmpty() || nombre.equals("Nombre de usuario") || 
                    correo.isEmpty() || correo.equals("Correo @unal.edu.co") || 
                    contrasena.isEmpty() || contrasena.equals("************")) {
                    JOptionPane.showMessageDialog(login.this, "Por favor, completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!correo.endsWith("@unal.edu.co")) {
                    JOptionPane.showMessageDialog(login.this, "El correo debe ser @unal.edu.co.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Verificar si el correo ya está registrado
                if (gestorRegistro.existeCorreo(correo)) {
                    JOptionPane.showMessageDialog(login.this, "Este correo ya está registrado. Por favor, inicia sesión.", "Usuario Existente", JOptionPane.INFORMATION_MESSAGE);
                    SimpleSlideAnimation.slide(FlipFrame, layout, "singin", "right");
                    return;
                }

                boolean enviado = gestorRegistro.enviarCodigoVerificacion(correo);
                if (!enviado) {
                    JOptionPane.showMessageDialog(login.this, "Correo inválido o error al enviar.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JDialog dialogo = new JDialog(login.this, "Confirmar Código", true);
                dialogo.setSize(300, 150);
                dialogo.setLocationRelativeTo(login.this);
                dialogo.setLayout(new GridLayout(3, 1, 10, 10));

                JLabel etiqueta = new JLabel("Ingresa el código de confirmación:");
                JTextField campoCodigo = new JTextField(10);
                campoCodigo.setFont(new Font("Roboto Medium", Font.PLAIN, 14));
                JPanel botonPanel = new JPanel();
                JLabel botonConfirmar = new JLabel("Confirmar");
                botonConfirmar.setFont(new Font("Roboto Medium", Font.BOLD, 18));
                botonConfirmar.setForeground(Color.WHITE);
                botonConfirmar.setHorizontalAlignment(SwingConstants.CENTER);
                botonConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
                PanelRedondeado panelConfirmar = new PanelRedondeado();
                panelConfirmar.setBackground(new Color(0, 153, 153));
                panelConfirmar.setLayout(new BorderLayout());
                panelConfirmar.add(botonConfirmar, BorderLayout.CENTER);

                botonConfirmar.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String codigoIngresado = campoCodigo.getText().trim();
                        if (gestorRegistro.verificarCodigo(correo, codigoIngresado)) {
                            if (gestorRegistro.registrarUsuario(nombre, "", correo, contrasena)) {
                                JOptionPane.showMessageDialog(dialogo, "Registro exitoso.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                dialogo.dispose();
                                int idUsuario = gestorRegistro.validarCredenciales(correo, contrasena);
                                PrincipalWindow principal = new PrincipalWindow(idUsuario, gestorRegistro);
                                principal.setVisible(true);
                                login.this.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialogo, "Error al registrar usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialogo, "Código incorrecto.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        panelConfirmar.setBackground(new Color(0, 102, 102));
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        panelConfirmar.setBackground(new Color(0, 153, 153));
                    }
                });

                dialogo.add(etiqueta);
                dialogo.add(campoCodigo);
                dialogo.add(panelConfirmar);
                dialogo.setVisible(true);
            }
            public void mouseEntered(MouseEvent evt) {
                SendRes.setBackground(new Color(0, 102, 102));
            }
            public void mouseExited(MouseEvent evt) {
                SendRes.setBackground(new Color(0, 153, 153));
            }
        });

        GroupLayout SendResLayout = new GroupLayout(SendRes);
        SendRes.setLayout(SendResLayout);
        SendResLayout.setHorizontalGroup(
            SendResLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(SendRest, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
        );
        SendResLayout.setVerticalGroup(
            SendResLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(SendRest, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
        );

        goSingIn.setFont(new Font("Roboto Medium", Font.PLAIN, 13));
        goSingIn.setForeground(new Color(0, 102, 102));
        goSingIn.setText("< Ir a Iniciar Sesión");
        goSingIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goSingIn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                SimpleSlideAnimation.slide(FlipFrame, layout, "singin", "right");
            }
            public void mouseEntered(MouseEvent evt) {
                goSingIn.setFont(new Font("Roboto Medium", Font.BOLD, 13));
            }
            public void mouseExited(MouseEvent evt) {
                goSingIn.setFont(new Font("Roboto Medium", Font.PLAIN, 13));
            }
        });

        MailText.setFont(new Font("Roboto SemiBold", Font.BOLD, 18));
        MailText.setForeground(new Color(0, 102, 102));
        MailText.setHorizontalAlignment(SwingConstants.LEFT);
        MailText.setText("Correo institucional");

        EntryMail.setFont(new Font("Roboto Medium", Font.PLAIN, 14));
        EntryMail.setForeground(new Color(153, 153, 153));
        EntryMail.setText("Correo @unal.edu.co");
        EntryMail.setBorder(null);
        EntryMail.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (EntryMail.getText().equals("Correo @unal.edu.co")) {
                    EntryMail.setText("");
                    EntryMail.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (EntryMail.getText().isEmpty()) {
                    EntryMail.setText("Correo @unal.edu.co");
                    EntryMail.setForeground(Color.GRAY);
                }
            }
        });
        
        EntryMail.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    EntryPass.requestFocus();
                }
            }
        });

        PassText.setFont(new Font("Roboto SemiBold", Font.BOLD, 18));
        PassText.setForeground(new Color(0, 102, 102));
        PassText.setHorizontalAlignment(SwingConstants.LEFT);
        PassText.setText("Contraseña");

        UserText.setFont(new Font("Roboto SemiBold", Font.BOLD, 18));
        UserText.setForeground(new Color(0, 102, 102));
        UserText.setHorizontalAlignment(SwingConstants.LEFT);
        UserText.setText("Nombre de usuario");

        EntryUser.setFont(new Font("Roboto Medium", Font.PLAIN, 14));
        EntryUser.setForeground(new Color(153, 153, 153));
        EntryUser.setText("Nombre de usuario");
        EntryUser.setBorder(null);
        EntryUser.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (EntryUser.getText().equals("Nombre de usuario")) {
                    EntryUser.setText("");
                    EntryUser.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (EntryUser.getText().isEmpty()) {
                    EntryUser.setText("Nombre de usuario");
                    EntryUser.setForeground(Color.GRAY);
                }
            }
        });
        
        EntryUser.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    EntryMail.requestFocus();
                }
            }
        });

        EntryPass.setFont(new Font("Arial", Font.PLAIN, 14));
        EntryPass.setForeground(new Color(153, 153, 153));
        EntryPass.setText("************");
        EntryPass.setBorder(null);
        EntryPass.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (String.valueOf(EntryPass.getPassword()).equals("************")) {
                    EntryPass.setText("");
                    EntryPass.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (String.valueOf(EntryPass.getPassword()).isEmpty()) {
                    EntryPass.setText("************");
                    EntryPass.setForeground(Color.GRAY);
                }
            }
        });
        
        EntryPass.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    SendRest.requestFocus();
                }
            }
        });

        GroupLayout SingUpFrameLayout = new GroupLayout(SingUpFrame);
        SingUpFrame.setLayout(SingUpFrameLayout);
        SingUpFrameLayout.setHorizontalGroup(
            SingUpFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(SingUpFrameLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(SingUpFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, SingUpFrameLayout.createSequentialGroup()
                            .addComponent(RegistrarText, GroupLayout.PREFERRED_SIZE, 490, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, SingUpFrameLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(SingUpFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(UserText, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(PassText, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(EntryMail, GroupLayout.Alignment.TRAILING)
                                .addComponent(EntryPass, GroupLayout.Alignment.TRAILING)
                                .addComponent(EntryUser, GroupLayout.Alignment.TRAILING)
                                .addComponent(MailText, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(MailUnderline, GroupLayout.PREFERRED_SIZE, 449, GroupLayout.PREFERRED_SIZE)
                                .addComponent(PassUnderline, GroupLayout.PREFERRED_SIZE, 449, GroupLayout.PREFERRED_SIZE)
                                .addComponent(UserUnderline, GroupLayout.PREFERRED_SIZE, 452, GroupLayout.PREFERRED_SIZE))
                            .addGap(28, 28, 28))))
                .addGroup(SingUpFrameLayout.createSequentialGroup()
                    .addGroup(SingUpFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(SingUpFrameLayout.createSequentialGroup()
                            .addGap(208, 208, 208)
                            .addComponent(SendRes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(SingUpFrameLayout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addComponent(goSingIn)))
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        SingUpFrameLayout.setVerticalGroup(
            SingUpFrameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(SingUpFrameLayout.createSequentialGroup()
                    .addComponent(RegistrarText, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                    .addGap(46, 46, 46)
                    .addComponent(UserText, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                    .addGap(3, 3, 3)
                    .addComponent(EntryUser, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(UserUnderline, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(MailText, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                    .addGap(4, 4, 4)
                    .addComponent(EntryMail, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(MailUnderline, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(PassText)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(EntryPass, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(PassUnderline, GroupLayout.PREFERRED_SIZE, 11, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(SendRes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(goSingIn)
                    .addContainerGap())
        );

        FlipFrame.add(SingUpFrame, "singup");

        jPanel2.add(FlipFrame, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 520, 460));

        Logo.setHorizontalAlignment(SwingConstants.CENTER);
        Logo.setIcon(new ImageIcon(getClass().getResource("/com/images/logo.png")));
        jPanel2.add(Logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 280, 140));

        TextLogo.setFont(new Font("Roboto ExtraBold", Font.BOLD, 24));
        TextLogo.setForeground(Color.WHITE);
        TextLogo.setHorizontalAlignment(SwingConstants.CENTER);
        TextLogo.setText("TASK-GESTOR");
        jPanel2.add(TextLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 170, 280, -1));

        CityLogo.setIcon(new ImageIcon(getClass().getResource("/com/images/Fondo.png")));
        jPanel2.add(CityLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 280, -1));

        Draggle.setBackground(new Color(255, 255, 255));
        Draggle.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                login.this.setLocation(x - xMouse, y - yMouse);
            }
        });
        Draggle.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                xMouse = evt.getX();
                yMouse = evt.getY();
            }
        });

        ExitBtn.setBackground(new Color(255, 255, 255));
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
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        DraggleLayout.setVerticalGroup(
            DraggleLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(DraggleLayout.createSequentialGroup()
                    .addComponent(ExitBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel2.add(Draggle, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 40));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 800, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
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
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new login().setVisible(true));
    }

    private JPanel jPanel2;
    private JPanel FlipFrame;
    private JPanel SingInFrame;
    private JLabel IniciarSesion;
    private JLabel Usuario;
    private PanelRedondeado UsernameSpace;
    private JTextField Username;
    private JLabel Contrasena;
    private PanelRedondeado EntrarBtn;
    private JLabel EntrarBtnt;
    private JLabel goSignup;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private PanelRedondeado PassSpace;
    private JPasswordField Pass;
    private JLabel jLabel2;
    private JPanel SingUpFrame;
    private JLabel RegistrarText;
    private PanelRedondeado SendRes;
    private JLabel SendRest;
    private JLabel goSingIn;
    private JLabel MailText;
    private JSeparator MailUnderline;
    private JTextField EntryMail;
    private JLabel PassText;
    private JSeparator PassUnderline;
    private JLabel UserText;
    private JSeparator UserUnderline;
    private JTextField EntryUser;
    private JPasswordField EntryPass;
    private JLabel Logo;
    private JLabel TextLogo;
    private JLabel CityLogo;
    private JPanel Draggle;
    private JPanel ExitBtn;
    private JLabel ExitBtnt;
}