package com.iesaguadulce.agilteammanager.util;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * FotoUtil — Gestión centralizada de fotos de perfil de profesionales.
 *
 * <p>Las fotos se copian a ~/.agilteammanager/avatars/ al seleccionarse.
 * En la BD se guarda únicamente el nombre del fichero (p.ej. "a3f9c1.png"),
 * lo que hace la ruta portable entre equipos.</p>
 *
 * <p>Flujo:</p>
 * <pre>
 *   Usuario elige foto  →  copiarAvatar(File)  →  devuelve "nombre.ext"
 *   BD guarda "nombre.ext"
 *   Al mostrar          →  cargarImagen("nombre.ext")  →  Image lista para ImageView
 * </pre>
 */
public class FotoUtil {

    /** Carpeta de avatares en el directorio home del usuario. */
    private static final Path AVATARS_DIR =
            Paths.get(System.getProperty("user.home"), ".agilteammanager", "avatars");

    // ── No instanciable ──────────────────────────────────────────────────────
    private FotoUtil() { }

    // ────────────────────────────────────────────────────────────────────────
    // API pública
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Copia el fichero de imagen elegido por el usuario a la carpeta de avatares
     * y devuelve el nombre de fichero resultante para guardar en BD.
     *
     * <p>El nombre se genera con un UUID para evitar colisiones, conservando
     * la extensión original (png, jpg…).</p>
     *
     * @param origen  Fichero seleccionado por el usuario
     * @return  Nombre del fichero copiado (solo nombre, sin ruta)
     * @throws IOException  Si no se puede crear la carpeta o copiar el fichero
     */
    public static String copiarAvatar(File origen) throws IOException {
        Files.createDirectories(AVATARS_DIR);

        String extension = extension(origen.getName());
        String nombreDestino = UUID.randomUUID().toString().replace("-", "").substring(0, 12) + extension;
        Path destino = AVATARS_DIR.resolve(nombreDestino);

        Files.copy(origen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return nombreDestino;
    }

    /**
     * Carga como {@link Image} de JavaFX el avatar cuyo nombre está guardado en BD.
     * Devuelve {@code null} si el nombre es nulo/vacío o el fichero no existe.
     *
     * @param nombreFichero  Solo el nombre del fichero (tal como está en BD)
     * @return  Image lista para asignar a un ImageView, o null si no disponible
     */
    public static Image cargarImagen(String nombreFichero) {
        if (nombreFichero == null || nombreFichero.isBlank()) return null;

        Path ruta = AVATARS_DIR.resolve(nombreFichero);
        if (!Files.exists(ruta)) return null;

        try {
            return new Image(ruta.toUri().toString());
        } catch (Exception e) {
            System.err.println("FotoUtil: no se pudo cargar la imagen '" + nombreFichero + "': " + e.getMessage());
            return null;
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Privado
    // ────────────────────────────────────────────────────────────────────────

    /** Extrae la extensión de un nombre de fichero, incluido el punto (p.ej. ".png"). */
    private static String extension(String nombre) {
        int idx = nombre.lastIndexOf('.');
        return (idx >= 0) ? nombre.substring(idx).toLowerCase() : ".png";
    }
}
