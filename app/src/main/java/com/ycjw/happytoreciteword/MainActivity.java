package com.ycjw.happytoreciteword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button start_recite;
    private TextView remain_days;
    private TextView remain_words;
    private TextView today_remain_words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_recite = findViewById(R.id.start_recite);
        remain_days = findViewById(R.id.remain_days);
        remain_words = findViewById(R.id.remain_words);
        today_remain_words = findViewById(R.id.today_remain_words);

        start_recite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordActivity.actionStart(MainActivity.this);
            }
        });

        getInformation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getInformation();
    }

    private void setUi(final Information information){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                remain_days.setText(information.getRemianDays() + "天");
                remain_words.setText("考研重点单词 " + information.getRecited_words() + "/" + information.getAll_words());

                SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                String time = preferences.getString("time","");
                int learned = preferences.getInt("learned", 0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String today = dateFormat.format(new Date(System.currentTimeMillis()));
                if(!time.equals(today)){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("time", today);
                    editor.putInt("learned",0);
                    editor.apply();
                }

                today_remain_words.setText("今日计划 " + learned + "/" + "200");
            }
        });
    }

    private void getInformation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url = NetUtil.host + "recite/getinformation";
                    OkHttpClient client = new OkHttpClient();
                    Request request  = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    Information information = gson.fromJson(responseData,Information.class);
                    setUi(information);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
