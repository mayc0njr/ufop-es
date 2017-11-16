package br.ufop.icea.encontrodesaberes.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import br.ufop.icea.encontrodesaberes.controller.Utils;

/**
 * Classe representa uma data que pode ser lida e escrita no arquivo.
 * Created by Maycon Junior on 06/06/2017.
 */

public class IODate implements IOObject {
    private Date data; //Objeto que contem o dado armazenado.

    /**
     * Construtor que recebe um long, representando os milissegundos.
     * Especialmente usado para leitura do arquivo.
     * @param data
     */
    public IODate(long data){
        this.data = new Date(data);
    }

    /**
     * Construtor padrão. Inicializado com a data atual.
     */
    public IODate(){
        this(Calendar.getInstance().getTime());
    }
    public IODate(Date data){
        this(data.getTime());
    }

    /**
     * Funcao de escrita no arquivo, converte a data toda para milissegundos e escreve-a no arquivo.
     * Escreve um unico long.
     * @param out   Stream de saida.
     * @throws IOException
     */
    public void write(OutputStream out) throws IOException {
        byte[] content = Utils.longToByte(data.getTime());
        out.write(content);
    }

    /**
     * Função de leitura do arquivo, converte os milissegundos lidos em uma data e armazena-a no objeto.
     * Lê um unico long.
     * @param in    Stream de entrada.
     * @throws IOException
     */
    public void read(InputStream in) throws IOException{
        byte[] content = new byte[Utils.longSize()];
        in.read(content);
        data = new Date(Utils.byteToLong(content));
    }
    public Date get(){
        return new Date(data.getTime());
    }

    public void set(Date data){
        this.data = new Date(data.getTime());
    }

    public String toString(){
        return data.toString();
    }
}
