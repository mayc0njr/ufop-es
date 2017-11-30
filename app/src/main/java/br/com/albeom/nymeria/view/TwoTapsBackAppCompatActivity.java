package br.com.albeom.nymeria.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;

/**
 * Classe herda de AppCompatActivity, e sobreescreve a função do botão voltar.
 * Requer o pressionamento duplo do botão dentro de um intervalo de tempo para sair.
 */

public class TwoTapsBackAppCompatActivity extends AppCompatActivity {

    private Toast exitPress;
    private long exitRequest;
    private long exitTime;
    private boolean exitOK = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_taps_back);
        exitTime = getResources().getInteger(R.integer.exit_time);
        exitRequest = 0;
        initToasts();
    }

    private void initToasts() {
        exitPress = Toast.makeText(this, getString(R.string.exit_press), Toast.LENGTH_LONG);
    }

    /**
     * Sobreescreve a função onBackPressed, para requisitar o toque duplo para sair.
     * Caso sobreescrita em classes que herdam desta, necessário realizar uma chamada
     * super, para manter a funcionalidade.
     */
    @Override
    public void onBackPressed() {
        long actual = Calendar.getInstance().getTimeInMillis();
        if (actual - exitRequest > exitTime) {
            exitRequest = actual;
//            exitPress.show();
            Utils.customShow(exitPress, exitTime);
            exitOK = false;
            return;
        }
        exitOK = true;
        super.onBackPressed();
    }

    protected boolean canExit(){
        return exitOK;
    }
}
