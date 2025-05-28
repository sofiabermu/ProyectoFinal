/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import laberintojuego.elements.GraphicContainer;

/**
 * Clase que representa al jugador principal en el juego del laberinto
 * Extiende de Personaje y gestiona el movimiento, herramientas y habilidades del jugador
 * Controla las interacciones con el teclado y el estado de las herramientas disponibles
 * 
 * @author Sofía Bermudez
 * @since 07052025
 * @version 1.0.0
 */
public class Jugador extends Personaje {
    
    /** Tamaño del paso de movimiento del jugador en píxeles */
    public static final int STEP = 10;
    
    /** Herramienta de luz que posee el jugador */
    private Luz herramientaLuz;
    
    /** Herramienta de lanza que posee el jugador */
    private Lanza herramientaLanza;
    
    /** Indica si el jugador tiene disponible la herramienta de luz */
    private boolean tieneLuz = true;
    
    /** Indica si el jugador tiene disponible la herramienta de lanza */
    private boolean tieneLanza = true;
    
    /**
     * Constructor para crear un nuevo jugador.
     * 
     * @param x Posición inicial en el eje X
     * @param y Posición inicial en el eje Y
     * @param height Alto del jugador
     * @param width Ancho del jugador
     * @param mapa Referencia al laberinto donde se mueve el jugador
     * @param gc Contenedor gráfico donde se dibuja el jugador
     */
    public Jugador(int x, int y, int height, int width, Laberinto mapa, GraphicContainer gc) {
        super(x, y, height, width, mapa, gc);
        this.image = new ImageIcon(getClass().getResource("/laberintojuego/images/explorador.png"));
        this.paso = STEP;
        
        // Inicializar herramientas
        this.herramientaLuz = new Luz(this);
        this.herramientaLanza = new Lanza(this);
    }

    /**
     * Maneja los eventos de teclado para controlar al jugador.
     * 
     * @param e Evento de teclado que activó la acción
     */
    public void handleKey(KeyEvent e) {
        int newX = x;
        int newY = y;
        
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                newY = y - STEP;
                break;
                
            case KeyEvent.VK_DOWN:
                newY = y + STEP;
                break;
                
            case KeyEvent.VK_LEFT:
                newX = x - STEP;
                break;
                
            case KeyEvent.VK_RIGHT:
                newX = x + STEP;
                break;
                
            case KeyEvent.VK_SPACE: 
                if (tieneLuz) {
                    herramientaLuz.usar();
                }
                break;
                
            case KeyEvent.VK_L: 
                if (tieneLanza) {
                    herramientaLanza.usar();
                }
                break;
        }

        if (newX != x || newY != y) {
            intentarMover(newX, newY);
        }
    }
    
    /**
     * Obtiene el mapa del juego asociado al jugador.
     * 
     * @return Referencia al laberinto donde se encuentra el jugador
     */
    public Laberinto getMapaJuego() {
        return mapaJuego;
    }
    
    /**
     * Verifica si la herramienta de luz está actualmente activa.
     * 
     * @return true si la luz está siendo usada, false en caso contrario
     */
    public boolean isLuzActiva() {
        return herramientaLuz.isEnUso();
    }

    /**
     * Dibuja al jugador y sus herramientas activas en el contenedor gráfico.
     * También muestra los cooldowns de las herramientas si están en espera.
     * 
     * @param g Objeto Graphics donde se realizará el dibujo
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(image.getImage(), x, y, width, height, null);

        if (herramientaLuz.isEnUso()) {
            herramientaLuz.paint(g);
        }
        if (herramientaLanza.isEnUso()) {
            herramientaLanza.paint(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        
        long cooldownLuz = herramientaLuz.getTiempoRestanteCooldown();
        if (cooldownLuz > 0) {
            g.drawString("Luz: " + (cooldownLuz/1000) + "s", x, y - 20);
        }
        
        long cooldownLanza = herramientaLanza.getTiempoRestanteCooldown();
        if (cooldownLanza > 0) {
            g.drawString("Lanza: " + (cooldownLanza/1000) + "s", x, y - 5);
        }
    }

    /**
     * Método de actualización del jugador (actualmente sin implementación específica).
     */
    @Override
    public void actualizar() {
        // Implementación vacía - para posible uso futuro
    }
}
