package mess;

import mess.Algorithm.ClassifierOLI;
import mess.Features.LexicalFeature;
import weka.core.Instances;

import static mess.utils.Utils.T;
import static mess.utils.Utils.pT;

public class Main {

    public static void main(String[] args) throws Exception {
	    // load data
        T();
        System.out.println("Start Loadind.");
        LexicalFeature feat = new LexicalFeature();
        feat.loadRawTxt("Data/txt_sentence_blocks/short_pipeline/");
        pT("Load");

        // save data
        T();
        System.out.println("Start Saving.");
        feat.saveData("Data/weka/sentences_blocks/short_pipeline/train.arff");
        pT("Save data");

        // compute lexical features
        T();
        System.out.println("Start Lexical features.");
        feat.computeUnigram();
        pT("Compute unigram");

        // save lexical features
        T();
        System.out.println("Save Lexical features.");
        feat.saveFeatures("Data/weka/unigram_feat/short_pipeline/unigram.arff");
        pT("Save unigram");


        // computer parse features
        System.out.println("Start parse features.");

        // compute homemade features
        System.out.println("Start homemade features.");

        // combine features
        System.out.println("Start combining.");
        Instances allFeat = feat.toWeka();

        // train/test
        T();
        System.out.println("Start CV");
        ClassifierOLI cloli = new ClassifierOLI();
        cloli.trainCV(allFeat);
        pT("Classif");


    }
}
