package mess.Features;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWikiString;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.WiktionaryFormatter;
import de.tudarmstadt.ukp.jwktl.api.filter.WiktionaryEntryFilter;
import de.tudarmstadt.ukp.jwktl.api.util.Language;
import edu.stanford.nlp.util.ArrayUtils;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public class LexicalFeature extends Features {
    private boolean m_isUnigram_train = false;
    private boolean m_isUnigram_test = false;

    private Instances m_unigram;
    private Instances m_unigram_test;



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

    //TODO
    public void computePOS() {
        /**From Matt:
        //You actually need to load another file separately for the trees.
        //Here's what you have to do:
            1. Input the directory for the trees ("Data/trees/[train OR test]"
            2. For each file:
                a. Call TreeToSentenceHandler h = new TreeToSentenceHandler(file)
                b. while (h.hasNext)
                    i. TextPOSTreeTriple t = h.generateSentence()
                    ii. List<String> POSSentenct = h.POS() //***this is your POS sentence.***
         */

    }

    //TODO
    public void computePOSbi() {

    }

    //TODO
    public void computeEtymology() {
        // Connect to the Wiktionary database.
        IWiktionaryEdition wkt = JWKTL.openEdition(new File("resources/wikiDirectory"));
        WiktionaryEntryFilter filter = new WiktionaryEntryFilter();
        filter.setAllowedWordLanguages(Language.ENGLISH);

        List<IWiktionaryEntry> entries = wkt.getEntriesForWord("to", true);
        for (IWiktionaryEntry entry : entries) {
            // we take only the first English entry
            if ((entry.getWordEtymology() != null) && (entry.getWordLanguage() == Language.ENGLISH)) {
                System.out.println(WiktionaryFormatter.instance().formatHeader(entry));
//                System.out.println(entry.getWordEtymology().getText());
//                System.out.println(entry.getWordEtymology().getPlainText());
                System.out.println(entry.getWordEtymology().getWikiLinks());
                System.out.println(Arrays.toString(entry.getWordEtymology().getWikiLinks().toArray()));
                IWikiString s = entry.getWordEtymology();
            }
        }

        // Close the database connection.
        wkt.close();

    }
}
