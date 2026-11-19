import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIDefaults;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

final class ResuMakeTheme extends OceanTheme {
    private static final ColorUIResource CANVAS = new ColorUIResource(0xF3F5F8);
    private static final ColorUIResource SURFACE = new ColorUIResource(0xFFFFFF);
    private static final ColorUIResource INK = new ColorUIResource(0x202C3A);
    private static final ColorUIResource OUTLINE = new ColorUIResource(0x9EAABA);
    private static final ColorUIResource ACCENT = new ColorUIResource(0x2E5D91);
    private static final ColorUIResource SELECTION = new ColorUIResource(0xDCE9F7);

    static void install() {
        MetalLookAndFeel.setCurrentTheme(new ResuMakeTheme());
    }

    @Override
    public void addCustomEntriesToTable(UIDefaults defaults) {
        super.addCustomEntriesToTable(defaults);
        defaults.putDefaults(new Object[] {
            "Panel.background", CANVAS,
            "Viewport.background", CANVAS,
            "ScrollPane.background", CANVAS,
            "Label.foreground", INK,
            "Button.background", SURFACE,
            "Button.foreground", INK,
            "Button.focus", ACCENT,
            "Button.select", SELECTION,
            "TextField.background", SURFACE,
            "TextField.foreground", INK,
            "TextField.selectionBackground", SELECTION,
            "TextField.selectionForeground", INK,
            "CheckBox.background", CANVAS,
            "CheckBox.foreground", INK,
            "CheckBox.focus", ACCENT,
            "ComboBox.background", SURFACE,
            "ComboBox.foreground", INK,
            "ComboBox.selectionBackground", SELECTION,
            "ComboBox.selectionForeground", INK,
            "OptionPane.background", CANVAS,
            "OptionPane.messageForeground", INK,
            "TitledBorder.titleColor", INK,
            "TitledBorder.border", new BorderUIResource.LineBorderUIResource(OUTLINE)
        });
        defaults.remove("Button.gradient");
        defaults.remove("CheckBox.gradient");
        defaults.remove("ScrollBar.gradient");

        for (String key : new String[] {"Button.border", "TextField.border"}) {
            defaults.put(key, new BorderUIResource(new Outline(defaults.getBorder(key))));
        }

        setDialogIcon(defaults, "information", "i", SELECTION, ACCENT);
        setDialogIcon(defaults, "question", "?", SELECTION, ACCENT);
        setDialogIcon(defaults, "warning", "!", new Color(0xFFF0D6), new Color(0x754600));
        setDialogIcon(defaults, "error", "\u00D7", new Color(0xFBE4E4), new Color(0x9A3038));
    }

    private static void setDialogIcon(UIDefaults defaults, String type, String symbol, Color fill, Color foreground) {
        String key = "OptionPane." + type + "Icon";
        defaults.put(key, new IconUIResource(new DialogIcon(defaults.getIcon(key), symbol, fill, foreground)));
    }

    private static final class Outline extends AbstractBorder {
        private final Border original;

        Outline(Border original) {
            this.original = original;
        }

        @Override
        public Insets getBorderInsets(Component component, Insets insets) {
            Insets originalInsets = original.getBorderInsets(component);
            insets.set(originalInsets.top, originalInsets.left, originalInsets.bottom, originalInsets.right);
            return insets;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            boolean defaultButton = component instanceof JButton && ((JButton) component).isDefaultButton();
            Color previous = graphics.getColor();
            graphics.setColor(component.isFocusOwner() || defaultButton ? ACCENT : OUTLINE);
            graphics.drawRect(x, y, width - 1, height - 1);
            graphics.setColor(previous);
        }
    }

    private static final class DialogIcon implements Icon {
        private final int width;
        private final int height;
        private final String symbol;
        private final Color fill;
        private final Color foreground;

        DialogIcon(Icon original, String symbol, Color fill, Color foreground) {
            width = original.getIconWidth();
            height = original.getIconHeight();
            this.symbol = symbol;
            this.fill = fill;
            this.foreground = foreground;
        }

        @Override public int getIconWidth() { return width; }
        @Override public int getIconHeight() { return height; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D drawing = (Graphics2D) graphics.create();
            drawing.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int diameter = Math.min(width, height) - 4;
            drawing.setColor(fill);
            drawing.fillOval(x + (width - diameter) / 2, y + (height - diameter) / 2, diameter, diameter);
            drawing.setColor(foreground);
            drawing.setFont(new Font(Font.DIALOG, Font.BOLD, diameter * 2 / 3));
            FontMetrics text = drawing.getFontMetrics();
            drawing.drawString(symbol, x + (width - text.stringWidth(symbol)) / 2,
                    y + (height + text.getAscent() - text.getDescent()) / 2);
            drawing.dispose();
        }
    }
}
