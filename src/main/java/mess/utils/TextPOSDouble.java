package mess.utils;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mad4672 on 5/6/16.
 */
public class TextPOSDouble {
    private List<String> words;
    private List<String> POS;

    TextPOSDouble(List<Word> words, List<Label> preYield) {
        this.words = new ArrayList<>();
        POS = new ArrayList<>();
        for (Word w : words) {
            this.words.add(w.word());
        }
        for (Label l : preYield) {
            POS.add(l.value());
        }
    }

    /**
     * Grabs the words in this sentence.
     * @return List<String> of words in a sentence.
     */
    public List<String> getWords() {
        return words;
    }

    /**
     * Grabs the POS tags that correlate to the words in the sentence.
     * @return List<String> of POS correlating to the words.
     */
    public List<String> getPOS() {
        return POS;
    }

}