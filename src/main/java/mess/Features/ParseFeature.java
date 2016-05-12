package mess.Features;

import edu.stanford.nlp.trees.Tree;
import mess.utils.TextPOSTreeTriple;
import mess.utils.TreeToSentenceHandler;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
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
    private ArrayList<Attribute> trainedSlices;

    private boolean m_isParse_train = false;
    private boolean m_isParse_test = false;





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
        trainedSlices = new ArrayList<>();

    }

    /**
     * Matt Changes here:
     * I decided it would make the most sense to go ahead and compute the HomemadeFeatures based off of the BFS being done here.
     * I changed from creating a Treebank to using my built in Treebank method since I also grab the words and POS, which I rely upon
     * heavily in Homemade Features.
     */
    public void parseMe(File source, String type) {
        HashMap<String, List<Integer>> featuresCounts;
        Instances homemadeInstances;

        //crunch number of files ahead of time
        int numFiles = 0;
        File directory = source;
        File[] files = directory.listFiles();

        for (File f: files) {
            File[] temp = f.listFiles();
            numFiles += temp.length;
        }

        //create Attributes for Homemade Features
        ArrayList<Attribute> list = new ArrayList<>();
        for (HomemadeFeature.HomemadeFeatureRatioNames f : HomemadeFeature.HomemadeFeatureRatioNames.values()) {
            Attribute a = new Attribute("HM: " + f.toString());
            list.add(a);
        }

        //initialize parameters based off of train and test.
        if (type.equals("train")) {
            hm.m_allFeat_train = new Instances("homemade_train", list, numFiles);
            homemadeInstances = hm.m_allFeat_train;
            featuresCounts = featuresCountsTrain;
        }
        else{
            hm.m_allFeat_test = new Instances("homemade_test", list, numFiles);
            homemadeInstances = hm.m_allFeat_test;
            featuresCounts = featuresCountsTest;
        }
        Integer flen = files.length;
        Integer totFiles = 0;




        //per file, parse the tree then compute homemade features.
        for (int j = 0; j < flen; j++) {

            File[] subDirectories = files[j].listFiles();

            Integer slen = subDirectories.length;

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
                            int val = arr.get(totFiles + k);
                            val++;
                            arr.set(totFiles + k, val);
                            featuresCounts.put(key, arr);
                        } else {
                            //Integer[] arr = Collections.nCopies(n, 0).toArray(new Integer[0]); ??????
                            List<Integer> arr = new ArrayList<>(numFiles);
                            //System.out.println("Size of array:");
                            //System.out.println(n);
                            //System.out.println(arr.toString());
                            //arr[j]++;
                            for (int m = 0; m < numFiles; m++) {
                                arr.add(m, 0);
                            }
                            arr.set(totFiles + k, 1);
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
                homemadeInstances.add(inst);

            }
            totFiles = totFiles + slen;
        }


        //now, depending on train and test, adjust flags and set up Instances for the Parse Features.
        if (type.equals("train")) {
            hm.m_isHomemade_train = true;
            m_isParse_train = true;

            //create trainedSlices set... if these don't appear in test, we exclude them.
            for (String s : featuresCountsTrain.keySet()) {
                Attribute a = new Attribute(s);
                trainedSlices.add(a);
            }
            m_allFeat_train = new Instances("Parse_train", trainedSlices, numFiles);
            int numAttributes = m_allFeat_train.numAttributes();
            for (int i = 0; i < numFiles; i++) {
                Instance inst = new DenseInstance(numAttributes);
                for (Attribute a : trainedSlices) {
//                    System.out.println(a.name());
                    inst.setValue(a,featuresCountsTrain.get(a.name()).get(i));
                }
                m_allFeat_train.add(inst);
            }

            //for the test portion
        } else {
            hm.m_isHomemade_test = true;
            m_isParse_test = true;

            //now just build another Instances set... we already have Attributes.
            m_allFeat_test = new Instances("Parse_test", trainedSlices, numFiles);
            int numAttributes = m_allFeat_test.numAttributes();
            for (int i = 0; i < numFiles; i++) {
                Instance inst = new DenseInstance(numAttributes);
                //just add only the slices detected in train set.
                for (Attribute a : trainedSlices) {
                    if (featuresCountsTest.containsKey(a.name())) {
                        inst.setValue(a, featuresCountsTest.get(a.name()).get(i));
                    } else {
                        inst.setValue(a, 0);
                    }
                }
                m_allFeat_test.add(inst);
            }
        }
        //System.out.println(featuresCounts.toString());

    }


    // use this then hm.getm_allFeat_train() and hm.getm_allFeat_test() to get train and test.
    public HomemadeFeature getHomemadeFeatures() {
        return hm;
    }

    public Instances getm_allFeat_train() {
        return m_allFeat_train;
    }

    public Instances getm_allFeat_test() {
        return m_allFeat_test;
    }

    public boolean isM_isParse_test() {
        return m_isParse_test;
    }

    public boolean isM_isParse_train() {
        return m_isParse_train;
    }

    public static void main(String[] args) {
        ParseFeature p = new ParseFeature();
        HomemadeFeature hm = p.getHomemadeFeatures();
        p.parseMe(new File("Data/block_trees/500/train"), "train");

        System.out.println("Parse Features after train:");
        System.out.println(p.m_allFeat_train);
        System.out.println("Homemade Features after train:");
        System.out.println(hm.trainToWeka());

        p.parseMe(new File("Data/block_trees/500/test"), "test");
        System.out.println(p.m_allFeat_test);
        System.out.println("Homemade Features after test:");
        System.out.println(hm.testToWeka());

    }
}

