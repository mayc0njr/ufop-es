package br.com.albeom.nymeria;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import br.com.albeom.nymeria.Reflex;

/**
 * Created by davidson on 12/09/17.
 */

public class Xml {


    /**
     * Recebe uma string xml, faz o parsing da mesma e preenche um arraylist com instancias da classe solicitada
     * @param xml
     * @param target
     * @param classe
     */
    public static void parseInArray(String xml, ArrayList target, Class classe) {
        //System.out.println("PARSING: "+xml);
        //ls = new ArrayList<>();
        try {

            InputStream registro = new ByteArrayInputStream(xml.getBytes());

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(registro);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nMeta = doc.getElementsByTagName("metadata");
            Node n1 = nMeta.item(0);
            Node n2 = n1.getFirstChild();
            String campos = n2.getTextContent();
            String vcampos[] = campos.split(",");

            NodeList nList = doc.getElementsByTagName("registro");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Object c = classe.newInstance();

                    for(String w: vcampos) {
                        String valor = getValue(w, element2);
                        Reflex.preecherObjeto(c, w, valor);
                    }


                    //c.setIdContato(Integer.valueOf(getValue("idcontato", element2)));
                   // c.setNome(getValue("nome", element2));
                    //c.setTelefone(getValue("telefone", element2));

                    target.add(c);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getItem(String blocoxml, String campo, int posicao) {
        //retorna um elemento de um bloco

        String nomea = "<" + campo + ">";
        String nomeb = "</" + campo + ">";
        int x = 0;
        int y = 0;
        for (int i = 0; i < posicao + 1; i++) {
            x = blocoxml.indexOf(nomea, y);
            y = blocoxml.indexOf(nomeb, y + 1);
        }

        if (x == -1) {
            //nao achou nada
            return "";
        }

        String d = blocoxml.substring(x + nomea.length(), y);
        return d;
    }


    public static int contaItens(String blocoxml, String campo) {
        int c = 0;
        String bl = blocoxml;
        String nomea = "<" + campo + ">";
        String nomeb = "</" + campo + ">";


        int index = 0;
        while ((index = blocoxml.indexOf(nomeb, index)) != -1) {
            c++;
            index++;
        }

        return c;
    }



}
