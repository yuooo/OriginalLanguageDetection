package mess;

import mess.Algorithm.ClassifierOLI;
import mess.Features.LexicalFeature;
import weka.core.Instances;

public class Main {

    public static void main(String[] args) throws Exception {
	    // load data
        System.out.println("Start Loadind.\n");
        LexicalFeature feat = new LexicalFeature();
        feat.loadRawTxt("Data/txt_sentence_blocks/500/");

        // compute lexical features


        System.out.println("Start Lexical features.\n\n");
        feat.computeUnigram();


        // computer parse features
        System.out.println("Start parse features.\n\n");

        // compute homemade features
        System.out.println("Start homemade features.\n\n");

        // combine features
        System.out.println("Start combining.\n\n");
        Instances allFeat = feat.toWeka();

        // train/test
        System.out.println("Start CV\n\n");
        ClassifierOLI cloli = new ClassifierOLI();
        double acc = 0;
        acc = cloli.trainCV(allFeat);
        System.out.println(acc);

        // confusion matrix

    }
}
