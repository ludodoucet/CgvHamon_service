package test;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

public abstract class FileWatcher extends TimerTask {

	private long timeStamp;
	  private File file;

	  public FileWatcher( File file ) {
	    this.file = file;
	    this.timeStamp = file.lastModified();
	  }

	  

	



	public final void run() {
	    long timeStamp = file.lastModified();

	    if( this.timeStamp != timeStamp ) {
	      this.timeStamp = timeStamp;
	      try {
			onChange(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }
	  }

	  protected abstract void onChange( File file ) throws InvalidPasswordException, IOException, PrinterException;

}
