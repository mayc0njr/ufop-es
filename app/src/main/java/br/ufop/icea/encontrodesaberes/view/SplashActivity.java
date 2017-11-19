package br.ufop.icea.encontrodesaberes.view;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.model.Voto;

/**
 * Tela inicial, responsável por carregar os votos.
 * Exibe um splash enquanto realiza a leitura do arquivo.
 */
public class SplashActivity extends Activity {
    int animDuration, splashDuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        animDuration = getResources().getInteger(R.integer.splash_animation_duration);
        splashDuration = getResources().getInteger(R.integer.splash_duration);
        Load ld = new Load();
        ld.execute();
    }

    class Load extends AsyncTask<String, Void, String> {
        ImageView eslogo = findViewById(R.id.imageSplash);
        long start;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            start = Calendar.getInstance().getTimeInMillis();
            eslogo.setVisibility(View.VISIBLE);
            eslogo.animate().setDuration(animDuration).alpha(1f);
        }

        @Override
        protected String doInBackground(String... params) {
            File file = new File(getFilesDir().getPath() + File.separator + Utils.STORE_FILE);
            if(!file.exists()){
                try{
                    file.createNewFile();
                }catch(IOException e){
                    Toast.makeText(SplashActivity.this, "Arquivo nao pode ser criado!", Toast.LENGTH_SHORT).show();
                }
            }

            Utils.loadVotes(SplashActivity.this);
            long wait = Calendar.getInstance().getTimeInMillis() - start;
            wait = splashDuration - wait;
            if(wait > 0){
                try{
                    Thread.sleep(wait);
                }catch (InterruptedException e){
                    //do nothing.
                }
            }
            return null; //return a string with the result.
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Intent it = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(it);
            finish();
        }
    }
}