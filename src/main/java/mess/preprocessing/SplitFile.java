package mess.preprocessing;

import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Treebank;

import java.io.*;
import java.util.Iterator;

/**
 * Created by mad4672 on 5/3/16.
 */
public class SplitFile {


    /**
     * Allows a user to split either text files or tree files. Both use \n to delimit, but differ slightly in delimitation:
     *      Text uses \n to delimit per sentence
     *      Tree delimits using \n as the only character on the line to note that a tree has finished.
     * User can indicate number of files they want using -trainnum # and -testnum # to determine. The sum of those make the number of files extracted.
     *      Defaults are 0 for both train and test, respectively.
     *      The train files will be read in first, then the test files will be read in second. Example, with 19 train files and 1 test file, our code
     *      reads in 19 files first for training, then 1 for testing.
     * Set the number of seeds per file by using the -seeds option. Default is 100.
     * Need to call either -tree or -text or neither will execute. Can't call both, of course.
     * Also need to provide the source directory and the destination directory, in that order.
     *      Source Directory: Needs to have languages in subdirectories, and these languages contain the novels in text or tree format.
     *      Destination Directory: Will write into this directory (it doesn't need to exist yet) train/ and test/ directories. From each, the languages,
     *          and all chunks of all novels will be written in there.
     *
     * The command should look something like this (hyphenated command order doesn't matter... BUT root directory must be first and destination second!
     *
     * Also, hyphenated commands must come first (like in Unix).
     *      SplitFile -tree/-text [-seednum #] [-trainnum #] [-testnum #] source_directory destination_directory
     * @param args
     */
    public static void main(String[] args) {
        int arg = 0;
        int seedNum = 100;
        int trainNum = 0;
        int testNum = 0;
        boolean treeFlag = false;
        boolean textFlag = false;
        //handle options
        while (args[arg].startsWith("-")) {
           switch(args[arg]) {
               case ("-tree"):
                   treeFlag = true;
                   break;
               case ("-text"):
                   textFlag = true;
                   break;
               case ("-seednum"):
                   arg++;
                   seedNum = Integer.parseInt(args[arg]);
                   break;
               case ("-trainnum"):
                   arg++;
                   trainNum = Integer.parseInt(args[arg]);
                   break;
               case ("-testnum"):
                   arg++;
                   testNum = Integer.parseInt(args[arg]);
                   break;
               case ("-trainall"):
                   trainNum = Integer.MAX_VALUE;
                   break;
               case ("-testall"):
                   testNum = Integer.MAX_VALUE;
                   break;
               default:
                   throw new IllegalArgumentException("Unrecognized command: " + args[arg]);
           }
            arg++;
        }
        File sourceDirectory = new File(args[arg]);
        arg++;
        File destinationDirectory = new File(args[arg]);

        if(treeFlag && textFlag) {
            System.err.println("Both text and tree flags detected, which one do you want?");
            System.exit(2);
        }

        if (trainNum == Integer.MAX_VALUE && testNum == Integer.MAX_VALUE) {
            System.err.println("Can't max both train and test values!");
            System.exit(3);
        }

        if (!sourceDirectory.isDirectory()) {
            System.err.println("ERROR: " + sourceDirectory.getName() + "is not a directory!");
            System.exit(4);
        }
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        } else if (!destinationDirectory.isDirectory()) {
            System.err.println("ERROR: " + destinationDirectory.getName() + "is not a directory!");
            System.exit(5);
        }

        if (treeFlag) {
            createSeededFiles("tree", seedNum, trainNum, testNum, sourceDirectory, destinationDirectory);
        } else if (textFlag) {
            createSeededFiles("text", seedNum, trainNum, testNum, sourceDirectory, destinationDirectory);
        }
    }

    //here's where the magic happens.
    private static void createSeededFiles(String type, int seedNum, int trainNum, int testNum, File sourceDirectory,
                                          File destinationDirectory) {

        File trainDirectory = new File(destinationDirectory.getAbsolutePath() + "/train");
        if(trainNum > 0 && !trainDirectory.exists()) {
            trainDirectory.mkdir();
        }
        File testDirectory = new File(destinationDirectory.getAbsolutePath() + "/test");
        if(testNum > 0 && !testDirectory.exists()) {
            testDirectory.mkdir();
        }

        //for each language, we want to create a directory for that language, then parse trees/sentences through each of the novels listed there
        File[] languages = sourceDirectory.listFiles();
        File[] trainDirectoryLanguages = new File[languages.length];
        File[] testDirectoryLanguages = new File[languages.length];
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].getName().startsWith(".")) {
                continue;
            }
            trainDirectoryLanguages[i] = new File(trainDirectory.getAbsolutePath() + "/" + languages[i].getName());
            testDirectoryLanguages[i] = new File(testDirectory.getAbsolutePath() + "/" + languages[i].getName());
            if (trainNum > 0)
                makeNewDirectoryIfNotExists(trainDirectoryLanguages[i]);
            if (testNum > 0)
                makeNewDirectoryIfNotExists(testDirectoryLanguages[i]);

            File[] novels = languages[i].listFiles();
            for (int j = 0; j < novels.length; j++) {
                if (novels[j].getName().startsWith(".")) {
                    continue;
                }
                try {
                    BufferedReader read = new BufferedReader(new FileReader(novels[j]));
                    //name w/o .txt
                    String fileShortName = novels[j].getName().substring(0, novels[j].getName().length()-4);
                    String line;
                    //max number of stuff we want to read is based on our inputs
                    int maxFiles = testNum + trainNum;
                    long maxLines = (long) maxFiles * (long) seedNum;
                    int lineCount = 0;
                    int fileCount = 1;
                    //File currentFile = createNewNumberedFile(trainDirectoryLanguages[i].getAbsolutePath() + "/" + fileShortName, fileCount);
                    String pathToUse =  (fileCount <= trainNum) ?  trainDirectoryLanguages[i].getAbsolutePath() :
                            testDirectoryLanguages[i].getAbsolutePath();
                    File currentFile = createNewNumberedFile(pathToUse + "/" + fileShortName, fileCount);
                    PrintWriter pw = new PrintWriter(currentFile);

                    //this is the part that varies based on calling for text or trees.
                    switch (type) {
                        case "text":
                            while ((line = read.readLine()) != null && lineCount < maxLines ) {
                                pw.println(line);
                                lineCount++;
                                //when our lineCount mod seedNum is 0, we want to create another PrintWriter
                                if (lineCount % seedNum == 0) {
                                    fileCount++;
                                    //put in train or test, depending on our current file count.
                                    if (fileCount <= maxFiles) {
                                        pathToUse = (fileCount <= trainNum) ? trainDirectoryLanguages[i].getAbsolutePath() :
                                                testDirectoryLanguages[i].getAbsolutePath();
                                        currentFile = createNewNumberedFile(pathToUse + "/" + fileShortName, fileCount);
                                        pw.flush();
                                        pw.close();
                                        pw = new PrintWriter(currentFile);
                                    }
                                }
                            }
                            break;
                        case "tree":
                            //need to build a Treebank... lifting code from HW 3 to aid in this.
                            Options op = new Options();
                            op.doDep = false;
                            op.doPCFG = true;
                            op.setOptions("-goodPCFG", "-evals", "tsv");
                            Treebank treeBank = op.tlpParams.diskTreebank();
                            treeBank.loadPath(novels[j]);
                            Iterator<Tree> it = treeBank.iterator();
                            while((it.hasNext()) && lineCount < maxLines) {
                                lineCount++;
                                Tree t = it.next();
                                t.pennPrint(pw);
                                if (lineCount % seedNum == 0) {
                                    fileCount++;
                                    //put in train or test, depending on our current file count.
                                    if (fileCount <= maxFiles) {
                                        pathToUse = (fileCount <= trainNum) ? trainDirectoryLanguages[i].getAbsolutePath() :
                                                testDirectoryLanguages[i].getAbsolutePath();
                                        currentFile = createNewNumberedFile(pathToUse + "/" + fileShortName, fileCount);
                                        pw.flush();
                                        pw.close();
                                        pw = new PrintWriter(currentFile);
                                    }
                                }
                            }
                            break;
                    }
                    pw.flush();
                    pw.close();
                    //if numlines is not equal to maxlines then we'll remove the last file.
                    if (lineCount != maxLines) {
                        currentFile = createNewNumberedFile(pathToUse + "/" + fileShortName, fileCount);
                        currentFile.delete();
                    }
                } catch (IOException e) {
                    System.err.println("Exception caught while reading " + novels[j] + ":");
                    e.printStackTrace();
                }
            }


        }


    }

    private static void makeNewDirectoryIfNotExists(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private static File createNewNumberedFile(String path, int num) {
        int digitLength = Integer.toString(num).length();
        int numZeroes = 5 - digitLength;
        path += "_";
        for (int i = 0; i < numZeroes; i++) {
            path += "0";
        }
        return new File(path + num + ".txt");
    }


}
