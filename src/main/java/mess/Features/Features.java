package mess.Features;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;
import weka.core.converters.TextDirectoryLoader;

import java.io.File;
import java.io.IOException;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public abstract class Features {
    protected Instances m_allFeat = null;

    public Instances m_data = null;
    protected boolean m_isLoaded = false;

    public Instances loadRawTxt(String pathFile) throws IOException {
        TextDirectoryLoader txtLoader = new TextDirectoryLoader();
        txtLoader.setDirectory(new File(pathFile));
        Instances inputInstances = txtLoader.getDataSet();
        m_data = inputInstances;
        m_isLoaded = true;
        return inputInstances;
    }

    public Instances toWeka() {
        return this.m_allFeat;
    }

    protected void safeMerge(Instances inst) {
        if (m_allFeat == null) {
            m_allFeat = inst;
        }
        else {
            Instances.mergeInstances(m_allFeat, inst);
        }
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


    private void saveCSV(Instances inst, String fileOut) throws IOException {
        CSVSaver saver = new CSVSaver();
        saver.setInstances(inst);
        saver.setFile(new File(fileOut));
        saver.writeBatch();
    }

    public void saveDataCSV(String fileOut) throws IOException {
        saveCSV(m_data, fileOut);
    }

    public void saveFeaturesCSV(String fileOut) throws IOException {
        saveCSV(m_allFeat, fileOut);
    }

}
