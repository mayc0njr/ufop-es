package br.com.albeom.nymeria.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;

public class TwoTapsBackActivity extends Activity {

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

