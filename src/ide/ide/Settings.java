package ide.ide;

import ide.editor.Editor;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

public class Settings {

    public static final String settingsPath = "resources/settings.cfg";
    public static String fontEditor = "";
    public static File file;

    public static String theme = "";
    public static HashMap<String, Theme> themes = new HashMap<>(){{
        put("Darcula", new DarculaTheme());
        put("Solarized Dark", new SolarizedDarkTheme());
        put("Contrast Dark", new HighContrastDarkTheme());
        put("Dark", new OneDarkTheme());
        put("Solarized Light", new SolarizedLightTheme());
        put("Contrast Light", new HighContrastLightTheme());
        put("IntelliJ", new IntelliJTheme());
    }};

    public static void settings_init() {
        try {
            FileInputStream propsInput = new FileInputStream(settingsPath);
            Properties prop = new Properties();
            prop.load(propsInput);

            Font font = new Font(Font.MONOSPACED, Font.BOLD, 18);
            {
                fontEditor = prop.getOrDefault("font", "JetBrainsMono-Regular.ttf").toString();
                Editor.font = loadFont(fontEditor, font);
                Editor.fontSize = getInt(prop.get("font_size"), 18);
                Editor.tabSize = getInt(prop.get("tab_size"), 4);
                Editor.INSTANCE.setFont(Editor.font);
                Editor.INSTANCE.setFontSize(Editor.fontSize);
                Editor.INSTANCE.setTabSize(Editor.tabSize);
            }
            theme = prop.getOrDefault("theme", "Dark").toString();
            LafManager.install(themes.get(theme));

            String project = prop.getOrDefault("last", "").toString();
            if (!Objects.equals(project, "")) {
                File p = new File(project);
                if (p.exists()) {
                    Settings.file = p;
                    Editor.INSTANCE.load();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(Main.INSTANCE, "Failed to load settings!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            settings_default();
        }
    }

    public static void settings_default() {
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 18);

        fontEditor = "JetBrainsMono-Regular.ttf";

        Editor.font = loadFont(fontEditor, font);
        Editor.fontSize = 18;
        Editor.tabSize = 4;
        Editor.INSTANCE.setFont(Editor.font);
        Editor.INSTANCE.setFontSize(Editor.fontSize);
        Editor.INSTANCE.setTabSize(Editor.tabSize);

        setTheme("Dark");
    }

    public static void settings_save(JFrame frame) {
        try {
            Properties prop = new Properties();

            prop.setProperty("font", fontEditor);
            prop.setProperty("font_size", String.valueOf((int) Editor.fontSize));
            prop.setProperty("tab_size", String.valueOf(Editor.tabSize));

            prop.setProperty("theme", theme);
            if (file != null)
                prop.setProperty("last", file.getAbsolutePath());

            FileOutputStream output = new FileOutputStream(settingsPath);
            prop.store(output, null);
            output.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to save settings.cfg!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void setTheme(String theme) {
        Settings.theme = theme;
        LafManager.install(themes.get(theme));
    }

    public static Font loadFont(String string, Font default_font) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("./resources/fonts/"+string));
        } catch (FontFormatException | IOException e) {
            JOptionPane.showMessageDialog(Main.INSTANCE, "Failed to load font \""+string+"\"!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            return default_font;
        }
    }

    public static int getInt(Object o, int $default) {
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return $default;
        }
    }
}
