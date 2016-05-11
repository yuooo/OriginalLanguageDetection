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
import mess.Features.HomemadeFeature;
import mess.utils.TextPOSTreeTriple;
import mess.utils.TreeToSentenceHandler;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

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

    //public HashMap<String, Integer[]> featuresCounts;
    private final Set<String> clauses;


    public ParseFeature() {
        List<String> temp = Arrays.asList("S",
        "SBAR",
        "SBARQ",
        "SINV",
        "SQ");
        clauses = new HashSet<>(temp);
    }

    /**
     * Matt Changes here:
     * I decided it would make the most sense to go ahead and compute the HomemadeFeatures based off of the BFS being done here.
     * I changed from creating a Treebank to using my built in Treebank method since I also grab the words and POS, which I rely upon
     * heavily in Homemade Features.
     */
    public void parseMe() {
        Options op = new Options();
        op.doDep = false;
        op.doPCFG = true;
        op.setOptions("-goodPCFG", "-evals", "tsv");
        File directory = new File("Data/parseFeatureTesting/train");
        File[] files = directory.listFiles();
        Integer n = files.length;
        List<HashMap<String, List<Integer>>> sliceMaps = new ArrayList<>();
        List<EnumMap<HomemadeFeature.HomemadeFeatureRatioNames, Double>> homemadeMaps = new ArrayList<>();
        for (int j = 0; j < n; j++) {

            sliceMaps.add(new HashMap<>());
            homemadeMaps.add(new EnumMap<>(HomemadeFeature.HomemadeFeatureRatioNames.class));
            //Changed by Matt: Using my function instead since this will make Homemade Computation much easier.
            //Treebank langTreeBank = makeTreebankie(files[j].toString(), op, null);
            HashMap<String, List<Integer>> featuresCounts = sliceMaps.get(j);
            HomemadeFeature hm = new HomemadeFeature();
            File[] subDirectories  = files[j].listFiles();
            //featuresCounts = new HashMap<>();
            for (int k = 0; k < subDirectories.length; k++) {
                TreeToSentenceHandler ite = new TreeToSentenceHandler(subDirectories[k]);
                List<Tree> children;
                Tree t1;

                while (ite.hasNext()) {
                    int simpleSentence = 0, complexSentence = 0, prepositions = 0, conjunctions = 0;
                    Queue<Tree> treeNodes = new LinkedList<Tree>();
                    TextPOSTreeTriple trip = ite.generateSentence();
                    t1 = trip.getTree();
                    treeNodes.add(t1.firstChild());
                    int clauseCount = 0; //num of clauses in a sentence

                    while (!treeNodes.isEmpty()) {
                        boolean prepPhraseFlag = false; //if true, we decrement conjunction count to not double count it.
                        Tree tNode = treeNodes.remove();
                        children = tNode.getChildrenAsList();
                        System.out.println(tNode.nodeString());
                        System.out.println("Children: ");
                        String key = tNode.nodeString();
                        //clause check!
                        if (clauses.contains(key)) {
                            clauseCount++;
                        //prepositional phrase check!
                        } else if (key.equals("PP")) {
                            prepositions++;
                            prepPhraseFlag = true;
                        }
                        for (Tree child : children) {
                            System.out.print(child.nodeString() + " ");
                            if (prepPhraseFlag && child.nodeString().equals("IN")) {
                                conjunctions--;
                            }
                            if (child.isLeaf() || child.firstChild().isLeaf()) {
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
                            List<Integer> arr = featuresCounts.get(key);
                            int val = arr.get(j);
                            val++;
                            arr.add(j,val);
                            featuresCounts.put(key, arr);
                        }
                        else {
                            //Integer[] arr = Collections.nCopies(n, 0).toArray(new Integer[0]); ??????
                            List<Integer> arr = new ArrayList<>(n);
                            //arr[j]++;
                            for (k = 0; k < j; k++) {
                                arr.add(k, 0);
                            }
                            arr.add(j, 1);
                            featuresCounts.put(key, arr);
                        }
                    }
                    if (clauseCount > 1)
                        complexSentence++;
                    else
                        simpleSentence++;

                    //Compute the homemade features here!
                    hm.computeHomemadeFeatures(trip, simpleSentence, complexSentence, prepositions, conjunctions);
                }


            }
            //Now crunch all of the counts for the HomemadeFeatures and the ParseFeatures.
            EnumMap<HomemadeFeature.HomemadeFeatureRatioNames, Double> ratios = hm.computePercentages();
            homemadeMaps.add(ratios);
            System.out.println(featuresCounts.toString());
        }
        /*
        //Compute Homemade Instances
        Set<HomemadeFeature.HomemadeFeatureRatioNames> s = new TreeSet<>();
        for (EnumMap<HomemadeFeature.HomemadeFeatureRatioNames, Double> m : homemadeMaps) {
            s.addAll(m.keySet());
        }
        ArrayList<Attribute> list = new ArrayList<>();
        for (HomemadeFeature.HomemadeFeatureRatioNames f : s) {
            Attribute a = new Attribute(f.toString());
            list.add(a);
        }

        //TODO: One Instances class per directory.
        Instances homemadeIns = new Instances("homemade", list, homemadeMaps.size());
        for (int i = 0; i < homemadeMaps.size(); i++) {
            Instance inst = new DenseInstance(HomemadeFeature.HomemadeFeatureRatioNames.size());
            for (Attribute a : list) {
                inst.setValue(a, homemadeMaps.get(i).get(HomemadeFeature.HomemadeFeatureRatioNames.valueOf(a.name())));
            }
        }*/
        //TODO: Compute the instances for slices, and return these?.
        //TODO: What's there that's left for both the HomemadeFeatures and ParseFeatures?
        /**
         * 1. Make sure that the HomemadeFeatures actually work.
         * 2. Make sure that the ParseFeatures with their changes also work.
         * 3. Create Instances for ParseFeatures.
         * 4. Make sure these are correct.
         * 5. Make tests for both ParseFeatures and HomemadeFeatures.
         * 6. Have them all merge together???
         */
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

