package net.denryu.android.wordcloud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

//import net.alhazmy13.example.R;
import net.alhazmy13.wordcloud.ColorTemplate;
import net.alhazmy13.wordcloud.WordCloud;
import net.alhazmy13.wordcloud.WordCloudView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WordCloudOutput extends AppCompatActivity {
    private TextView mostWordResult;
    private TextView appearanceResult;
    private TextView uniqueResult;
    private TextView totalCountResult;

    private WordCounter wordCounter;
    private WordCounterDB wordCounterDB;

    private static final String TAG = "WordCloudActivity";
    WordcloudActivity origin = new WordcloudActivity();

    List<WordCloud> list ;
    String text = "word word word word word word word word word word word word word word word word word word different";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordcloud_output);

        mostWordResult = (TextView) findViewById(R.id.commonWord);
        appearanceResult = (TextView) findViewById(R.id.appearanceResult);
        uniqueResult = (TextView) findViewById(R.id.distinctResult);
        totalCountResult = (TextView) findViewById(R.id.totalCountings);

        wordCounter = new WordCounter();
        wordCounterDB = new WordCounterDB(this);



        generateRandomText();
        WordCloudView wordCloud = (WordCloudView) findViewById(R.id.wordCloud);
        wordCloud.setDataSet(list);
        wordCloud.setSize(300,300);
        wordCloud.setColors(ColorTemplate.MATERIAL_COLORS);
        wordCloud.notifyDataSetChanged();

        Intent i = getIntent();
        String getText = i.getStringExtra("txtInput");
        Log.d("get Text", getText);

        processInput(getText);
        populateResults();
    }

    public void populateResults() {
        //wordCounterDB.insertWords(wordCounter.getWordCountMap(), null, null, null);
        uniqueResult.setText(String.valueOf(wordCounter.distinctWordCount()));
        totalCountResult.setText(String.valueOf(wordCounter.totalWordCount()));
        mostWordResult.setText(String.valueOf(wordCounter.mostCommonWord));
        String appearanceRateString = String.valueOf((int) (100 * wordCounter.appearanceRate)) + '%';
        appearanceResult.setText(appearanceRateString);
    }

    private void processInput(String text) {
        wordCounter.countWords(text);
    }
    
//
//    public void maptoString() {
//        Map<Integer, String> sourceMap = output;
//        Collection<String> values = sourceMap.values();
//        String[] targetArray = values.toArray(new String[values.size()]);
//
//        list = new ArrayList<>();
//        Random random = new Random();
//        for (String s : targetArray) {
//            list.add(new WordCloud(s,random.nextInt(50)));
//        }
//    }


    private void generateRandomText() {
        String[] data = text.split(" ");
        list = new ArrayList<>();
        Random random = new Random();
        for (String s : data) {
            list.add(new WordCloud(s,random.nextInt(50)+10));
        }
    }
}


