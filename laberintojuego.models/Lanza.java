/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import laberintojuego.ui.ReproducirSonido;

/**
 * Clase que representa la herramienta Lanza en el juego
 * Extiende de Herramienta y permite al jugador atacar enemigos a distancia
 * Incluye efectos visuales y de sonido durante su uso
 * 
 * @author Sofía Bermudez
 * @since 07052025
 * @version 1.0.0
 */
public class Lanza extends Herramienta {
    
    /** Rango máximo de ataque en píxeles */
    private int rangoAtaque;
    
    /** Daño que inflige la lanza a los enemigos */
    private int danio;
    
    /**
     * Constructor para crear una nueva lanza asociada a un jugador.
     * 
     * @param jugador Referencia al jugador que poseerá esta lanza
     */
    public Lanza(Jugador jugador) {
        super(jugador.getX(), jugador.getY(), 20, 60, jugador, 3000, "Lanza"); 
        this.rangoAtaque = 50;
        this.danio = 1;
        this.image = new ImageIcon(getClass().getResource("/laberintojuego/images/lanza.png"));
    }
    
    /**
     * Implementación del efecto especial de la lanza.
     * Busca enemigos cercanos y los elimina si están en rango.
     * Reproduce sonidos y otorga puntos por eliminar enemigos.
     */
    @Override
    public void ejecutarEfecto() {
        new Thread(() -> {
            try {
                // Efecto de sonido al lanzar
                ReproducirSonido.reproducirEfecto("/laberintojuego/audios/sonido_lanza.wav");
                System.out.println("¡Lanza activada! Buscando sombras cercanas...");

                // Sincronizar posición con el jugador
                this.x = jugador.getX();
                this.y = jugador.getY();

                if (jugador.getMapaJuego() != null) {
                    // Buscar enemigo más cercano
                    Enemigo sombraCercana = jugador.getMapaJuego().getSombraMasCercana(jugador);

                    if (sombraCercana != null && estaEnRangoDeAtaque(sombraCercana)) {
                        Thread.sleep(200); // Pequeña pausa dramática

                        // Eliminar enemigo
                        sombraCercana.setVivo(false);
                        ReproducirSonido.reproducirEfecto("/laberintojuego/audios/sonido_muerteEnemigo.wav");
                        System.out.println("¡Sombra eliminada con la lanza!");

                        // Recompensa por eliminar enemigo
                        jugador.getMapaJuego().aumentarPuntaje(10);
                        jugador.getMapaJuego().removerSombra(sombraCercana);
                    } else {
                        System.out.println("No hay sombras en rango de ataque.");
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Verifica si un enemigo está dentro del rango de ataque.
     * 
     * @param sombra Enemigo a verificar
     * @return true si el enemigo está en rango, false en caso contrario
     */
    private boolean estaEnRangoDeAtaque(Enemigo sombra) {
        double distancia = Math.sqrt(
            Math.pow(this.x - sombra.getX(), 2) + 
            Math.pow(this.y - sombra.getY(), 2)
        );
        return distancia <= rangoAtaque;
    }
    
    /**
     * Dibuja efectos visuales cuando la lanza está en uso.
     * Muestra un área circular que representa el rango de ataque.
     * 
     * @param g Contexto gráfico donde se dibujará
     */
    @Override
    public void paint(Graphics g) {
        if (enUso) {
            // Dibujar área de efecto semitransparente
            g.setColor(new Color(255, 0, 0, 120)); 
            g.fillOval(x - rangoAtaque/2, y - rangoAtaque/2, rangoAtaque, rangoAtaque);

            // Dibujar cruz indicando el centro
            g.setColor(Color.RED);
            g.drawLine(x, y, x + rangoAtaque/2, y);
            g.drawLine(x, y, x - rangoAtaque/2, y);
            g.drawLine(x, y, x, y + rangoAtaque/2);
            g.drawLine(x, y, x, y - rangoAtaque/2);
        }
    }
}