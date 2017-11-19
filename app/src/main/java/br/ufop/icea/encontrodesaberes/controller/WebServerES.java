package br.ufop.icea.encontrodesaberes.controller;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.albeom.nymeria.*;
import br.ufop.icea.encontrodesaberes.model.Trabalho;

/**
 * Extende WebServer para fornecer a conexão com o Servidor.
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


    /**
     * Constrói um objeto de servidor a partir do endereço do servidor e da Activity.
     * @param urlBase
     * @param c
     */
    private WebServerES(String urlBase, Context c) {
        super(urlBase, c);
        authStatus = -1;
        votoStatus = -1;
    }
    public static WebServerES singleton(){
        return instance;
    }

    /**
     * Inicializa a instancia do objeto que será recuperada pelo singleton.
     * @param urlBase Endereco base do servidor, onde serão executados os scripts.
     * @param c Contexto/Activity usada.
     */
    public static void initialize(String urlBase, Context c) {
        instance = new WebServerES(urlBase, c);
    }

    /**
     * Autentica o usuário e senha no servidor.
     * Recebe a resposta, e executa a função recebida no último parâmetro.
     * @param login Usuário a ser autenticado.
     * @param senha Senha correspondente ao usuário.
     * @param exec função a ser executada.
     */
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

    /**
     * Envia o voto para o servidor.
     * @param parametros Map contendo todas as informações do voto, como notas, Id's, etc.
     * @param exec Função a ser executada após receber a resposta do servidor.
     */
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

    /**
     * Remove a autenticação do usuário no servidor.
     */
    public void logout(){
        postData("logout.php", new HashMap(), new WebServerCallback() {
            @Override
            public void executar(String w) {
                Log.d("WebServer", "logout");
            }
        });
    }

    /**
     * Usado para obter o estado da autenticação do servidor.
     * @return
     *          AUTH_OK (0) caso a autenticação tenha sido bem sucedida.
     *          AUTH_FAIL (1) caso a autenticação tenha sido negada.
     *          AUTH_ERROR (2) caso tenha ocorrido alguma falha.
     *          AUTH_INIT (-1) caso a autenticação não tenha sido requisitada.
     */
    public int getAuthStatus(){
        return authStatus;
    }


    /**
     * Usado para obter o estado do voto enviado ao servidor.
     * @return
     *          AUTH_OK (0) caso o voto tenha sido bem sucedida.
     *          AUTH_FAIL (1) caso a autenticação tenha sido negada.
     *          AUTH_ERROR (2) caso tenha ocorrido alguma falha.
     *          AUTH_INIT (-1) caso o voto não tenha sido requisitada.
     */
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
