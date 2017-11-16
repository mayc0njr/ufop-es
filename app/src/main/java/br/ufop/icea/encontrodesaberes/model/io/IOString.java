package br.ufop.icea.encontrodesaberes.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import br.ufop.icea.encontrodesaberes.controller.Utils;

/**
 * Created by Maycon Junior on 06/06/2017.
 */

public class IOString implements IOObject {
    private String data;

    public IOString(){
        this("");
    }
    public IOString(String data){
        this.data = ""+data;
    }
    public void write(OutputStream out) throws IOException{
        byte[] content = data.getBytes();
        byte[] size = Utils.intToByte(content.length);
        out.write(size);
        out.write(content);
    }
    public void read(InputStream in) throws IOException{
        byte[] size = new byte[Utils.intSize()];
        in.read(size);
        byte[] content = new byte[Utils.byteToInt(size)];
        in.read(content);
        data = new String(content);

    }

    public String get(){
        return data;
    }

    public void set(String data){
        this.data = data;
    }

    public String toString(){
        return data;
    }
}
