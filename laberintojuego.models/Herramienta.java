/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import laberintojuego.elements.Sprite;

/**
 * Clase abstracta que representa una herramienta o habilidad en el juego
 * Extiende de Sprite e implementa Runnable para permitir efectos temporizados
 * Proporciona funcionalidad base como cooldown y gestión de estado de uso
 * 
 * @author Sofía Rudas
 * @since 07052025
 * @version 1.0.0
 */
public abstract class Herramienta extends Sprite implements Runnable {
    
    /** Jugador que posee y utiliza esta herramienta */
    protected Jugador jugador;
    
    /** Tiempo de cooldown en milisegundos entre usos */
    protected long cooldownTime;
    
    /** Marca de tiempo del último uso en milisegundos */
    protected long ultimoUso = 0;
    
    /** Indica si la herramienta está actualmente en uso */
    protected boolean enUso = false;
    
    /** Nombre identificativo de la herramienta */
    protected String nombre;
    
    /**
     * Constructor para crear una nueva herramienta.
     * 
     * @param x Posición inicial en el eje X
     * @param y Posición inicial en el eje Y
     * @param width Ancho de la herramienta
     * @param height Alto de la herramienta
     * @param jugador Referencia al jugador que posee la herramienta
     * @param cooldown Tiempo de espera entre usos en milisegundos
     * @param nombre Nombre identificativo de la herramienta
     */
    public Herramienta(int x, int y, int width, int height, Jugador jugador, long cooldown, String nombre) {
        super(x, y, width, height);
        this.jugador = jugador;
        this.cooldownTime = cooldown;
        this.nombre = nombre;
    }
    
    /**
     * Verifica si la herramienta puede ser usada (cooldown completado).
     * 
     * @return true si la herramienta está disponible para usar, false si está en cooldown
     */
    public boolean puedeUsar() {
        long tiempoActual = System.currentTimeMillis();
        return (tiempoActual - ultimoUso) >= cooldownTime;
    }
    
    /**
     * Intenta usar la herramienta. Si está disponible, inicia su efecto.
     * Si está en cooldown, muestra el tiempo restante.
     */
    public void usar() {
        if (puedeUsar()) {
            ultimoUso = System.currentTimeMillis();
            enUso = true;
            Thread hiloHerramienta = new Thread(this);
            hiloHerramienta.start();
        } else {
            long tiempoRestante = cooldownTime - (System.currentTimeMillis() - ultimoUso);
            System.out.println(nombre + " en cooldown. Espera " + (tiempoRestante/1000) + " segundos.");
        }
    }
    
    /**
     * Método abstracto que define el efecto específico de la herramienta.
     * Debe ser implementado por las clases concretas que hereden de Herramienta.
     */
    public abstract void ejecutarEfecto();
    
    /**
     * Método run del hilo que ejecuta el efecto de la herramienta.
     * Se ejecuta automáticamente al llamar a usar() cuando está disponible.
     */
    @Override
    public void run() {
        ejecutarEfecto();
        enUso = false;
    }
    
    /**
     * Indica si la herramienta está actualmente en uso.
     * 
     * @return true si la herramienta está activa y en uso, false en caso contrario
     */
    public boolean isEnUso() {
        return enUso;
    }
    
    /**
     * Calcula el tiempo restante para que la herramienta esté disponible.
     * 
     * @return Tiempo restante de cooldown en milisegundos (0 si está disponible)
     */
    public long getTiempoRestanteCooldown() {
        long tiempoTranscurrido = System.currentTimeMillis() - ultimoUso;
        return Math.max(0, cooldownTime - tiempoTranscurrido);
    }
}
