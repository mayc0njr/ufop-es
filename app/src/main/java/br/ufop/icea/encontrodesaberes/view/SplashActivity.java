package br.ufop.icea.encontrodesaberes.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.ufop.icea.encontrodesaberes.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
        finish();
    }
}