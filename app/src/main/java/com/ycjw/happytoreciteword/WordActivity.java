package com.ycjw.happytoreciteword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordActivity extends AppCompatActivity {
    private TextView word_self;
    private TextView word_mean;
    private TextView remain_words;
    private Button word_yes;
    private Button word_no;
    private ImageButton sound_button;
    private List<ImportantWord> allWords = new ArrayList<>();
    private List<ImportantWord> firstWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        word_self = findViewById(R.id.word_self);
        word_mean = findViewById(R.id.word_mean);
        remain_words = findViewById(R.id.today_remain_word_word);
        word_yes = findViewById(R.id.word_yes);
        word_no = findViewById(R.id.word_no);
        sound_button = findViewById(R.id.sound_button);
        word_self.setTextIsSelectable(true);

        getWords();

        word_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextOne(1);
                word_mean.setText("");
                setTodayRecited();
                setRemain_words();
            }
        });

        word_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextOne(2);
                word_mean.setText("");
                setRemain_words();
            }
        });

        word_mean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    word_mean.setText(firstWords.get(0).getMean());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        sound_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource("http://dict.youdao.com/dictvoice?audio=" + word_self.getText().toString());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            Thread.sleep(2000);
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
        setRemain_words();
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,WordActivity.class);
        //intent.putExtra("param",value);
        context.startActivity(intent);
    }


    private void setWord(final String wordSelf, final String wordMean){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                word_self.setText(wordSelf);
                //word_mean.setText(wordMean);
            }
        });
    }

    private void getWords(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request  = new Request.Builder()
                            .url(NetUtil.host + "recite/getword")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    List<ImportantWord> importantWords = gson.fromJson(responseData,new TypeToken<List<ImportantWord>>(){}.getType());
                    allWords.addAll(importantWords);
                    startRecite();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     *
     * @param type 0-只显示,1-认识，2-不认识
     */
    private void nextOne(int type){
       if(type == 0 && firstWords.size() > 0){
           setWord(firstWords.get(0).getWord(),firstWords.get(0).getMean());
       }else if(type == 1 && firstWords.size() > 0){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try{
                       String url = NetUtil.host + "recite/setword?word=" + firstWords.get(0).getWord();
                       firstWords.remove(0);
                       if(firstWords.size()>0){
                           setWord(firstWords.get(0).getWord(),firstWords.get(0).getMean());
                       }else {
                           setWord("finished","今日已完成！");
                       }
                       OkHttpClient client = new OkHttpClient();
                       Request request  = new Request.Builder()
                               .url(url)
                               .build();
                       client.newCall(request).execute();
                   }catch (Exception e){
                       e.printStackTrace();
                   }
               }
           }).start();
       }else if(type == 2 && firstWords.size() > 0){
           if(firstWords.size() > 10){
               firstWords.add(10,firstWords.remove(0));
           }else {
               firstWords.add(firstWords.remove(0));
           }
           setWord(firstWords.get(0).getWord(),firstWords.get(0).getMean());
       }else {
           setWord("finished","今日已完成！");
       }
    }

    private void startRecite(){
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        String time = preferences.getString("time","");
        int learned = preferences.getInt("learned", 0);

        for(int i = 0;i < 200 - learned; i++){
            firstWords.add(allWords.get(i));
        }

        if(learned == 200){
            setWord("finished","今日已完成！");
        }
        nextOne(0);
    }

    private void setTodayRecited(){
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        int learned = preferences.getInt("learned", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("learned",learned + 1);
        editor.apply();
    }

    private void setRemain_words(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                int learned = preferences.getInt("learned", 0);
                remain_words.setText("今日计划 " + learned + "/" + "200");
            }
        });
    }
}
