package mess.preprocessing;

import org.apache.pdfbox.tools.ExtractText;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;

/**
 * Created by mad4672 on 4/28/16.
 */
public class PDFToText {


    public static void main(String[] args) {

        //log( "PDFToTextTask executing" );
        File inputFile = new File(args[0]);
        //Iterator fileSetIter = fileSets.iterator();
        //while( fileSetIter.hasNext() )
        //{
            //FileSet next = (FileSet)fileSetIter.next();
            //DirectoryScanner dirScanner = next.getDirectoryScanner( getProject() );
            //dirScanner.scan();
            //String[] files = dirScanner.getIncludedFiles();
            //for (String file : files)
            //{
                //File f = new File(dirScanner.getBasedir(), file);
        System.err.println( "processing: " + inputFile.getAbsolutePath() );
        String pdfFile = inputFile.getAbsolutePath();
        if( pdfFile.toUpperCase().endsWith( ".PDF" ) )
        {
            String textFile = pdfFile.substring( 0, pdfFile.length() -3 );
            textFile = textFile + "txt";
            try
            {
                ExtractText.main(new String[]{pdfFile, textFile});
            }
            catch( Exception e )
            {
                System.err.println( "Error processing " + pdfFile + e.getMessage() );
            }
        }
            //}


    }
}
