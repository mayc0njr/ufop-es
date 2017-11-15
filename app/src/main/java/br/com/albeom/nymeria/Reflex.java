/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.albeom.nymeria;

import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author davidson
 */
public class Reflex {

    public static Method getter(Object o, String nome) {
        try {
            Method m = o.getClass().getMethod("get" + nome.substring(0, 1).toUpperCase() + nome.substring(1));
            return m;
        } catch (Exception e) {
            //e.printStackTrace();
            //System.out.println(e.getMessage());
        }
        return null;
    }

    public static Method setter(Object o, String tipodado, String nome) {
        try {
            switch (tipodado) {
                case "java.lang.Integer":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Integer.class);
                case "java.lang.Long":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Long.class);
                case "long":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Long.class);
                case "java.lang.String":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), String.class);
                case "java.lang.Double":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Double.class);
                case "java.util.Date":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Date.class);
                case "java.lang.Float":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Float.class);
                case "java.util.Timestamp":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Date.class);
                case "java.sql.Date":
                    return o.getClass().getMethod("set" + nome.substring(0, 1).toUpperCase() + nome.substring(1), Date.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void invocar(Object o, Method m, Object valor) {
        try {
            //System.out.println("Invocando " + m + " - " + valor);
            m.invoke(o, valor);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setPreparedStatement(PreparedStatement psql, int c, String columnClassName, String campo, Object o) {
        try {
            //System.out.println("Setando st: "+campo+": "+columnClassName);
            switch (columnClassName) {
                case "java.lang.String":
                    psql.setString(c, (String) (Reflex.getter(o, campo).invoke(o)));
                    break;
                case "java.lang.Integer":
                    Integer v = (Integer) Reflex.getter(o, campo).invoke(o);
                    psql.setInt(c, v);
                    break;
                case "java.lang.Double":
                    psql.setDouble(c, (double) Reflex.getter(o, campo).invoke(o));
                    break;
                case "java.sql.Date":
                    Date dt = (Date) Reflex.getter(o, campo).invoke(o);
                    java.sql.Date sd = new java.sql.Date(dt.getTime());
                    psql.setDate(c, sd);
                    break;
                case "java.lang.Float":
                    psql.setFloat(c, Float.valueOf(String.valueOf(Reflex.getter(o, campo).invoke(o))));
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    public static void lerResultSet(ResultSet rs, Object p) {
        try {
            ResultSetMetaData mt = rs.getMetaData();
            for (int i = 0; i < mt.getColumnCount(); i++) {
                if (Reflex.temCampo(p.getClass(), mt.getColumnName(i + 1))) {

                    //System.out.println(mt.getColumnName(i + 1));
                    //System.out.println("cls: " + mt.getColumnClassName(i + 1));
                    switch (mt.getColumnClassName(i + 1)) {
                        case "java.lang.String":
                            Reflex.setter(p, mt.getColumnClassName(i + 1), mt.getColumnName(i + 1)).invoke(p, rs.getString(mt.getColumnName(i + 1)));
                            break;
                        case "java.lang.Integer":
                            Reflex.setter(p, mt.getColumnClassName(i + 1), mt.getColumnName(i + 1)).invoke(p, rs.getInt(mt.getColumnName(i + 1)));
                            break;
                        case "java.lang.Double":
                            Reflex.setter(p, mt.getColumnClassName(i + 1), mt.getColumnName(i + 1)).invoke(p, rs.getDouble(mt.getColumnName(i + 1)));
                            break;
                        case "java.lang.Date":
                            Reflex.setter(p, mt.getColumnClassName(i + 1), mt.getColumnName(i + 1)).invoke(p, rs.getDate(mt.getColumnName(i + 1)));
                            break;
                        case "java.lang.Float":
                            Reflex.setter(p, mt.getColumnClassName(i + 1), mt.getColumnName(i + 1)).invoke(p, rs.getFloat(mt.getColumnName(i + 1)));
                            break;

                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * Metodo gerador de dados. O arquivo de dados e definido via annotation
     * diretamente na classe de entidade
     *
     * @param a: Classe a ser analisada
     * @param nome: Nome da anotacao a ser lida
     * @return String: Valor string da anotacao definida na classe
     */
    public static String lerAnnotation(Class a, String nome) {
        try {
            for (Annotation annotation : a.getAnnotations()) {
                Class<? extends Annotation> type = annotation.annotationType();
                // System.out.println("Values of " + type.getName());

                for (Method method : type.getDeclaredMethods()) {
                    Object value = method.invoke(annotation, (Object[]) null);
                    //System.out.println(" " + method.getName() + ": " + value);
                    if (method.getName().equals(nome)) {
                        return value.toString();
                    }
                    if (value.toString().equals(nome)) {
                        return nome;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * M�todo gerador de dados. O arquivo de dados � definido via annotation
     * diretamente na classe de entidade
     *
     * @param a: Campo a ser analisado
     * @param nome: Nome da anota��o a ser lida
     * @return String: Valor string da anota��o definida na classe
     */
    public static String lerAnnotation(Field a, String nome) {
        try {
            for (Annotation annotation : a.getAnnotations()) {
                Class<? extends Annotation> type = annotation.annotationType();

                for (Method method : type.getDeclaredMethods()) {
                    Object value = method.invoke(annotation, (Object[]) null);

                    if (method.getName().equals(nome)) {
                        return value.toString();
                    }
                    if (value.toString().equals(nome)) {
                        return nome;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * M�todo gerador de dados. O arquivo de dados � definido via annotation
     * diretamente na classe de entidade
     *
     * @param a: Campo a ser analisado
     * @param nome: Nome da anota��o a ser lida
     * @return String: Valor string da anota��o definida no campo da classe
     */
    public static int lerAnnotationInt(Field a, String nome) {
        try {
            for (Annotation annotation : a.getAnnotations()) {
                Class<? extends Annotation> type = annotation.annotationType();

                for (Method method : type.getDeclaredMethods()) {
                    Object value = method.invoke(annotation, (Object[]) null);

                    if (method.getName().equals(nome)) {
                        return Integer.valueOf(value.toString());
                    }
                    if (value.toString().equals(nome)) {
                        return Integer.valueOf(nome);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean lerAnnotationBool(Field a, String nome) {
        try {
            for (Annotation annotation : a.getAnnotations()) {
                Class<? extends Annotation> type = annotation.annotationType();

                for (Method method : type.getDeclaredMethods()) {
                    Object value = method.invoke(annotation, (Object[]) null);

                    if (method.getName().equals(nome)) {
                        return Boolean.valueOf(value.toString());
                    }
                    if (value.toString().equals(nome)) {
                        return Boolean.valueOf(nome);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * M�todo gerador de dados. O arquivo de dados � definido via annotation
     * diretamente na classe de entidade
     *
     * @param a: Campo a ser analisado
     * @param nome: Nome da anota��o a ser lida
     * @return String: Valor string da anota��o definida no campo da classe
     */
    public static int lerAnnotationInt(Class a, String nome) {
        try {
            for (Annotation annotation : a.getAnnotations()) {
                Class<? extends Annotation> type = annotation.annotationType();

                for (Method method : type.getDeclaredMethods()) {
                    Object value = method.invoke(annotation, (Object[]) null);

                    if (method.getName().equals(nome)) {
                        return Integer.valueOf(value.toString());
                    }
                    if (value.toString().equals(nome)) {
                        return Integer.valueOf(nome);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * M�todo gerador de dados. O arquivo de dados � definido via annotation
     * diretamente na classe de entidade
     *
     * @param o: Objeto a ser analisado
     * @param f: Campo do objeto
     * @param t: �ndice do campo
     * @return String: Valor string da anota��o definida no campo da classe
     */
    public static String lerCampoTamanhoFixo(Object o, Field f, int t) {
        try {
            String r;
            Method m = o.getClass().getMethod("get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1));
            r = m.invoke(o).toString();
            if (t > r.length()) {
                for (int i = r.length(); i < t; i++) {
                    r = r.concat(" ");
                }

            }
            return r.substring(0, t);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return "";
    }

    /**
     * M�todo gerador de dados. O arquivo de dados � definido via annotation
     * diretamente na classe de entidade
     *
     * @param o: Objeto a ser analisado
     * @param f: Campo a ser lido
     * @return String: Valor string da anota��o definida no campo da classe
     */
    public static String lerCampoTamanhoVariavel(Object o, Field f) {
        try {
            String r;
            Method m = o.getClass().getMethod("get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1));
            r = m.invoke(o).toString();
            return r;

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return "";
    }

    /**
     * Verifica se um objeto possui o campo espeficado
     *
     * @return
     */
    public static boolean temCampo(Class c, String f) {
        for (Field ff : c.getDeclaredFields()) {
            if (ff.getName().equals(f)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o valor da propriedade e nula
     *
     * @return
     */
    public static boolean isNull(Object o, String campo) {
        try {
            Object v = Reflex.getter(o, campo).invoke(o);
            return v == null;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    /**
     * Garante que uma determinada string vai ter exatamente o tamanho
     * especificado
     *
     * @param q: String original
     * @param t: Tamanho esperado
     * @return String: String com o tamanho solicitado
     */
    public String stringTamanhoFixo(String q, int t) {
        char[] z = new char[t];
        String w = String.copyValueOf(z);
        return q.concat(w).substring(0, t);
    }

    /**
     * Retorna o numero de vezes que determinada anota��o aparece na classe
     *
     * @param a: Classe a ser examinada
     * @param nome: nome da anota��o
     * @return : N�mero de vezes que a anota��o aparece
     */
    public int contarAnotacao(Class a, String nome) {
        int n = 0;
        try {
            for (Annotation annotation : a.getAnnotations()) {
                Class<? extends Annotation> type = annotation.annotationType();

                for (Method method : type.getDeclaredMethods()) {
                    Object value = method.invoke(annotation, (Object[]) null);

                    if (method.getName().equals(nome)) {
                        n++;
                    }
                    if (value.toString().equals(nome)) {
                        n++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }

    /**
     * M�todo gerador de dados. O arquivo de dados � definido via annotation
     * diretamente na classe de entidade
     *
     * @param a: Campo a ser analisado
     * @param nome: Nome da anota��o a ser lida
     * @return String: Valor string da anota��o definida no campo da classe
     */
    public static ArrayList<Field> obterCamposAnotados(Class a, String nome) {
        try {
            ArrayList<Field> l = new ArrayList<>();

            for (Field f : a.getDeclaredFields()) {
                for (Annotation aa : f.getAnnotations()) {
                    Class<? extends Annotation> type = aa.annotationType();
                    for (Method method : type.getDeclaredMethods()) {
                        Object value = method.invoke(aa, (Object[]) null);

                        if (method.getName().equals(nome)) {
                            l.add(f);
                        }
                    }
                }
            }
            return l;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insereStringVetor(char[] v, String s, int posicao) {
        int c = 0;
        for (int i = posicao; i < posicao + s.length(); i++) {
            v[i] = s.charAt(c++);
        }
    }

    public static int lerInteiro(String c) {
        if (c == null || c.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(c.trim());
    }

    /**
     * Le um campo do objeto e retorna o correspondente em string
     *
     * @param o
     * @param campo
     * @return
     */
    public static String lerString(Object o, String campo) {
        try {
            Method getter = Reflex.getter(o, campo);
            Object v = getter.invoke(o);
            return String.valueOf(v);
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
    }

    public static void preecherObjeto(Object c, String nomecampo, String valor) {

        if(temCampo(c.getClass(), nomecampo)) {
            try {
                Method get = getter(c, nomecampo);

                Class rt = get.getReturnType();
                Method set = setter(c, rt.getName(), nomecampo);

                //System.out.println(rt);

                switch (rt.getCanonicalName()) {
                    case "long":
                    case "java.lang.String":
                        set.invoke(c, valor);
                        break;
                    case "java.lang.Integer":
                        int v = valor.isEmpty()?0:Integer.valueOf(valor);
                        set.invoke(c, v);
                        break;
                    case "java.lang.Double":
                        double dv = valor.isEmpty()?0:Double.valueOf(valor);
                        set.invoke(c, dv);
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static ArrayList<Method> setters(Object o) {
        ArrayList<Method> lm = new ArrayList<>();

        for(Method m: o.getClass().getDeclaredMethods()) {
            if(m.getName().startsWith("set")) {
                lm.add(m);
            }
        }
        return lm;

    }

    public static ArrayList<Method> getters(Object o) {
        ArrayList<Method> lm = new ArrayList<>();

        for(Method m: o.getClass().getDeclaredMethods()) {
            if(m.getName().startsWith("get")) {
                lm.add(m);
            }
        }
        return lm;

    }

    public static HashMap converterEmMap(Object o) {
        HashMap m = new HashMap();
        for(Field f: o.getClass().getDeclaredFields()) {
            m.put(f.getName(), Reflex.lerString(o, f.getName()));
            //System.out.println(f.getName() + " = " +Reflex.lerString(o, f.getName()));
        }
        return m;
    }

}
