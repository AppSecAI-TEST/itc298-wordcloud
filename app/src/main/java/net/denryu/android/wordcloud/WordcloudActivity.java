package net.denryu.android.wordcloud;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.alhazmy13.wordcloud.ColorTemplate;
import net.alhazmy13.wordcloud.WordCloud;
import net.alhazmy13.wordcloud.WordCloudView;

/**
 * Created by hsMacbook on 2017. 7. 17..
 */

public class WordcloudActivity extends AppCompatActivity implements
        OnClickListener {

    private static final int LOAD_IMAGE_RESULTS = 1;

    private static final int OPEN_DOCUMENT_REQUEST = 1;
    private EditText txtInput;
    private Button clearInputButton;
    private Button openFileButton;
    private Button generateButton;
    private TextView mostWordResult;
    private TextView appearanceResult;
    private TextView uniqueResult;
    private TextView totalCountResult;

    //this button will be used for creating the output image
    private Button generateImage;

    private WordCounter wordCounter;
    private WordCounterDB wordCounterDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordcloud_activity);

        txtInput = (EditText) findViewById(R.id.txtInput);
        generateButton = (Button) findViewById(R.id.generateButton);
        generateButton.setOnClickListener(this);
        openFileButton = (Button) findViewById(R.id.openFileButton);
        openFileButton.setOnClickListener(this);
        clearInputButton = (Button) findViewById(R.id.clearInputButton);
        clearInputButton.setOnClickListener(this);

        mostWordResult = (TextView) findViewById(R.id.commonWord);
        appearanceResult = (TextView) findViewById(R.id.appearanceResult);
        uniqueResult = (TextView) findViewById(R.id.distinctResult);
        totalCountResult = (TextView) findViewById(R.id.totalCountings);

        wordCounter = new WordCounter();
        wordCounterDB = new WordCounterDB(this);

        //this button will be used for creating the output image
        generateImage = (Button) findViewById(R.id.generateImage);
        generateImage.setOnClickListener(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.d("WordcloudActivity", "intentData: " + intent.getClipData().getItemAt(0).toString());
            if ("text/plain".equals(type) && (intent.getClipData().getItemAt(0) != null)) {
                Uri uri = intent.getClipData().getItemAt(0).getUri();
                try {
                    String content = readFileContent(uri);
                    txtInput.setText(content);
                } catch (IOException e) {
                    //Some log, Alert or Toast goes here
                }
            }
            else if ("text/plain".equals(type)) {
                handleSendText(intent);
                Log.d("WordcloudActivity", "UriIntent: " + intent.toString());
            }
        }

        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if (intent.getData() != null) {
                Uri uri = intent.getData();
                try {
                    String content = readFileContent(uri);
                    txtInput.setText(content);
                } catch (IOException e) {
                    //Some log, Alert or Toast goes here
                }

            }
            }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            txtInput.setText(sharedText);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_word_input, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_share:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "Your body here";
                String shareSub = "Your subject here";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
                return true;
            case R.id.item_clear_history:
                clearHistory();
                break;
            case R.id.about:
                new AlertDialog.Builder(this).setTitle("About")
                        .setMessage("This will have our ABOUT messages")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generateButton:
                processInput(txtInput.getText().toString());
                populateResults();
                break;
            case R.id.openFileButton:
                openFile();
//              openPDFDOC();
                break;
            case R.id.clearInputButton:
                new AlertDialog.Builder(this).
                        setMessage("Are you sure you want to clear all text input?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                txtInput.setText("");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case R.id.generateImage:
                Intent i = new Intent(WordcloudActivity.this, WordCloudOutput.class);
                Log.d("txtInput", i.toString());
                i.putExtra("txtInput", txtInput.getText().toString());
                startActivity(i);
                break;
        }
    }

    public void populateResults() {
        wordCounterDB.insertWords(wordCounter.getWordCountMap(), null, null, null);
        uniqueResult.setText(String.valueOf(wordCounter.distinctWordCount()));
        totalCountResult.setText(String.valueOf(wordCounter.totalWordCount()));
        mostWordResult.setText(String.valueOf(wordCounter.mostCommonWord));
        String appearanceRateString = String.valueOf((int) (100 * wordCounter.appearanceRate)) + '%';
        appearanceResult.setText(appearanceRateString);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST);
    }

//    private void openPDFDOC() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("application/pdf");
//        startActivityForResult(intent, LOAD_IMAGE_RESULTS);
//    }

    private void processInput(String text) {
        wordCounter.countWords(text);
    }

    private void clearHistory() {
        wordCounterDB.clearDB();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_DOCUMENT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
//                String content = getStringFile(uri);
//                txtInput.setText(content);
                try {
                    String content = readFileContent(uri);
                    txtInput.setText(content);
                } catch (IOException e) {
                    //Some log, Alert or Toast goes here
                }

            }
        }
    }

    public String getStringFile(Uri uri) {
        InputStream inputStream = null;
        String encodedFile= "", lastVal;
        try {
            inputStream = getContentResolver().openInputStream(uri); //new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile =  output.toString();
        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    private String readFileContent(Uri uri) throws IOException {
        InputStream inStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline + "\n");
        }
        inStream.close();
        return stringBuilder.toString();
    }

}
