package ide.editor;

import com.github.weisj.darklaf.theme.ColorPalette;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

public class Liner extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener {
    public final static float RIGHT = 1.0f;

    private final static Border OUTER = new MatteBorder(0, 0, 0, 0, Color.GRAY);

    private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

    private final JTextComponent component;

    private final float digitAlignment;
    private final int minimumDisplayDigits;

    private int lastDigits;
    private int lastHeight;
    private int lastLine;

    private HashMap<String, FontMetrics> fonts;

    public Liner(JTextComponent component) {
        this(component, 2);
    }

    public Liner(JTextComponent component, int minimumDisplayDigits) {
        this.component = component;

        setFont(component.getFont());

        Border inner = new EmptyBorder(0, 5, 0, 5);
        setBorder(new CompoundBorder(OUTER, inner));
        lastDigits = 0;
        this.digitAlignment = RIGHT;
        this.minimumDisplayDigits = minimumDisplayDigits;
        setPreferredWidth();

        component.getDocument().addDocumentListener(this);
        component.addCaretListener(this);
        component.addPropertyChangeListener("font", this);
    }

    private void setPreferredWidth() {
        Element root = component.getDocument().getDefaultRootElement();
        int lines = root.getElementCount();
        int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);

        if (lastDigits != digits) {
            lastDigits = digits;
            FontMetrics fontMetrics = getFontMetrics(getFont());
            int width = fontMetrics.charWidth('0') * digits;
            Insets insets = getInsets();
            int preferredWidth = insets.left + insets.right + width;

            Dimension d = getPreferredSize();
            d.setSize(preferredWidth, HEIGHT);
            setPreferredSize(d);
            setSize(d);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

        FontMetrics fontMetrics = component.getFontMetrics(component.getFont());
        Insets insets = getInsets();
        int availableWidth = getSize().width - insets.left - insets.right;

        Rectangle clip = g.getClipBounds();
        int rowStartOffset = component.viewToModel2D(new Point(0, clip.y));
        int endOffset = component.viewToModel2D(new Point(0, clip.y + clip.height));

        while (rowStartOffset <= endOffset) {
            try {
                if (isCurrentLine(rowStartOffset))
                    g.setColor(ColorPalette.GRAY.darker());
                else
                    g.setColor(ColorPalette.GRAY);

                String lineNumber = getTextLineNumber(rowStartOffset);
                int stringWidth = fontMetrics.stringWidth(lineNumber);
                int x = getOffsetX(availableWidth, stringWidth) + insets.left;
                int y = getOffsetY(rowStartOffset, fontMetrics);
                g.setFont(Editor.font);
                g.drawString(lineNumber, x, y);

                rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
            } catch (Exception e) {
                break;
            }
        }
        g.setColor(ColorPalette.GRAY);
        g.drawLine(g.getClipBounds().width-1, 0, g.getClipBounds().width-1, getHeight());
    }

    private boolean isCurrentLine(int rowStartOffset) {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();

        return root.getElementIndex(rowStartOffset) == root.getElementIndex(caretPosition);
    }

    private String getTextLineNumber(int rowStartOffset) {
        Element root = component.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(rowStartOffset);
        Element line = root.getElement(index);

        if (line.getStartOffset() == rowStartOffset)
            return String.valueOf(index + 1);
        else
            return "";
    }

    private int getOffsetX(int availableWidth, int stringWidth) {
        return (int) ((availableWidth - stringWidth) * digitAlignment);
    }

    private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics) throws BadLocationException {
        Rectangle r = component.modelToView(rowStartOffset);
        int lineHeight = fontMetrics.getHeight();
        int y = r.y + r.height;
        int descent = 0;

        if (r.height == lineHeight) {
            descent = fontMetrics.getDescent();
        } else {
            if (fonts == null)
                fonts = new HashMap<>();

            Element root = component.getDocument().getDefaultRootElement();
            int index = root.getElementIndex(rowStartOffset);
            Element line = root.getElement(index);

            for (int i = 0; i < line.getElementCount(); i++) {
                Element child = line.getElement(i);
                AttributeSet as = child.getAttributes();
                String fontFamily = (String) as.getAttribute(StyleConstants.FontFamily);
                Integer fontSize = (Integer) as.getAttribute(StyleConstants.FontSize);
                String key = fontFamily + fontSize;

                FontMetrics fm = fonts.get(key);

                if (fm == null) {
                    Font font = new Font(fontFamily, Font.PLAIN, fontSize);
                    fm = component.getFontMetrics(font);
                    fonts.put(key, fm);
                }

                descent = Math.max(descent, fm.getDescent());
            }
        }

        return y - descent;
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();
        int currentLine = root.getElementIndex(caretPosition);

        if (lastLine != currentLine) {
            getParent().repaint();
            lastLine = currentLine;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentChanged();
    }

    private void documentChanged() {
        SwingUtilities.invokeLater(() -> {
            try {
                int endPos = component.getDocument().getLength();
                Rectangle rect = component.modelToView(endPos);

                if (rect != null && rect.y != lastHeight) {
                    setPreferredWidth();
                    getParent().repaint();
                    lastHeight = rect.y;
                }
            } catch (BadLocationException ignored) {
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof Font newFont) {
            setFont(newFont);
            lastDigits = 0;
            setPreferredWidth();
        }
    }
}