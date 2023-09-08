package ide;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import ide.editor.Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static javax.swing.KeyStroke.getKeyStroke;

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

        ide.editor.Settings.settings_init();

        JRootPane rootPane = getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_F5, 0, true), "BUILD&RUN");
        rootPane.getActionMap().put("BUILD&RUN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Builder.buildRun();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_F5, InputEvent.SHIFT_DOWN_MASK, true), "BUILD");
        rootPane.getActionMap().put("BUILD", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Builder.build();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK, true), "RUN");
        rootPane.getActionMap().put("RUN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Builder.run();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK, true), "OPEN");
        rootPane.getActionMap().put("OPEN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Editor.INSTANCE.open();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK, true), "OPEN_E");
        rootPane.getActionMap().put("OPEN_E", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported() && ide.editor.Settings.file != null) {
                    try {
                        Desktop.getDesktop().open(ide.editor.Settings.file.getParentFile());
                        return;
                    } catch (IOException ignored) {
                    }
                }
                JOptionPane.showMessageDialog(Main.INSTANCE, "Failed to open Explorer!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK, true), "QUIT");
        rootPane.getActionMap().put("QUIT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK, true), "SETTINGS");
        rootPane.getActionMap().put("SETTINGS", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Settings().setVisible(true);
            }
        });

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
            JMenuItem $open = new JMenuItem("(Ctrl O)  Open");
            $open.addActionListener(e -> Editor.INSTANCE.open());
            file.add($open);
        }
        {
            JMenuItem $save = new JMenuItem("(Ctrl S)  Save");
            $save.addActionListener(e -> Editor.INSTANCE.save());
            file.add($save);
        }
        {
            JMenuItem $open = new JMenuItem("(Ctrl E)  Open in Explorer     ");
            $open.addActionListener(e -> {
                if (Desktop.isDesktopSupported() && ide.editor.Settings.file != null) {
                    try {
                        Desktop.getDesktop().open(ide.editor.Settings.file.getParentFile());
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
            JMenuItem $settings = new JMenuItem("(Ctrl T)  Settings");
            $settings.addActionListener(e -> new Settings().setVisible(true));
            file.add($settings);
        }
        {
            JMenuItem $exit = new JMenuItem("(Ctrl Q)  Exit");
            $exit.addActionListener(e -> System.exit(0));
            file.add($exit);
        }
        return file;
    }

    private static JMenu createBuildMenu() {
        JMenu build = new JMenu("Build");
        {
            JMenuItem $build = new JMenuItem("(F5)  Build & Run    ");
            $build.addActionListener(e -> Builder.buildRun());
            build.add($build);
        }
        build.addSeparator();
        {
            JMenuItem $build = new JMenuItem("(Shift F5)  Build");
            $build.addActionListener(e -> Builder.build());
            build.add($build);
        }
        {
            JMenuItem $run = new JMenuItem("(Ctrl  F5)  Run");
            $run.addActionListener(e -> Builder.run());
            build.add($run);
        }
        return build;
    }

    public static void main(String[] args) {
        new Main();
    }
}