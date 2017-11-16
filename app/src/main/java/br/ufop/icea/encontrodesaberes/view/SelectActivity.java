package br.ufop.icea.encontrodesaberes.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.albeom.nymeria.WebServerCallback;
import br.com.albeom.nymeria.view.TwoTapsBackAppCompatActivity;
import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.controller.WebServerES;
import br.ufop.icea.encontrodesaberes.model.Trabalho;

public class SelectActivity extends TwoTapsBackAppCompatActivity {

    WebServerES servidor; //Objeto responsável por tratar a comunicação com o Servidor.
    WebServerCallback processarTrabalhos; //Objeto responsável por definir o comportamento ao receber a resposta do servidor.
    ArrayList<Trabalho> trabalhos;
    ListView listTrabalhos;
    TrabalhoAdapter adapter;
    TextView textSearching, textName;
    ProgressBar progressSearching;

    private static final int TRABALHO_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initializeValues();
        listarTrabalhos(null);
    }


    public void listarTrabalhos(View v){
        textSearching.setVisibility(View.VISIBLE);
        progressSearching.setVisibility(View.VISIBLE);
        servidor.obterTrabalhos(trabalhos, processarTrabalhos);
    }

    /**
     * Inicializa as variaveis utilizadas na activity.
     */
    private void initializeValues(){
        listTrabalhos = (ListView)findViewById(R.id.listTrabalhos);
        textSearching = (TextView)findViewById(R.id.textSearching);
        textName = (TextView)findViewById(R.id.textName);
        progressSearching = (ProgressBar)findViewById(R.id.progressSearching);
        servidor = WebServerES.singleton();
        trabalhos = new ArrayList<>();
        processarTrabalhos = new WebServerCallback() {
            @Override
            public void executar(String w) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SelectActivity", "OnActivityResult");
        if(requestCode == 1){
            adapter.notifyDataSetChanged();
            listTrabalhos.refreshDrawableState();
        }
    }


    public void getTrabs(View v){
        servidor.obterTrabalhos(new ArrayList<Trabalho>(), processarTrabalhos);
    }

    public void logout(View v){
        servidor.logout();
        finish();
//        Intent it = new Intent(this, LoginActivity.class);
//        startActivity(it);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        servidor.logout();
        Utils.saveVotes();
    }
}
