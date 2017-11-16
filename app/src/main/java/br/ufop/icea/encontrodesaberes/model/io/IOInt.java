package br.ufop.icea.encontrodesaberes.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import br.ufop.icea.encontrodesaberes.controller.Utils;

/**
 * Created by Maycon Junior on 06/06/2017.
 */

public class IOInt implements IOObject {
    public int data;

    public IOInt(int data){
        this.data = data;
    }
    public IOInt(){
        this(0);
    }

    public void write(OutputStream out) throws IOException{
        byte[] content = Utils.intToByte(data);
        out.write(content);
    }

    public void read(InputStream in) throws IOException{
        byte[] content = new byte[Utils.intSize()];
        in.read(content);
        data = Utils.byteToInt(content);
    }
    public int get(){
        return data;
    }

    public void set(int data){
        this.data = data;
    }

}
