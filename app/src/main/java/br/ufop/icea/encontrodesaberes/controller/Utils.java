package br.ufop.icea.encontrodesaberes.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import br.ufop.icea.encontrodesaberes.model.Trabalho;

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

    /** Usada para compartilhar trabalhos entre activities. */
    private static Trabalho trabalho;
    private static String cpf;
    private static String name;

    /** Variavel usada para controlar o teclado android (esconder/mostrar) */
    private static InputMethodManager keyboard;
    private Utils(){}


    public static void hideKeyboard(Activity activity){
        keyboard = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if(v == null)
            v = new View(activity);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
}
