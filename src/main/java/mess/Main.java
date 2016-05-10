package mess;

import mess.Algorithm.ClassifierOLI;
import mess.Features.LexicalFeature;
import weka.core.Instances;

import static mess.utils.Utils.T;
import static mess.utils.Utils.pT;

public class Main {

    public static void main(String[] args) throws Exception {
        String sizeSlice = "500/";

	    // load data
        T();
        System.out.println("Start Loadind.");
        LexicalFeature feat = new LexicalFeature();

        feat.loadRawTxt("Data/blocks/" + sizeSlice + "train/", true);
        pT("Load");

        // save data
        T();
        System.out.println("Start Saving.");
        feat.saveData("Data/weka/sentences_blocks/" + sizeSlice + "train.arff", true);
        pT("Save data");

        // test
        // load data
        T();
        System.out.println("Start Loadind test.");
        feat.loadRawTxt("Data/blocks/" + sizeSlice + "test/", false);
        pT("Load test.");

        // save data
        T();
        System.out.println("Start Saving test.");
        feat.saveData("Data/weka/sentences_blocks/" + sizeSlice + "test.arff", false);
        pT("Save data test");

        // compute lexical features
        T();
        System.out.println("Start Lexical features.");
        feat.computeUnigram();
        pT("Compute unigram");

        // function words
        T();
        System.out.println("Most common words.");
        feat.computeMostCommon();
        pT();

        // save lexical features
        T();
        System.out.println("Save Lexical features.");
        feat.saveFeatures("Data/weka/unigram_feat/" + sizeSlice + "unigram.arff", true);
        feat.saveFeaturesCSV("Data/csv/" + sizeSlice + "unigram.csv", true);
        feat.saveFeatures("Data/weka/unigram_feat/" + sizeSlice + "unigram_test.arff", false);
        feat.saveFeaturesCSV("Data/csv/" + sizeSlice + "unigram_test.csv", false);
        pT("Save unigram");




        // combine features
        System.out.println("Start combining.");
        Instances allFeat = feat.trainToWeka();
        Instances allFeat_test = feat.testToWeka();

        // computer parse features
        System.out.println("Start parse features.");

        // compute homemade features
        System.out.println("Start homemade features.");


        // train/test
        T();
        System.out.println("Start train");
        ClassifierOLI cloli = new ClassifierOLI("rf");
        cloli.train(allFeat);
        pT("Train");

        cloli.test(allFeat, allFeat_test);



    }
}
