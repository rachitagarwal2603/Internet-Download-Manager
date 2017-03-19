import java.util.*;
import java.net.*;			// to use URL class
import java.io.*;			// to use RandomAccessFile class etc.

//This class download a file from URL
/**
 * @author Rachit Agarwal
 *
 * @Observable Class holds the instances of observers
 * to whom we need to notify
 * when current state is changed
 * 
 */
public class Download extends Observable implements Runnable {

	//Max size of download buffer
	private static final int MAX_BUFFER_SIZE = 1024;
	//Various States name
	static final String STATUSES[] = {"Downloading", "Paused", "Complete", "Cancelled", "Error"};
	//Status codes
	static final int DOWNLOADING = 0;
	static final int PAUSED = 1;
	static final int COMPLETE = 2;
	static final int CANCELLED = 3;
	static final int ERROR= 4;
	
	private URL mUrl; 	// internet url of the file to be downloaded
	private int mSize;	// size of download in bytes
	private int mDownloaded;	// number of bytes downloaded
	private int mStatus;	// current status of download
	
	public Download(URL url) {
		this.mUrl = url;
		mSize = -1;
		mDownloaded = 0;
		mStatus = DOWNLOADING;
		
		// Begin download
		download();
	}

	//Start or Resume downloading
	private void download(){
		Thread thread= new Thread(this);
		thread.start();
	}
	
	//Get file name portion of Url
	private String getFileName(URL url){
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}
		
	// Notify observers that this download's status has changed
	public void stateChanged(){
		setChanged();
		notifyObservers();
	}
	
	// Download File
	@Override
	public void run() {
		RandomAccessFile file = null;		// Set up a file which support reading and writing operation.
		InputStream stream = null;			// Stores stream of bytes fetched from network.
		
		try{
			// Open connection to url
			HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
			
			//Specify what portion of file to download
			connection.setRequestProperty("Range", "bytes=" + mDownloaded + "-");		//Throws IllegalStateException and NullPointerException
			connection.connect();
			
			// Make sure Response code is in the range of 200, as 200 range indicates success.
			if(connection.getResponseCode()/100 != 2)
				error();
			
			int contentLength = connection.getContentLength();
			if(contentLength<1)
				error();					// it automatically changes status to error and stateChanged() method is called
			
			// Set the size for this download if it hasnot been already set.
			if(mSize == -1){
				mSize = contentLength;
				stateChanged();
			}
			
			// Open file and seek to the end of it
			file = new RandomAccessFile(getFileName(mUrl), "rw");
			file.seek(mDownloaded);
			
			stream = connection.getInputStream();
			while (mStatus == DOWNLOADING){
				// Size of buffer acc to how much of the file is left to be downloaded
				byte buffer[];
				if(mSize - mDownloaded > MAX_BUFFER_SIZE)
					buffer = new byte[MAX_BUFFER_SIZE];
				else
					buffer = new byte[mSize - mDownloaded];
				
				// Read from server into buffer
				int read = stream.read(buffer);
				if (read == -1)
					break;
				
				// Write buffer to file
				file.write(buffer, 0, read);
				mDownloaded += read;
				stateChanged();
			}
			
			// Now downloading has finished
			if (mStatus == DOWNLOADING){
				mStatus = COMPLETE;
				stateChanged();
			}
		} catch (Exception e){
			error();
		} finally {
			// Close file 
			if (file != null){
				try {
					file.close();
				} catch (Exception e){
					error();
				}
			}
		
			// Close connection to server
			if (stream != null){
				try{
					stream.close();
				} catch (Exception e){
					error();
				}
			}
		}
	}
	
	public String getUrl(){
		return mUrl.toString();
	}
	
	public int getSize(){
		return mSize;
	}
	
	public float getProgress(){
		return ((float)mDownloaded/mSize)*100;
	}

	public int getStatus(){
		return mStatus;
	}
	
	public void pause(){
		mStatus = PAUSED;
		stateChanged();
	}
	
	public void resume(){
		mStatus = DOWNLOADING;
		stateChanged();
		download();
	}
	
	public void cancel(){
		mStatus = CANCELLED;
		stateChanged();
	}
	
	public void error(){
		mStatus = ERROR;
		stateChanged();
	}
}
