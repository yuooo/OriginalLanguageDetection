package mess.preprocessing;

import de.tudarmstadt.ukp.jwktl.JWKTL;

import java.io.File;

/**
 * Created by jessicahoffmann on 09/05/2016.
 */
public class ParseWiktionary {
    public static void main(String[] args) throws Exception {
        File dumpFile = new File("resources/enwiktionary-20160501-pages-articles-multistream.xml");
        File outputDirectory = new File("resources/wikiDirectory");
        boolean overwriteExisting = true;

        JWKTL.parseWiktionaryDump(dumpFile, outputDirectory, overwriteExisting);
    }

}
