import java.awt.Component;

import javax.swing.*;			// To use JProgressBar class
import javax.swing.table.TableCellRenderer;

/**
 * 
 * @ProgressRenderer class renders a JProgressBar in a table cell.
 *
 */
public class ProgressRenderer extends JProgressBar implements TableCellRenderer{

	public ProgressRenderer(int min, int max) {
		super(min, max);
	}

	// Returns the JProgressBar for the given table cell.
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		//Set JProgressBar percent value
		setValue((int) ((Float) value).floatValue());
		return this;
	}
}
