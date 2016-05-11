package mess.utils;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Treebank;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mad4672 on 5/6/16.
 */
public class TreeToSentenceHandler {


    private Treebank trees; //Stanford Parser treebank
    private Iterator<Tree> it; //iterator to handle one tree at a time.

    /** Takes a file with trees in it and allows you to parse one tree at a time to grab two lists:
     *  One with the words of the sentence in them,
     *  and one with the best guess POS tags that correlate to the words given, provided by the Stanford Parser.
     *  Takes a File object that correlates to the file you wish to read in.
     * @param textFile The file that you want parsed.
     */
    public TreeToSentenceHandler (File textFile) {
        Options op = new Options();
        op.doDep = false;
        op.doPCFG = true;
        op.setOptions("-goodPCFG", "-evals", "tsv");
        trees = op.tlpParams.diskTreebank();
        trees.loadPath(textFile);
        it = trees.iterator();
    }


    /**
     * Iterates over a sentence in the Treebank and generates a TextPOSDouble object.
     * @return a Double that has a list of the words in a sentence, and a list of POS in the same sentence.
     */
    public TextPOSTreeTriple generateSentence() {
        Tree t = it.next();
        List<Word> words = t.yieldWords();
        List<Label> preYield = t.preTerminalYield();
        //t.pennPrint();
        return new TextPOSTreeTriple(words, preYield, t);
    }

    /**
     * Check the iterator to see if another sentence exists in the Treebank.
     * @return true if another sentence exists, false if not.
     */
    public boolean hasNext() {
        return it.hasNext();
    }


    /**
     * Simple test of one sentence from one file... used for testing this class.
     * @param args First arg should be the name of a text.
     */
    public static void main(String[] args) {
        File file = new File(args[0]);
        TreeToSentenceHandler t = new TreeToSentenceHandler(file);
        TextPOSTreeTriple test= t.generateSentence();
        System.out.println(test.getWords());
        System.out.println(test.getPOS());
        test= t.generateSentence();
        System.out.println(test.getWords());
        System.out.println(test.getPOS());
    }



}
