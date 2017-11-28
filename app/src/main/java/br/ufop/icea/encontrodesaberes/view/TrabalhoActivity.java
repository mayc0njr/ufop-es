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

import java.util.Map;

import br.com.albeom.nymeria.WebServerCallback;
import br.com.albeom.nymeria.view.TwoTapsBackAppCompatActivity;
import br.ufop.icea.encontrodesaberes.R;
import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.controller.WebServerES;
import br.ufop.icea.encontrodesaberes.model.Trabalho;
import br.ufop.icea.encontrodesaberes.model.Voto;

/**
 * Classe responsável por captar o voto, e enviar ao servidor.
 *
 * Instancia dinamicamente RatingBars de acordo com os critérios
 * do trabalho a ser avaliado.
 * Carrega (se já houver dados), um voto já efetuado pelo mesmo
 * avaliador, para o mesmo trabalho, e preenche os campos adequadamente.
 */
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
    Toast votoOk, votoError, notFoundToast, notFoundFinalToast, premiadoToast, comoToast, justificarToast;
    WebServerCallback processarVoto;
    WebServerCallback carregarVoto;
    Voto lastVote;

    private int tries; //tentativas de obter um trabalho votado.
    private int maxTries;

    /**Usado para baixar o voto do servidor;*/
    private String[] fields;

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
//        generateSeekbars();
//        fillVotoFooter();

    }

    /**
     * Inicializa os toasts que podem ser mostrados na tela.
     */
    private void initializeToasts(){
        notFoundToast = Toast.makeText(this, R.string.notFoundVoto, Toast.LENGTH_SHORT);
        notFoundFinalToast = Toast.makeText(this, R.string.notFoundVotoFinal, Toast.LENGTH_SHORT);
        votoOk = Toast.makeText(this, R.string.textOk, Toast.LENGTH_SHORT);
        votoError = Toast.makeText(this, R.string.textError, Toast.LENGTH_SHORT);
        premiadoToast = Toast.makeText(this, R.string.premiadoToast, Toast.LENGTH_SHORT);
        comoToast = Toast.makeText(this, R.string.comoToast, Toast.LENGTH_SHORT);
        justificarToast = Toast.makeText(this, R.string.justificarToast, Toast.LENGTH_SHORT);
    }

    /**
     * Inicializa as views e preenche (se necessário) com os valores padrão
     * e/ou lidos do Trabalho.
     */
    private void initializeValues(){
        tries = 0;
        maxTries = getResources().getInteger(R.integer.triesGet);
        fields = new String[Utils.VOTOS_CAMPOS];

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


//        loadVote(); //carrega o voto.

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


        checkMelhor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    checkMencao.setChecked(false);
            }
        });
        checkMencao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    checkMelhor.setChecked(false);
            }
        });
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

        Log.d("initializeValues", "initializing carregarVoto");
        carregarVoto = new WebServerCallback() {
            @Override
            public void executar(String w) {
                loadResult();
            }
        };
        loadVote(); //carrega o voto.
    }

    /**
     * Carrega a função de inflar as views, de acordo com os critérios lidos do trabalho.
     */
    private void generateSeekbars(){
        criterios = trabalho.getItensSplitted();
        if(notas == null){
            notas = new int[criterios.length];
        }
        if(notas == null) {
            ratingVotos = new int[criterios.length];
        }
        Log.d("generateSeekBars", "Criterios: " + criterios.length);
//        notas = new int[criterios.length];
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
            if(loaded) {
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

    /**
     * Obtém os dados dos campos preenchidos pelo usuário,
     * Preenche um voto, e manda-o para o servidor.
     * Também chama a função utilitária para salvar no arquivo.
     * @param v View que chamou a função.
     */
    public void votar(View v){
        if (waitingCallback) {
            return;
        }
        hideKeyboard(v);
        int idradio = radioPremiado.getCheckedRadioButtonId();
        if(idradio != R.id.radioNao && idradio != R.id.radioSim){
            premiadoToast.show();
            return;
        }
        if(idradio == R.id.radioSim && !checkMelhor.isChecked() && !checkMencao.isChecked()){
            comoToast.show();
            return;
        }
        if((checkMelhor.isChecked() || checkMencao.isChecked())
                && !checkClareza.isChecked() && !checkPesquisador.isChecked()
                && !checkRelevancia.isChecked() && !checkCritica.isChecked()
                && !(checkOutro.isChecked() && editOutro.length() > 0)){
            justificarToast.show();
            return;
        }
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
        Voto vt = new Voto(Utils.getTrabalho().getAvaliador().toString(), Utils.getCpf(), Integer.toString(idTrabalho), criterios, notas, premiado, como, justifique);
        Map vtmap = vt.asMap();
        Log.d("Voto", ""+vtmap);
        waitingCallback = true;
        lastVote = vt;
        servidor.votar(vtmap, processarVoto);
    }

//    /**
//     * Busca o voto correspondente ao trabalho e carrega-o na interface.
//     */
//    private void loadVote(){ //Arquivo
//        Voto v = Utils.getVotoByTrabalho(Utils.getTrabalho());
//        if (v == null)
//            return;
//        votoPremiado = v.getPremiado();
//        String[] como = v.getComo();
//        String[] justificar = v.getJustificar();
//        ratingVotos = v.getNotas();
//        this.como = new boolean[como.length];
//        for(int x=0 ; x < como.length ; x++)
//            this.como[x] = como[x].length() > 0;
//        this.justificar = new boolean[justificar.length];
//        for(int x=0 ; x < justificar.length ; x++)
//            this.justificar[x] = justificar[x].length() > 0;
//
//        this.outro = justificar[justificar.length-1];
//
//        loaded = true;
//
//    }

    /**
     * Busca o voto correspondente ao trabalho e carrega-o na interface.
     */
    private void loadVote(){ //Servidor
        Log.d("Loading Votes.", ".");
        if(!Utils.getTrabalho().isVoted()){
            generateSeekbars();
            return; //Trabalho nao foi votado.
        }
        connectingText.setVisibility(View.VISIBLE);
        connectingBar.setVisibility(View.VISIBLE);
        waitingCallback = true;
        servidor.obterVotos(fields, carregarVoto);
    }

    /**
     * Preenche os dados das checkboxes do voto, relacionadas à premiação.
     * Usada após carregar um voto do arquivo.
     */
    private void fillVotoFooter(){
//        if(!loaded)
//            return;
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

    /**
     * Processa o resultado do voto.
     * Caso o servidor responda positivamente, salva o voto, e retorna à lista.
     * Caso o servidor não responda, ou responda negativamente, nada é salvo, e um feedback é dado ao usuário.
     */
    private void votoResult() {
        connectingText.setVisibility(View.GONE);
        connectingBar.setVisibility(View.GONE);
        int votoStatus = servidor.getVotoStatus();
        waitingCallback = false;
        switch (votoStatus) {
            case WebServerES.AUTH_OK:
                votoOk.show();
                trabalho.setVotado(1);
                Utils.addVoto(lastVote);
                finish();
                break;
            case WebServerES.AUTH_ERROR:
                votoError.show();
                break;
        }
    }

    private void emptyFields(){
        this.como = new boolean[2];
        this.justificar = new boolean[5];
        this.criterios = trabalho.getItensSplitted();
        this.notas = new int[criterios.length];
        this.ratingVotos = new int[notas.length];
    }

    private boolean fillFields(String[] fields){
        this.como = new boolean[2];
        this.justificar = new boolean[5];
        Log.d("TrabalhoActivity","Fill Fields");
        String[] comostr = fields[Utils.CAMPO_COMO].split(Utils.CAMPO_SEPARATOR);
        String[] notastr = fields[Utils.CAMPO_NOTAS].split(Utils.CAMPO_SEPARATOR);
        String[] justificarstr = fields[Utils.CAMPO_JUSTIFICAR].split(Utils.CAMPO_SEPARATOR);
        notas = new int[trabalho.getItensSplitted().length];
        ratingVotos = new int[trabalho.getItensSplitted().length];
        if(notastr.length != notas.length)
            return false;
        for(int x=0 ; x < comostr.length ; x++){
            Log.d("fillFields", "comostr: " + comostr[x]);
            this.como[x] = Integer.parseInt(comostr[x]) > 0;
            Log.d("fillFields", "como: " + this.como[x]);
            if(como[x] == true)
                votoPremiado = 1;
                Log.d("fillFields", "votoPremiado: " + votoPremiado);
        }
        for(int x=0 ; x < notastr.length ; x++){
            this.notas[x] = Integer.parseInt(notastr[x]);
            this.ratingVotos[x] = notas[x];
        }
        //justificar
//        checkClareza.setChecked(justificar[cont++]);
//        checkPesquisador.setChecked(justificar[cont++]);
//        checkRelevancia.setChecked(justificar[cont++]);
//        checkCritica.setChecked(justificar[cont++]);
//        checkOutro.setChecked(justificar[cont++]);
//        editOutro.setText(outro);
        try{
        for(int x=0 ; x < justificarstr.length ; x++){
            if(justificarstr[x].contains(getString(R.string.textClareza))){
                Log.d("textClareza...", justificarstr[x]);
                justificar[0] = true;
            }
            else if(justificarstr[x].contains(getString(R.string.textPesquisador))){
                Log.d("textPesquisador...", justificarstr[x]);
                justificar[1] = true;
            }
            else if(justificarstr[x].contains(getString(R.string.textRelevancia))){
                Log.d("textRelevancia...", justificarstr[x]);
                justificar[2] = true;
            }
            else if(justificarstr[x].contains(getString(R.string.textCritica))){
                Log.d("textCritica...", justificarstr[x]);
                justificar[3] = true;
            }else if(justificarstr[x].length() > 0){
                Log.d("outro...", justificarstr[x]);
                justificar[4] = true;
                outro = justificarstr[x];
            }
        }
        }catch(Exception e){
            Log.e("FillVotes", e.toString());
            return false;
        }
        return true;
    }
    /**
     * Processa o carregamento do voto.
     * Caso o servidor responda positivamente, carrega os atributos.
     * Caso o servidor não responda, ou responda negativamente, tenta novamente algumas vezes.
     */
    private void loadResult() {
        Log.d("TrabalhoActivity", "loadResult");
        tries++;
        boolean filled = fillFields(fields);
        loaded = filled;
        if(!filled){
            if(tries < maxTries){
                notFoundToast.setText(String.format(getString(R.string.notFoundVoto), tries, maxTries));
                notFoundToast.show();
                servidor.obterVotos(fields, carregarVoto);
                return;
            }
            else{
                notFoundFinalToast.show();
                emptyFields();
            }
        }
        waitingCallback = false;
        connectingText.setVisibility(View.GONE);
        connectingBar.setVisibility(View.GONE);
        generateSeekbars();
        fillVotoFooter();
    }

    /**
     * Mostra as checkboxes adicionais.
     * Usado quando o usuário toca no radioButton 'Sim' no grupo 'Premiado'.
     */
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

    /**
     * Esconde as checkboxes adicionais.
     * Usado quando o usuário toca no radioButton 'Não' no grupo 'Premiado'.
     */
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

    /**
     * Sobreescrita do método onBackPressed.
     * Usado para impedir que o usuário volte à lista enquanto espera resposta do servidor.
     */
    @Override
    public void onBackPressed(){
        if(waitingCallback)
            return;
        super.onBackPressed();
    }

    /**
     * Função do botão cancelar, volta à view anterior, cancelando o voto.
     * Não funciona enquanto espera resposta do servidor.
     * @param v View que chamou a função.
     */
    public void cancelar(View v){
        if(waitingCallback)
            return;
        finish();
    }

    /**
     * Chama a função utilitária para esconder o teclado.
     * @param v View que chamou a função.
     */
    public void hideKeyboard(View v){
        Utils.hideKeyboard(this, v);
    }
}
