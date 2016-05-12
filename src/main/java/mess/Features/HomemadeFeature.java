package mess.Features;

import mess.utils.POS;
import mess.utils.TextPOSTreeTriple;
import weka.core.Instances;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public class HomemadeFeature extends Features {

    private HomemadeFeatureVector v;
    private Set<String> wordType;
    boolean m_isHomemade_train = false;
    boolean m_isHomemade_test = false;
    


    public HomemadeFeature() {
        //map = new EnumMap<POS, Set<String>>(POS.class);
        //for (POS p : map.keySet()) {

        //}
        v = new HomemadeFeatureVector();
        wordType = new HashSet<>();

    }
//File file = new File ("Data/blocks/500/train/American/The_Adventures_of_Huckleberry_Finn_clean_00001.txt");
    //TreeToSentenceHandler t = new TreeToSentenceHandler();
    //Massive Mapping: File to HomemadeFeatureVector.
    // FeatureCounterFactory (FeatureCounterFactory takes in Features you're trying to compute and creates FeatureCounters. Has a
    //bunch of numbers, then we compute the ratios that we need at the very end and put them into HomemadeFeatureVector.
    //For each file
    //Create a TreeToSentenceHandler
    // For each TextPOSTreeTriple we can get from TreeToSentenceHandler
    //First, do the clause parsing... by using a BFS.
    //Here, If a sentence has more than one clause, complex. Otherwise, simple.
    //ALSO... If there is an IN inside a PP, DECREMENT conjunctions.


    //ISSUES SO FAR:
    //PPs are in PPs... I hope this isn't common and was just because of an omitted subject.
    public void computeHomemadeFeatures (TextPOSTreeTriple t, int simpleSentence, int complexSentence, int prepositions, int conjunctions) {

        List<String> words = t.getWords();
        List<String> POStags = t.getPOS();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            //if (word.length() == 1) {
            //    System.out.print(word + " ");
            //}

            //kinda ugly, but hey... think this is the cleanest way to prevent punctuation crashing my POS Enum.
            if (word.matches("([\\.\\?!,;:\\-\\[\\]'`\\*\\$])+|(\\-LRB\\-)|(\\-RRB\\-)")){
                continue;
            }

            //
            String POSStr = POStags.get(i);
            if (!POS.contains(POSStr)) {
                continue;
            }
            List<String> features = POS.valueOf(POSStr).getHomemadeFeatures();
            //increment ALL of the features
            for (String feat : features) {
                switch (feat) {
                    case "Word":
                        v.incrementFeature(HomemadeFeatureCounterMapping.TotalWords);
                        wordType.add(word.toLowerCase());
                        break;
                    case "FV":
                        v.incrementFeature(HomemadeFeatureCounterMapping.FiniteVerbs);
                        break;
                    case "Num":
                        v.incrementFeature(HomemadeFeatureCounterMapping.Numerals);
                        break;
                    case "Conjuncts":
                        v.incrementFeature(HomemadeFeatureCounterMapping.Conjunctions);
                        break;
                    case "Noun":
                        v.incrementFeature(HomemadeFeatureCounterMapping.Nouns);
                        break;
                    case "Pron":
                        v.incrementFeature(HomemadeFeatureCounterMapping.Pronouns);
                        break;
                    case "Closed": //case closed! hahahahaha
                        v.incrementFeature(HomemadeFeatureCounterMapping.ClosedClassWords);
                        break;
                    case "Open":
                        v.incrementFeature(HomemadeFeatureCounterMapping.OpenClassWords);
                        break;
                    case "Neither":
                        break;
                    default:
                        System.err.println("Unexpected feature!!! " + feat);
                }
            }
            //character count
            v.addToFeature(HomemadeFeatureCounterMapping.TotalCharacters, word.length());
        }

        //these came from the ParsingFeatures
        v.addToFeature(HomemadeFeatureCounterMapping.SimpleSentences, simpleSentence);
        v.addToFeature(HomemadeFeatureCounterMapping.ComplexSentences, complexSentence);
        v.addToFeature(HomemadeFeatureCounterMapping.Prepositions, prepositions);
        v.addToFeature(HomemadeFeatureCounterMapping.Conjunctions, conjunctions);
        //increment total sentences
        v.incrementFeature(HomemadeFeatureCounterMapping.TotalSentences);



                //Then, grab the List created from the enum of homemade feature types.
                //Set lastWord to "".
                //For each word in this list:
                    //Add to a wordSet that will be used for the
                    //Increment the appropriate FeatureCounter
                    //SPECIAL: If VB and prev word is to (ignore case), don't increment.
                    //Note this word as prevWord.
            //Once Finished, compute the ratios and put into a HomemadeFeatureVector.
        //Return the Mapping of File to HomemadeFeatureVector.

    }

    public void resetVector() {
        v = new HomemadeFeatureVector();
    }


    //Done counting, now I compute percentages.
    public EnumMap<HomemadeFeatureRatioNames, Double> computePercentages() {
        //first, compute the wordTypes for the ENTIRE directory.
        int wordTypes = wordType.size();
        v.addToFeature(HomemadeFeatureCounterMapping.WordTypes, wordTypes);

        EnumMap<HomemadeFeatureRatioNames, Double> m = new EnumMap<>(HomemadeFeatureRatioNames.class);
        EnumMap<HomemadeFeatureCounterMapping, Integer> counts = v.gethomemadeAttributes();
        for (HomemadeFeatureRatioNames r : HomemadeFeatureRatioNames.values()) {
            switch (r) {
                case simplecomplex:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.SimpleSentences)
                            /(double) counts.get(HomemadeFeatureCounterMapping.ComplexSentences));
                    break;
                case simpletotal:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.SimpleSentences)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalSentences));
                    break;
                case complextotal:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.ComplexSentences)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalSentences));
                    break;
                case avgsent:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.TotalWords)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalSentences));
                    break;
                case avgwordlength:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.TotalCharacters)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case fverbratio:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.FiniteVerbs)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case numratio:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.Numerals)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case prepratio:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.Prepositions)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case conjratio:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.Conjunctions)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case infoload:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.OpenClassWords)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case nounratio:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.FiniteVerbs)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case grammlex:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.OpenClassWords)
                            /(double) counts.get(HomemadeFeatureCounterMapping.ClosedClassWords));
                    break;
                case pronounratio:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.Pronouns)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
                case typetoken:
                    m.put(r, (double) counts.get(HomemadeFeatureCounterMapping.WordTypes)
                            /(double) counts.get(HomemadeFeatureCounterMapping.TotalWords));
                    break;
            }
        }
        return m;

    }


    public Instances getM_allFeat_train() {
        return m_allFeat_train;
    }

    public Instances getM_allFeat_test() {
        return m_allFeat_test;
    }

    /**
     * Helps us store counts that are later computed.
     */
    class HomemadeFeatureVector {
        private EnumMap<HomemadeFeatureCounterMapping, Integer> homemadeAttributes;

        HomemadeFeatureVector() {
            homemadeAttributes = new EnumMap<>(HomemadeFeatureCounterMapping.class);
            for (HomemadeFeatureCounterMapping f : HomemadeFeatureCounterMapping.values()) {
                homemadeAttributes.put(f, 0);
            }
        }

        void incrementFeature(HomemadeFeatureCounterMapping m) {
            int i = homemadeAttributes.get(m);
            i++;
            homemadeAttributes.put(m,i);
        }

        void addToFeature (HomemadeFeatureCounterMapping m, int num) {
            int i = homemadeAttributes.get(m);
            i+=num;
            homemadeAttributes.put(m,i);
        }

        EnumMap<HomemadeFeatureCounterMapping, Integer> gethomemadeAttributes () {
            return homemadeAttributes;
        }

    }

    /**
     * Helpful enum that makes counting things much easier.
     */
    public enum HomemadeFeatureCounterMapping {
        SimpleSentences(0),
        ComplexSentences(1),
        TotalSentences(2),
        TotalWords(3),
        TotalCharacters(4),
        FiniteVerbs(5),
        Numerals(6),
        Prepositions(7),
        Conjunctions(8),
        OpenClassWords(9),
        Nouns(10),
        ClosedClassWords(11),
        Pronouns(12),
        WordTypes(13);

        private int index;

        HomemadeFeatureCounterMapping (int index) {
            this.index = index;
        }
    }

    /**
     * Enum that gives names to all of the ratios. These directly get turned into Attributes for Features, so extremely useful.
     */
    public enum HomemadeFeatureRatioNames {
        simplecomplex,
        simpletotal,
        complextotal,
        avgsent,
        avgwordlength,
        fverbratio,
        numratio,
        prepratio,
        conjratio,
        infoload,
        nounratio,
        grammlex,
        pronounratio,
        typetoken;

        /**
         * Computes the number of elements in this enum. For loop only is there in case more enums are added.
         * @return size, which should always be 14, unless if more enums are added.
         */
        public static int size() {
            int i = 0;
            for (HomemadeFeatureRatioNames h : HomemadeFeatureRatioNames.values()) {
                i++;
            }
            return i;
        }
    }

}
