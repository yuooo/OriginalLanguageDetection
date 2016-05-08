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

        feat.loadRawTxt("Data/blocks/" + sizeSlice + "train/");
        pT("Load");

        // save data
        T();
        System.out.println("Start Saving.");
        feat.saveData("Data/weka/sentences_blocks/" + sizeSlice + "train.arff");
        pT("Save data");

        // compute lexical features
        T();
        System.out.println("Start Lexical features.");
        feat.computeUnigram();
        pT("Compute unigram");

        // save lexical features
        T();
        System.out.println("Save Lexical features.");
        feat.saveFeatures("Data/weka/unigram_feat/" + sizeSlice + "unigram.arff");
        pT("Save unigram");

        // test
        // load data
        T();
        System.out.println("Start Loadind test.");
        LexicalFeature feat_test = new LexicalFeature();
        feat_test.loadRawTxt("Data/blocks/" + sizeSlice + "test/");
        pT("Load test.");

        // save data
        T();
        System.out.println("Start Saving test.");
        feat_test.saveData("Data/weka/sentences_blocks/" + sizeSlice + "test.arff");
        pT("Save data test");

        // compute lexical features
        T();
        System.out.println("Start Lexical features test.");
        feat_test.computeUnigram();
        pT("Compute unigram test ");

        // save lexical features
        T();
        System.out.println("Save Lexical features.");
        feat.saveFeatures("Data/weka/unigram_feat/" + sizeSlice + "unigram_test.arff");
        pT("Save unigram test");

        // combine features
        System.out.println("Start combining.");
        Instances allFeat_test = feat_test.toWeka();

        // computer parse features
        System.out.println("Start parse features.");

        // compute homemade features
        System.out.println("Start homemade features.");

        // combine features
        System.out.println("Start combining.");
        Instances allFeat = feat.toWeka();

        // train/test
        T();
        System.out.println("Start train");
        ClassifierOLI cloli = new ClassifierOLI("rf");
        cloli.train(allFeat);
        pT("Train");

        cloli.test(allFeat, allFeat_test);



    }
}
