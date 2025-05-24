/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import laberintojuego.elements.GraphicContainer;

/**
 * Clase que representa un enemigo en el juego del laberinto
 * Extiende de Personaje e implementa Runnable para permitir movimiento autónomo
 * El comportamiento del enemigo varía según su nivel de dificultad
 * 
 * @author Sofía Bermudez
 * @since 07052025
 * @version 1.0.0
 */
public class Enemigo extends Personaje implements Runnable {

    /** Jugador al que el enemigo persigue o del que huye */
    private Jugador jugadorObjetivo;
    
    /** Indica si el enemigo está huyendo de una fuente de luz */
    private boolean huyendoDeLuz = false;
    
    /** Velocidad base del enemigo sin modificadores */
    private int velocidadBase;
    
    /** Nivel de dificultad del enemigo (1-3) */
    private int nivelDificultad;
    
    /**
     * Constructor para crear un nuevo enemigo.
     * 
     * @param x Posición inicial en el eje X
     * @param y Posición inicial en el eje Y
     * @param width Ancho del enemigo
     * @param height Alto del enemigo
     * @param jugador Referencia al jugador que será el objetivo
     * @param mapa Referencia al laberinto donde se mueve el enemigo
     * @param gc Contenedor gráfico donde se dibuja el enemigo
     * @param dificultad Nivel de dificultad del enemigo (1-3)
     */
    public Enemigo(int x, int y, int width, int height, Jugador jugador, Laberinto mapa, GraphicContainer gc, int dificultad) {
        super(x, y, width, height, mapa, gc);
        this.jugadorObjetivo = jugador;
        this.nivelDificultad = dificultad;       
        
        switch (nivelDificultad) {
            case 1:
                this.paso = 2;
                this.image = new ImageIcon(getClass().getResource("/laberintojuego/images/sombra.png"));
                break;
            case 2:
                this.paso = 3;
                this.image = new ImageIcon(getClass().getResource("/laberintojuego/images/monstruo_castillo.png"));
                break;
            case 3:
                this.paso = 4;
                this.image = new ImageIcon(getClass().getResource("/laberintojuego/images/final_boss.png"));
                break;
        }
        
        this.velocidadBase = paso;
    }
    
    /**
     * Método principal del hilo que controla el movimiento del enemigo.
     * Actualiza la posición del enemigo periódicamente según su velocidad.
     */
    public void run() {
        while (vivo && !Thread.currentThread().isInterrupted()) {
            actualizar();

            try {
                int sleepTime;
                switch (nivelDificultad) {
                    case 1: sleepTime = 100; break;
                    case 2: sleepTime = 80; break;
                    case 3: sleepTime = 60; break;
                    default: sleepTime = 100;
                }
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Actualiza el estado del enemigo en cada ciclo del juego.
     * Decide si perseguir o huir del jugador según el estado actual.
     */
    @Override
    public void actualizar() {
        if (jugadorObjetivo != null && jugadorObjetivo.estaVivo()) {
            if (huyendoDeLuz) {
                huirDelJugador(jugadorObjetivo);
            } else {
                moveTowards(jugadorObjetivo);
                killIfReached(jugadorObjetivo);
            }
        }
    }
    
    /**
     * Activa el modo de huida del enemigo.
     * Aumenta la velocidad del enemigo cuando huye de la luz.
     */
    public void activarHuida() {
        huyendoDeLuz = true;
        this.paso = velocidadBase + 1; // Se mueve más rápido al huir
    }
    
    /**
     * Desactiva el modo de huida del enemigo.
     * Restaura la velocidad normal del enemigo.
     */
    public void desactivarHuida() {
        huyendoDeLuz = false;
        this.paso = velocidadBase;
    }

    /**
     * Mueve al enemigo en dirección opuesta al jugador.
     * 
     * @param target Jugador del que el enemigo está huyendo
     */
    private void huirDelJugador(Jugador target) {
        int nextX = x;
        int nextY = y;
        
        // Movimiento opuesto al jugador
        if (target.getX() > x) nextX = x - paso;
        else if (target.getX() < x) nextX = x + paso;
        
        if (target.getY() > y) nextY = y - paso;
        else if (target.getY() < y) nextY = y + paso;
        
        // Intentar moverse alejándose
        if (!intentarMover(nextX, nextY)) {
            // Si no puede moverse en diagonal, intentar solo horizontal o vertical
            if (!intentarMover(nextX, y)) {
                intentarMover(x, nextY);
            }
        }
    }

    /**
     * Mueve al enemigo hacia el jugador objetivo.
     * 
     * @param target Jugador al que el enemigo está persiguiendo
     */
    private void moveTowards(Jugador target) {
        int dx = target.getX() - x;
        int dy = target.getY() - y;

        int nextX = x;
        int nextY = y;

        if (Math.abs(dx) > Math.abs(dy)) {
            nextX += (dx > 0) ? paso : -paso;
            if (!intentarMover(nextX, y)) {
                nextY += (dy > 0) ? paso : -paso;
                intentarMover(x, nextY);
            }
        } else {
            nextY += (dy > 0) ? paso : -paso;
            if (!intentarMover(x, nextY)) {
                nextX += (dx > 0) ? paso : -paso;
                intentarMover(nextX, y);
            }
        }
    }

    /**
     * Mata al jugador si el enemigo ha alcanzado su posición.
     * 
     * @param target Jugador que puede ser eliminado
     */
    private void killIfReached(Jugador target) {
        if (this.checkCollision(target)) {
            System.out.println("¡El jugador ha sido capturado por un enemigo!");
            target.setVivo(false);
        }
    }

    /**
     * Dibuja al enemigo en el contenedor gráfico.
     * 
     * @param g Objeto Graphics donde se dibujará el enemigo
     */
    @Override
    public void paint(Graphics g) {
        if (image != null && image.getImage() != null) {
            g.drawImage(image.getImage(), x, y, width, height, null);
        }
    }
}
