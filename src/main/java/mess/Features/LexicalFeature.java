package mess.Features;

import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public class LexicalFeature extends Features {


    public Instances computeUnigram() throws Exception {
        // assert init
        assert m_isLoaded;

        // load the data
        Instances inputInstances = m_data;

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
        safeMerge(outputInstances);
        return outputInstances;
    }

    public Instances computeUnigram(Instances inst) throws Exception {
        m_data = inst;
        m_isLoaded = true;
        return computeUnigram();
    }

    // TODO
    public Instances computeBigram(String pathFile) {
        return null;
    }

    // TODO
    public Instances computeFunctionWords(String pathFile) {
        return null;
    }
}
