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

        //public int getIndex() {
        //    return index;
        //}

        //public int getValue() {
        //    return value;
        //}

        public static int size() {
            int size = 0;
            for (SizeSentenceMapping s : values()) {
                size++;
            }
            return size;
        }

        //public Size_Sentence_Mapping
    }
    /**
     * Takes a directory and does two things:
     * 1. Makes a 1000 sentence variation of a novel
     * 2. Makes a 1000 parse tree of the novel
     * This will be modified later to create an arbritary number of chunks per text, but for now it'll take the first
     * @param args arg[0] should be the language (the directory American/French/Russian/other language, arg[1] should be the novel name (INCLUDING .txt)
     */
    public static final int NUMBER_OF_SENTENCES = 1000;

    public static void main(String[] args) {
        //for (SizeSentenceMapping s : SizeSentenceMapping.values()) {
        //    System.out.println(s.getIndex());
        //}
        //System.out.println(SizeSentenceMapping.size());
        String directory = args[0];
        File rootDirectory = new File(directory);
        if (!rootDirectory.isDirectory()) {
            System.err.println("ERROR: " + rootDirectory.getName() + "is not a directory!");
        } else {
            //String parserModel = "Data/trees/englishPCFG.ser.gz";
            //LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
            //writeTrees(lp, rootDirectory);
            writeVaryingSentenceBlocks(rootDirectory);
        }
    }



    public static void writeVaryingSentenceBlocks (File directory) {
        File[] languages = directory.listFiles();
        outerloop:
        for (int i = 0; i < languages.length; i++) {
            File[] languageDirectories = new File[SizeSentenceMapping.size()];
            for (SizeSentenceMapping s: SizeSentenceMapping.values()) {
                languageDirectories[s.index] = new File("Data/txt_sentence_blocks/" + s.value + "/" + languages[i].getName());
                //if (!languageDirectories[s.index].exists()) {
                //    languageDirectories[s.index].mkdirs();
                //}
            }
            File[] novels = languages[i].listFiles();
            for (int j = 0; j < novels.length; j++) {
                if (j == 1) break outerloop;
                String noSuffixNovel = novels[j].getName().substring(0, novels[j].getName().length()-4);
                //two arrays to handle our enumerated cases as created in txt_sentence_blocks
                PrintWriter[] outputs = new PrintWriter[SizeSentenceMapping.size()];
                String[] fileRootNames = new String[SizeSentenceMapping.size()];
                int[] counters = new int[SizeSentenceMapping.size()];
                for (SizeSentenceMapping s : SizeSentenceMapping.values()) {
                    File newFile = new File(languageDirectories[s.index].getAbsolutePath() + "/" + noSuffixNovel);
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
                        String str = sentenceToString(sentence);
                        countSentences++;
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

    /**
     * Writes both a 1000 sentence variation of our original text AND a tree parsed version of it too.
     * @param lp
     * @param directory
     */
    public static void writeTrees(LexicalizedParser lp, File directory) {
        File[] languages = directory.listFiles();
        outerloop:
        for (int i = 0; i < languages.length; i++) {
            File[] novels = languages[i].listFiles();
            for (int j = 0; j < novels.length; j++) {
                if (j == 1) break outerloop;
                //if (j == 1) System.out.println("Failed to break out of loop completely!");
                String txt = novels[j].getAbsolutePath();
                txt = txt.substring(0, txt.length()-4);
                txt = txt + "_1000sentences.txt";
                File textFile = new File(txt);
                String treeName =  "Data/trees/" + languages[i].getName() + "/"  + textFile.getName();;
                File treeFile = new File(treeName);
                System.err.println("Processing " + textFile.getName() + ":");
                //first, outputing the text file of just 1000 sentences. not the prettiest way of doing things but I think it'll work.
                /*try {
                    textFile.createNewFile();
                    BufferedWriter textOutputStream = new BufferedWriter(new PrintWriter(textFile));
                    Reader textReader = new BufferedReader(new FileReader(novels[j]));

                    //a bit ugly, but I have to read a character at a time in order to find punctuation.
                    // If we hit 1000 sentences, I make sure the next character isn't a quotation mark to properly ensure
                    //quotes are closed in our final sentence
                    int countPunct = 0;
                    do {
                        int c = textReader.read();
                        if (c == -1)
                            break;
                        char d = (char) c;
                        textOutputStream.write(c);
                        if ((char) c == '.' || (char) c == '?' || (char) c == '!') {
                            countPunct++;
                            if (countPunct == 1000) {
                                c = textReader.read();
                                if ((char) c == '"') {
                                    textOutputStream.write(c);
                                }
                            }
                            textOutputStream.flush();
                        }

                    } while(countPunct < NUMBER_OF_SENTENCES);
                    textOutputStream.close();
                } catch (IOException e) {
                    System.err.println("Exception caught: " + e);
                }*/


                //next, outputting the penn tree using the Stanford parser.
                try {
                    treeFile.createNewFile();
                    PrintWriter treeOutputStream = new PrintWriter(treeFile);
                    DocumentPreprocessor p = new DocumentPreprocessor(novels[j].getAbsolutePath());
                    Iterator<List<HasWord>> it = p.iterator();
                    for (int k = 0; k < NUMBER_OF_SENTENCES; k++) {
                        List<HasWord> sentence = it.next();
                        Tree parse = lp.apply(sentence);
                        parse.pennPrint(treeOutputStream);
                        treeOutputStream.println();
                        treeOutputStream.flush();
                    }
                    treeOutputStream.close();
                } catch (IOException e) {
                    System.err.println("Exception caught: " + e);
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


