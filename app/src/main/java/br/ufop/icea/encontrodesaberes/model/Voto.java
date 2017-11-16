package br.ufop.icea.encontrodesaberes.model;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.model.io.*;

/**
 * Created by maycon on 26/10/17.
 */

public class Voto implements Comparable<Voto>{
    public static int MELHOR_TRABALHO = 0;
    public static int MENCAO_HONROSA = 1;
    public static int CLAREZA = 0;
    public static int PESQUISADOR = 1;
    public static int RELEVANCIA = 2;
    public static int CRITICA = 3;
    public static int OUTRO = 4;


    private int premiado;
    private String idAvaliador, idTrabalho;
    private String[] criterios;
    private int[] notas;
    private String[] como;
    private String[] justificar;
//    private String[] comentarios;
//    private boolean[] checkBox;

    /** Construtor usado para inicializar um objeto vazio.
     * Usado apenas para carregar dados do arquivo.
     * */
    public Voto(){}

    /**
     * Constrói um voto para ser enviado para o servidor.
     * @param idAvaliador
     * @param idTrabalho
     * @param criterios
     * @param notas
     * @param premiado
     * @param como
     * @param justificar
     */
    public Voto(String idAvaliador, String idTrabalho, String[] criterios, int[] notas, int premiado, String[] como, String[] justificar){
        if(criterios.length != notas.length)
            throw new IllegalArgumentException("Criterios e Notas devem estar em quantidade igual");
        this.idAvaliador = idAvaliador;
        this.idTrabalho = idTrabalho;
        this.criterios = new String[criterios.length];
        this.notas = new int[notas.length];
        this.como = new String[como.length];
        this.justificar = new String[justificar.length];
        this.premiado = premiado;
        for(int x=0 ; x < criterios.length ; x++){
            this.criterios[x] = criterios[x];
            this.notas[x] = notas[x];
        }
        for(int x=0 ; x < como.length ; x++){
            this.como[x] = como[x];
        }
        for(int x=0 ; x < justificar.length ; x++){
            this.justificar[x] = justificar[x];
        }
    }

    /**
     * Obtem o objeto do voto em forma de mapa,
     * usado para enviar os dados do voto para o servidor.
     * @return Um Map, contendo os dados do voto.
     */
    public Map asMap(){
        HashMap map = new HashMap();
        map.put(Utils.ID_AVALIADOR, idAvaliador);
        map.put(Utils.ID_TRABALHO, idTrabalho);
        StringBuilder sb = new StringBuilder();

        if(criterios.length <= 0)// Retorna imediatamente se nao houver criterios.
            return map;

            /*ADICIONANDO AS ESTRELAS NO VOTO*/
        sb.append(criterios[0]);
        sb.append('=');
        sb.append(notas[0]);
        for(int x=1 ; x < criterios.length ; x++){
            sb.append(';');
            sb.append(criterios[x]);
            sb.append('=');
            sb.append(notas[x]);
        }
        map.put(Utils.CRITERIOS, sb.toString());
        map.put(Utils.PREMIADO, Integer.toString(premiado));
        if(premiado == 1){
            /*ADICIONANDO AS CHECK BOX 'COMO' NO VOTO*/
            sb = new StringBuilder();
            for(int x=0 ; x < como.length ; x++){
                sb.append(como[x]);
                sb.append(';');
            }
            sb.deleteCharAt(sb.length()-1);
            map.put(Utils.COMO, sb.toString());


            /*ADICIONANDO AS CHECK BOX 'JUSTIFICAR' NO VOTO*/
            sb = new StringBuilder();
            for(int x=0 ; x < justificar.length ; x++){
                sb.append(justificar[x]);
                sb.append(';');
            }
            sb.deleteCharAt(sb.length()-1);
            map.put(Utils.JUSTIFICAR, sb.toString());
        }
        return map;
    }

    public int compareTo(Voto v){
        return this.idTrabalho.hashCode() - v.idTrabalho.hashCode();
    }

    public int getIdTrabalho(){
        return Integer.parseInt(this.idTrabalho);
    }

    public int[] getNotas(){
        return notas;
    }

    public String[] getComo(){
        return como;
    }

    public String[] getJustificar(){
        return justificar;
    }

    public int getPremiado(){
        return premiado;
    }


    public void write(OutputStream out) throws IOException {
        int fields;
        WriteObject[] io = new WriteObject[7];
        IOObject[] ioCriterios = new IOObject[criterios.length];
        for(int x=0 ; x < criterios.length ; x++){
            ioCriterios[x] = new IOString(criterios[x]);
            Log.d("Writing", "Criterios: " + criterios[x]);
        }
        IOObject[] ioNotas = new IOObject[notas.length];
        for(int x=0 ; x < notas.length ; x++){
            ioNotas[x] = new IOInt(notas[x]);
            Log.d("Writing", "Notas: " + notas[x]);
        }
        IOObject[] ioComo = new IOObject[como.length];
        for(int x=0 ; x < como.length ; x++){
            ioComo[x] = new IOString(como[x]);
            Log.d("Writing", "Como: " + como[x]);
        }
        IOObject[] ioJustificar = new IOObject[justificar.length];
        for(int x=0 ; x < justificar.length ; x++){
            ioJustificar[x] = new IOString(justificar[x]);
            Log.d("Writing", "Justificar: " + justificar[x]);
        }
        io[0] = new IOString(this.idAvaliador);
        io[1]  = new IOString(this.idTrabalho);
        io[2]  = new IOArray(ioCriterios);
        io[3]  = new IOArray(ioNotas);
        io[4]  = new IOInt(this.premiado);
        io[5]  = new IOArray(ioComo);
        io[6]  = new IOArray(ioJustificar);
        for(WriteObject wo : io){
            wo.write(out);
        }
    }

    public void read(InputStream in) throws IOException{
        ReadObject[] io = new ReadObject[7];
        io[0] = new IOString();
        io[1]  = new IOString();
        io[2]  = new IOArray(new IOString());
        io[3]  = new IOArray(new IOInt());
        io[4]  = new IOInt();
        io[5]  = new IOArray(new IOString());
        io[6]  = new IOArray(new IOString());
        for(ReadObject ro : io){
            ro.read(in);
        }
        IOObject[] ioCriterios;
        IOObject[] ioNotas;
        IOObject[] ioComo;
        IOObject[] ioJustificar;
        this.idAvaliador = ((IOString)io[0]).get();
        this.idTrabalho = ((IOString)io[1]).get();

        ioCriterios = ((IOArray)io[2]).get();
        this.criterios = new String[ioCriterios.length];
        for(int x=0 ; x < criterios.length ; x++){
            criterios[x] = ((IOString)ioCriterios[x]).get();
            Log.d("Reading", "Criterios: " + criterios[x]);
        }

        ioNotas = ((IOArray)io[3]).get();
        this.notas = new int[ioNotas.length];
        for(int x=0 ; x < notas.length ; x++){
            notas[x] = ((IOInt)ioNotas[x]).get();
            Log.d("Reading", "Notas: " + notas[x]);
        }

        this.premiado = ((IOInt)io[4]).get();

        ioComo = ((IOArray)io[5]).get();
        this.como = new String[ioComo.length];
        for(int x=0 ; x < como.length ; x++){
            como[x] = ((IOString)ioComo[x]).get();
            Log.d("Reading", "Como: " + como[x]);
        }

        ioJustificar = ((IOArray)io[6]).get();
        this.justificar = new String[ioJustificar.length];
        for(int x=0 ; x < justificar.length ; x++){
            justificar[x] = ((IOString)ioJustificar[x]).get();
            Log.d("Reading", "Justificar: " + justificar[x]);
        }
    }

}
