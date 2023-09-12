package ide;

import ide.editor.Editor;

import javax.swing.*;

import java.awt.*;

import static ide.editor.Settings.loadFont;

public class Help extends JFrame {

    private static final String value = """
                  Библиотека макросов "io.inc":
                    PRINT_UDEC size, data
                    PRINT_DEC size, data
                    PRINT_HEX size, data
                    PRINT_CHAR ch
                    PRINT_STRING data
                    NEWLINE
                    GET_UDEC size, data
                    GET_DEC size, data
                    GET_HEX size, data
                    GET_CHAR data
                    GET_STRING data, maxsz
                  
                  Функции ввода-вывода:
                    io_get_dec выход: (eax: число)
                    io_get_udec выход: (eax: число)
                    io_get_hex выход: (eax: число)
                    io_get_char выход: (eax: символ)
                    io_get_string вход (eax: адрес, edx: число)
                    io_print_dec вход: (eax: число)
                    io_print_udec вход: (eax: число)
                    io_print_hex вход: (eax: число)
                    io_print_char вход: (eax: символ)
                    io_print_string вход: (eax: адрес)
                    io_newline
                  """;

    public Help() {
        super("Help");

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(600, 400);

        ImageIcon icon = new ImageIcon("resources/icon.png");
        setIconImage(icon.getImage());

        Box root = Box.createVerticalBox();
        add(root);
        JTextPane label = new JTextPane();
        label.setText(value);
        label.setFont(Editor.font);
        label.setEditable(false);

        JPanel noWrapPanel = new JPanel(new GridLayout());
        noWrapPanel.add(label);
        JScrollPane scroll = new JScrollPane(noWrapPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        root.add(scroll);

        setVisible(true);
    }

}
