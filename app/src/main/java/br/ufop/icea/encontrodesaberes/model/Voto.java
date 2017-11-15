package br.ufop.icea.encontrodesaberes.model;

import java.util.HashMap;
import java.util.Map;

import br.ufop.icea.encontrodesaberes.controller.Utils;

/**
 * Created by maycon on 26/10/17.
 */

public class Voto {
    public static int MELHOR_TRABALHO = 0;
    public static int MENCAO_HONROSA = 1;
    public static int CLAREZA = 0;
    public static int PESQUISADOR = 1;
    public static int RELEVANCIA = 2;
    public static int CRITICA = 3;
    public static int OUTRO = 4;


    private String idAvaliador, idTrabalho;
    private String[] criterios;
    private int[] notas;
    private int premiado;
    private String[] como;
    private String[] justificar;
//    private String[] comentarios;
//    private boolean[] checkBox;

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

}
