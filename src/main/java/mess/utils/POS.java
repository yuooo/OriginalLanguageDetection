package mess.utils;

import mess.Features.HomemadeFeature;

import java.util.*;

/**
 * Created by mad4672 on 5/9/16.
 */
public enum POS {
    CC(0, "Coordinating conjunction", Arrays.asList("Word", "Conjuncts", "Closed")),
    CD(1, "Cardinal number", Arrays.asList("Word", "Num", "Closed")),
    DT(2, "Determiner", Arrays.asList("Word", "Closed")),
    EX(3, "Existential there", Arrays.asList("Word", "Closed")),
    FW(4, "Foreign word", Arrays.asList("Word", "Neither")),
    IN(5, "Preposition or subordinating conjunction", Arrays.asList("Word", "Conjuncts", "Closed")),
    JJ(6, "Adjective", Arrays.asList("Word", "Open")),
    JJR(7, "Adjective, comparative", Arrays.asList("Word", "Open")),
    JJS(8, "Adjective, superlative", Arrays.asList("Word", "Open")),
    LS(9, "List item marker", Arrays.asList("Word", "Neither")),
    MD(10, "Modal", Arrays.asList("Word", "Closed")),
    NN(11, "Noun, singular or mass", Arrays.asList("Word", "Noun", "Open")),
    NNS(12, "Noun, plural", Arrays.asList("Word", "Noun", "Open")),
    NNP(13, "Proper noun, singular", Arrays.asList("Word", "Noun", "Open")),
    NNPS(14, "Proper noun, plural", Arrays.asList("Word", "Noun", "Open")),
    PDT(15, "Predeterminer", Arrays.asList("Word", "Closed")),
    POS(16, "Possessive ending", Arrays.asList("Neither")),
    PRP(17, "Personal pronoun", Arrays.asList("Word", "Pron", "Closed")),
    PRP$(18, "Possessive pronoun", Arrays.asList("Word", "Pron", "Closed")),
    RB(19, "Adverb", Arrays.asList("Word", "Open")),
    RBR(20, "Adverb, comparative", Arrays.asList("Word", "Open")),
    RBS(21, "Adverb, superlative", Arrays.asList("Word", "Open")),
    RP(22, "Particle", Arrays.asList("Word", "Closed")),
    SYM(23, "Symbol", Arrays.asList("Word", "Neither")),
    TO(24, "to", Arrays.asList("Word", "Closed")),
    UH(25, "Interjection", Arrays.asList("Word", "Open")),
    VB(26, "Verb, base form", Arrays.asList("Word", "FV", "Open")),
    VBD(27, "Verb, past tense", Arrays.asList("Word", "FV", "Open")),
    VBG(28, "Verb, gerund or present participle", Arrays.asList("Word", "Open")),
    VBN(29, "Verb, past participle", Arrays.asList("Word", "Open")),
    VBP(30, "Verb, non-3rd person singular present", Arrays.asList("Word", "FV", "Open")),
    VBZ(31, "Verb, 3rd person singular present", Arrays.asList("Word", "FV", "Open")),
    WDT(32, "Wh-determiner", Arrays.asList("Word", "Closed")),
    WP(33, "Wh-pronoun", Arrays.asList("Word", "Pron", "Closed")),
    WP$(34, "Possessive wh-pronoun", Arrays.asList("Word", "Pron", "Closed")),
    WRB(35, "Wh-adverb", Arrays.asList("Word", "Closed"));

    private int index;
    private String description;
    private List<String> homemadeFeatures;

    POS (int num, String description, List<String> homemadeFeatures) {
        this.index = num;
        this.description = description;
        this.homemadeFeatures = homemadeFeatures;
    }

    public int getIndex(){
        return index;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getHomemadeFeatures() {
        return homemadeFeatures;
    }

    public static boolean contains(String s) {
        HashSet<String> m = new HashSet<>();
        for (POS p : POS.values()) {
            m.add(p.toString());
        }
        return m.contains(s);
    }
}
