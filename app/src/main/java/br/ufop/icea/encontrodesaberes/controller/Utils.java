package br.ufop.icea.encontrodesaberes.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
    public static String NOTA = "nota";
    public static String PREMIADO = "premiado";
    public static String COMO = "como";
    public static String JUSTIFICAR = "justificar";
    public static String OUTRO = "outro";

    /** IO Objects file*/
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
     * @param num
     *            : Um valor do tipo int.
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
     * @param b
     *          : Vetor de bytes contendo o valor a ser desserializado.
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
     * @param num
     *            : Um valor do tipo long.
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
     * @param b
     *          : Vetor de bytes contendo o valor a ser desserializado.
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

    public static void saveVotes(){
        FileOutputStream out;
        try{
            out = ioActivity.openFileOutput(Utils.STORE_FILE, MODE_PRIVATE);
            Collections.sort(savedVotes);
            for(Voto v : savedVotes){
                v.write(out);
            }
        }catch(IOException e){
            Log.e("Splash WRITE", e.toString());
        }

    }
    public static void loadVotes(Activity a){
        ioActivity = a;
        FileInputStream in;
        savedVotes = new ArrayList<>();
        try{
            in = a.openFileInput(Utils.STORE_FILE);
            while(in.available() > 0){
                Voto temp = new Voto();
                temp.read(in);
                savedVotes.add(temp);
            }
            in.close();
        }catch(IOException e){
            Log.e("Splash READ", e.toString());
        }
    }

    /**
     * Obtem o voto que corresponde ao trabalho, comparando os ids.
     * @param idTrabalho
     * @return
     */
    public static Voto getVotoByIdTrabalho(int idTrabalho){
        for(Voto v : savedVotes)
            if(v.getIdTrabalho() == idTrabalho)
                return v;
        return null;
    }

    public static Voto getVotoByTrabalho(Trabalho t){
        return getVotoByIdTrabalho(t.getIdtrabalho());
    }

    public static void addVoto(Voto v){
        for(int x=0 ; x < savedVotes.size() ; x++){
            if(v.getIdTrabalho() == savedVotes.get(x).getIdTrabalho()){
                savedVotes.remove(x);
                break;
            }
        }
        savedVotes.add(v);
    }
}
