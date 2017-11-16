package br.ufop.icea.encontrodesaberes.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import br.ufop.icea.encontrodesaberes.controller.Utils;

/**
 * Created by maycon on 16/11/17.
 */

public class IOArray implements IOObject {
    private IOObject[] data;
    IOObject tipo;

    public IOArray(IOObject tipo){
        this.tipo = tipo;
    }
    public IOArray(IOObject[] ioObject){
            data = ioObject;
    }
    @Override
    public void read(InputStream in) throws IOException {
        byte[] content = new byte[Utils.intSize()];
        in.read(content);
        int fields = Utils.byteToInt(content);
        data = new IOObject[fields];
        for(int x=0 ; x < data.length ; x++){
            tipo = instantiate();
            tipo.read(in);
            data[x] = tipo;
        }
    }

    private IOObject instantiate(){
        for(Constructor<?> ctor : tipo.getClass().getConstructors()){
            if(ctor.getParameterTypes().length == 0){
                try {
                    return (IOObject)ctor.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(tipo instanceof IOString)
            return new IOString();
        if(tipo instanceof IOInt)
            return new IOInt();
        if(tipo instanceof IODate)
            return new IODate();
    return new IOArray(((IOArray) tipo).tipo);
    }

    public IOObject[] get(){
        return data;
    }

    public void set(IOObject[] data){
        this.data = data;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        byte[] content = Utils.intToByte(data.length);
        out.write(content);
        for(IOObject x : data){
            x.write(out);
        }
    }
}
