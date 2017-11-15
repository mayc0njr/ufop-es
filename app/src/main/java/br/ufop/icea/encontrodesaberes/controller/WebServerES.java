package br.ufop.icea.encontrodesaberes.controller;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.albeom.nymeria.*;
import br.ufop.icea.encontrodesaberes.model.Trabalho;

/**
 * Created by mayconjr on 19/10/17.
 */

public class WebServerES extends WebServer {
    public static final int AUTH_OK = 0;
    public static final int AUTH_FAIL = 1;
    public static final int AUTH_ERROR = 2;
    public static final int AUTH_INIT = -1;
    private int authStatus;
    private int votoStatus;
    private static WebServerES instance;


    private WebServerES(String urlBase, Context c) {
        super(urlBase, c);
        authStatus = -1;
    }

    public static WebServerES singleton(){
        return instance;
    }

    public static void initialize(String urlBase, Context c) {
        instance = new WebServerES(urlBase, c);
    }


    public void authenticate(String login, String senha, final WebServerCallback exec) {
        authStatus = AUTH_INIT;
        Map parametros = new HashMap();
        parametros.put("login", login);
        parametros.put("senha", senha);

        postData("login.php", parametros, new WebServerCallback() {
            @Override
            public void executar(String w) {
                Log.d("postData->Executar","Recebendo resposta: " + w);
                Log.d("postData->Executar","Size resposta: " + w.length());
                String response = w.toUpperCase().trim();
                if(response.equals(Utils.FAIL_RESPONSE)) {
                    WebServerES.this.authStatus = AUTH_FAIL;
                } else if(response.startsWith(Utils.OK_RESPONSE)) {
                    String[] name = response.split(";");
                    Utils.setName(name[name.length-1]);
                    WebServerES.this.authStatus = AUTH_OK;
                }else{
                    WebServerES.this.authStatus = AUTH_ERROR;
                }
                exec.executar(w);
            }
        });

    }


//    public void votar(String idAvaliador, String idTrabalho, String criterios[], int notas[], final WebServerCallback exec) {
//        votoStatus = AUTH_INIT;
//        String crit;
//        int nota;
//        for(int x=0 ; x < criterios.length ; x++){
//            crit = criterios[x];
//            nota = notas[x];
//            Log.d("Criterio", crit);
//            Log.d("Nota", ""+nota);
//            Map parametros = new HashMap();
//            parametros.put(Utils.ID_AVALIADOR, idAvaliador);
//            parametros.put(Utils.ID_TRABALHO, idTrabalho);
//            parametros.put(Utils.CRITERIO, crit);
//            parametros.put(Utils.NOTA, Integer.toString(nota));
//
//            postData("votar.php", parametros, new WebServerCallback() {
//                @Override
//                public void executar(String w) {
//                    Log.d("postData->Executar","Recebendo resposta: " + w);
//                    String response = w.toUpperCase();
//                    if(response.toUpperCase().contains(Utils.OK_RESPONSE)) {
//                        Log.d("postData OK", "entrou");
//                        WebServerES.this.votoStatus = AUTH_OK;
//                    }else{
//                        Log.d("postData ERROR", "entrou");
//                        WebServerES.this.votoStatus = AUTH_ERROR;
//                    }
//                    exec.executar(w);
//                }
//            });
//            if(votoStatus == AUTH_ERROR){
//                break;
//            }
//        }
//
//    }

    public void votar(Map parametros, final WebServerCallback exec) {
        votoStatus = AUTH_INIT;
        Log.d("Criterios", (String)parametros.get(Utils.CRITERIOS));

        postData("votar2.php", parametros, new WebServerCallback() {
            @Override
            public void executar(String w) {
                Log.d("postData->Executar","Recebendo resposta: " + w);
                String response = w.toUpperCase();
                if(response.toUpperCase().contains(Utils.OK_RESPONSE)) {
                    Log.d("postData OK", "entrou");
                    WebServerES.this.votoStatus = AUTH_OK;
                }else{
                    Log.d("postData ERROR", "entrou");
                    WebServerES.this.votoStatus = AUTH_ERROR;
                }
                exec.executar(w);
            }
        });
    }

    public void logout(){
        postData("logout.php", new HashMap(), new WebServerCallback() {
            @Override
            public void executar(String w) {
                Log.d("WebServer", "logout");
            }
        });
    }

    public int getAuthStatus(){
        return authStatus;
    }

    public int getVotoStatus(){
        return votoStatus;
    }


    /**
     * Obtem a lista de contatos do servidor, recebendo um xml. A funcao xml.parse preenche um arraylist com os objetos
     * obtidos e especificados na chamada
     * @param ls
     * @param exec
     */
    public void obterTrabalhos(final ArrayList<Trabalho> ls, final WebServerCallback exec) {
        Log.d("WebServer", "obterTrabalhos");
        obterString("listar_trabalhos.php", new WebServerCallback() {
            @Override
            public void executar(String w) {
                Log.d("obterTrabalhos", w);
                Xml.parseInArray(w, ls, Trabalho.class);
                exec.executar(w);
            }
        });
    }
}
