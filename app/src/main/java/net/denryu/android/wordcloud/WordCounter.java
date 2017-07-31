package net.denryu.android.wordcloud;

//import java.util.Comparator;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeMap;
//import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import static java.util.Arrays.stream;
//import android.util.Log;

/**
 * Created by Joe on 7/16/2017.
 */

public class WordCounter {

    private Map<String, Integer> wordCountMap;
    private int countOfWords;
    public String mostCommonWord;
    public double appearanceRate;

    public Map<String, Integer> getWordCountMap() {
        return wordCountMap;
    }

    public void countWords(String in){

        //this regex uses all non alpha-numeric characters (plus apostrophes) as delimiters
        String[] allWords = in.split("[^a-zA-Z0-9']+");

        this.countOfWords = allWords.length;
        this.wordCountMap = new HashMap<>();

        Map<String, Long> preliminaryWordMap = Arrays.stream(allWords).collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));
        for (Map.Entry<String,Long> wordGroup : preliminaryWordMap.entrySet())
        {
            this.wordCountMap.put(wordGroup.getKey(), wordGroup.getValue().intValue());
        }

        this.setMostCommonWord();
    }

    private Map.Entry<String, Integer> deriveMostCommonWordStat()
    {
        Stream<Map.Entry<String,Integer>> wordStream = wordCountMap.entrySet().stream();

        Optional<Map.Entry<String,Integer>> foundWord = wordStream.sorted(Comparator.comparingInt(Map.Entry<String,Integer>::getValue).reversed())
                .findFirst();

        return foundWord.isPresent() ? foundWord.get() : new AbstractMap.SimpleEntry<>("[no words entered]",0);
    }

    private void setMostCommonWord() {
        Map.Entry<String, Integer> mostCommonWordStat = this.deriveMostCommonWordStat();
        this.mostCommonWord = mostCommonWordStat.getKey();

        // Ensure CountOfWords is not zero before dividing by it to avoid a DivideByZero exception
        if(this.countOfWords != 0)
        {
            this.appearanceRate = ((float) mostCommonWordStat.getValue()) / countOfWords;
        }
        else
        {
            this.appearanceRate = 0.0f;
        }
    }

    public int distinctWordCount() {
        return wordCountMap.size();
    }

    public int totalWordCount() {
        return countOfWords;
    }

}
