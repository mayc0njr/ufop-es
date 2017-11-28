package br.ufop.icea.encontrodesaberes.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.albeom.nymeria.WebServerCallback;
import br.com.albeom.nymeria.view.TwoTapsBackAppCompatActivity;
import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.controller.WebServerES;
import br.ufop.icea.encontrodesaberes.model.Trabalho;

/**
 * Classe responsável por exibir a lista de trabalhos, e o status de cada um,
 * Votado ou não-votado, exibe também o nome do usuário autenticado e um botão para sair do sistema.
 */
public class SelectActivity extends TwoTapsBackAppCompatActivity {
    private int tries, maxTries; //Conta quantas vezes tentou buscar trabalhos.
    WebServerES servidor; //Objeto responsável por tratar a comunicação com o Servidor.
    WebServerCallback processarTrabalhos; //Objeto responsável por definir o comportamento ao receber a resposta do servidor.
    ArrayList<Trabalho> trabalhos;
    ListView listTrabalhos;
    TrabalhoAdapter adapter;
    TextView textSearching, textName;
    ProgressBar progressSearching;
    Toast notFoundToast;
    String notFoundText;

    private static final int TRABALHO_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initializeValues();
        listarTrabalhos(null);
    }

    /**
     * Busca uma lista de trabalhos do servidor, para preencher a lista de trabalhos.
     * @param v View que chama a função.
     */
    public void listarTrabalhos(View v){
        textSearching.setVisibility(View.VISIBLE);
        progressSearching.setVisibility(View.VISIBLE);
        servidor.obterTrabalhos(trabalhos, processarTrabalhos);
    }

    /**
     * Inicializa as variaveis utilizadas na activity.
     */
    private void initializeValues(){
        tries = 0;
        maxTries = getResources().getInteger(R.integer.triesGet);
        notFoundToast = Toast.makeText(this, R.string.notFoundTrabalho, Toast.LENGTH_SHORT);
        listTrabalhos = (ListView)findViewById(R.id.listTrabalhos);
        textSearching = (TextView)findViewById(R.id.textSearching);
        textName = (TextView)findViewById(R.id.textName);
        progressSearching = (ProgressBar)findViewById(R.id.progressSearching);
        servidor = WebServerES.singleton();
        trabalhos = new ArrayList<>();
        processarTrabalhos = new WebServerCallback() {
            @Override
            public void executar(String w) {
                tries++;
                if(trabalhos.size() == 0){
                    if(tries < maxTries) {
                        //formata a string para exibir o numero de tentativas.
                        notFoundText = String.format(getString(R.string.notFoundTrabalho), tries, (maxTries-1));
                        notFoundToast.setText(notFoundText);
                        notFoundToast.show();
                        servidor.obterTrabalhos(trabalhos, processarTrabalhos);
                        return;
                    }
                }
                progressSearching.setVisibility(View.GONE);
                textSearching.setVisibility(View.GONE);
                adapter = new TrabalhoAdapter(SelectActivity.this, trabalhos);
                listTrabalhos.setAdapter(adapter);
                listTrabalhos.refreshDrawableState();
            }
        };

        listTrabalhos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.shareTrabalho((Trabalho)parent.getItemAtPosition(position));
                Intent it = new Intent(SelectActivity.this, TrabalhoActivity.class);
                startActivityForResult(it, TRABALHO_ACTIVITY);
            }
        });

        textName.setText(Utils.getName());
    }

    /**
     * Chamada após retornar da tela de votos, para atualizar a lista de trabalhos,
     * marcando que o trabalho selecionado foi votado.
     * @param requestCode Codigo da requisição de startActivity
     * @param resultCode Resultado enviado pela activity
     * @param data dados recebidos da activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SelectActivity", "OnActivityResult");
        if(requestCode == 1){
            adapter.notifyDataSetChanged();
            listTrabalhos.refreshDrawableState();
        }
    }

    /**
     * Usado para efetuar o logout no servidor.
     * Também fecha a tela.
     * @param v View que chama a função.
     */
    public void logout(View v){
        servidor.logout();
        finish();
    }

    /**
     * Chamada quando a tela é destruída, para efetuar o logout.
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        servidor.logout();
        Utils.saveVotes();
    }

    /**
     * Sobreescrita da funcao onBackPressed.
     * Salva os votos e desloga antes de sair da tela.
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(canExit()){
            servidor.logout();
            Utils.saveVotes();
        }
    }
}
