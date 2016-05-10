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
         * <p/>
         * Usage: {@code java ParserDemo [[model] textFile]}
         * e.g.: java ParserDemo edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz data/chinese-onesent-utf8.txt
         */

        public HashMap<String, Integer[]>featuresCounts;


        public ParseFeature(){

        }

        public void parseMe(){
            Options op = new Options();
            op.doDep = false;
            op.doPCFG = true;
            op.setOptions("-goodPCFG", "-evals", "tsv");
            Treebank testTreeBank = makeTreebankie("/Users/lakshmiprakash/Documents/NLPProject/OriginalLanguageDetection/Data/parseFeatureTesting/train/American"
                    ,op ,null );

            featuresCounts = new HashMap<>();
            Iterator<Tree> ite = testTreeBank.iterator();
            List<Tree> children;
//            Iterator<Tree> ;
            Tree t1;
            Tree t2;
            Tree t3;

            while(ite.hasNext()){
                t1=ite.next();
                children = t1.getChildrenAsList();
                System.out.println(t1);
                System.out.println("Children: ");
                for(Tree child1: children){
                    System.out.println(child1.nodeString());
                    System.out.println(child1.firstChild().nodeString());
                    System.out.println(child1.firstChild().firstChild().nodeString());
                    String key = child1.nodeString() + " "
                                + child1.firstChild().nodeString() + " "
                                + child1.firstChild().firstChild().nodeString();
                    System.out.println(key);
                    System.out.println("");
                    Integer[] arr = Collections.nCopies(testTreeBank.size(), 0).toArray(new Integer[0]);
                    featuresCounts.put(key, arr);
                }
            }

            System.out.println(featuresCounts);
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
            ParseFeature p  = new ParseFeature();
            p.parseMe();
        }

        public void blah(String[] args)
            {
            int argIndex = 0;
            boolean train = false;
            String treebankPath = null;
            String selfTrainPath = null;
            String testPath = null;
            FileFilter selfTrainFilter = null;
            Treebank selfTrainTreebank = null;
            FileFilter testFilter = null;
            Treebank testTreebank = null;
            MemoryTreebank results = new MemoryTreebank();
            LexicalizedParser lp = null;
            LexicalizedParser domainlp = null;
            Options op = new Options();
            op.doDep = false;
            op.doPCFG = true;
            op.setOptions("-goodPCFG", "-evals", "tsv");
            Treebank main_tree_bankie=null;
            train = true;

            while (argIndex < args.length && args[argIndex].charAt(0) == '-') {
                if (args[argIndex].equalsIgnoreCase("-train") ||
                        args[argIndex].equalsIgnoreCase("-trainTreebank")) {

                    Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-train");
                    argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
                    treebankPath = treebankDescription.first();
                    lp = LexicalizedParser.trainFromTreebank(treebankPath, treebankDescription.second(), op);
                    main_tree_bankie = makeTreebankie(treebankPath,op,treebankDescription.second());
//                main_tree_bankie.
                }
                if (args[argIndex].equalsIgnoreCase("-selfTrain")) {
                    Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-selfTrain");
                    argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
                    selfTrainPath = treebankDescription.first();
                    selfTrainFilter = treebankDescription.second();
                    selfTrainTreebank = op.tlpParams.testMemoryTreebank();
                    selfTrainTreebank.loadPath(selfTrainPath, selfTrainFilter);
                    for (Tree t : selfTrainTreebank) {
                        List<? extends HasWord> sentence = Sentence.toCoreLabelList(t.yieldWords());
                        Tree parse = lp.apply(sentence);
                        results.add(parse);
                    }
                    domainlp = LexicalizedParser.getParserFromTreebank(main_tree_bankie,results,1.0,null,op,null,null);
                }
                if (args[argIndex].equalsIgnoreCase("-test")) {
                    Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-test");
                    argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
                    testPath = treebankDescription.first();
                    testFilter = treebankDescription.second();
                    Treebank testTreeBank = makeTreebankie(testPath,op,testFilter);
                    EvaluateTreebank evaluator = new EvaluateTreebank(domainlp);
                    evaluator.testOnTreebank(testTreeBank);
                }
            }
        }
    }
