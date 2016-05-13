package mess.Features;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;
import de.tudarmstadt.ukp.jwktl.api.util.Language;
import edu.stanford.nlp.util.ArrayUtils;
import edu.stanford.nlp.util.StringUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public class LexicalFeature extends Features {
    private boolean m_isUnigram_train = false;
    private boolean m_isUnigram_test = false;

    private Instances m_unigram;
    private Instances m_unigram_test;

    private boolean m_isLoaded_POS = false;

    private Instances m_POS;
    private Instances m_POS_test;



    public void computeUnigram() throws Exception {
        // assert init
        assert m_isLoaded_train;

        // load the data
        Instances inputInstances = m_data_train;

        // Set the tokenizer
        NGramTokenizer tokenizer = new NGramTokenizer();
        // We want unigrams
        tokenizer.setNGramMinSize(1);
        tokenizer.setNGramMaxSize(1);

        // Set the filter
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(inputInstances);
        filter.setTokenizer(tokenizer);
        filter.setWordsToKeep(1000000);
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);

        // Filter the input instances into the output ones
        Instances outputInstances = Filter.useFilter(inputInstances,filter);

        // add to allFeat
        m_unigram = outputInstances;
        safeMerge(outputInstances, true);
        m_isUnigram_train = true;

        // compute the test instances
        Instances inputTest = m_data_test;
        Instances outputTest = Filter.useFilter(inputTest,filter);
        m_unigram_test = outputTest;
        safeMerge(outputTest, false);
        m_isUnigram_test = true;
    }



    public void computeUnigram(Instances inst) throws Exception {
        m_data_train = inst;
        m_isLoaded_train = true;
        computeUnigram();
    }

    // TODO
    public Instances computeBigram(String pathFile) {
        return null;
    }


    public void computeFunctionWords() throws Exception {
        assert m_isUnigram_train;
        assert m_isUnigram_test;
        List<String> functionWordList = Arrays.asList("a", "about", "above", "above", "across", "after", "afterwards",
                "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am",
                "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything",
                "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes",
                "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between",
                "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "con", "could",
                "couldnt", "cry", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg",
                "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every",
                "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire",
                "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full",
                "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here",
                "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how",
                "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its",
                "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me",
                "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",
                "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody",
                "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one",
                "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own",
                "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming",
                "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty",
                "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such",
                "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence",
                "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv",
                "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to",
                "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up",
                "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence",
                "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether",
                "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with",
                "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the");
        Remove filter = new Remove();
        List<Integer> toRemove = new ArrayList<Integer>();
        for (int i=1; i<m_unigram.numAttributes(); i++) {
            if (!functionWordList.contains(m_unigram.attribute(i).name())) {
                toRemove.add(i);
            }
        }
        Integer[] wrapperArr = toRemove.toArray(new Integer[toRemove.size()]);
        int[] toRem = ArrayUtils.toPrimitive(wrapperArr);
        filter.setAttributeIndicesArray(toRem);
        filter.setInputFormat(m_unigram);
        m_allFeat_train = Filter.useFilter(m_unigram, filter);
        m_allFeat_test = Filter.useFilter(m_unigram_test, filter);
    }

    public void onlyClasses() throws Exception {
        assert m_isUnigram_train;
        assert m_isUnigram_test;
        List<String> functionWordList = Arrays.asList();
        Remove filter = new Remove();
        List<Integer> toRemove = new ArrayList<Integer>();
        for (int i=1; i<m_unigram.numAttributes(); i++) {
            if (!functionWordList.contains(m_unigram.attribute(i).name())) {
                toRemove.add(i);
            }
        }
        Integer[] wrapperArr = toRemove.toArray(new Integer[toRemove.size()]);
        int[] toRem = ArrayUtils.toPrimitive(wrapperArr);
        filter.setAttributeIndicesArray(toRem);
        filter.setInputFormat(m_unigram);
        m_allFeat_train = Filter.useFilter(m_unigram, filter);
        m_allFeat_test = Filter.useFilter(m_unigram_test, filter);
    }

    public void loadFeatures(String fileIn, boolean train) throws Exception {
        if (train) {
            this.m_allFeat_train = Features.loadARFF(fileIn);
            this.m_isUnigram_train = true;
        }
        else {
            this.m_allFeat_test = Features.loadARFF(fileIn);
            this.m_isUnigram_test = true;
        }
    }

    public void computeMostCommon() throws Exception {
        assert m_isUnigram_train;
        assert m_isUnigram_test;
        Scanner s = new Scanner(new File("resources/mostCommon.txt"));
        ArrayList<String> mostCommonList = new ArrayList<String>();
        while (s.hasNext()){
            mostCommonList.add(s.next());
        }
        s.close();

        Remove filter = new Remove();
        List<Integer> toRemove = new ArrayList<Integer>();
        for (int i=1; i<m_unigram.numAttributes(); i++) {
            if (!mostCommonList.contains(m_unigram.attribute(i).name())) {
                toRemove.add(i);
            }
        }
        Integer[] wrapperArr = toRemove.toArray(new Integer[toRemove.size()]);
        int[] toRem = ArrayUtils.toPrimitive(wrapperArr);
        filter.setAttributeIndicesArray(toRem);
        filter.setInputFormat(m_unigram);
        m_allFeat_train = Filter.useFilter(m_unigram, filter);
        m_allFeat_test = Filter.useFilter(m_unigram_test, filter);
    }


    public void computePOS() throws Exception {
        // assert init
        assert m_isLoaded_POS;

        // load the data
        Instances inputInstances = m_POS;

        // Set the tokenizer
        NGramTokenizer tokenizer = new NGramTokenizer();
        // We want unigrams
        tokenizer.setNGramMinSize(1);
        tokenizer.setNGramMaxSize(1);

        // Set the filter
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(inputInstances);
        filter.setTokenizer(tokenizer);
        filter.setWordsToKeep(1000000);
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);

        // Filter the input instances into the output ones
        Instances outputInstances = Filter.useFilter(inputInstances,filter);

        // Remove @@class@@
        Remove rem = new Remove();
        int[] toRem = {0};
        rem.setAttributeIndicesArray(toRem);
        rem.setInputFormat(outputInstances);
        outputInstances = Filter.useFilter(outputInstances, rem);
        renamedPOS(outputInstances);

        // add to allFeat
        safeMerge(outputInstances, true);

        // compute the test instances
        Instances inputTest = m_POS_test;
        Instances outputTest = Filter.useFilter(inputTest,filter);
        outputTest = Filter.useFilter(outputTest, rem);

        renamedPOS(outputTest);
        safeMerge(outputTest, false);
    }

    //TODO
    public void computePOSbi() {

    }

    private void renamedPOS(Instances inst) {
        for (int iAtt=0; iAtt< inst.numAttributes(); iAtt++) {
            inst.renameAttribute(iAtt, inst.attribute(iAtt).name() + "POS");
        }
    }

    public void computeEtymology() throws IOException {
        assert m_isUnigram_train;
        assert m_isUnigram_test;
        // Connect to the Wiktionary database.
        IWiktionaryEdition wkt = JWKTL.openEdition(new File("resources/wikiDirectory"));

        // Get map of all attributes
        HashMap<String,Integer> etymologyToEtymAttribute = new HashMap<>();
        HashMap<String,Integer> wordToEtymologyList = new HashMap<>();
        HashMap<Integer,String> attributeToWord = new HashMap<>();
        List<List<String>> wordToEtymology = new ArrayList<>();

        wordToEtymology.add(new ArrayList<String>());

        int nAttributes = 0;
        int nGoodWords = 0;
        String currWord;

        // Create Instance
        ArrayList<Attribute> etymInstance = new ArrayList<Attribute>();

        // lookup all etymologies of all words
        boolean first = false;
        for (int iWord = 1; iWord < m_unigram.numAttributes(); iWord++) {
            List<String> lEtym = new ArrayList<>();
            currWord = m_unigram.attribute(iWord).name();
            if (StringUtils.isAlpha(currWord) ) {
                // look up current word
                List<IWiktionaryEntry> entries = wkt.getEntriesForWord(currWord, true);

                // only keep the first (=most common) English entry
                first = false;
                for (IWiktionaryEntry entry : entries) {
                    if (first) {
                        break;
                    }
                    if ((entry.getWordEtymology() != null) && (entry.getWordLanguage() == Language.ENGLISH)) {
                        first = true;
                        if (!goodEntry(entry)) {
                            break;
                        }
                        // remember the index of this word
                        wordToEtymologyList.put(currWord, nGoodWords);
                        attributeToWord.put(iWord, currWord);
                        nGoodWords++;

                        //remember the etymologies of this word
                        lEtym = getEtymologiesFromPlainText(entry.getWordEtymology().getText());

//                        System.out.println(currWord);
//                        System.out.println(Arrays.toString(lEtym.toArray()));

                        // remember all etymologies
                        for (String etym : lEtym) {
                            // check if we've seen this etymology before
                            if (!etymologyToEtymAttribute.containsKey(etym)) {
                                etymologyToEtymAttribute.put(etym, nAttributes);
                                nAttributes++;
                                etymInstance.add(new Attribute("etyl:" + etym));
                            }
                        }

                    }
                }
            }
            wordToEtymology.add(lEtym);
        }
//        int i=0;
//        for (List<String> l: wordToEtymology) {
//            System.out.println(attributeToWord.get(i) + Arrays.toString(l.toArray()));
//            i++;
//        }

        // Create an empty training set
        Instances etymInstances = new Instances("Rel", etymInstance, m_unigram.numInstances());

        Instance currInstance;
        String word;
        List<String> lEtym;
        double val;
//        for (int iAtt=0; iAtt<nAttributes;iAtt++) {
//                System.out.printf(etymInstances.attribute(iAtt).name() + ", ");
//        }
//        System.out.println(etymInstances.numAttributes());

        // train
        for (int iInstance=0; iInstance < m_unigram.numInstances(); iInstance++) {
            Instance newInstance = new DenseInstance(nAttributes);
            for (int iAtt=0; iAtt<nAttributes;iAtt++) {
                newInstance.setValue(iAtt, 0);
            }
            currInstance = m_unigram.instance(iInstance);

            // compute etymologies for one instance
            for (int iWord=1; iWord < m_unigram.numAttributes(); iWord++) {
                if (currInstance.value(iWord) > 0 && attributeToWord.containsKey(iWord)) {
                    lEtym = wordToEtymology.get(iWord);
//                    System.out.printf(attributeToWord.get(iWord) + " ");
//                    System.out.printf("%n");
                    for (String etym : lEtym) {
//                        System.out.printf(etym + " " + etymologyToEtymAttribute.get(etym) +", ");
                        val = newInstance.value(etymologyToEtymAttribute.get(etym));
                        newInstance.setValue(etymologyToEtymAttribute.get(etym), val + 1);
                    }
//                    System.out.printf("%n");
                }

            }
//            System.out.println(Arrays.toString(newInstance.toDoubleArray()));
            etymInstances.add(newInstance);
        }

        safeMerge(etymInstances, true);

        // test
        Instances etymInstances_test = new Instances("Rel", etymInstance, m_unigram_test.numInstances());
        for (int iInstance=0; iInstance < m_unigram_test.numInstances(); iInstance++) {
            Instance newInstance = new DenseInstance(nAttributes);
            for (int iAtt=0; iAtt<nAttributes;iAtt++) {
                newInstance.setValue(iAtt, 0);
            }
            currInstance = m_unigram_test.instance(iInstance);

            // compute etymologies for one instance
            for (int iWord=1; iWord < m_unigram_test.numAttributes(); iWord++) {
                if (currInstance.value(iWord) > 0 && attributeToWord.containsKey(iWord)) {
                    lEtym = wordToEtymology.get(iWord);
//                    System.out.printf(attributeToWord.get(iWord) + " ");
//                    System.out.printf("%n");
                    for (String etym : lEtym) {
//                        System.out.printf(etym + " " + etymologyToEtymAttribute.get(etym) +", ");
                        val = newInstance.value(etymologyToEtymAttribute.get(etym));
                        newInstance.setValue(etymologyToEtymAttribute.get(etym), val + 1);
                    }
//                    System.out.printf("%n");
                }

            }
//            System.out.println(Arrays.toString(newInstance.toDoubleArray()));
            etymInstances_test.add(newInstance);
        }

        safeMerge(etymInstances_test, false);

        wkt.close();

    }

    private boolean goodEntry(IWiktionaryEntry entry) {
        boolean b;
        b = ((entry.getPartOfSpeech() == PartOfSpeech.ADJECTIVE) ||
                (entry.getPartOfSpeech() == PartOfSpeech.ADVERB) || (entry.getPartOfSpeech() == PartOfSpeech.NOUN) ||
                (entry.getPartOfSpeech() == PartOfSpeech.VERB));
        return b;
    }

    private String getEtymFromPattern(String pattern) {
        String s;
        assert pattern.startsWith("{{etyl|");
        int iLetter = 7;
//        System.out.println(pattern);
        while ((pattern.charAt(iLetter) != '|') && pattern.charAt(iLetter) != '}') {
            iLetter++;
        }
        s = pattern.substring(7, iLetter);
        return s;
    }

    private List<String> getEtymologiesFromPlainText(String descr) {
        List<String> l = new ArrayList<>();
        String[] lWords = descr.split("[ ]+");
        for (String word: lWords) {
            if (word.startsWith("{{etyl|") && word.endsWith("}}")) {
                l.add(getEtymFromPattern(word));
            }
        }
        return l;
    }


    public void loadPOS(String fileIn, boolean train) throws Exception {
        TextDirectoryLoader txtLoader = new TextDirectoryLoader();
        txtLoader.setDirectory(new File(fileIn));
        Instances inputInstances = txtLoader.getDataSet();
        if (train) {
            m_POS = inputInstances;
        }
        else {
            m_POS_test = inputInstances;
        }
    }
}
