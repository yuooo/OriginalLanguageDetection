package mess;

import mess.Algorithm.ClassifierOLI;
import mess.Features.LexicalFeature;
import weka.core.Instances;

public class Main {

    public static void main(String[] args) throws Exception {
	    // load data

        // compute lexical features
        System.out.println("Start Loadind.\n");
        LexicalFeature feat = new LexicalFeature();

        System.out.println("Start Lexical features.\n\n");
        feat.computeUnigram("Data/txt_clean/");


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
