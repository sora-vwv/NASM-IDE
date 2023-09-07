package ide;

import ide.editor.Editor;

import javax.swing.*;
import java.awt.*;

public class Settings extends JFrame {

    public Settings() {
        super("Settings");

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(300, 400);
        setResizable(false);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        ImageIcon icon = new ImageIcon("resources/icon.png");
        setIconImage(icon.getImage());

        Box root = Box.createVerticalBox();
        add(root);
        Box box = Box.createVerticalBox();
        box.setMaximumSize(new Dimension(300, 370));
        box.setBorder (BorderFactory.createEmptyBorder(12,12,12,12));
        root.add(box);
        {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.setMaximumSize(new Dimension(300, 30));
            box.add(panel);

            JLabel $label = new JLabel("color theme");
            panel.add($label);

            String[] items = new String[] {
                    "Dark",
                    "Darcula",
                    "Solarized Dark",
                    "Contrast Dark",
                    "Solarized Light",
                    "Contrast Light",
                    "IntelliJ"
            };
            JComboBox<String> $combo = new JComboBox<>(items);
            $combo.setSelectedItem(ide.ide.Settings.theme);
            $combo.addActionListener(e -> ide.ide.Settings.setTheme((String) $combo.getSelectedItem()));
            panel.add($combo);
        }
        JTextField font_editor;
        {
            {
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.setMaximumSize(new Dimension(300, 30));
                box.add(panel);

                JLabel label = new JLabel("Editor");
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                panel.add(label);
            }
            {
                JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.setMaximumSize(new Dimension(300, 30));
                box.add(panel);

                JLabel $label = new JLabel("font size");
                panel.add($label);

                Integer[] items = {14, 16, 18, 20, 22, 24, 26, 28, 30};
                JComboBox<Integer> $combo = new JComboBox<>(items);
                $combo.setSelectedItem((int)Editor.fontSize);
                $combo.addActionListener(e -> {
                    Editor.INSTANCE.setFontSize(items[$combo.getSelectedIndex()]);
                    Editor.fontSize = items[$combo.getSelectedIndex()];
                });
                panel.add($combo);
            }
            {
                JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.setMaximumSize(new Dimension(300, 30));
                box.add(panel);

                JLabel $label = new JLabel("tab size");
                panel.add($label);

                Integer[] items = {2, 4, 6, 8};
                JComboBox<Integer> $combo = new JComboBox<>(items);
                $combo.setSelectedItem(Editor.tabSize);
                $combo.addActionListener(e -> {
                    Editor.INSTANCE.setTabSize(items[$combo.getSelectedIndex()]);
                    Editor.tabSize = items[$combo.getSelectedIndex()];
                });
                panel.add($combo);
            }
            {
                JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.setMaximumSize(new Dimension(300, 30));
                box.add(panel);

                JLabel $label = new JLabel("font");
                panel.add($label);

                font_editor = new JTextField(ide.ide.Settings.fontEditor);
                panel.add(font_editor);
            }
        }
        {
            JPanel name = new JPanel(new GridLayout(0, 2));
            name.setBorder (BorderFactory.createEmptyBorder(12,12,12,12));
            name.setMaximumSize(new Dimension(300, 54));
            root.add(name);

            JLabel cancel = new JLabel("");
            cancel.setMaximumSize(new Dimension(80, 30));
            name.add(cancel);

            JButton ok = new JButton("Save");
            ok.setMaximumSize(new Dimension(80, 30));
            ok.addActionListener(e -> {
                ide.ide.Settings.fontEditor = font_editor.getText();
                Editor.INSTANCE.setFont(ide.ide.Settings.loadFont(font_editor.getText(), Editor.font));
                ide.ide.Settings.settings_save(this);
            });
            name.add(ok);
        }
        setVisible(false);
    }

}
																																																																																										