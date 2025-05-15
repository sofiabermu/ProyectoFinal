/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.List;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import laberintojuego.ui.ReproducirSonido;

/**
 * Clase que representa la herramienta Luz en el juego
 * Extiende de Herramienta y permite al jugador ahuyentar enemigos temporalmente
 * Crea un área de efecto circular que hace huir a los enemigos dentro de su radio
 * 
 * @author Sofía Rudas
 * @since 07052025
 * @version 1.0.0
 */
public class Luz extends Herramienta {
    
    /** Radio del área de efecto en píxeles */
    private int radioEfecto;
    
    /** Duración del efecto en milisegundos */
    private int duracionEfecto;
    
    /** Lista de enemigos afectados por la luz actualmente */
    private ArrayList<Enemigo> sombrasAfectadas;
    
    /**
     * Constructor para crear una nueva luz asociada a un jugador.
     * 
     * @param jugador Referencia al jugador que poseerá esta herramienta
     */
    public Luz(Jugador jugador) {
        super(jugador.getX(), jugador.getY(), 0, 0, jugador, 5000, "Luz"); // 5 segundos cooldown
        this.radioEfecto = 100;
        this.duracionEfecto = 2000; // 2 segundos de duración
        this.sombrasAfectadas = new ArrayList<>();
        this.image = new ImageIcon(getClass().getResource("/laberintojuego/images/luz.png"));
    }
    
    /**
     * Implementación del efecto especial de la luz.
     * Activa el modo huida en todos los enemigos dentro del radio de efecto
     * durante un tiempo limitado. Incluye efectos de sonido y manejo de
     * interrupciones para limpieza adecuada.
     */
    @Override
    public void ejecutarEfecto() {
        new Thread(() -> {
            try {
                // Efecto de sonido al activar
                ReproducirSonido.reproducirEfecto("/laberintojuego/audios/sonido_luz.wav");
                System.out.println("¡Luz activada! Las sombras huyen temporalmente.");

                // Sincronizar posición con el jugador
                this.x = jugador.getX();
                this.y = jugador.getY();

                // Aplicar efecto a enemigos en rango
                if (jugador.getMapaJuego() != null) {
                    ArrayList<Enemigo> todasLasSombras = jugador.getMapaJuego().getSombras();
                    for (Enemigo sombra : todasLasSombras) {
                        if (sombra.estaVivo() && estaEnRango(sombra)) {
                            sombra.activarHuida();
                            sombrasAfectadas.add(sombra);
                        }
                    }
                }

                // Mantener efecto durante el tiempo establecido
                Thread.sleep(duracionEfecto);

                // Restaurar comportamiento normal de los enemigos
                for (Enemigo sombra : sombrasAfectadas) {
                    if (sombra.estaVivo()) {
                        sombra.desactivarHuida();
                    }
                }

                sombrasAfectadas.clear();
                System.out.println("Efecto de luz terminado.");

            } catch (InterruptedException e) {
                // Limpieza en caso de interrupción
                Thread.currentThread().interrupt();
                for (Enemigo sombra : sombrasAfectadas) {
                    if (sombra.estaVivo()) {
                        sombra.desactivarHuida();
                    }
                }
            }
        }).start();
    }

    /**
     * Verifica si un enemigo está dentro del radio de efecto de la luz.
     * 
     * @param sombra Enemigo a verificar
     * @return true si el enemigo está en rango, false en caso contrario
     */
    private boolean estaEnRango(Enemigo sombra) {
        double distancia = Math.sqrt(
            Math.pow(this.x - sombra.getX(), 2) + 
            Math.pow(this.y - sombra.getY(), 2)
        );
        return distancia <= radioEfecto;
    }
    
    /**
     * Dibuja efectos visuales cuando la luz está activa.
     * Muestra un área circular semitransparente que representa el radio de efecto.
     * 
     * @param g Contexto gráfico donde se dibujará
     */
    @Override
    public void paint(Graphics g) {
        if (enUso) {
            // Dibujar área de efecto (amarillo semitransparente)
            g.setColor(new Color(255, 255, 0, 80));
            g.fillOval(x - radioEfecto/2, y - radioEfecto/2, radioEfecto, radioEfecto);
            
            // Dibujar borde del área (amarillo menos transparente)
            g.setColor(new Color(255, 255, 0, 150));
            g.drawOval(x - radioEfecto/2, y - radioEfecto/2, radioEfecto, radioEfecto);
        }
    }
}

