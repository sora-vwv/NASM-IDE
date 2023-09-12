package ide.editor.colorizer;

import ide.editor.Editor;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;

public class Colorize {

    private final Editor textPane;
    private final StyledDocument styledDocument;

    public Colorize(Editor textPane) {
        this.textPane = textPane;
        styledDocument = textPane.getStyledDocument();
    }

    public void updateTextStylesView() {
        JViewport viewport = textPane.scroll.getViewport();
        Point startPoint = viewport.getViewPosition();
        Dimension size = viewport.getExtentSize();
        Point endPoint = new Point(startPoint.x + size.width, startPoint.y + size.height);
        int start = textPane.viewToModel2D(startPoint);
        int end = textPane.viewToModel2D(endPoint);
        int length = end - start;
        styledDocument.setCharacterAttributes(start, length, Theme.default_attr, true);

        String text;
        try {
            text = textPane.getText(start, length).replace("\r", "");
        } catch (BadLocationException e) {
            return;
        }

        Matcher matcher = Theme.keyword_regex.matcher(text);
        while (matcher.find())
            styledDocument.setCharacterAttributes(start + matcher.start(), matcher.end() - matcher.start(), Theme.keyword_attr, false);

        matcher = Theme.datatype_regex.matcher(text);
        while (matcher.find())
            styledDocument.setCharacterAttributes(start + matcher.start(), matcher.end() - matcher.start(), Theme.keyword_attr, false);

        matcher = Theme.number_regex.matcher(text);
        while (matcher.find())
            styledDocument.setCharacterAttributes(start + matcher.start(), matcher.end() - matcher.start(), Theme.number_attr, false);

        matcher = Theme.regs_regex.matcher(text);
        while (matcher.find())
            styledDocument.setCharacterAttributes(start + matcher.start(), matcher.end() - matcher.start(), Theme.number_attr, false);

        matcher = Theme.string_regex.matcher(text);
        while (matcher.find())
            styledDocument.setCharacterAttributes(start + matcher.start(), matcher.end() - matcher.start(), Theme.string_attr, false);

        matcher = Theme.comment_regex.matcher(text);
        while (matcher.find())
            styledDocument.setCharacterAttributes(start + matcher.start(), matcher.end() - matcher.start(), Theme.comment_attr, false);
    }

}

