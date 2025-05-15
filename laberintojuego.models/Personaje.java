/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import laberintojuego.elements.GraphicContainer;
import laberintojuego.elements.Sprite;
import laberintojuego.elements.SpriteContainer;

/**
 * Clase abstracta que representa un personaje en el juego del laberinto
 * Proporciona la funcionalidad base para movimiento, colisiones y estado vital
 * Debe ser extendida por clases concretas que implementen comportamientos específicos
 * 
 * @author Sofía Rudas
 * @since 07052025
 * @version 1.0.0
 */
public abstract class Personaje extends Sprite {
    
    /** Indica si el personaje está vivo o no */
    protected boolean vivo = true; 
    
    /** Referencia al laberinto donde se mueve el personaje */
    protected Laberinto mapaJuego; 
    
    /** Tamaño del paso/pixelaje de movimiento del personaje */
    protected int paso;
    
    /** Contenedor gráfico asociado para actualizaciones visuales */
    protected GraphicContainer gameContainer;
    
    /**
     * Constructor base para crear un personaje.
     * 
     * @param x Posición inicial en el eje X
     * @param y Posición inicial en el eje Y
     * @param height Alto del personaje
     * @param width Ancho del personaje
     * @param mapa Referencia al laberinto donde se moverá
     * @param gc Contenedor gráfico para actualizaciones
     */
    public Personaje(int x, int y, int height, int width, Laberinto mapa, GraphicContainer gc) {
        super(x, y, height, width);
        this.mapaJuego = mapa;
        this.gameContainer = gc;
    }
    
    /**
     * Verifica el estado vital del personaje.
     * 
     * @return true si el personaje está vivo, false si está muerto
     */
    public boolean estaVivo() {
        return vivo;
    }
    
    /**
     * Establece el estado vital del personaje.
     * 
     * @param vivo Nuevo estado vital (true = vivo, false = muerto)
     */
    public void setVivo(boolean vivo) { 
        this.vivo = vivo;
    }
    
    /**
     * Método abstracto para actualizar el estado del personaje.
     * Debe ser implementado por las clases concretas.
     */
    public abstract void actualizar();
    
    /**
     * Verifica si una posición está fuera de los límites del juego.
     * 
     * @param nx Nueva posición X a verificar
     * @param ny Nueva posición Y a verificar
     * @return true si la posición está fuera de límites, false si está dentro
     */
    protected boolean fueraLimitesJuego(int nx, int ny) {
        return nx < 0 || ny < 0 || 
               (nx + width) > mapaJuego.getGameWidth() || 
               (ny + height) > mapaJuego.getGameHeight();
    }
    
    /**
     * Verifica si una posición es transitable en el laberinto.
     * 
     * @param nx Nueva posición X a verificar
     * @param ny Nueva posición Y a verificar
     * @return true si la posición es caminable, false si es un obstáculo
     */
    protected boolean esPosicionCaminable(int nx, int ny) {
        return mapaJuego.isWalkable(nx, ny); 
    }
    
    /**
     * Intenta mover el personaje a una nueva posición, verificando límites y obstáculos.
     * 
     * @param newX Nueva posición X deseada
     * @param newY Nueva posición Y deseada
     * @return true si el movimiento fue exitoso, false si no se pudo mover
     */
    protected boolean intentarMover(int newX, int newY) {
        if (!fueraLimitesJuego(newX, newY) && esPosicionCaminable(newX, newY)) {
            this.x = newX;
            this.y = newY;
            if (gameContainer != null) {
                gameContainer.refresh();
            }
            return true;
        }
        return false;
    }
    
    /**
     * Calcula la distancia euclidiana entre dos puntos.
     * 
     * @param x1 Coordenada X del primer punto
     * @param y1 Coordenada Y del primer punto
     * @param x2 Coordenada X del segundo punto
     * @param y2 Coordenada Y del segundo punto
     * @return Distancia entre los dos puntos
     */
    protected double calcularDistancia(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    /**
     * Verifica colisión entre este personaje y otro.
     * 
     * @param otro Otro personaje con el que verificar colisión
     * @return true si hay superposición (colisión), false en caso contrario
     */
    protected boolean colisionaCon(Personaje otro) {
        return this.x < otro.x + otro.width &&
               this.x + this.width > otro.x &&
               this.y < otro.y + otro.height &&
               this.y + this.height > otro.y;
    }
    
    // Métodos de acceso (Getters)
    
    /**
     * @return Posición X actual del personaje
     */
    public int getX() { return x; }
    
    /**
     * @return Posición Y actual del personaje
     */
    public int getY() { return y; }
    
    /**
     * @return Ancho actual del personaje
     */
    public int getWidth() { return width; }
    
    /**
     * @return Alto actual del personaje
     */
    public int getHeight() { return height; }
    
    // Métodos de modificación (Setters)
    
    /**
     * Establece una nueva posición X para el personaje.
     * 
     * @param x Nueva coordenada horizontal
     */
    public void setX(int x) { this.x = x; }
    
    /**
     * Establece una nueva posición Y para el personaje.
     * 
     * @param y Nueva coordenada vertical
     */
    public void setY(int y) { this.y = y; }
}
