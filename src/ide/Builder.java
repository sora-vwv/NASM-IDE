package ide;

import ide.editor.Editor;
import ide.editor.Settings;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

public class Builder {
    public static void build() {
        if (Settings.file == null || !Settings.file.exists()) {
            JOptionPane.showMessageDialog(Main.INSTANCE, "Файл не найден", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Editor.INSTANCE.save();

        try {
            Runtime.getRuntime().exec("cmd /c start build.bat " + Settings.file.getAbsolutePath());
        } catch(IOException ignored) {
        }
    }

    public static void buildRun() {
        if (Settings.file == null || !Settings.file.exists()) {
            JOptionPane.showMessageDialog(Main.INSTANCE, "Файл не найден", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Editor.INSTANCE.save();

        try {
            Runtime.getRuntime().exec("cmd /c start build-run.bat " + Settings.file.getAbsolutePath());
        } catch(IOException ignored) {
        }
    }

    public static void run() {
        if (Settings.file == null || !Settings.file.exists()) {
            JOptionPane.showMessageDialog(Main.INSTANCE, "Файл не найден", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Runtime.getRuntime().exec("cmd /c start run.bat " + Settings.file.getAbsolutePath());
        } catch(IOException ignored) {
        }
    }
}
