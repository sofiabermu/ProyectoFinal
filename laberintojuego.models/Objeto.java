/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Clase que representa los objetos coleccionables en el juego del laberinto
 * Estos objetos pueden ser recogidos por el jugador y son necesarios para completar niveles
 * La apariencia del objeto varía según el nivel actual del juego
 * 
 * @author Sofía Bermudez
 * @since 07052025
 * @version 1.0.0
 */
public class Objeto {
    /** Posición X del objeto en el laberinto */
    private int x;
    
    /** Posición Y del objeto en el laberinto */
    private int y;
    
    /** Ancho del objeto en píxeles */
    private int width;
    
    /** Alto del objeto en píxeles */
    private int height;
    
    /** Indica si el objeto ha sido encontrado/recogido */
    private boolean encontrado;
    
    /** Tipo de objeto (tesoro, llave, etc.) */
    private String tipo;
    
    /** Imagen que representa visualmente al objeto */
    private Image imagen;

    /**
     * Constructor principal para crear un objeto con dimensiones personalizadas.
     * 
     * @param x Posición horizontal inicial
     * @param y Posición vertical inicial
     * @param width Ancho del objeto
     * @param height Alto del objeto
     * @param tipo Tipo/categoría del objeto
     * @param nivel Nivel actual del juego (afecta la apariencia visual)
     */
    public Objeto(int x, int y, int width, int height, String tipo, int nivel) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tipo = tipo;
        this.encontrado = false;
        actualizarImagen(nivel);
    }

    /**
     * Constructor alternativo que crea un objeto con dimensiones predeterminadas (30x30).
     * 
     * @param x Posición horizontal inicial
     * @param y Posición vertical inicial
     * @param tipo Tipo/categoría del objeto
     * @param nivel Nivel actual del juego
     */
    public Objeto(int x, int y, String tipo, int nivel) {
        this(x, y, 30, 30, tipo, nivel);
    }
    
    /**
     * Actualiza la imagen del objeto según el nivel actual del juego.
     * 
     * @param nivel Nivel actual que determina la apariencia visual
     */
    public void actualizarImagen(int nivel) {
        String rutaImagen = "";
        switch (nivel) {
            case 1:
                rutaImagen = "/laberintojuego/images/flor.png";
                break;
            case 2:
                rutaImagen = "/laberintojuego/images/llave.png";
                break;
            case 3:
                rutaImagen = "/laberintojuego/images/estrella.png";
                break;
        }
        ImageIcon icono = new ImageIcon(getClass().getResource(rutaImagen));
        this.imagen = icono.getImage();
    }
   
    // Métodos de acceso (Getters y Setters)
    
    /**
     * @return Posición X actual del objeto
     */
    public int getX() { return x; }
    
    /**
     * @return Posición Y actual del objeto
     */
    public int getY() { return y; }
    
    /**
     * @return Ancho actual del objeto
     */
    public int getWidth() { return width; }
    
    /**
     * @return Alto actual del objeto
     */
    public int getHeight() { return height; }
    
    /**
     * @return true si el objeto ha sido recogido, false en caso contrario
     */
    public boolean isEncontrado() { return encontrado; }
    
    /**
     * @return Tipo/categoría del objeto
     */
    public String getTipo() { return tipo; }
    
    /**
     * Establece una nueva posición X para el objeto.
     * @param x Nueva coordenada horizontal
     */
    public void setX(int x) { this.x = x; }
    
    /**
     * Establece una nueva posición Y para el objeto.
     * @param y Nueva coordenada vertical
     */
    public void setY(int y) { this.y = y; }
    
    /**
     * Cambia el estado de encontrado del objeto.
     * @param encontrado Nuevo estado (true = recogido, false = no recogido)
     */
    public void setEncontrado(boolean encontrado) { this.encontrado = encontrado; }
    
    /**
     * Verifica si el objeto colisiona con un rectángulo dado (generalmente el jugador).
     * 
     * @param jugadorX Posición X del rectángulo a verificar
     * @param jugadorY Posición Y del rectángulo a verificar
     * @param jugadorWidth Ancho del rectángulo a verificar
     * @param jugadorHeight Alto del rectángulo a verificar
     * @return true si hay colisión, false en caso contrario
     */
    public boolean colisionaCon(int jugadorX, int jugadorY, int jugadorWidth, int jugadorHeight) {
        if (encontrado) return false;
        
        return jugadorX < x + width &&
               jugadorX + jugadorWidth > x &&
               jugadorY < y + height &&
               jugadorY + jugadorHeight > y;
    }
    
    /**
     * Intenta recoger el objeto verificando colisión con el jugador.
     * 
     * @param jugadorX Posición X del jugador
     * @param jugadorY Posición Y del jugador
     * @param jugadorWidth Ancho del jugador
     * @param jugadorHeight Alto del jugador
     * @return true si el objeto fue recogido, false en caso contrario
     */
    public boolean recoger(int jugadorX, int jugadorY, int jugadorWidth, int jugadorHeight) {
        if (!encontrado && colisionaCon(jugadorX, jugadorY, jugadorWidth, jugadorHeight)) {
            encontrado = true;
            return true;
        }
        return false;
    }
    
    /**
     * Dibuja el objeto en el contexto gráfico proporcionado.
     * Solo se dibuja si no ha sido recogido.
     * 
     * @param g Contexto gráfico donde se dibujará
     */
    public void paint(Graphics g) {
        if (!encontrado && imagen != null) {
            g.drawImage(imagen, x, y, width, height, null);
        }
    }

    /**
     * Reinicia el estado del objeto para poder ser recogido nuevamente.
     * Actualiza su imagen según el nivel proporcionado.
     * 
     * @param nivel Nivel actual del juego para cargar la imagen adecuada
     */
    public void reiniciar(int nivel) {
        encontrado = false;
        actualizarImagen(nivel);
    }
}
