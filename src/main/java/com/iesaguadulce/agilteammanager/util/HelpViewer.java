package com.iesaguadulce.agilteammanager.util;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelpViewer {
    private static final String nameFileHelp = "/ayuda/centro-ayuda.pdf";

    public void mostrarAyuda() throws IOException {
        InputStream pdfStream = getClass().getResourceAsStream(nameFileHelp);
        if (pdfStream == null) {
            throw new IOException("No se encontró el recurso: " + nameFileHelp);
        }

        File temp = File.createTempFile("centro-ayuda", ".pdf");
        temp.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(temp)) {
            pdfStream.transferTo(out);
        }

        // Abrimos con el visor PDF asociado en Windows (sin AWT)
        // new ProcessBuilder("cmd", "/c", "start", "", temp.getAbsolutePath()).start();
        Desktop.getDesktop().open(temp);
    }
}
