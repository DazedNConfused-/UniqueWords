import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * A JSON-wrapping class that sorts its inner results alphabetically upon construction.
 * */
public class JSONResultWrapper {

    @SerializedName("results")
    private List<StemWord> results;

    //Constructor
    public JSONResultWrapper(Collection<StemWord> results) {
        this.results = new ArrayList<>();
        this.results.addAll(results);

        //Sort results alphabetically
        this.results.sort(Comparator.comparing( x -> x.getWord().toLowerCase()));
    }

    //Getters & Setters
    public List<StemWord> getResults() {
        return results;
    }
    public void setResults(List<StemWord> results) {
        this.results = results;
    }
}
