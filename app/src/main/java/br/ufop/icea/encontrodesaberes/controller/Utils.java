package br.ufop.icea.encontrodesaberes.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufop.icea.encontrodesaberes.model.Trabalho;
import br.ufop.icea.encontrodesaberes.model.Voto;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mayconjr on 23/10/17.
 */
public class Utils {

    /** Respostas do Servidor */
    public static String OK_RESPONSE = "OK";
    public static String FAIL_RESPONSE = "FAIL";
    public static String ERRO_RESPONSE = "ERRO";
    public static String ID_AVALIADOR = "idavaliador";
    public static String ID_TRABALHO = "idtrabalho";
    public static String CRITERIOS = "criterios";
    public static String NOTAS = "notas";
    public static String PREMIADO = "premiado";
    public static String COMO = "como";
    public static String MELHOR_TRABALHO="melhor";
    public static String MENCAO_HONROSA="mencao";
    public static String JUSTIFICAR = "justificar";
    public static String OUTRO = "outro";

    public static int CAMPO_CRITERIOS = 0;
    public static int CAMPO_NOTAS = 1;
    public static int CAMPO_COMO = 2;
    public static int CAMPO_JUSTIFICAR = 3;
    public static int VOTOS_CAMPOS = 4;

    public static String CAMPO_SEPARATOR = ";";

    /** Endereco do Servidor. */
    public static final String SERVER_ADDRESS = "http://albeom.com.br/ufop/encontrodesaberes/mobile/";
    /** IO Objects file */
    public static final String STORE_FILE = "dbves.dat";
    private static Activity ioActivity;

    /** Usada para compartilhar trabalhos entre activities. */
    private static Trabalho trabalho;
    private static String cpf;
    private static String name;
    private static List<Voto> savedVotes;

    /** Variavel usada para controlar o teclado android (esconder/mostrar) */
    private static InputMethodManager keyboard;

    static{
        savedVotes = new ArrayList<>();
    }
    private Utils(){}

    /**
     * Esconde o teclado.
     * @param activity Activity ativa que contém a view esconderá o teclado.
     */
    public static void hideKeyboard(Activity activity){
        keyboard = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if(v == null)
            v = new View(activity);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static int intSize(){
        return Integer.SIZE/Byte.SIZE;
    }
    public static int longSize(){
        return Long.SIZE/Byte.SIZE;
    }

    /**
     * Método intToByte: Convete de um valor inteiro em um vetor de bytes.
     *
     * @param num Um valor do tipo int.
     *
     * @return Um vetor de bytes obtido a partir da serialização do valor do int.
     */
    public static byte[] intToByte(int num) {
        int size = intSize();
        byte[] value = new byte[size];
        int shift;
        for (int i = 0; i < size; i++) {
            shift = (size - 1 - i) * 8;
            value[i] = (byte) ((num >> shift) & 0xFF);
        }
        return value;
    }


    /**
     * Método byteToInt: Converte um vetor de bytes em um valor inteiro.
     *
     * @param b Vetor de bytes contendo o valor a ser desserializado.
     *
     * @return Um valor de tipo int obtido a partir da desserialização do vetor de bytes.
     */
    public static int byteToInt(byte[] b) {
        int size = intSize();
        int value = 0, shift;
        for (int i = 0; i < size; i++) {
            shift = (size - 1 - i) * 8;
            value = value | ((int) ((b[i] & 0xFF)) << shift);
        }
        return value;
    }

    /**
     * Método longToByte: Convete de um valor long em um vetor de bytes.
     *
     * @param num Um valor do tipo long.
     *
     * @return Um vetor de bytes obtido a partir da serialização do valor do long.
     */
    public static byte[] longToByte(long num) {
        int size = longSize();
        byte[] value = new byte[size];
        int shift;
        for (int i = 0; i < size; i++) {
            shift = (size - 1 - i) * 8;
            value[i] = (byte) ((num >> shift) & 0xFF);
        }
        return value;
    }

    /**
     * Método byteToLong: Converte um vetor de bytes em um valor long.
     *
     * @param b Vetor de bytes contendo o valor a ser desserializado.
     *
     * @return Um valor de tipo long obtido a partir da desserialização do vetor de bytes.
     */
    public static long byteToLong(byte[] b) {
        int size = longSize();
        long value = 0;
        int shift;
        for (int i = 0; i < size; i++) {
            shift = (size - 1 - i) * 8;
            value = value | ((long) ((b[i] & 0xFF)) << shift);
        }
        return value;
    }

    public static void hideKeyboard(Activity activity, View v){
        keyboard = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(v == null)
            v = new View(activity);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Método de compartilhar objetos do tipo Trabalho entre Activities.
     * @param t Trabalho a ser compartilhado.
     */
    public static void shareTrabalho(Trabalho t){
        trabalho = t;
    }

    /**
     * Método de obter objetos do tipo Trabalho compartilhados entre Activities.
     * @return Objeto compartilhado.
     */
    public static Trabalho getTrabalho(){
        return trabalho;
    }

    public static String getName(){
        return name;
    }

    public static void setName(String name){
        Utils.name = name;
    }

    public static String getCpf(){
        return cpf;
    }

    public static void setCpf(String cpf){
        Utils.cpf = cpf;
    }
    private static boolean saved = false;

    public static void saveVotes(){
        if(saved)
            return;
        Log.d("Utils", "saveVoto: ");
        FileOutputStream out;
        try{
            out = ioActivity.openFileOutput(Utils.STORE_FILE, MODE_PRIVATE);
            Collections.sort(savedVotes);
            for(Voto v : savedVotes){
                v.write(out);
            }
            out.flush();
            out.close();
        }catch(IOException e){
            Log.e("Splash WRITE", e.toString());
        }
        saved = true;
    }
    public static void loadVotes(Activity context){
        Log.d("Utils", "loadVoto: ");
        ioActivity = context;
        FileInputStream in;
        savedVotes = new ArrayList<>();
        try{
            in = context.openFileInput(Utils.STORE_FILE);
            while(in.available() > 0){
                Voto temp = new Voto();
                temp.read(in);
                savedVotes.add(temp);
            }
            in.close();
        }catch(IOException e){
            Log.e("Splash READ", e.toString());
        }
        saved = true;
    }

    /**
     * Obtem o voto que corresponde ao trabalho e ao avaliador, comparando os ids.
     * @param idTrabalho
     * @return
     */
    public static Voto getVotoByIdTrabalhoIdAvaliador(String idTrabalho, String avaliador){
        Log.d("params", "idt: " + idTrabalho);
        Log.d("params", "aval: " + avaliador);
        for(Voto v : savedVotes){
            Log.d("lista", "idt: " + v.getIdTrabalho());
            Log.d("lista", "aval: " + v.getTrueIdAvaliador());
            if(v.getIdTrabalho().equals(idTrabalho) && v.getTrueIdAvaliador().equals(avaliador))
                return v;
        }
        return null;
    }

    public static Voto getVotoByTrabalho(Trabalho t){
        return getVotoByIdTrabalhoIdAvaliador(Integer.toString(t.getIdtrabalho()), Integer.toString(t.getAvaliador()));
    }

    /**
     * Adiciona um voto na lista de votos.
     *
     * Caso já haja um voto presente na lista, para o trabalho, e avaliador,
     * remove o voto antigo, e adiciona o voto novo.
     * @param v
     */
    public static void addVoto(Voto v){
        saved = false;
        Log.d("Utils", "addVoto: ");
        for(int x=0 ; x < savedVotes.size() ; x++){
            if(v.compareTo(savedVotes.get(x)) == 0){
                savedVotes.remove(x);
                break;
            }
        }
        savedVotes.add(v);
    }

    /**
     * Display the toast, for a custom duration.
     * The duration should be less than default toast duration.
     * e.g. A 500ms custom duration, can be a Toast builted with 'LENGTH_SHORT' parameter,
     * but a 5000ms custom duration for example, should use a 'LENGTH_LONG' parameter.
     * @param t Toasted to be displayed
     * @param duration duration on screen.
     */
    public static void customShow(final Toast t, long duration){
        t.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.cancel();
            }
        }, duration);
    }
}
