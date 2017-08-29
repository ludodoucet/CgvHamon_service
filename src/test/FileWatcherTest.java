package test;
import java.util.*;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWatcherTest {
  public static void main(String args[]) {
	///config file
		Properties prop = new Properties();
		InputStream input = null;

		try {
			File f = new File("config.ini");
			if (f.exists() == false) {
				
				try {
					FileWriter fw = new FileWriter("config.ini")  ;
					PrintWriter pw = new PrintWriter(fw);

					pw.println("nomfichierentree=facture.pdf");
					pw.println("nomfichiersortie=reportrectoverso.pdf");
					pw.println("imprimer=true");
					
				      
					pw.close();
				        	
				     
				    } catch (FileNotFoundException e) {
				      e.printStackTrace();
				    } catch (IOException e) {
				      e.printStackTrace();
				    }  
			}
			input = new FileInputStream("config.ini");
			
			// load a properties file
			prop.load(input);

			// get the property value and print it out
			System.out.println(prop.getProperty("nomfichierentree"));
			System.out.println(prop.getProperty("nomfichiersortie"));
			System.out.println(prop.getProperty("imprimer"));
			String nomfichierentree = prop.getProperty("nomfichierentree");
			String nomfichiersortie = prop.getProperty("nomfichiersortie");
			boolean imprimer = Boolean.parseBoolean(prop.getProperty("imprimer"));
			
	  /// fin config file
    // monitor a single file
    TimerTask task = new FileWatcher( new File(nomfichierentree) ) {
      protected void onChange( File file5 ) throws InvalidPasswordException, IOException, PrinterException {
    	  System.out.println("Start...");
		   
		   String dossieruser = System.getProperty("user.dir");
		   String dossier = dossieruser + "\\";
		   String dossiertemp = dossier + "temp\\";
		   
		 //Loading an existing PDF document
		      File file = new File(dossier + nomfichierentree);
		      PDDocument document = PDDocument.load(file); 

		      //Instantiating Splitter class
		      Splitter splitter = new Splitter();

		      //splitting the pages of a PDF document
		      List<PDDocument> Pages = splitter.split(document);

		      //Creating an iterator 
		      Iterator<PDDocument> iterator = Pages.listIterator();

		      //Saving each page as an individual document
		      int i = 1;
		      while(iterator.hasNext()) {
		         PDDocument pd = iterator.next();
		         pd.save(dossiertemp + "sample"+ i++ +".pdf");
		      }
		      
		      document.close();
		      int nombrepages =i - 1;
		      System.out.println(nombrepages);

		      
		      
		      for (int ii=1; ii < nombrepages + 1; ii++) {
		        //Creating PDF document object 
		    	  
		      PDDocument document2 = new PDDocument();    	
		         //Creating a blank page 
		         PDPage blankPage = new PDPage();

		         //Adding the blank page to the document
		         document2.addPage( blankPage );
		      
		     
		      //Saving the document
		      document2.save(dossiertemp + "pageblanche"+ ii +".pdf");
		      
		      
		      //Closing the document
		      document2.close();
		      }
		    //Instantiating PDFMergerUtility class
		      PDDocument doc1 = null;
		      PDDocument doc2 = null;	
		      PDDocument doc3 = null;
		      PDFMergerUtility PDFmerger = new PDFMergerUtility();
		      for (int iii=1; iii < nombrepages + 1; iii++) {

			      File file1;
			      File file2;
			      File file3 = new File(dossiertemp + "merged.pdf");
		    	  if (iii>1) {
		    		  file3 = new File(dossiertemp + "merged"+ (iii - 1) +".pdf");
		    		  
				      doc3 = PDDocument.load(file3);
				      PDFmerger.addSource(file3);				      
		    	  }
		    	  file1 = new File(dossiertemp + "sample"+ iii +".pdf");
			      doc1 = PDDocument.load(file1);
			      PDFmerger.addSource(file1);
		      
		      if (iii < nombrepages) {
		      file2 = new File(dossiertemp + "pageblanche"+ iii +".pdf");
		      doc2 = PDDocument.load(file2);
		      PDFmerger.addSource(file2);
		      }
		      else {
		      	        file2 = new File(dossier + "cgv.pdf");
		                doc2 = PDDocument.load(file2);
		                PDFmerger.addSource(file2);
		      }	
		      //Setting the destination file
		      PDFmerger.setDestinationFileName(dossiertemp + "merged"+ iii +".pdf");
		      if (iii == nombrepages) {
		    	  PDFmerger.setDestinationFileName(dossier + nomfichiersortie);  
		      }
		      System.out.println("ici");
		      System.out.println(file3);
		      System.out.println(file1);
		      System.out.println(file2);
		      PDFmerger.mergeDocuments(null);
		      
		      PDFmerger = new PDFMergerUtility();
		      
		      System.out.println("Documents merged");
	            doc1.close();
	            doc2.close();
	            if (iii>1) {doc3.close();}
		      }

		      //imprimer ou pas...
		      File filefichiersortie = new File(dossier + nomfichiersortie);
		      PDDocument fichiersortie = PDDocument.load(filefichiersortie);
		      
		      if (imprimer == true) {
		          PrinterJob job = PrinterJob.getPrinterJob();
		          job.setPageable(new PDFPageable(fichiersortie));
		          PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();;
				  attr.add(Sides.DUPLEX);
		          job.print(attr); 
		          System.out.println("imprime");
		      }
		      fichiersortie.close();
		      //fin imprimer ou pas
		      //Path from = nomfichiersortie.toPath();
		      
		      Path patch = Paths.get(dossier + nomfichierentree);
		      Files.delete(patch);
		      //System.exit(0);
	           
		      
			
      
        System.out.println( "File "+ file.getName() +" have change !" );
      }
    };

    Timer timer = new Timer();
    // repeat the check every second
    timer.schedule( task , new Date(), 1000 );
		}
    catch (IOException ex) {
		
		ex.printStackTrace();
	}
  }
}