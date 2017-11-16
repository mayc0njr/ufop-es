package br.ufop.icea.encontrodesaberes.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.model.Voto;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        File file = new File(getFilesDir().getPath() + File.separator + Utils.STORE_FILE);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch(IOException e){
                Toast.makeText(this, "Arquivo nao pode ser criado!", Toast.LENGTH_SHORT).show();
            }
        }

        Utils.loadVotes(this);

        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
        finish();
    }

    class LoginRegister extends AsyncTask<String, Void, String> {
        ImageView eslogo; //image to animate.

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //animate image to appear gradually here.
        }

        @Override
        protected String doInBackground(String... params) {
            //read file here.
            return null; //return a string with the result.
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //Process the result. a.k.a. load votes...
        }
    }
}