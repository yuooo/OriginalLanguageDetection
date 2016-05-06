package mess.preprocessing;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mad4672 on 4/24/16.
 */
public class MassPreprocessor {

    //this enum is no longer needed but I'm so proud having written it (my first ever enum!) that I'm just gonna keep this here...
    private enum SizeSentenceMapping {
        ONE (0, 1),
        FIVE (1, 5),
        TEN (2, 10),
        FIFTY (3, 50),
        HUNDRED (4, 100),
        FIVEHUNDRED (5, 500),
        THOUSAND (6, 1000),
        FIVETHOUSAND (7, 5000);

        private final int index;
        private final int value;

        SizeSentenceMapping (int index, int value) {
            this.index = index;
            this.value = value;
        }

        static int size() {
            int size = 0;
            for (SizeSentenceMapping s : values()) {
                size++;
            }
            return size;
        }

    }
    /**
     * Takes a directory and does two things:
     * 1. Makes a 1000 sentence variation of a novel
     * 2. Makes a 1000 parse tree of the novel
     * This will be modified later to create an arbritary number of chunks per text, but for now it'll take the first
     * @param args arg[0] should be the language (the directory American/French/Russian/other language, arg[1] should be the novel name (INCLUDING .txt)
     */
    public static final int NUMBER_OF_SENTENCES = 1000;

    /**
     * Takes either a single text file or a whole set of files, and converts them to either trees, text, or both.
     * Usage: MassPreprocessor [-text] [-tree] [-oneNovel novelName] rootDirectory
     * @param args could be the following:
     *             -text returns text files.
     *             -tree returns tree files.
     *             -oneNovel novel_file runs for one specified file. NOTE: In this mode, the rootDirectory MUST be the language of the book!
     *             rootDirectory the directory in which files are to be converted. Subdirectories need to have language names, and the novel names need to be
     *             in the language directories after that.
     *             -outputDirectory Output will go into this directory instead of the default directories
     */

    public static void main(String[] args) {
        int arg = 0;
        boolean txtFlag = false;
        boolean treeFlag = false;
        String oneNovelName = null;
        String textDirectory = null, treeDirectory  = null;
        while (args[arg].startsWith("-")) {
            if (args[arg].equals("-text")) {
                txtFlag = true;
            } else if (args[arg].equals("-tree")) {
                treeFlag = true;
            } else if (args[arg].equals("-oneNovel")) {
                arg++;
                oneNovelName = new File(args[arg]).getName();
            } else if (args[arg].equals("-textDirectory")) {
                arg++;
                textDirectory = new File(args[arg]).getName();
            } else if (args[arg].equals("-treeDirectory")) {
                arg++;
                textDirectory = new File(args[arg]).getName();
            } else  {
                throw new IllegalArgumentException("Invald option given: " + args[arg]);
            }
            arg++;
        }
        String directory = args[arg];
        File rootDirectory = new File(directory);
        if (!rootDirectory.isDirectory()) {
            System.err.println("ERROR: " + rootDirectory.getName() + "is not a directory!");
        } else {
            if (txtFlag) {
                writeToFiles(rootDirectory, oneNovelName, "text", textDirectory);
            } if (treeFlag) {
                writeToFiles(rootDirectory, oneNovelName, "tree", treeDirectory);
            }
        }
    }



    private static void writeToFiles (File directory, String oneNovelName, String type, String outputDirectory) {
        File[] languages;

        //really hacky way of doing one novel, otherwise we list all languages.
        if (oneNovelName != null) {
            languages = new File[1];
            languages[0] = directory;
        } else{
            languages= directory.listFiles();
        }
        LexicalizedParser lp = null;

        //dataDirectory can either be what we put into our command line or a default generic name.
        String dataDirectory = null;
        if (outputDirectory != null) {
            dataDirectory = outputDirectory;
        }
        switch(type) {
            case "tree":
                if (dataDirectory == null)
                    dataDirectory = "trees_merged";
                String parserModel = "Data/trees/englishPCFG.ser.gz";
                lp = LexicalizedParser.loadModel(parserModel);
                break;
            case "text":
                if (dataDirectory == null)
                    dataDirectory = "txt_sentence_blocks_merged";
                break;
        }
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].getName().startsWith(".")) {
                continue;
            }
            File languageDirectory = new File("Data/" + dataDirectory + "/" + languages[i].getName());
            File[] novels;
            if (oneNovelName != null) {
                novels = new File[1];
                novels[0] = new File(directory.getAbsolutePath() + "/" + oneNovelName);
            } else {
                novels = languages[i].listFiles();
            }

            //write either text or trees for each novel in our given directory.
            for (int j = 0; j < novels.length; j++) {
                if (novels[j].getName().startsWith(".")) {
                    continue;
                }
                //PrintWriter[] outputs = new PrintWriter[1];

                File newFile = new File(languageDirectory.getAbsolutePath());
                if (!newFile.exists()) {
                    newFile.mkdirs();
                }
                String pathWithNovelName = newFile.getAbsolutePath() + "/" + novels[j].getName();
                File temp = new File(pathWithNovelName);
                try {
                    PrintWriter output = new PrintWriter(temp);
                    System.err.println("Processing " + novels[j].getName() + ":");

                    //Calls Stanford Parser tool to detect sentences and split words for purposes of both text and trees...
                    //Very, very useful. Reads the entirety of a novel.
                    DocumentPreprocessor p = new DocumentPreprocessor(novels[j].getAbsolutePath());
                    Iterator<List<HasWord>> it = p.iterator();
                    while (it.hasNext()) {
                        List<HasWord> sentence = it.next();
                        //for text, just convert to a String and print it. for tree, apply the parse to a sentence and pennPrint it.
                        switch (type) {
                            case "text":
                                String str = sentenceToString(sentence);
                                output.println(str);
                                break;
                            case "tree":
                                Tree parse = lp.apply(sentence);
                                parse.pennPrint(output);
                                break;
                        }
                    }
                    output.flush();
                    output.close();
                } catch (FileNotFoundException e) {
                    System.err.println("Could not create file for " + temp.getName() + "!");
                }

            }
        }
    }


    public static String sentenceToString(List<HasWord> sentence) {
        String s = "";
        for (HasWord h: sentence) {
            s += h.word() + " ";
        }
        return s;
    }

    public static File createNewNumberedFile(String path, int num) {
        int digitLength = Integer.toString(num).length();
        int numZeroes = 5 - digitLength;
        path += "_";
        for (int i = 0; i < numZeroes; i++) {
            path += "0";
        }
        return new File(path + num + ".txt");
    }
}


