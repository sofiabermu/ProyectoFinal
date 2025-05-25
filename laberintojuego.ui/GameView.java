/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package laberintojuego.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import laberintojuego.models.Jugador;
import laberintojuego.models.Laberinto;
import laberintojuego.models.Enemigo;

/**
 *
 * @author SOFIA RUDAS
 */
public class GameView extends javax.swing.JPanel implements KeyListener  {

    private Jugador jugador;
    private Laberinto laberinto;
    private ArrayList<Enemigo> sombras;
    private ArrayList<Thread> hilosSombras;
    private ImageIcon imagenFondo;
    private boolean juegoActivo = true;
    private int nivelActual = 1;
    
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private ImageIcon imagenPared;
    private ImageIcon fondo;

    public GameView(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        addKeyListener((KeyListener) this);
        int nivel =1;
        setBackground(Color.BLACK);
        
        switch (nivel) {
            case 1:
                fondo = new ImageIcon(getClass().getResource("/laberintojuego/images/fondo.png"));
                imagenPared = new ImageIcon(getClass().getResource("/laberintojuego/images/Arbusto.png"));
                break;
            case 2:
                fondo = new ImageIcon(getClass().getResource("/laberintojuego/images/castillo.png"));
                imagenPared = new ImageIcon(getClass().getResource("/laberintojuego/images/Antorcha.png"));
                break;
            case 3:
                fondo = new ImageIcon(getClass().getResource("/laberintojuego/images/fondo_cueva.png"));
                imagenPared = new ImageIcon(getClass().getResource("/laberintojuego/images/radioactivo.png"));
                break;
        }

        inicializarJuego(width, height);
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }
    
    private void inicializarJuego(int width, int height) {

        laberinto = new Laberinto(width, height, nivelActual);

        jugador = new Jugador(40, 40, 30, 30, laberinto, null);

        sombras = new ArrayList<>();
        hilosSombras = new ArrayList<>();
        crearSombras();

        iniciarHilosSombras();
    }
    
    private void crearSombras() {
        int numSombras = 2 + nivelActual; 
        Random rand = new Random();

        int anchoDisponible = Math.max(100, getWidth() - 100);
        int altoDisponible = Math.max(100, getHeight() - 100);

        for (int i = 0; i < numSombras; i++) {
            int x, y;
            do {
                x = rand.nextInt(anchoDisponible) + 50;
                y = rand.nextInt(altoDisponible) + 50;
            } while (!laberinto.isWalkable(x, y) || 
                     Math.abs(x - jugador.getX()) < 100 || 
                     Math.abs(y - jugador.getY()) < 100);

            Enemigo sombra = new Enemigo(x, y, 25, 25, jugador, laberinto, null, nivelActual);
            sombras.add(sombra);
            laberinto.agregarSombra(sombra);
        }
    }
    
    private void iniciarHilosSombras() {
        for (Enemigo sombra : sombras) {
            Thread hiloSombra = new Thread(sombra);
            hilosSombras.add(hiloSombra);
            hiloSombra.start();
        }
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 

        if (imagenFondo != null && imagenFondo.getImage() != null) {

            Image img = imagenFondo.getImage();
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        } else {

            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(20, 40, 20),
                0, getHeight(), new Color(10, 20, 10)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        laberinto.paint(g);

        for (Enemigo sombra : sombras) {
            if (sombra.estaVivo()) {
                sombra.paint(g);
            }
        }

        if (jugador.estaVivo()) {
            jugador.paint(g);
        }

        dibujarInterfaz(g);
    }

    private void dibujarInterfaz(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.BOLD, 16));

        g.drawString("Nivel: " + nivelActual, 10, 25);

        if (laberinto.isObjetoEncontrado()) {
            g.setColor(Color.GREEN);
            g.drawString("¡Objeto encontrado! Busca la salida", 10, 50);
        } else {
            g.setColor(Color.YELLOW);
            g.drawString("Busca el objeto especial", 10, 50);
        }

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Serif", Font.PLAIN, 12));
        g.drawString("Controles: Flechas=Mover, Espacio=Luz, L=Lanza", 10, getHeight() - 40);
        g.drawString("Puntaje: " + laberinto.getPuntaje(), 10, 60);
        g.drawString("Puntaje maximo: " + laberinto.getPuntajeMaximo(), 10, 80);

        if (!jugador.estaVivo()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Serif", Font.BOLD, 24));
            ReproducirSonido.reproducirEfecto("/laberintojuego/audios/sonido_perder.wav");
            g.drawString("¡GAME OVER!", getWidth()/2 - 80, getHeight()/2);
            g.drawString("Presiona R para reiniciar", getWidth()/2 - 120, getHeight()/2 + 30);
        }
    }
    
    private boolean estabaEnSalida = false;

    private void verificarEstadoJuego() {
        boolean objetoYaEncontrado = laberinto.isObjetoEncontrado(); // guardar el estado antes
        laberinto.verificarObjetoEspecial(jugador.getX(), jugador.getY()); // puede cambiar el estado

        if (!objetoYaEncontrado && laberinto.isObjetoEncontrado()) {
            ReproducirSonido.reproducirEfecto("/laberintojuego/audios/sonido_objeto.wav");
        }

        boolean enSalidaAhora = laberinto.verificarSalida(jugador.getX(), jugador.getY());

        if (laberinto.isObjetoEncontrado() && enSalidaAhora && !estabaEnSalida) {
            if (nivelActual == 3) {
                mostrarOutro();
            } else {
                ReproducirSonido.reproducirEfecto("/laberintojuego/audios/sonido_nivel.wav");
                siguienteNivel();
            }
        }

        estabaEnSalida = enSalidaAhora;

        sombras.removeIf(sombra -> !sombra.estaVivo());
    }
    
    private void siguienteNivel() {
        nivelActual++;
        System.out.println("¡Nivel " + nivelActual + " alcanzado!");

        // Detener y limpiar los hilos
        for (Thread hilo : hilosSombras) {
            hilo.interrupt();
        }
        hilosSombras.clear(); // Limpiar lista de hilos
        sombras.clear();      // Limpiar lista de sombras

        inicializarJuego(getWidth(), getHeight()); // Reinicializar todo
        repaint();
    }
    
    private void reiniciarNivelActual() {
        for (Thread hilo : hilosSombras) {
            hilo.interrupt();
        }
        hilosSombras.clear();
        sombras.clear();

        inicializarJuego(getWidth(), getHeight());
        repaint();
    }

    
    private void mostrarOutro() {
        detenerJuego(); // detiene hilos y sombras

        try {
            Thread.sleep(1000); // pausa breve para transición
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Cierra la ventana actual
        java.awt.Window ventana = SwingUtilities.getWindowAncestor(this);
        if (ventana != null) {
            ventana.dispose();
        }

        // Muestra la ventana del outro
        new Outro();
    }

    
    public void detenerJuego() {
        juegoActivo = false;
        for (Thread hilo : hilosSombras) {
            hilo.interrupt();
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (jugador.estaVivo()) {
            jugador.handleKey(e);
            verificarEstadoJuego();
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            reiniciarNivelActual();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
