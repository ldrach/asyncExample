package truedeveloper.finalexam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity {
    // Start 5:14 pm
    int counter = 3;
    int seconds = 0;
    TextView text01, text02, textView04;
    ProgressBar progressBar01,progressBar02;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    SharedPreferences prefs;
    SharedPreferences.Editor ed;
    Set<String> passes = new HashSet<String>();
    private BroadcastReceiver receiver;
    String Key = "key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text01 = (TextView)findViewById(R.id.textView01);
        text02 = (TextView)findViewById(R.id.textView02);
        textView04 = (TextView)findViewById(R.id.textView04);
        progressBar01 = (ProgressBar)findViewById(R.id.progressBar01);
        progressBar02 = (ProgressBar)findViewById(R.id.progressBar02);
        progressBar02.setVisibility(View.INVISIBLE);
        prefs = this.getSharedPreferences(
                "ee", Context.MODE_PRIVATE);
        ed = prefs.edit();

        if (!prefs.contains(Key)){
            Random rand = new Random();
            int rand_num = Math.abs(rand.nextInt() % 99999);
            text01.setText(String.valueOf(rand_num));
            passes.add(String.valueOf(rand_num));
            Set<String> passes_updated = new HashSet<String>();
            passes_updated = prefs.getStringSet(Key,passes_updated);
            passes_updated.addAll(passes);
            ed.putStringSet(Key, passes_updated);
            ed.commit();
        } else {

            Intent intent03 = getIntent();
            String temp_pass = intent03.getStringExtra("temp");
            text01.setText(temp_pass);
        }
        progressBar01.setMax(60);
        receiver=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0)
                {
                    handler2.removeCallbacks(update_bar);
                    seconds = Integer.valueOf(Calendar.getInstance().get(Calendar.SECOND));
                    //textView04.setText(String.valueOf(60-seconds));
                    progressBar01.setProgress(seconds);
                    handler2.postDelayed(update_bar, 0);

                    GuessinBG guesstask = new GuessinBG();
                    guesstask.execute(0);
                }

            }
        };

        //Register the broadcast receiver to receive TIME_TICK
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs!=null) {

        } else {

        }

        handler2.removeCallbacks(update_bar);

        seconds = Integer.valueOf(Calendar.getInstance().get(Calendar.SECOND));
        progressBar01.setProgress(seconds);
        handler2.postDelayed(update_bar,0);
    }

    public class GuessinBG extends AsyncTask<Integer, String, Integer> {
        long start_ts,end_ts = 0;
        private Handler handler = new Handler();


        @Override
        protected Integer doInBackground(Integer... integers) {
            int range  = 99999;
            int rand_num = 0;
            int rate = 0;
            Random rand = new Random();
            while (counter >0) {
                rate ++;
                rand_num = Math.abs(rand.nextInt() % range);
                if (rate % 1000 == 0) publishProgress(String.valueOf(rand_num));

            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            text01.setText(String.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            counter = 3;
            text02.setText("Generating a New Passcode ");
            progressBar02.setVisibility(View.VISIBLE);
            handler.postDelayed(update_counter, 1000);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressBar02.setVisibility(View.INVISIBLE);
            passes.add(text01.getText().toString());
            Set<String> passes_updated = new HashSet<String>();



            passes_updated = prefs.getStringSet(Key,passes_updated);
            passes_updated.addAll(passes);

            ed.putStringSet(Key, passes_updated);
            ed.commit();
        }

        private Runnable update_counter = new Runnable() {
            @Override
            public void run() {
                if (counter >0) {
                    counter --;
                    text02.setText(text02.getText()+".");
                    handler.postDelayed(this, 1000);
                } else {

                }
            }
        };

    }

    @Override
    public void onStop()
    {
        super.onStop();
        //unregister broadcast receiver.
        if(receiver!=null)
            unregisterReceiver(receiver);
        handler.removeCallbacks(update_bar);
        handler2.removeCallbacks(update_bar);

        this.finish();
    }

    public void showHistory (View v){
        Intent intent01 = new Intent(this,History.class);
        intent01.putExtra("temp",text01.getText().toString());
        startActivity(intent01);
    }

    private Runnable update_bar = new Runnable() {
        @Override
        public void run() {
            Log.d("RR","seconds"+seconds);
            if (seconds <=60) {
                seconds ++;
                progressBar01.setProgress(seconds);
                textView04.setText(String.valueOf(60-seconds));
                handler2.postDelayed(this, 1000);
            } else {

            }
        }
    };

}
