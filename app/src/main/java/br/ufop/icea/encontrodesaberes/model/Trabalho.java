package br.ufop.icea.encontrodesaberes.model;

import android.util.Log;

/**
 * Modelo do objeto Trabalho usado para definir o trabalho a ser votado pelos avaliadores do evento.
 * Created by mayconjr on 19/10/17.
 */
public class Trabalho {

    String titulo, autorprincipal, nomeavaliador;
    Integer idtrabalho, avaliador;
    String criterios;
    Integer votado;
    private String[] itensSplited;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutorprincipal() {
        return autorprincipal;
    }

    public void setAutorprincipal(String autorprincipal) {
        this.autorprincipal = autorprincipal;
    }

    public String getNomeavaliador() {
        return nomeavaliador;
    }

    public void setNomeavaliador(String nomeavaliador) {
        this.nomeavaliador = nomeavaliador;
    }

    public Integer getIdtrabalho() {
        return idtrabalho;
    }

    public void setIdtrabalho(Integer idtrabalho) {
        this.idtrabalho = idtrabalho;
    }

    public Integer getAvaliador() {
        return avaliador;
    }

    public void setAvaliador(Integer avaliador) {
        this.avaliador = avaliador;
    }

    public String getCriterios() {
        return criterios;
    }

    /**
     * Atribui os criterios recebidos pela rede, e também os atribui
     * em forma de strings separadas ao vetor 'itensSplited',
     * que está no formato adequado para ser usado nas telas de exibição.
     * @param criterios Criterios que devem ser votados os trabalhos
     */
    public void setCriterios(String criterios) {
        this.criterios = criterios;
        itensSplited = criterios.split(",");
        for(int x=0 ; x < itensSplited.length ; x++)
            itensSplited[x] = itensSplited[x].trim();
//        itensSplited = new String[]{"CLAREZA", "CONTEUDO", "FORMATAÇÃO", "DOMINIO DO TEMA", "TEMPO DE APRESENTAÇÃO"};
        Log.d("itensSplited", itensSplited.toString());
    }

    /**
     * Obtem o array de criterios, ja separados.
     * @return obtem o array que contem o nome dos criterios.
     */
    public String[] getItensSplitted(){
        return itensSplited;
    }

    public void setVotado(Integer votado){
        Log.d("TRABALHO", "SetVotado");
        this.votado = votado;
    }

    public Integer getVotado(){
        return votado;
    }

    public boolean isVoted(){
        return votado != 0;
    }
}