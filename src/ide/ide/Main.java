package ide.ide;

import ide.Settings;
import ide.editor.Editor;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.OneDarkTheme;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {

    public static final String SAVE_ACTION = "Save";

    public static Main INSTANCE;

    public Main() {
        super("NasmIDE");
        INSTANCE = this;
        LafManager.install(new OneDarkTheme());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        createMenu();
        setSize(600, 400);
        toCenter();

        ImageIcon icon = new ImageIcon("resources/icon.png");
        setIconImage(icon.getImage());

        JPanel panel = new JPanel(new GridLayout());
        add(panel);

        panel.add(new Editor().getComponent());

        ide.ide.Settings.settings_init();

        setVisible(true);
    }

    public void setName(String s) {
        setTitle("NasmIDE - " + s);
    }

    private void toCenter() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createBuildMenu());
        setJMenuBar(menuBar);
    }

    private static JMenu createFileMenu() {
        JMenu file = new JMenu("File");
        {
            JMenuItem $open = new JMenuItem("Open");
            $open.addActionListener(e -> Editor.INSTANCE.open());
            file.add($open);
        }
        {
            JMenuItem $save = new JMenuItem("Save");
            $save.addActionListener(e -> Editor.INSTANCE.save());
            file.add($save);
        }
        {
            JMenuItem $open = new JMenuItem("Open in Explorer     ");
            $open.addActionListener(e -> {
                if (Desktop.isDesktopSupported() && ide.ide.Settings.file != null) {
                    try {
                        Desktop.getDesktop().open(ide.ide.Settings.file.getParentFile());
                        return;
                    } catch (IOException ignored) {
                    }
                }
                JOptionPane.showMessageDialog(Main.INSTANCE, "Failed to open Explorer!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            });
            file.add($open);
        }
        file.addSeparator();
        {
            JMenuItem $settings = new JMenuItem("Settings");
            $settings.addActionListener(e -> new Settings().setVisible(true));
            file.add($settings);
        }
        {
            JMenuItem $exit = new JMenuItem("Exit");
            $exit.addActionListener(e -> System.exit(0));
            file.add($exit);
        }
        return file;
    }

    private static JMenu createBuildMenu() {
        JMenu build = new JMenu("Build");
        {
            JMenuItem $build = new JMenuItem("Build   ");
            $build.addActionListener(e -> {});
            build.add($build);
        }
        build.addSeparator();
        {
            JMenuItem $run = new JMenuItem("Run");
            $run.addActionListener(e -> {});
            build.add($run);
        }
        {
            JMenuItem $stop = new JMenuItem("Stop      ");
            $stop.setEnabled(false);
            build.add($stop);
        }
        return build;
    }

    public static void main(String[] args) {
        new Main();
    }
}