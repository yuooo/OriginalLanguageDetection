package mess.Features;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.IOException;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public class LexicalFeature extends Features {
    Instances m_data = null;
    private boolean m_isLoaded = false;


    public Instances loadRawTxt(String pathFile) throws IOException {
        TextDirectoryLoader txtLoader = new TextDirectoryLoader();
        txtLoader.setDirectory(new File(pathFile));
        Instances inputInstances = txtLoader.getDataSet();
        m_data = inputInstances;
        m_isLoaded = true;
        return inputInstances;
    }

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



    private Instances loadARFF(String fileIn) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(fileIn);
        Instances inst = source.getDataSet();
        return inst;
    }

    public void loadData(String fileIn) throws Exception {
        this.m_data = loadARFF(fileIn);
    }

    public void loadFeatures(String fileIn) throws Exception {
        this.m_allFeat = loadARFF(fileIn);
    }

    private void saveARFF(Instances inst, String fileOut) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(inst);
        saver.setFile(new File(fileOut));
        saver.writeBatch();
    }


    public void saveData(String fileOut) throws IOException {
        saveARFF(m_data, fileOut);
    }

    public void saveFeatures(String fileOut) throws IOException {
        saveARFF(m_allFeat, fileOut);
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
