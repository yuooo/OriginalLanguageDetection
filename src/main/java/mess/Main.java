package mess;

import mess.Algorithm.ClassifierOLI;
import mess.Features.LexicalFeature;
import mess.Features.ParseFeature;
import weka.core.Instances;

import java.io.File;

import static mess.utils.Utils.T;
import static mess.utils.Utils.pT;

public class Main {

    public static void main(String[] args) throws Exception {
        String sizeSlice = "500/";

	    // load data
        T();
        System.out.println("Start Loadind.");
        LexicalFeature feat = new LexicalFeature();

        feat.loadRawTxt("Data/block_text/" + sizeSlice + "train/", true);
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
        feat.loadRawTxt("Data/block_text/" + sizeSlice + "test/", false);
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

        // etymology
//        T();
//        System.out.println("Etymology.");
//        feat.computeEtymology();
//        pT("Etymology");

//        // load POS
//        T();
//        System.out.println("Load POS.");
//        feat.loadPOS("Data/block_POS/" + sizeSlice +"train/", true);
//        feat.loadPOS("Data/block_POS/" + sizeSlice +"test/", false);
//        pT("Load POS");
//
//        // compute POS
//        T();
//        System.out.println("Compute POS.");
//        feat.computePOS();
//        pT("Compute POS");

        // save lexical features
        T();
        System.out.println("Save Lexical features.");
        feat.saveFeatures("Data/weka/lexica_feat/" + sizeSlice + "lexical.arff", true);
        feat.saveFeaturesCSV("Data/csv/" + sizeSlice + "unigram.csv", true);
        feat.saveFeatures("Data/weka/lexical_feat/" + sizeSlice + "lexical_test.arff", false);
        feat.saveFeaturesCSV("Data/csv/" + sizeSlice + "unigram_test.csv", false);
        pT("Save unigram");



        // computer parse features
        T();
        System.out.println("Start parse features.");
        ParseFeature parseFeat = new ParseFeature();
        parseFeat.parseMe(new File("Data/block_trees/" + sizeSlice + "train/"), "train");
        parseFeat.parseMe(new File("Data/block_trees/" + sizeSlice + "test/"), "test");

        Instances parseInst = parseFeat.trainToWeka();
        Instances parseInst_test = parseFeat.testToWeka();
        pT("Parse Features");



//        // compute homemade features
//        T();
//        System.out.println("Start homemade features.");
//        HomemadeFeature homeFeat = parseFeat.getHomemadeFeatures();
//        Instances homeFeatInst = homeFeat.trainToWeka();
//        Instances homeFeatInst_test = homeFeat.testToWeka();
//        pT("Homemade Features");


        // combine features
        T();
        System.out.println("Start combining.");
        feat.brutalMerge(parseInst, true);
        feat.brutalMerge(parseInst_test, false);
        pT("Merged Parse.");

//        T();
//        feat.brutalMerge(homeFeatInst, true);
//        feat.brutalMerge(homeFeatInst_test, false);
//        pT("Merged Homemade.");

        // saving
        T();
        System.out.println("Save all features.");
        feat.saveFeatures("Data/weka/all_feat/" + sizeSlice + "allFeat.arff", true);
        feat.saveFeaturesCSV("Data/csv/" + sizeSlice + "allFeat.csv", true);
        feat.saveFeatures("Data/weka/all_feat/" + sizeSlice + "allFeat_test.arff", false);
        feat.saveFeaturesCSV("Data/csv/" + sizeSlice + "allFeat_test.csv", false);
        pT("Save unigram");

        Instances allFeat = feat.trainToWeka();
        Instances allFeat_test = feat.testToWeka();

        // train/test
        T();
        System.out.println("Start train");
        ClassifierOLI cloli = new ClassifierOLI("rf");
        cloli.train(allFeat);
        pT("Train");

        cloli.test(allFeat, allFeat_test);



    }
}
