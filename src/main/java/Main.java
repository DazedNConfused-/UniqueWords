import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class Main {
    public static void main(String[] args) throws Exception {

        //Initialize paragraph
        String originalParagraph = "Take this paragraph of text and return an alphabetized list of ALL unique words.  A unique word is any form of a word often communicated with essentially the same meaning. For example, fish and fishes could be defined as a unique word by using their stem fish. For each unique word found in this entire paragraph, determine the how many times the word appears in total. Also, provide an analysis of what unique sentence index position or positions the word is found. The following words should not be included in your analysis or result set: \"a\", \"the\", \"and\", \"of\", \"in\", \"be\", \"also\" and \"as\".  Your final result MUST be displayed in a readable console output in the same format as the JSON sample object shown below.";

        //Initialize result
        Map<String, StemWord> results = new HashMap<>();

        //Split paragraph into individual sentences
        String sentences[] = originalParagraph.split("\\.");

        //Define stop-words
        Set<String> stopWords = new HashSet<String>();
        stopWords.add("a");
        stopWords.add("an"); //not explicitly defined as a stopWord in the exercise, but considered as such based on sample result ('a' being its stem)
        stopWords.add("the");
        stopWords.add("and");
        stopWords.add("of");
        stopWords.add("in");
        stopWords.add("be");
        stopWords.add("also");
        stopWords.add("as");

        //Process text
        for(int sentenceIndex = 0; sentenceIndex < sentences.length; sentenceIndex++){
            //Clean sentence of foreign elements, and split into individual words
            String[] cleanSentence = cleanSentence(sentences[sentenceIndex], stopWords).split(" ");

            for(String word : cleanSentence){
                StemWord stemWord = new StemWord(word, removeStopWordsAndStem(word, stopWords)); //possible improvement: stop words have already been removed in cleaning stage

                StemWord existingStemWord = results.get(stemWord.getStem());

                if(existingStemWord != null){
                    existingStemWord.addSentenceIndex(sentenceIndex);
                } else {
                    stemWord.addSentenceIndex(sentenceIndex);
                    results.put(stemWord.getStem(), stemWord);
                }
            }
        }

        //Format results as JSON and pretty-print into console
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(new JSONResultWrapper(results.values())));
    }

    /**
     * Process each individual word of the given {@code input} sentence, trimming and cleaning it of {@code stopWords},
     * before rebuilding it free of pollution.
     * */
    public static String cleanSentence(String input, Set<String> stopWords) {

        //Make all stopwords lower-case (in case this method is called and they already aren't)
        stopWords = stopWords.stream().map(String::toLowerCase).collect(Collectors.toSet());

        //Prepare result StringBuilder
        StringBuilder finalSentence = new StringBuilder();

        //Trim and split input sentence into individual words
        String[] s = input.trim().replace("\"", "").replace(":", "").replace(",", "").split(" ");

        //Process each individual word
        for (String inputWord : s){
            if(!stopWords.contains(inputWord.toLowerCase())){
                finalSentence.append(inputWord).append(" ");
            }
        }

        return finalSentence.toString();
    }

    /**
     * Gets the processed stems from the given {@code input}. Can be used either with sentences or individual words.
     * */
    public static String removeStopWordsAndStem(String input, Set<String> stopWords) throws IOException {

        //Make input lower-case (since case is irrelevant when defining the String's stem words)
        input = input.toLowerCase();

        //Make all stopwords lower-case (in case this method is called and they already aren't)
        CharArraySet stopWordsSet = StopFilter.makeStopSet(stopWords.stream().map(String::toLowerCase).collect(Collectors.toList()));

        //Stem input
        Analyzer analyzer = new StandardAnalyzer();

        TokenStream tokenStream = analyzer.tokenStream(null, input);
        tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, stopWordsSet);

        CharTermAttribute resultAttr = tokenStream.addAttribute(CharTermAttribute.class);
        
        tokenStream.reset();

        StringBuilder tokens = new StringBuilder();
        while (tokenStream.incrementToken()) {
            tokens.append(resultAttr.toString());
        }

        analyzer.close();
        tokenStream.close();
        
        return tokens.toString();
    }
}