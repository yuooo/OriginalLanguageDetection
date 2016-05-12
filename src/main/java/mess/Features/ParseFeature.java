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

    private  HashMap<String, List<Integer>> featuresCountsTrain;
    private  HashMap<String, List<Integer>> featuresCountsTest;
    private final Set<String> clauses; //clauses we need to detect for simple/complex sentences.
    private HomemadeFeature hm; //This goes here because we need parse trees for a few homemade features.
    Instances l_parse;
    Instances l_parse_test;

    public ParseFeature() {
        featuresCountsTrain = new HashMap<>();
        featuresCountsTest = new HashMap<>();
                List<String> temp = Arrays.asList("S",
        "SBAR",
        "SBARQ",
        "SINV",
        "SQ");
        clauses = new HashSet<>(temp);
        hm = new HomemadeFeature();
    }

    /**
     * Matt Changes here:
     * I decided it would make the most sense to go ahead and compute the HomemadeFeatures based off of the BFS being done here.
     * I changed from creating a Treebank to using my built in Treebank method since I also grab the words and POS, which I rely upon
     * heavily in Homemade Features.
     */
    public void parseMe(File source, String type) {
        HashMap<String, List<Integer>> featuresCounts;
        if (type.equals("train")) {
            featuresCounts = featuresCountsTrain;
        }
        else{
            featuresCounts = featuresCountsTest;
        }
        Options op = new Options();
        op.doDep = false;
        op.doPCFG = true;
        op.setOptions("-goodPCFG", "-evals", "tsv");
        File directory = source;
        File[] files = directory.listFiles();
        Integer flen = files.length;
        Integer n = 0;

        int numFiles = 0;
        //ALL THIS IS FOR INITIALIZING HOMEMADE INSTANCES (which have a fixed size)
        for (File f: files) {
            File[] temp = f.listFiles();
            numFiles += temp.length;
        }
        ArrayList<Attribute> list = new ArrayList<>();
        for (HomemadeFeature.HomemadeFeatureRatioNames f : HomemadeFeature.HomemadeFeatureRatioNames.values()) {
            Attribute a = new Attribute(f.toString());
            list.add(a);
        }

        hm.m_homemade = new Instances("homemade", list, numFiles);
        //NOW WE HAVE HOMEMADE INSTANCES

        //featuresCounts = new HashMap<>();
        //List<HashMap<String, List<Integer>>> sliceMaps = new ArrayList<>();
        //List<EnumMap<HomemadeFeature.HomemadeFeatureRatioNames, Double>> homemadeMaps = new ArrayList<>();
        for (int j = 0; j < flen; j++) {

            //sliceMaps.add(new HashMap<>());
            //homemadeMaps.add(new EnumMap<>(HomemadeFeature.HomemadeFeatureRatioNames.class));
            //Changed by Matt: Using my function instead since this will make Homemade Computation much easier.
            //Treebank langTreeBank = makeTreebankie(files[j].toString(), op, null);
            //HashMap<String, List<Integer>> featuresCounts = sliceMaps.get(j);

            File[] subDirectories = files[j].listFiles();

            Integer slen = subDirectories.length;
            n = flen * slen;
            //featuresCounts = new HashMap<>();
            for (int k = 0; k < slen; k++) {
                //System.out.println(subDirectories[k]);
                hm.resetVector();

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
                        //System.out.println(tNode.nodeString());
                        //System.out.println("Children: ");
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
                            //System.out.print(child.nodeString() + " ");
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
                        //System.out.println();
                        //System.out.println(key);
                        //System.out.println("");
                        if (featuresCounts.containsKey(key)) {
                            List<Integer> arr = featuresCounts.get(key);
                            int val = arr.get(j);
                            val++;
                            arr.set(j * slen + k, val);
                            featuresCounts.put(key, arr);
                        } else {
                            //Integer[] arr = Collections.nCopies(n, 0).toArray(new Integer[0]); ??????
                            List<Integer> arr = new ArrayList<>(n);
                            //System.out.println("Size of array:");
                            //System.out.println(n);
                            //System.out.println(arr.toString());
                            //arr[j]++;
                            for (int m = 0; m < n; m++) {
                                arr.add(m, 0);
                            }
                            arr.set(j * slen + k, 1);
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

                //Now crunch all of the counts for the HomemadeFeatures and the ParseFeatures, and put into instances.
                EnumMap<HomemadeFeature.HomemadeFeatureRatioNames, Double> ratios = hm.computePercentages();
                Instance inst = new DenseInstance(HomemadeFeature.HomemadeFeatureRatioNames.size());
                int i = 0;
                for (HomemadeFeature.HomemadeFeatureRatioNames r : ratios.keySet()) {
                    inst.setValue(list.get(i), ratios.get(r));
                    i++;
                }
                hm.m_homemade.add(inst);

            }
        }
//            Instance parse_inst = new DenseInstance(n);
//            parse_inst
//            featuresCounts.values();
//            for (HomemadeFeature.HomemadeFeatureRatioNames r : ratios.keySet()) {
//                inst.setValue(list.get(i), ratios.get(r));
//            }
//            l_parse.add(parse_inst);

            hm.m_isHomemade_train = true; //TODO: Have code ready for both train and test
            System.out.println(featuresCounts.toString());

    }


    // use this then hm.getM_homemade() and hm.getM_homemade_test() to get train and test.
    public HomemadeFeature getHomemadeFeaturesInstances() {
        return hm;
    }

    public static void main(String[] args) {
        ParseFeature p = new ParseFeature();

        p.parseMe(new File("Data/block_trees/500/train"), "train");

        p.parseMe(new File("Data/block_trees/500/test"), "test");

    }
}

