package br.ufop.icea.encontrodesaberes.model.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface para definir que um objeto pode ser lido do arquivo.
 * Classes que implementam a interface devem implementar toda a rotina de leitura do objeto.
 * Created by Maycon Junior on 06/06/2017.
 */

public interface ReadObject {
    public void read(InputStream in) throws IOException;
}
