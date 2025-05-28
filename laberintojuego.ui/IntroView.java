package laberintojuego.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class IntroView extends JFrame {
    private JTextArea texto;
    private JButton botonIniciar;
    private String[] dialogos = {
        "Hola, bienvenido al juego.",
        "Sere tu Guardian y te acompañaré en esta aventura.",
        "Hace siglos, una antigua maldicion fue sellada en este pueblo",
        "Ahora, esa maldicion está despertando, y solo tú puedes detenerla.",
        "Explora, encuentra el objeto sagrado y escapa antes de que te atrapen.",
        "Controles:\n - Flechas para moverte\n - Espacio para usar luz\n - L para activar la lanza\n",
        "¿Estás listo? ¡Haz clic en 'Iniciar' para comenzar tu misión!"
    };
    private int indice = 0;

    public IntroView() {
        setTitle("Introducción");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ReproducirSonido.reproducirAmbiente("/laberintojuego/audios/sonido_intro.wav");
        PanelConFondo fondo = new PanelConFondo("/laberintojuego/images/intro.png");
        fondo.setLayout(null); // Layout absoluto

        // Área de texto
        JPanel panelDialogo = new JPanel() {
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 25;
                int width = getWidth();
                int height = getHeight();

                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, width - 2, height - 2, arc, arc);

                g2.dispose();
            }
            
        };
        
        panelDialogo.setLayout(new BorderLayout());
        panelDialogo.setBounds(20, 250, 445, 100); // 600 - 2*20 (márgenes)
        panelDialogo.setOpaque(false); // El fondo lo pintamos manualmente

        texto = new JTextArea(dialogos[indice]);
        texto.setMargin(new Insets(10, 15, 10, 15)); // top, left, bottom, right
        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setOpaque(false);
        texto.setForeground(Color.WHITE);
        texto.setEditable(false);
        texto.setFont(new Font("Serif", Font.PLAIN, 16));

        

        // Botón de iniciar
        botonIniciar = new JButton("Iniciar");
        botonIniciar.setEnabled(false);
        botonIniciar.setFocusable(false);
        botonIniciar.setBounds(200, 320, 100, 30);
        

        botonIniciar.addActionListener(e -> {
            
            ReproducirSonido.detenerAmbiente();
            
            System.out.println("Boton iniciar presionado");
            dispose();
            JFrame ventanaJuego = new JFrame("Laberinto Aventura");
            GameView vistaJuego = new GameView(1000, 500);
            ventanaJuego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventanaJuego.setContentPane(vistaJuego);
            ventanaJuego.pack();
            ventanaJuego.setLocationRelativeTo(null);
            ventanaJuego.setVisible(true);
        });
        
        panelDialogo.add(texto);
        fondo.add(botonIniciar);
        fondo.add(panelDialogo);

        setContentPane(fondo);
        setFocusable(true);
        requestFocusInWindow(); // fuerza el enfoque en el JFrame
        

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    avanzarDialogo();
                }
            }
        });
        
        setVisible(true);
    }

    private void avanzarDialogo() {
        if (indice < dialogos.length - 1) {
            indice++;
            texto.setText(dialogos[indice]);
            if (indice == dialogos.length - 1) {
                botonIniciar.setEnabled(true);
            }
        }
    }

    class PanelConFondo extends JPanel {
        private Image fondo;

        public PanelConFondo(String rutaImagen) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/laberintojuego/images/intro.png"));
            fondo = icon.getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (fondo != null) {
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
