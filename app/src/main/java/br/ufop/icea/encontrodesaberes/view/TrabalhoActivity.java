package br.ufop.icea.encontrodesaberes.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.albeom.nymeria.WebServerCallback;
import br.com.albeom.nymeria.view.TwoTapsBackAppCompatActivity;
import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.controller.WebServerES;
import br.ufop.icea.encontrodesaberes.model.Trabalho;
import br.ufop.icea.encontrodesaberes.model.Voto;

public class TrabalhoActivity extends TwoTapsBackAppCompatActivity {
    TextView titulo, autor, apresentador;
    WebServerES servidor;
    Trabalho trabalho;
    ViewGroup layoutCriterios;
    ScrollView layoutScroll;
    View convertView;
    Button buttonSalvar;
    TextView connectingText;
    ProgressBar connectingBar;
//    SeekBar seekNota;
    RatingBar ratingNota;
    Toast votoOk, votoError;
    WebServerCallback processarVoto;

    /**Usado para carregar o voto;*/
    boolean loaded;
    int[] ratingVotos;
    int votoPremiado;
    boolean[] como;
    boolean[] justificar;
    String outro;

    boolean waitingCallback;

    //check-boxes adicionais
    RadioGroup radioPremiado;
    RadioButton radioSim;
    TextView textComo, textJustifique;
    CheckBox checkMelhor, checkMencao, checkClareza, checkPesquisador, checkRelevancia, checkCritica, checkOutro;
    TextView editOutro;

    String[] criterios;
    int[] notas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabalho);
        loaded = false;
        initializeValues();
        initializeToasts();
        generateSeekbars();
        fillVotoFooter();

    }

    private void initializeToasts(){
        votoOk = Toast.makeText(this, R.string.textOk, Toast.LENGTH_SHORT);
        votoError = Toast.makeText(this, R.string.textError, Toast.LENGTH_SHORT);
    }

    private void initializeValues(){
        servidor = WebServerES.singleton();
        connectingText = (TextView)findViewById(R.id.textConnecting);
        connectingBar = (ProgressBar) findViewById(R.id.progressConnecting);

        //initializing header
        titulo = (TextView)findViewById(R.id.textTitle);
        autor = (TextView)findViewById(R.id.textAutor);
        apresentador = (TextView)findViewById(R.id.textApres);
        trabalho = Utils.getTrabalho();
        layoutCriterios = (ViewGroup)findViewById(R.id.layoutCriterios);
        buttonSalvar = (Button)findViewById(R.id.buttonSalvar);
        layoutScroll = (ScrollView)findViewById(R.id.layoutScroll);

        //setting header info
        titulo.setText(trabalho.getTitulo());
        autor.setText(trabalho.getAutorprincipal());
        apresentador.setText(trabalho.getAutorprincipal());
        if(trabalho.isVoted())
            buttonSalvar.setText(R.string.alterarVoto);


        loadVote(); //carrega o voto.

        //initializing outros
        radioPremiado = (RadioGroup)findViewById(R.id.radioPremiado);
        radioSim = (RadioButton)findViewById(R.id.radioSim);
        textComo = (TextView)findViewById(R.id.textComo);
        textJustifique = (TextView)findViewById(R.id.textJustifique);
        checkMelhor = (CheckBox)findViewById(R.id.checkMelhor);
        checkMencao = (CheckBox)findViewById(R.id.checkMencao);
        checkClareza = (CheckBox)findViewById(R.id.checkClareza);
        checkPesquisador = (CheckBox)findViewById(R.id.checkPesquisador);
        checkRelevancia = (CheckBox)findViewById(R.id.checkRelevancia);
        checkCritica = (CheckBox)findViewById(R.id.checkCritica);
        checkOutro = (CheckBox)findViewById(R.id.checkOutro);
        editOutro = (EditText) findViewById(R.id.editOutro);

        checkOutro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editOutro.setEnabled(isChecked);
            }
        });

        radioPremiado.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == radioSim.getId())
                    showExtras();
                else
                    hideExtras();
            }
        });

        processarVoto = new WebServerCallback() {
            @Override
            public void executar(String w) {
                votoResult();
            }
        };
    }

    private void generateSeekbars(){
        criterios = trabalho.getItensSplitted();
        notas = new int[criterios.length];
        inflateViews(criterios);
    }

    /**
     * Infla as views correspondente às ratingBars, de acordo com os criterios.
     * @param criterios
     */
    private void inflateViews(String[] criterios){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int contadorNotas=0;
        for(String crit : criterios){
            TextView textLabel;
            convertView = inflater.inflate(R.layout.criterio_item, null);
            textLabel = convertView.findViewById(R.id.textLabel);
            final TextView textValue = convertView.findViewById(R.id.textValue);
            final int notaIndex = contadorNotas;
            ratingNota = convertView.findViewById(R.id.ratingNota);
            if(loaded){
                ratingNota.setRating(ratingVotos[contadorNotas]);
                notas[contadorNotas] = ratingVotos[contadorNotas];
            }
            textLabel.setText(crit);
            textValue.setText(Integer.toString(ratingNota.getProgress()));

            ratingNota.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    textValue.setText(Integer.toString((int)rating));
                    notas[notaIndex] = (int)rating;
                }
            });
            contadorNotas++;
            layoutCriterios.addView(convertView);
        }
    }

    public void votar(View v){
        if (waitingCallback) {
            return;
        }
        hideKeyboard(v);
        connectingText.setVisibility(View.VISIBLE);
        connectingBar.setVisibility(View.VISIBLE);
        int idTrabalho = Utils.getTrabalho().getIdtrabalho();
        int idpremiado = radioPremiado.getCheckedRadioButtonId();
        int premiado = (idpremiado == R.id.radioSim ? 1 : 0);

        //Inserindo checkboxes como.
        String[] como = new String[2];
        como[0] = checkMelhor.isChecked() ? (getString(R.string.textMelhor)) : "";
        como[1] = checkMencao.isChecked() ? (getString(R.string.textMencao)) : "";

        //Inserindo checkboxes justifique.
        String[] justifique = new String[5];
        justifique[0] = checkClareza.isChecked() ? (getString(R.string.textClareza)) : "";
        justifique[1] = checkPesquisador.isChecked() ? (getString(R.string.textPesquisador)) : "";
        justifique[2] = checkRelevancia.isChecked() ? (getString(R.string.textRelevancia)) : "";
        justifique[3] = checkCritica.isChecked() ? (getString(R.string.textCritica)) : "";
        justifique[4] = checkOutro.isChecked() ? (editOutro.getText().toString()) : "";
        Voto vt = new Voto(Utils.getCpf(), Integer.toString(idTrabalho), criterios, notas, premiado, como, justifique);
        Utils.addVoto(vt);
        Map vtmap = vt.asMap();
        Log.d("Voto", ""+vtmap);
        waitingCallback = true;
        servidor.votar(vtmap, processarVoto);
    }

    /**
     * Busca o voto correspondente ao trabalho e carrega-o na interface.
     */
    private void loadVote(){
        Voto v = Utils.getVotoByTrabalho(Utils.getTrabalho());
        if (v == null)
            return;
        votoPremiado = v.getPremiado();
        String[] como = v.getComo();
        String[] justificar = v.getJustificar();
        ratingVotos = v.getNotas();
        this.como = new boolean[como.length];
        for(int x=0 ; x < como.length ; x++)
            this.como[x] = como[x].length() > 0;
        this.justificar = new boolean[justificar.length];
        for(int x=0 ; x < justificar.length ; x++)
            this.justificar[x] = justificar[x].length() > 0;

        this.outro = justificar[justificar.length-1];

        loaded = true;

    }

    private void fillVotoFooter(){
        if(!loaded)
            return;
        int cont=0;
        //premiado
        int idPremiado = votoPremiado == 1 ? R.id.radioSim : R.id.radioNao;
        radioPremiado.check(idPremiado);
        //como
        checkMelhor.setChecked(como[cont++]);
        checkMencao.setChecked(como[cont++]);
        cont=0;
        //justificar
        checkClareza.setChecked(justificar[cont++]);
        checkPesquisador.setChecked(justificar[cont++]);
        checkRelevancia.setChecked(justificar[cont++]);
        checkCritica.setChecked(justificar[cont++]);
        checkOutro.setChecked(justificar[cont++]);
        editOutro.setText(outro);
    }

    private void votoResult() {
        connectingText.setVisibility(View.GONE);
        connectingBar.setVisibility(View.GONE);
        int votoStatus = servidor.getVotoStatus();
        waitingCallback = false;
        switch (votoStatus) {
            case WebServerES.AUTH_OK:
                votoOk.show();
                trabalho.setVotado(1);
                finish();
                break;
            case WebServerES.AUTH_ERROR:
                votoError.show();
                break;
        }
    }

    private void showExtras(){
        textComo.setVisibility(View.VISIBLE);
        textJustifique.setVisibility(View.VISIBLE);
        checkMelhor.setVisibility(View.VISIBLE);
        checkMencao.setVisibility(View.VISIBLE);
        checkClareza.setVisibility(View.VISIBLE);
        checkPesquisador.setVisibility(View.VISIBLE);
        checkRelevancia.setVisibility(View.VISIBLE);
        checkCritica.setVisibility(View.VISIBLE);
        checkOutro.setVisibility(View.VISIBLE);
        editOutro.setVisibility(View.VISIBLE);
        layoutScroll.fullScroll(View.FOCUS_DOWN);
//        layoutScroll.scrollTo(0, layoutScroll.getBottom());
    }

    private void hideExtras(){
        textComo.setVisibility(View.GONE);
        textJustifique.setVisibility(View.GONE);
        checkMelhor.setVisibility(View.GONE);
        checkMencao.setVisibility(View.GONE);
        checkClareza.setVisibility(View.GONE);
        checkPesquisador.setVisibility(View.GONE);
        checkRelevancia.setVisibility(View.GONE);
        checkCritica.setVisibility(View.GONE);
        checkOutro.setVisibility(View.GONE);
        editOutro.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed(){
        if(waitingCallback)
            return;
        super.onBackPressed();
    }
    public void cancelar(View v){
        if(waitingCallback)
            return;
        finish();
    }

    public void hideKeyboard(View v){
        Utils.hideKeyboard(this, v);
    }
}
