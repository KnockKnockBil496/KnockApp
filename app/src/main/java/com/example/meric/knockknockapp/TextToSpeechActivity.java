package com.example.meric.knockknockapp;
import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.View;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import android.widget.Toast;

import java.util.Locale;

public class TextToSpeechActivity extends Activity implements OnClickListener,OnInitListener {

    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button speakButton = (Button) findViewById(R.id.textToSpeech);
        speakButton.setOnClickListener(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    public void onClick(View v) {
        Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        speakName("Meric");
    }

    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(Locale.US);
        }
        
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }

        if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE) myTTS.setLanguage(Locale.US);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public void speakName(String name){
        Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        myTTS.speak(name, TextToSpeech.QUEUE_FLUSH, null);
        myTTS.shutdown();
    }
}
