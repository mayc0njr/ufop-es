package br.ufop.icea.encontrodesaberes.model.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface para definir que um objeto pode ser escrito no arquivo.
 * Classes que implementam a interface devem implementar toda a rotina de escrita do objeto.
 * Created by Maycon Junior on 06/06/2017.
 */

public interface WriteObject {

    public void write(OutputStream out) throws IOException;
}
