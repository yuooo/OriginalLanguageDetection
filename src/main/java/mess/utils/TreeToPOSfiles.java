package mess.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by mad4672 on 5/10/16.
 */
public class TreeToPOSfiles {



    public static void recurse(File root, File destination) {
        File[] files = root.listFiles();
        for (int i = 0; i < files.length; i++) {
            File output = new File(destination.getAbsolutePath() + "/" + files[i].getName());

            //recurse
            if(files[i].isDirectory()) {
                if (!output.exists()) {
                    output.mkdirs();
                }
                recurse(files[i], output);

            //feed the trees into this
            } else {
                TreeToSentenceHandler h = new TreeToSentenceHandler(files[i]);
                try {
                    PrintWriter w = new PrintWriter(new FileWriter(output));
                    while (h.hasNext()) {
                        TextPOSTreeTriple t = h.generateSentence();
                        List<String> POSSentence = t.getPOS();
                        for (String s : POSSentence) {
                            w.print(s + " ");
                        }
                        w.println();
                    }
                    w.flush();
                    w.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public static void main(String[] args) {
        File root = new File("Data/block_trees");
        File destination = new File ("Data/block_POS");
        recurse(root, destination);

    }


}
