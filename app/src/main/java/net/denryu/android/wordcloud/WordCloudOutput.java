package net.denryu.android.wordcloud;

import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

//import net.alhazmy13.example.R;
import net.alhazmy13.wordcloud.ColorTemplate;
import net.alhazmy13.wordcloud.WordCloud;
import net.alhazmy13.wordcloud.WordCloudView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordCloudOutput extends AppCompatActivity {
    private static final String TAG = "WordCloudActivity";
    List<WordCloud> list ;
    String text = "0 1 2 3 4 5 6 7 8 9 a b c d e f";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordcloud_output);
        Log.d("WordCloudOutput", "OnCreate");
        generateRandomText();

        WordCloudView wordCloud = (WordCloudView) findViewById(R.id.wordCloud);
        wordCloud.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        wordCloud.setDataSet(list);
        //wordCloud.setSize(wordCloud.getMeasuredWidth(),wordCloud.getMeasuredHeight());
        wordCloud.setSize(500,500);
        wordCloud.setColors(ColorTemplate.MATERIAL_COLORS);
        wordCloud.setScale(20,10);
        wordCloud.notifyDataSetChanged();
        Log.d("WordCloudOutput", "wordCloud: " + wordCloud.toString());

    }

    private void generateRandomText() {
        String[] data = text.split(" ");
        list = new ArrayList<>();
        Random random = new Random();
        for (String s : data) {
            WordCloud w = new WordCloud(s,random.nextInt(2) + 10001);
            Log.d("WordList", "text: " + w.getText() + " Weight: " + w.getWeight());
            list.add(w);
        }
    }
}


