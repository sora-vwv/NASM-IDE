package ide.editor;

import ide.editor.colorizer.Colorize;
import ide.Main;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class Editor extends JTextPane {

    public Colorize colorize;
    public JScrollPane scroll;

    private final JComponent main;

    public static Font font;
    public static float fontSize;
    public static int tabSize;

    public static Editor INSTANCE;

    public Editor() {
        INSTANCE = this;

        setStyledDocument(new DefaultStyledDocument() {
            @Override
            public void insertString(int offset, String string, AttributeSet attr) throws BadLocationException {end: {
                if (string.length() != 1) {
                    super.insertString(offset, string, attr);
                    break end;
                }
                if (string.endsWith("["))
                    super.insertString(offset, string + "]", attr);
                else if (string.endsWith("\""))
                    if (Objects.equals(getText(offset, 1), "\""))
                        super.insertString(offset, "", attr);
                    else
                        super.insertString(offset, string + "\"", attr);
                else if (string.endsWith("'"))
                    if (Objects.equals(getText(offset, 1), "'"))
                        super.insertString(offset, "", attr);
                    else
                        super.insertString(offset, string + "'", attr);
                else if (string.endsWith("]") && Objects.equals(getText(offset, 1), "]"))
                    super.insertString(offset, "", attr);
                else if (string.endsWith("\n")) {
                    int whitespaces = 0;
                    int tabs = 0;
                    int bracket = 0;
                    for (int i = offset-1; i >= 0 && !getText(i, 1).endsWith("\n"); i--) {
                        String c = getText(i, 1);
                        if (c.endsWith("\t")) tabs++;
                        else if (c.endsWith(" ")) whitespaces++;
                        else if (c.endsWith(":")) {
                            bracket++;
                            tabs = 0;
                            whitespaces = 0;
                        } else {
                            tabs = 0;
                            whitespaces = 0;
                        }
                    }
                    tabs += Editor.tabSize == 0 ? 0 : whitespaces/Editor.tabSize;
                    tabs += bracket > 0 ? 1 : 0;
                    string = string+("\t".repeat(tabs));
                    int cursor = offset + string.length();
                    try {
                        if (getText(offset, 1).endsWith(":"))
                            string = string+"\n"+("\t".repeat(Math.max(tabs-1, 0)));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    super.insertString(offset, string, attr);
                    select(cursor, cursor);
                    break end;
                }
                else
                    super.insertString(offset, string, attr);
                select(offset + 1, offset + 1);
            }

                SwingUtilities.invokeLater(colorize::updateTextStylesView);
            }

            @Override
            public void remove(int offset, int length) throws BadLocationException {
                super.remove(offset, length);
                SwingUtilities.invokeLater(colorize::updateTextStylesView);
            }

            @Override
            public void replace(int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
                super.replace(offset, length, text, attributeSet);
                SwingUtilities.invokeLater(colorize::updateTextStylesView);
            }
        });
        colorize = new Colorize(this);

        JPanel noWrapPanel = new JPanel(new GridLayout());
        noWrapPanel.add(this);
        scroll = new JScrollPane(noWrapPanel);
        scroll.setRowHeaderView(new Liner(this));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().addAdjustmentListener(e -> SwingUtilities.invokeLater(colorize::updateTextStylesView));
        this.main = scroll;

        getInputMap().put(KeyStroke.getKeyStroke("control S"), Main.SAVE_ACTION);
        getActionMap().put(Main.SAVE_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        setFont(Editor.font);
        setTabSize(Editor.tabSize);
    }

    public void setFontSize(float size) {
        fontSize = size;
        Editor.font = Editor.font.deriveFont(size);
        setFont(Editor.font);
        setTabs(tabSize);
    }

    public void setFont(Font font) {
        Editor.font = font.deriveFont(fontSize);
        super.setFont(Editor.font);
    }

    public void setTabSize(int size) {
        tabSize = size;
        setTabs(size);
    }

    public void setTabs(int charactersPerTab) {
        FontMetrics fm = getFontMetrics(font);
        int charWidth = fm.charWidth(' ');
        int tabWidth = charWidth * charactersPerTab;

        TabStop[] tabs = new TabStop[5];

        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = getDocument().getLength();
        getStyledDocument().setParagraphAttributes(0, length, attributes, false);
    }

    public Component getComponent() {
        return main;
    }

    public void save() {
        if (Settings.file != null && Settings.file.exists()) {
            try {
                Files.writeString(Settings.file.toPath(), getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Main.INSTANCE, "Файл '" + Settings.file.getName() + "' не сохранен!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilterAsm());
        chooser.setDialogTitle("Сохранить файл");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showSaveDialog(Main.INSTANCE);
        if (result == JFileChooser.APPROVE_OPTION ) {
            if(chooser.getFileFilter() instanceof FileFilterAsm)
                Settings.file = new File(chooser.getSelectedFile().getAbsolutePath()+".asm");
            else
                Settings.file = chooser.getSelectedFile();
            Settings.settings_save(new ide.Settings());

            try {
                if(Settings.file.createNewFile()) {
                    Files.writeString(Settings.file.toPath(), getText());
                    Main.INSTANCE.setName(Settings.file.getName());
                    return;
                }
            } catch (IOException ignored) {
            }
            JOptionPane.showMessageDialog(Main.INSTANCE, "Файл '" + Settings.file.getName() + "' не сохранен!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void load() {
        if (Settings.file != null && Settings.file.exists()) {
            try {
                setText(Files.readString(Settings.file.toPath()));
                Main.INSTANCE.setName(Settings.file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Main.INSTANCE, "Не удалось загрузить файл '" + Settings.file.getName() + "'!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        }
    }

    public void open() {
        JFileChooser chooser = new JFileChooser(Settings.file);
        chooser.setFileFilter(new FileFilterAsm());
        chooser.setDialogTitle("Открыть файл");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(Main.INSTANCE);
        if (result == JFileChooser.APPROVE_OPTION ) {
            Settings.file = chooser.getSelectedFile();
            Settings.settings_save(new ide.Settings());

            try {
                if (Settings.file.exists()) {
                    load();
                    return;
                } else  {
                    if(chooser.getFileFilter() instanceof FileFilterAsm)
                        Settings.file = new File(chooser.getSelectedFile().getAbsolutePath()+".asm");

                    if (Settings.file.createNewFile()) {
                        Files.writeString(Settings.file.toPath(), getText());
                        Main.INSTANCE.setName(Settings.file.getName());
                        return;
                    }
                }
            } catch (IOException ignored) {
            }
            JOptionPane.showMessageDialog(Main.INSTANCE, "Не удалось открыть файл '" + Settings.file.getName() + "'!", "NasmIDE", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class FileFilterAsm extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.getName().endsWith(".asm");
        }

        @Override
        public String getDescription() {
            return "Assembly Source Code (*.asm)";
        }
    }

}