package laberintojuego.ui;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ReproducirSonido {
    private static Clip sonidoAmbiente;
    private static Clip sonidoEfecto;

    public static void reproducirAmbiente(String ruta) {
        detenerAmbiente();
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(ReproducirSonido.class.getResource(ruta));
            sonidoAmbiente = AudioSystem.getClip();
            sonidoAmbiente.open(audioStream);
            sonidoAmbiente.loop(Clip.LOOP_CONTINUOUSLY); // Repite continuamente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void detenerAmbiente() {
        if (sonidoAmbiente != null && sonidoAmbiente.isRunning()) {
            sonidoAmbiente.stop();
            sonidoAmbiente.close();
        }
    }

    public static void reproducirEfecto(String ruta) {
        try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                ReproducirSonido.class.getResource(ruta)
            );
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
        } catch (Exception e) {
            System.out.println("No se pudo reproducir el sonido: " + ruta);
            e.printStackTrace();
        }
    }
}
