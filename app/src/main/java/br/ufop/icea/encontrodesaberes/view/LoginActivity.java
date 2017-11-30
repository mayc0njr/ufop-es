package br.ufop.icea.encontrodesaberes.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import br.com.albeom.nymeria.WebServerCallback;

import br.com.albeom.nymeria.view.TwoTapsBackActivity;
import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.*;

/**
 * Classe responsável por receber os dados do usuário e autenticar no servidor.
 */
public class LoginActivity extends Activity {

    WebServerES servidor; // objeto de conexão com o servidor.
    TextView login, password, connectingText;
    ProgressBar connectingBar;

    Toast authOk, authFail, authError;
    WebServerCallback processarLogin;

    boolean waitingCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeValues();
        initializeToasts();
        initializeListeners();
    }

    private void initializeToasts(){
        authOk = Toast.makeText(this, R.string.authOk, Toast.LENGTH_SHORT);
        authFail = Toast.makeText(this, R.string.authFail, Toast.LENGTH_SHORT);
        authError = Toast.makeText(this, R.string.authError, Toast.LENGTH_SHORT);
    }

    private void initializeValues(){
        WebServerES.initialize(Utils.SERVER_ADDRESS, this);
        servidor = WebServerES.singleton();
        login = (TextView)findViewById(R.id.editLogin);
        password = (TextView)findViewById(R.id.editPassword);
        connectingText = (TextView)findViewById(R.id.textConnecting);
        connectingBar = (ProgressBar) findViewById(R.id.progressConnecting);
        processarLogin = new WebServerCallback() {
            @Override
            public void executar(String w) {
                authResult();
            }
        };
    }
    //Inicializa o listener do campo senha, para avançar automaticamente ao pressionar GO (enter)
    private void initializeListeners(){

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    authenticate(password);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Funcao para esconder o teclado.
     * @param v View que está chamando a função.
     */
    public void hideKeyboard(View v){
        Utils.hideKeyboard(this, v);
    }


    /**
     * Requisita ao servidor a autenticação do usuario de acordo com os
     * dados lidos nos campos "CPF e Senha".
     * @param v View que está chamando esta função.
     */
    public void authenticate(View v){
        if (waitingCallback) {
            return;
        }
        hideKeyboard(v);
        connectingText.setVisibility(View.VISIBLE);
        connectingBar.setVisibility(View.VISIBLE);
        String login, password;
        login = this.login.getText().toString();
        password = this.password.getText().toString();
        Utils.setCpf(login);
        servidor.authenticate(login, password, processarLogin);
        waitingCallback = true;
    }

    /**
     * Processa os resultados da autenticação.
     * Caso o servidor retorne sucesso na autenticação, é dado um feedback ao usuário,
     * e ele é levado à próxima tela da aplicação.
     * Caso o servidor retorne falha, ou acesso negado, é dado um feedback ao usuário.
     */
    private void authResult(){
        connectingBar.setVisibility(View.GONE);
        connectingText.setVisibility(View.GONE);
        int authStatus = servidor.getAuthStatus();
        waitingCallback = false;
        switch (authStatus) {
            case WebServerES.AUTH_OK:
//                authOk.show();
                Intent it = new Intent(this, SelectActivity.class);
                startActivity(it);
                login.setText("");
                password.setText("");
                break;
            case WebServerES.AUTH_FAIL:
                authFail.show();
                break;
            case WebServerES.AUTH_ERROR:
                authError.show();
                break;
        }
    }

    public void goToAbout(View v){
        Intent it = new Intent(this, AboutActivity.class);
        startActivity(it);
    }
}
