package net.denryu.android.wordcloud;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;
import net.alhazmy13.wordcloud.WordCloud;

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

        List<String> wordsList = new ArrayList<String>(Arrays.asList(allWords));
        Pattern regex = Pattern.compile("[a-zA-Z0-9']+");
        List<String> cleanWordsList = wordsList.stream()
                .filter(regex.asPredicate())
                .collect(Collectors.toList());

        this.countOfWords = cleanWordsList.size();
        this.wordCountMap = new HashMap<>();

        Map<String, Long> preliminaryWordMap = cleanWordsList.stream().collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));
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

        return foundWord.isPresent() ? foundWord.get() : new AbstractMap.SimpleEntry<>("[No Words Entered]",0);
    }

    protected List<WordCloud> deriveMostCommonWordsStat()
    {
        Stream<Map.Entry<String,Integer>> wordStream = wordCountMap.entrySet().stream();
        ArrayList<WordCloud> cloudList = new ArrayList<>();

        wordStream.sorted(Comparator.comparingInt(Map.Entry<String,Integer>::getValue).reversed())
                .limit(12).forEach(word -> cloudList.add(new WordCloud(word.getKey(),word.getValue())));

        return cloudList;
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
