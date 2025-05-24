package laberintojuego.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Outro extends JFrame {
    public Outro() {
        setTitle("Gracias por jugar");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        OutroPanel panel = new OutroPanel();
        setContentPane(panel);
        setVisible(true);
    }

    class OutroPanel extends JPanel {
        private Image fondo;

        public OutroPanel() {
            fondo = new ImageIcon(getClass().getResource("/laberintojuego/images/out.png")).getImage();
            setLayout(null); // diseño absoluto

            // Panel de texto con fondo semitransparente
            JPanel cajaTexto = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 0, 0, 180)); // negro con transparencia
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            cajaTexto.setLayout(new BorderLayout());
            cajaTexto.setOpaque(false);
            cajaTexto.setBounds(100, 420, 600, 100); // parte inferior y centrado

            ReproducirSonido.reproducirAmbiente("/laberintojuego/audios/sonido_outro.wav");
            JTextArea texto = new JTextArea("Gracias por salvar a nuestro pueblo de la maldición.\nTe estaremos siempre agradecidos.");
            texto.setWrapStyleWord(true);
            texto.setLineWrap(true);
            texto.setOpaque(false);
            texto.setForeground(Color.WHITE);
            texto.setEditable(false);
            texto.setFocusable(false);
            texto.setFont(new Font("Serif", Font.PLAIN, 18));
            texto.setMargin(new Insets(10, 15, 10, 15));

            cajaTexto.add(texto, BorderLayout.CENTER);
            add(cajaTexto);

            // Botón "Salir"
            JButton salirBtn = new JButton("Salir");
            salirBtn.setFocusable(false);
            salirBtn.setBounds(350, 530, 100, 30); // debajo del panel de texto
            salirBtn.addActionListener(e -> System.exit(0));
            add(salirBtn);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
