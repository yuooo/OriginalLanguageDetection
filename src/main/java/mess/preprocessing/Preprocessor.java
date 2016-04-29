package main.java.mess.preprocessing;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/**
 * Created by mad4672 on 4/24/16.
 */
public class Preprocessor {
    /**
     * How to build this:
     * 1. Take a PDF file (have it be located in PDFFiles/) and convert the entirety of that file into a text file. Use this tool: https://pdfbox.apache.org/
     * 2. Save this text file into a new document. We'll put this file into TextFiles/.
     * 2. Using this text file, create a parse tree file as well, using the default Stanford Parser We'll put this file into .
     */

    /**
     * Takes a file in a .txt format and returns the trees of it.
     *
     * @param args
     */
    public static void main(String[] args) {
        String language = args[0];
        String novel = args[1];
        File file = new File("Data/txt_clean/" + language + "/" + novel);
        if (!file.exists()) {
            System.err.println("ERROR: " + file.getName() + "does not exist!");
        } else {
            String parserModel = "Data/trees/englishPCFG.ser.gz";
            LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
            File outputFile = new File("Data/trees/" + language + "/" + novel);
            writeTrees(lp, file.getPath(), outputFile);
        }
    }

    public static void writeTrees(LexicalizedParser lp, String filename, File outputfile) {
        try {
            PrintWriter outputStream = new PrintWriter(outputfile);
            for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
                Tree parse = lp.apply(sentence);
                parse.pennPrint(outputStream);
                outputStream.println();
            }
        } catch (IOException e) {
            System.err.println("Exception caught: " + e);
        }
    }
}


