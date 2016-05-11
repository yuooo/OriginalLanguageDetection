package mess.Features;

import mess.utils.POS;
import mess.utils.TextPOSTreeTriple;
import mess.utils.TreeToSentenceHandler;
import weka.core.Attribute;

import java.io.File;
import java.util.*;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public class HomemadeFeature extends Features {
    /**
    * BELOW ARE THOUGHTS FOR SENTENCE RATIOS
    */

    //TODO: Initial thought of simple sentence counting: Could just count literal number of clauses in sentence. I think this works.
    /**
     * S - simple declarative clause, i.e. one that is not introduced by a (possible empty) subordinating conjunction or a wh-word and that does not exhibit subject-verb inversion.
     SBAR - Clause introduced by a (possibly empty) subordinating conjunction.
     SBARQ - Direct question introduced by a wh-word or a wh-phrase. Indirect questions and relative clauses should be bracketed as SBAR, not SBARQ.
     SINV - Inverted declarative sentence, i.e. one in which the subject follows the tensed verb or modal.
     SQ - Inverted yes/no question, or main clause of a wh-question, following the wh-phrase in SBARQ.
     */


    /**
     * BELOW ARE THOUGHTS FOR AVERAGES.
     */
    //TODO: Averages are easy. Just count words/sentence (exclude punctuation), and characters per word/total characters (filter for only alpha-numeric characters. Also exclude hyphens.)

    /**
     * BELOW ARE THOUGHTS FOR WORD RATIOS
     */

    //TODO: Word Type: Type-Token Ratio may not be useful in our case but we'll try it anyways. Just throw all the (alpha numeric) words into a HashSet and the size of that is the # of word types.

    //TODO: Verbs that are finite:
    /**
     27.	VB	Verb, base form IFF not following TO
     28.	VBD	Verb, past tense
     31.	VBP	Verb, non-3rd person singular present
     32.	VBZ	Verb, 3rd person singular present

     These are NOT:
     27.	VB	Verb, base form followed by TO
     29.	VBG	Verb, gerund or present participle
     30.	VBN	Verb, past participle
     */

    //TODO: Numerals: Just use CD tag.

    //NEED PARSE TREE.
    //TODO: Preps: Count PPs instead of TO or IN.

    //NEED PARSE TREE.
    //TODO: Conjunctions: Count CCs. For IN, count IN NOT in PP.

    //TODO: Info load: Open vs. total... no problem.

    //TODO: Discourse markers: Particles and discourse connectives. I don't think this is possible.

    //TODO: Open vs. closed class: Mark words that are closed class. Assume rest are open.
    /**
     * Closed class words:
     1.	CC	Coordinating conjunction
     2.	CD	Cardinal number
     3.	DT	Determiner
     4.	EX	Existential there
     6.	IN	Preposition or subordinating conjunction
     11.MD	Modal
     16.PDT	Predeterminer
     18.PRP	Personal pronoun
     19.PRP$Possessive pronoun
     23.RP	Particle
     25.TO	to
     33.WDT	Wh-determiner
     34.WP	Wh-pronoun
     35.WP$	Possessive wh-pronoun
     */

    /**
     * Neither open nor closed (we can't tell or not technically a word):
     * 5.	FW	Foreign word
     * 10.	LS	List item marker
     * 17.	POS	Possessive ending
     * 24.	SYM	Symbol
     */

    /**
     * Rest are open.
     */

    //TODO: Set words that are nouns.
    /**
     12.	NN	Noun, singular or mass
     13.	NNS	Noun, plural
     14.	NNP	Proper noun, singular
     15.	NNPS	Proper noun, plural
     */

    //TODO: Pronouns:
    /**
     18.	PRP	Personal pronoun
     19.	PRP$	Possessive pronoun
     34.	WP	Wh-pronoun
     35.	WP$	Possessive wh-pronoun
     */

    //TODO: Lemmas: May be tricky, but I think it's possible, because I have to lookup a dictionary to see if each word in fact exists within it.
    //TODO: Will call this low priority. A book like Huck Finn would severely skew the lemmas with its Southern style.

    //TODO: Create a static mapping for each POS or clause and how we count them. The key should be the label,
    //TODO: and the values are those in which we increment. Also, a mapping for each count would be good.
    //TODO: Make each mapping a special Object, or just a String? Hmm...

    //private EnumMap<POS, Set<String>> map;
    private HomemadeFeatureVector v;
    private Set<String> wordType;


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
            if (word.matches("([\\.\\?!,;:\\-\\(\\)\\[\\]'`])+")){
                continue;
            }
            String POSStr = POStags.get(i);
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


    protected class HomemadeFeatureVector {


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

        public static int size() {
            int i = 0;
            for (HomemadeFeatureRatioNames h : HomemadeFeatureRatioNames.values()) {
                i++;
            }
            return i;
        }
    }

}
