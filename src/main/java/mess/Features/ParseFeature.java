package mess.Features;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.common.ArgUtils;
import edu.stanford.nlp.parser.lexparser.EvaluateTreebank;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.Timing;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public class ParseFeature extends Features {

    /**
     * The main method demonstrates the easiest way to load a parser.
     * Simply call loadModel and specify the path of a serialized grammar
     * model, which can be a file, a resource on the classpath, or even a URL.
     * For example, this demonstrates loading a grammar from the models jar
     * file, which you therefore need to include on the classpath for ParserDemo
     * to work.
     * <p>
     * Usage: {@code java ParserDemo [[model] textFile]}
     * e.g.: java ParserDemo edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz data/chinese-onesent-utf8.txt
     */

    public HashMap<String, Integer[]> featuresCounts;


    public ParseFeature() {

    }

    public void parseMe() {
        Options op = new Options();
        op.doDep = false;
        op.doPCFG = true;
        op.setOptions("-goodPCFG", "-evals", "tsv");
        File directory = new File("Data/parseFeatureTesting/train");
        File[] files = directory.listFiles();
        Integer n = files.length;
        for (int j = 0; j < n; j++) {

            Treebank langTreeBank = makeTreebankie(files[j].toString(), op, null);

            featuresCounts = new HashMap<>();
            Iterator<Tree> ite = langTreeBank.iterator();
            List<Tree> children;
//            Iterator<Tree> ;
            Tree t1;

            while (ite.hasNext()) {
                Queue<Tree> treeNodes = new LinkedList<Tree>();
                t1 = ite.next();
                treeNodes.add(t1);
                while (!treeNodes.isEmpty()) {
                    Tree tNode = treeNodes.remove();
                    children = tNode.getChildrenAsList();
                    System.out.println(tNode.nodeString());
                    System.out.println("Children: ");
                    String key = tNode.nodeString();
                    for (Tree child : children) {
                        System.out.print(child.nodeString() + " ");
                        if (child.isLeaf()) {
                            continue;
                        }
                        treeNodes.add(child);
                        //System.out.println(child1.firstChild().nodeString());
                        //System.out.println(child1.firstChild().firstChild().nodeString());
                        key = key + "_" + child.nodeString();
                    }
                    System.out.println();
                    System.out.println(key);
                    System.out.println("");
                    if (featuresCounts.containsKey(key)) {
                        Integer[] arr = featuresCounts.get(key);
                        arr[j]++;
                        featuresCounts.put(key, arr);
                    }
                    else {
                        Integer[] arr = Collections.nCopies(n, 0).toArray(new Integer[0]);
                        arr[j]++;
                        featuresCounts.put(key, arr);
                    }
                }
            }


            System.out.println(featuresCounts.toString());
        }
    }

    public static Treebank makeTreebankie(String treebankPath, Options op, FileFilter filt) {
        System.err.println("Training a parser from treebank dir: " + treebankPath);
        Treebank trainTreebank = op.tlpParams.diskTreebank();
        System.err.print("Reading trees...");
        if (filt == null) {
            trainTreebank.loadPath(treebankPath);
        } else {
            trainTreebank.loadPath(treebankPath, filt);
        }

        Timing.tick("done [read " + trainTreebank.size() + " trees].");
        return trainTreebank;
    }

    public static void main(String[] args) {
        ParseFeature p = new ParseFeature();
        p.parseMe();
    }
}

