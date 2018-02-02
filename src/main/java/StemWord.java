import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StemWord {

    @SerializedName("word")
    private String word;

    private transient String stem;

    @SerializedName("total-occurrences")
    private Integer totalOccurrences;

    @SerializedName("sentence-indexes")
    private List<Integer> sentenceIndexes;

    //Constructor
    public StemWord(String word, String stem) {
        this.word = word;
        this.stem = stem;
        this.totalOccurrences = 0; //total ocurrences initializes at 0, and will be incremented with each call to addSentenceIndex
        this.sentenceIndexes = new ArrayList<Integer>();
    }

    //Methods
    public void addSentenceIndex(Integer index){
        if(!this.getSentenceIndexes().contains(index)){
            this.getSentenceIndexes().add(index);
        }
        this.totalOccurrences++;
    }

    @Override
    public String toString() {
        return "StemWord{" +
                "word='" + word + '\'' +
                ", stem='" + stem + '\'' +
                ", totalOccurrences=" + totalOccurrences +
                ", sentenceIndexes=[" + sentenceIndexes.stream().map(Object::toString).collect(Collectors.joining(",")) + "]" +
                '}';
    }

    //Getters & Setters
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public String getStem() {
        return stem;
    }
    public void setStem(String stem) {
        this.stem = stem;
    }
    public Integer getTotalOccurrences() {
        return totalOccurrences;
    }
    public List<Integer> getSentenceIndexes() {
        return sentenceIndexes;
    }

}
