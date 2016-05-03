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

    //makes doing everything associated with these values much easier, plus allows us to more flexibly add future sentence values if needed.
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
     */

    public static void main(String[] args) {
        int arg = 0;
        boolean txtFlag = false;
        boolean treeFlag = false;
        boolean oneNovel = false;
        String oneNovelName = null;
        while (args[arg].startsWith("-")) {
            if (args[arg].equals("-text")) {
                txtFlag = true;
            } else if (args[arg].equals("-tree")) {
                treeFlag = true;
            } else if (args[arg].equals("-oneNovel")) {
                arg++;
                oneNovel = true;
                oneNovelName = new File(args[arg]).getName();
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
            //String parserModel = "Data/trees/englishPCFG.ser.gz";
            //LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
            //writeTrees(lp, rootDirectory);
            if (txtFlag) {
                writeToFiles(rootDirectory, oneNovelName, "text");
            } if (treeFlag) {
                writeToFiles(rootDirectory, oneNovelName, "tree");
            }
        }
    }



    private static void writeToFiles (File directory, String oneNovelName, String type) {
        File[] languages;
        if (oneNovelName != null) {
            languages = new File[1];
            languages[0] = directory;
        } else{
            languages= directory.listFiles();
        }
        LexicalizedParser lp = null;
        String dataDirectory = null;
        switch(type) {
            case "tree":
                dataDirectory = "trees";
                String parserModel = "Data/trees/englishPCFG.ser.gz";
                lp = LexicalizedParser.loadModel(parserModel);
                break;
            case "text":
                dataDirectory = "txt_sentence_blocks_CORRECT";
                break;
        }
        //outerloop:
        for (int i = 0; i < languages.length; i++) {
            File[] languageDirectories = new File[SizeSentenceMapping.size()];
            File[] novels;
            if (oneNovelName != null) {
                novels = new File[1];
                novels[0] = new File(directory.getAbsolutePath() + "/" + oneNovelName);
            } else {
                novels = languages[i].listFiles();
            }



            for (SizeSentenceMapping s: SizeSentenceMapping.values()) {
                languageDirectories[s.index] = new File("Data/" + dataDirectory + "/" + s.value + "/" + languages[i].getName());
                //if (!languageDirectories[s.index].exists()) {
                //    languageDirectories[s.index].mkdirs();
                //}
            }

            for (int j = 0; j < novels.length; j++) {

                String noSuffixNovel = novels[j].getName().substring(0, novels[j].getName().length() - 4);

                //two arrays to handle our enumerated cases as created in txt_sentence_blocks
                PrintWriter[] outputs = new PrintWriter[SizeSentenceMapping.size()];
                String[] fileRootNames = new String[SizeSentenceMapping.size()];
                int[] counters = new int[SizeSentenceMapping.size()];
                for (SizeSentenceMapping s : SizeSentenceMapping.values()) {
                    File newFile = new File(languageDirectories[s.index].getAbsolutePath());
                    if (!newFile.exists()) {
                        newFile.mkdirs();
                    }
                    String pathWithNovelName = newFile.getAbsolutePath() + "/" + noSuffixNovel;
                    fileRootNames[s.index] = pathWithNovelName;
                    counters[s.index] = 1;
                    File temp = createNewNumberedFile(pathWithNovelName, counters[s.index]);
                    try {
                        outputs[s.index] = new PrintWriter(temp);
                    } catch (FileNotFoundException e) {
                        System.err.println("Could not create file for " + temp.getName() + "!");
                    }
                }

                System.err.println("Processing " + novels[j].getName() + ":");
                //first, outputing the text file of just 1000 sentences. not the prettiest way of doing things but I think it'll work.
                try {
                    DocumentPreprocessor p = new DocumentPreprocessor(novels[j].getAbsolutePath());
                    Iterator<List<HasWord>> it = p.iterator();
                    int countSentences = 0;
                    while (it.hasNext()) {
                        List<HasWord> sentence = it.next();
                        countSentences++;
                        switch (type) {
                            case "text":
                                String str = sentenceToString(sentence);
                                for (SizeSentenceMapping s : SizeSentenceMapping.values()) {
                                    outputs[s.index].print(str);
                                    outputs[s.index].println();
                                    if (countSentences % s.value == 0) {
                                        outputs[s.index].flush();
                                        outputs[s.index].close();
                                        counters[s.index]++;
                                        File temp = createNewNumberedFile(fileRootNames[s.index], counters[s.index]);
                                        outputs[s.index] = new PrintWriter(temp);
                                    }
                                }
                                break;
                            case "tree":
                                Tree parse = lp.apply(sentence);
                                for (SizeSentenceMapping s : SizeSentenceMapping.values()) {
                                    parse.pennPrint(outputs[s.index]);
                                    outputs[s.index].println();
                                    if (countSentences % s.value == 0) {
                                        outputs[s.index].flush();
                                        outputs[s.index].close();
                                        counters[s.index]++;
                                        File temp = createNewNumberedFile(fileRootNames[s.index], counters[s.index]);
                                        outputs[s.index] = new PrintWriter(temp);
                                    }
                                }
                                break;
                        }

                    }
                } catch (IOException e) {
                    System.err.println("Exception caught: " + e);
                }

                //ensure PrintWriters Finish
                //try{Thread.sleep(5000);}catch (InterruptedException e){}

                //delete the last file of each directory... likely not going to be empty so just delete it.
                for (SizeSentenceMapping s : SizeSentenceMapping.values()) {
                    File temp = createNewNumberedFile(fileRootNames[s.index], counters[s.index]);
                    temp.delete();
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


