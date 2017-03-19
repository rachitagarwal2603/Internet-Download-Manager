import java.util.*;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

// This class manages download table's data
public class DownloadsTableModel extends AbstractTableModel implements Observer{

	private static final String[] columnNames = {"URL", "Size", "Progress", "Status"};							   // Table's column names
	private static final Class[] columnClasses = {String.class, String.class, JProgressBar.class, String.class};   // classes for each column values
	
	private ArrayList<Download> downloadList = new ArrayList<Download>();			// Table's list of downloads.
	
	// Add a new download to table
	public void addDownload(Download download){
		download.addObserver(this);
		downloadList.add(download);
		
		// Fire table row insertion notification to table.
		fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
	}
	
	public Download getDownload(int row){
		return downloadList.get(row);
	}
	
	public void clearDownload(int row){
		downloadList.remove(row);	
		fireTableRowsDeleted(row, row);
	}
	
	
	public String getColumnName(int col){
		return columnNames[col];
	}
	
	public Class<?> getColumnClass(int col){
		return columnClasses[col];
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return downloadList.size();
	}

	// Get value for a specific row and column combination
	@Override
	public Object getValueAt(int row, int col) {
		Download download = downloadList.get(row);
		switch (col){
			case 0 : return download.getUrl();							// to fetch url
			case 1 : return download.getSize();							// to fetch size
			case 2 : return new Float(download.getProgress());			// to fetch Progress
			case 3 : return Download.STATUSES[download.getStatus()];	// to fetch status
		}
		return "";
	}

	// Called when a Download notifies Observer of any changes
	@Override
	public void update(Observable o, Object arg) {
		int index = downloadList.indexOf(o);
		
		// Fire table row update notification to table
		fireTableRowsUpdated(index, index);
	}
}
