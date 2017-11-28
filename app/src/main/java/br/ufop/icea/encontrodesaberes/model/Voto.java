package br.ufop.icea.encontrodesaberes.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import br.ufop.icea.encontrodesaberes.controller.Utils;
import br.ufop.icea.encontrodesaberes.model.io.*;

/**
 * Representa um voto que será armazenado no arquivo e enviado ao servidor.
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
    private String cpfAvaliador, idTrabalho, trueIdAvaliador;
    private String[] criterios;
    private int[] notas;
    private String[] como;
    private String[] justificar;

    /** Construtor usado para inicializar um objeto vazio.
     * Usado apenas para carregar dados do arquivo.
     * */
    public Voto(){}

    /**
     * Constrói um voto para ser enviado para o servidor.
     * @param trueIdAvaliador id do avaliador (igual ao do trabalho. ~ Nao eh enviado ao servidor, usado para recuperar e carregar votos do arquivo.
     * @param cpfAvaliador cpf do avaliador.
     * @param idTrabalho id do trabalho a ser avaliado
     * @param criterios lista de criterios a serem avaliados
     * @param notas notas dos criterios
     * @param premiado se deve ser premiado
     * @param como como deve ser premiado (melhor trabalho, mencao honrosa)
     * @param justificar justificativas para o motivo da premiacao.
     */
    public Voto(String trueIdAvaliador, String cpfAvaliador, String idTrabalho, String[] criterios, int[] notas, int premiado, String[] como, String[] justificar){
        if(criterios.length != notas.length)
            throw new IllegalArgumentException("Criterios e Notas devem estar em quantidade igual");
        this.trueIdAvaliador = trueIdAvaliador;
        this.cpfAvaliador = cpfAvaliador;
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
     * Obs.:    O campo trueIdAvaliador não é enviado ao servidor, mas é escrito no arquivo,
     *          pois é necessário para comparar com o id escrito no trabalho
     *          para carregar os votos do arquivo.
     *
     * @return Um Map, contendo os dados do voto.
     */
    public Map asMap(){
        HashMap map = new HashMap();
        map.put(Utils.ID_AVALIADOR, cpfAvaliador);
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
        int x = this.idTrabalho.compareTo(v.idTrabalho);
        if (x != 0)
            return x;
        return this.cpfAvaliador.compareTo(v.cpfAvaliador);
    }

    public String getIdTrabalho(){
        return this.idTrabalho;
    }

    public String getCpfAvaliador(){
        return cpfAvaliador;
    }

    public String getTrueIdAvaliador(){
        return trueIdAvaliador;
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


    /**
     * Efetua a escrita de um voto no Stream de saída.
     * Cria objetos de leitura e escrita compatíveis com os campos do voto,
     * Então, escreve campo a campo e preenche os elementos do voto no stream.
     * @param out Stream de saída onde serão salvos os votos.
     * @throws IOException Caso o arquivo não possa ser escrito.
     */
    public void write(OutputStream out) throws IOException {
        int fields;
        WriteObject[] io = new WriteObject[8];
        IOObject[] ioCriterios = new IOObject[criterios.length];
        for(int x=0 ; x < criterios.length ; x++){
            ioCriterios[x] = new IOString(criterios[x]);
//            Log.d("Writing", "Criterios: " + criterios[x]);
        }
        IOObject[] ioNotas = new IOObject[notas.length];
        for(int x=0 ; x < notas.length ; x++){
            ioNotas[x] = new IOInt(notas[x]);
//            Log.d("Writing", "Notas: " + notas[x]);
        }
        IOObject[] ioComo = new IOObject[como.length];
        for(int x=0 ; x < como.length ; x++){
            ioComo[x] = new IOString(como[x]);
//            Log.d("Writing", "Como: " + como[x]);
        }
        IOObject[] ioJustificar = new IOObject[justificar.length];
        for(int x=0 ; x < justificar.length ; x++){
            ioJustificar[x] = new IOString(justificar[x]);
//            Log.d("Writing", "Justificar: " + justificar[x]);
        }
        io[0] = new IOString(this.trueIdAvaliador);
        io[1] = new IOString(this.cpfAvaliador);
        io[2]  = new IOString(this.idTrabalho);
        io[3]  = new IOArray(ioCriterios);
        io[4]  = new IOArray(ioNotas);
        io[5]  = new IOInt(this.premiado);
        io[6]  = new IOArray(ioComo);
        io[7]  = new IOArray(ioJustificar);
        for(WriteObject wo : io){
            wo.write(out);
        }
    }

    /**
     * Efetua a leitura de um voto salvo no Stream de entrada.
     * Cria objetos de leitura e escrita compatíveis com os campos do voto,
     * Então, carrega campo a campo e preenche os elementos do voto.
     * @param in Stream de entrada que contém os dados salvos dos votos.
     * @throws IOException Exceção caso o arquivo não possa ser lido.
     */
    public void read(InputStream in) throws IOException{
        ReadObject[] io = new ReadObject[8];
        io[0] = new IOString();
        io[1] = new IOString();
        io[2]  = new IOString();
        io[3]  = new IOArray(new IOString());
        io[4]  = new IOArray(new IOInt());
        io[5]  = new IOInt();
        io[6]  = new IOArray(new IOString());
        io[7]  = new IOArray(new IOString());
        for(ReadObject ro : io){
            ro.read(in);
        }
        IOObject[] ioCriterios;
        IOObject[] ioNotas;
        IOObject[] ioComo;
        IOObject[] ioJustificar;
        this.trueIdAvaliador = ((IOString)io[0]).get();
        this.cpfAvaliador = ((IOString)io[1]).get();
        this.idTrabalho = ((IOString)io[2]).get();

        ioCriterios = ((IOArray)io[3]).get();
        this.criterios = new String[ioCriterios.length];
        for(int x=0 ; x < criterios.length ; x++){
            criterios[x] = ((IOString)ioCriterios[x]).get();
//            Log.d("Reading", "Criterios: " + criterios[x]);
        }

        ioNotas = ((IOArray)io[4]).get();
        this.notas = new int[ioNotas.length];
        for(int x=0 ; x < notas.length ; x++){
            notas[x] = ((IOInt)ioNotas[x]).get();
//            Log.d("Reading", "Notas: " + notas[x]);
        }

        this.premiado = ((IOInt)io[5]).get();

        ioComo = ((IOArray)io[6]).get();
        this.como = new String[ioComo.length];
        for(int x=0 ; x < como.length ; x++){
            como[x] = ((IOString)ioComo[x]).get();
//            Log.d("Reading", "Como: " + como[x]);
        }

        ioJustificar = ((IOArray)io[7]).get();
        this.justificar = new String[ioJustificar.length];
        for(int x=0 ; x < justificar.length ; x++){
            justificar[x] = ((IOString)ioJustificar[x]).get();
//            Log.d("Reading", "Justificar: " + justificar[x]);
        }
    }

}
