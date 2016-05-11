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
    protected Instances m_allFeat_train = null;
    protected Instances m_allFeat_test = null;

    public Instances m_data_train = null;
    public Instances m_data_test = null;

    protected boolean m_isLoaded_train = false;
    protected boolean m_isLoaded_test = false;

    public Instances loadRawTxt(String pathFile, boolean train) throws IOException {
        TextDirectoryLoader txtLoader = new TextDirectoryLoader();
        txtLoader.setDirectory(new File(pathFile));
        Instances inputInstances = txtLoader.getDataSet();
        if (train) {
            m_data_train = inputInstances;
            m_isLoaded_train = true;
        }
        else {
            m_data_test = inputInstances;
            m_isLoaded_test = true;
        }
        return inputInstances;
    }

    public Instances trainToWeka() {
        return this.m_allFeat_train;
    }

    public Instances testToWeka() {
        return this.m_allFeat_test;
    }

    protected void safeMerge(Instances inst, boolean train) {
        if (train) {
            if (m_allFeat_train == null) {
                m_allFeat_train = inst;
            } else {
                m_allFeat_train = Instances.mergeInstances(m_allFeat_train, inst);
                m_allFeat_train.setClassIndex(0);
            }
        }
        else {
            if (m_allFeat_test == null) {
                m_allFeat_test = inst;
            } else {
                m_allFeat_test = Instances.mergeInstances(m_allFeat_test, inst);
                m_allFeat_test.setClassIndex(0);
            }
        }
    }

    public static Instances loadARFF(String fileIn) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(fileIn);
        Instances inst = source.getDataSet();
        return inst;
    }

    public void loadData(String fileIn, boolean train) throws Exception {
        if (train) {
            this.m_data_train = loadARFF(fileIn);
            this.m_isLoaded_train = true;
        }
        else {
            this.m_data_test = loadARFF(fileIn);
            this.m_isLoaded_test = true;
        }
    }

    public void loadFeatures(String fileIn, boolean train) throws Exception {
        if (train) {
            this.m_allFeat_train = loadARFF(fileIn);
        }
        else {
            this.m_allFeat_test = loadARFF(fileIn);
        }
    }

    private void saveARFF(Instances inst, String fileOut) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(inst);
        saver.setFile(new File(fileOut));
        saver.writeBatch();
    }

    public void saveData(String fileOut, boolean train) throws IOException {
        if (train) {
            saveARFF(m_data_train, fileOut);
        }
        else {
            saveARFF(m_data_test, fileOut);
        }
    }

    public void saveFeatures(String fileOut, boolean train) throws IOException {
        if (train) {
            saveARFF(m_allFeat_train, fileOut);
        }
        else {
            saveARFF(m_allFeat_test, fileOut);
        }
    }


    private void saveCSV(Instances inst, String fileOut) throws IOException {
        CSVSaver saver = new CSVSaver();
        saver.setInstances(inst);
        saver.setFile(new File(fileOut));
        saver.writeBatch();
    }

    public void saveDataCSV(String fileOut, boolean train) throws IOException {
        if (train) {
            saveCSV(m_data_train, fileOut);
        }
        else {
            saveCSV(m_data_test, fileOut);
        }
    }

    public void saveFeaturesCSV(String fileOut, boolean train) throws IOException {
        if (train) {
            saveCSV(m_allFeat_train, fileOut);
        }
        else {
            saveCSV(m_allFeat_test, fileOut);
        }
    }

}
