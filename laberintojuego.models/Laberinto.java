/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package laberintojuego.models;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.List;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.ImageIcon;
import laberintojuego.ui.ReproducirSonido;

/**
 * Clase que representa el laberinto del juego, incluyendo su estructura
 * objetos especiales, enemigos y lógica de generación
 * Maneja la representación visual y las reglas de navegación del laberinto
 * 
 * @author Sofía Rudas
 * @since 07052025
 * @version 1.0.0
 */

public class Laberinto {
    private int[][] matriz; 
    private int width, height;
    private int cellSize = 20;
    private ArrayList<Enemigo> enemigos;
    private Objeto objetoEspecial; 
    private int nivelActual = 1;
    private ImageIcon imagenPared;
    private ImageIcon fondo;
    private ImageIcon imagenSalida;
    private final String archivoPuntaje = "C:/Users/sofia/OneDrive/Desktop/ProyectoFinal/puntaje_maximo.txt";
    private int puntaje = 0;
    private int puntajeMaximo = 0;

    /**
     * Constructor que crea un nuevo laberinto según el nivel especificado.
     * 
     * @param width Ancho del laberinto en píxeles
     * @param height Alto del laberinto en píxeles
     * @param nivel Nivel de dificultad del laberinto (1-3)
     */
    
    public Laberinto(int width, int height, int nivel) {
        this.width = width;
        this.height = height;
        this.nivelActual = nivel;
        
        ReproducirSonido.detenerAmbiente();
        
        // Configuración visual y de audio según el nivel
        switch (nivel) {
            case 1:
                fondo = new ImageIcon(getClass().getResource("/laberintojuego/images/fondo.png"));
                ReproducirSonido.reproducirAmbiente("/laberintojuego/audios/sonido_bosque.wav");
                imagenPared = new ImageIcon(getClass().getResource("/laberintojuego/images/Arbusto.png"));
                imagenSalida = new ImageIcon(getClass().getResource("/laberintojuego/images/portal_bosque.png"));
                break;
            case 2:
                fondo = new ImageIcon(getClass().getResource("/laberintojuego/images/castillo.png"));
                ReproducirSonido.reproducirAmbiente("/laberintojuego/audios/sonido_nivel2.wav");
                imagenPared = new ImageIcon(getClass().getResource("/laberintojuego/images/Antorcha.png"));
                imagenSalida = new ImageIcon(getClass().getResource("/laberintojuego/images/portal_castillo.png"));
                break;
            case 3:
                fondo = new ImageIcon(getClass().getResource("/laberintojuego/images/fondo_final.png"));
                ReproducirSonido.reproducirAmbiente("/laberintojuego/audios/sonido_nivel3.wav");
                imagenPared = new ImageIcon(getClass().getResource("/laberintojuego/images/radioactivo.png"));
                imagenSalida = new ImageIcon(getClass().getResource("/laberintojuego/images/portal_final.png"));
                break;
        }

        
        this.matriz = new int[height/cellSize][width/cellSize];
        this.enemigos = new ArrayList<>();
        
        generarLaberinto(nivel);
    }
    
    /**
     * Genera aleatoriamente la estructura del laberinto asegurando que haya un camino válido.
     * 
     * @param nivel Nivel de dificultad que afecta la densidad de paredes
     */
    
    private void generarLaberinto(int nivel) {
        Random rand = new Random();
        do {
            // Generación de paredes aleatorias con probabilidad basada en el nivel
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    matriz[i][j] = rand.nextDouble() < (0.3 + nivel * 0.05) ? 1 : 0;
                }
            }

            // Asegurar caminos libres cada dos celdas
            for (int i = 1; i < matriz.length - 1; i += 2) {
                for (int j = 1; j < matriz[i].length - 1; j += 2) {
                    matriz[i][j] = 0;
                }
            }

            // Posición de la salida
            matriz[matriz.length - 2][matriz[0].length - 2] = 3;

            // Asegurar zona inicial libre
            int startX = 1;
            int startY = 1;
            matriz[startY][startX] = 0;
            if (startY + 1 < matriz.length) matriz[startY + 1][startX] = 0;
            if (startX + 1 < matriz[0].length) matriz[startY][startX + 1] = 0;
            if (startY + 1 < matriz.length && startX + 1 < matriz[0].length)
                matriz[startY + 1][startX + 1] = 0;

        } while (!hayCaminoSalida()); // Repetir hasta que haya camino a la salida

        // Posición del objeto especial (en celda caminable)
        int objX, objY;
        do {
            objX = rand.nextInt(width - 100) + 50;
            objY = rand.nextInt(height - 100) + 50;
        } while (!isWalkable(objX, objY));

        objetoEspecial = new Objeto(objX, objY, "tesoro", nivel);
    }
    
    /**
     * Verifica si existe un camino desde el inicio hasta la salida usando DFS.
     * 
     * @return true si existe un camino válido, false en caso contrario
     */
    
    private boolean hayCaminoSalida() {
        boolean[][] visitado = new boolean[matriz.length][matriz[0].length];
        return dfs(1, 1, visitado);
    }
    
    /**
     * Implementación recursiva de Depth-First Search para verificar caminos.
     * 
     * @param y Coordenada Y actual
     * @param x Coordenada X actual
     * @param visitado Matriz de celdas visitadas
     * @return true si desde esta posición se puede llegar a la salida
     */
    
    private boolean dfs(int y, int x, boolean[][] visitado) {
        if (x < 0 || y < 0 || y >= matriz.length || x >= matriz[0].length) return false;
        if (matriz[y][x] == 1 || visitado[y][x]) return false;
        if (matriz[y][x] == 3) return true;

        visitado[y][x] = true;

        return dfs(y + 1, x, visitado) || dfs(y - 1, x, visitado) ||
               dfs(y, x + 1, visitado) || dfs(y, x - 1, visitado);
    }
    
    /**
     * Verifica si una posición es transitable (no es pared).
     * 
     * @param x Coordenada X en píxeles
     * @param y Coordenada Y en píxeles
     * @return true si la posición es transitable, false si es pared o está fuera de límites
     */

    public boolean isWalkable(int x, int y) {
        
        int matrixX = x / cellSize;
        int matrixY = y / cellSize;
        
        if (matrixY < 0 || matrixY >= matriz.length || matrixX < 0 || matrixX >= matriz[0].length) {
            return false;
        }
        
        return matriz[matrixY][matrixX] != 1; // No es pared
    }
    
    /**
     * Intenta recoger el objeto especial si el jugador está en su posición.
     * 
     * @param x Posición X del jugador
     * @param y Posición Y del jugador
     * @return true si el objeto fue recogido, false en caso contrario
     */
    
    public boolean verificarObjetoEspecial(int x, int y) {
        if (objetoEspecial != null && !objetoEspecial.isEncontrado()) {
            return objetoEspecial.recoger(x, y, 30, 30); // Asumiendo tamaño del jugador
        }
        return false;
    }
    
    /**
     * Verifica si el objeto especial ya fue encontrado.
     * 
     * @return true si el objeto fue encontrado, false en caso contrario
     */
    
    public boolean isObjetoEncontrado() {
        return objetoEspecial != null && objetoEspecial.isEncontrado();
    }
    
    /**
     * Verifica si el jugador está en la salida y puede completar el nivel.
     * 
     * @param x Posición X del jugador
     * @param y Posición Y del jugador
     * @return true si el jugador puede salir, false en caso contrario
     */
    public boolean verificarSalida(int x, int y) {
        if (!isObjetoEncontrado()) return false;

        // Coordenadas reales de la salida
        int salidaX = (matriz[0].length - 2) * cellSize;
        int salidaY = (matriz.length - 2) * cellSize;

        int margen = 10;

        boolean estaEnSalida = Math.abs(x - salidaX) < margen && Math.abs(y - salidaY) < margen;

        return estaEnSalida;
    }

    // Métodos de acceso básicos
    
    /**
     * Obtiene el ancho del laberinto.
     * 
     * @return Ancho en píxeles
     */
    public int getGameWidth() {
        return width;
    }
    
    /**
     * Obtiene el alto del laberinto.
     * 
     * @return Alto en píxeles
     */
    public int getGameHeight() {
        return height;
    }
    
    /**
     * Activa el efecto de luz que hace huir a los enemigos.
     * 
     * @param jugador Referencia al jugador que activa la luz
     */
    public void activarEfectoLuz(Jugador jugador) {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                enemigo.activarHuida();
            }
        }
    }
    
    /**
     * Desactiva el efecto de luz sobre los enemigos.
     */
    public void desactivarEfectoLuz() {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                enemigo.desactivarHuida();
            }
        }
    }
    
    /**
     * Encuentra el enemigo más cercano al jugador.
     * 
     * @param jugador Referencia al jugador
     * @return El enemigo más cercano o null si no hay enemigos vivos
     */
    public Enemigo getSombraMasCercana(Jugador jugador) {
        Enemigo masCercana = null;
        double distanciaMinima = Double.MAX_VALUE;
        
        for (Enemigo sombra : enemigos) {
            if (sombra.estaVivo()) {
                double distancia = Math.sqrt(
                    Math.pow(jugador.getX() - sombra.getX(), 2) + 
                    Math.pow(jugador.getY() - sombra.getY(), 2)
                );
                
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    masCercana = sombra;
                }
            }
        }
        
        return masCercana;
    }
    
    /**
     * Obtiene el puntaje actual.
     * 
     * @return Puntaje actual
     */
    public int getPuntaje() {
        return puntaje;
    }
    
    /**
     * Aumenta el puntaje del jugador y actualiza el puntaje máximo si es necesario.
     * 
     * @param puntos cantidad de puntos a sumar
     */
    
    public void aumentarPuntaje(int puntos) {
        puntaje += puntos;
        if (puntaje > puntajeMaximo) {
            puntajeMaximo = puntaje;
            guardarPuntajeMaximo();
        }
    }
    
    /**
     * Retorna el puntaje máximo registrado.
     * 
     * @return puntaje máximo
     */
    
    public int getPuntajeMaximo() {
        return puntajeMaximo;
    }
    
    /**
     * Guarda el puntaje máximo actual en un archivo para persistencia.
     */
    
    public void guardarPuntajeMaximo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoPuntaje))) {
            bw.write(String.valueOf(puntajeMaximo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Carga el puntaje máximo desde un archivo, si existe.
     */
    
    public void cargarPuntajeMaximo() {
        File archivo = new File(archivoPuntaje);
        if (!archivo.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                bw.write("0");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine();
            if (linea != null) {
                puntajeMaximo = Integer.parseInt(linea);
            }
        } catch (IOException | NumberFormatException e) {
            puntajeMaximo = 0;
        }
    }
    
    /**
     * Agrega un enemigo al laberinto.
     * 
     * @param sombra Enemigo a agregar
     */
    public void agregarSombra(Enemigo sombra) {
        enemigos.add(sombra);
    }
    
    /**
     * Elimina un enemigo del laberinto.
     * 
     * @param sombra Enemigo a eliminar
     */
    public void removerSombra(Enemigo sombra) {
        enemigos.remove(sombra);
    }
    
    /**
     * Obtiene una copia de la lista de enemigos.
     * 
     * @return Lista de enemigos
     */
    public ArrayList<Enemigo> getSombras() {
        return new ArrayList<>(enemigos);
    }
    
    /**
     * Obtiene el objeto especial del laberinto.
     * 
     * @return Referencia al objeto especial
     */
    public Objeto getObjetoEspecial() {
        return objetoEspecial;
    }
    
    /**
     * Reinicia el objeto especial para el nivel actual.
     */
    public void reiniciarObjeto() {
        if (objetoEspecial != null) {
            objetoEspecial.reiniciar(nivelActual);
        }
    }
    
    /**
     * Dibuja el laberinto completo incluyendo paredes, salida y objeto especial.
     * 
     * @param g Contexto gráfico donde se dibujará
     */
    public void paint(Graphics g) {
        
        if (fondo != null && fondo.getImage() != null) {
            g.drawImage(fondo.getImage(), 0, 0, width, height, null);
        }

        // Dibujar el laberinto
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                int x = j * cellSize;
                int y = i * cellSize;
                
                switch (matriz[i][j]) {
                    case 1: // Pared/Obstáculo
                        if (imagenPared != null && imagenPared.getImage() != null) {
                            g.drawImage(imagenPared.getImage(), x, y, cellSize, cellSize, null);
                        } else {
                            g.setColor(Color.BLACK);
                            g.fillRect(x, y, cellSize, cellSize);
                        }
                        break;
                    case 3: // Salida
                         if (imagenSalida != null && imagenSalida.getImage() != null) {
                            g.drawImage(imagenSalida.getImage(), x, y, 40, 40, null);
                        } else {
                            // fallback, si no hay imagen
                            g.setColor(Color.GREEN);
                            g.fillRect(x, y, cellSize, cellSize);
                            g.setColor(Color.BLACK);
                            g.drawRect(x, y, cellSize, cellSize);
                        }
                        break;
                    default: // Camino libre
                        break;
                }
            }
        }
        
        // Dibujar objeto especial si no ha sido encontrado
        if (objetoEspecial != null) {
            objetoEspecial.paint(g);
        }
    }
}
