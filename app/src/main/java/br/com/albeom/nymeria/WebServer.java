package br.com.albeom.nymeria;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by davidson on 07/09/17.
 * Esta classe não deve ser mexida e sim extendida para adicionar funcionalidades especificas do projeto
 */


public abstract class WebServer {

    protected String enderecoBase;
    protected Context context;
    protected RequestQueue queue;
    public static String authstatus;
    protected CookieManager cookieManager;
    private CookieStore cookieJar;


    public WebServer(String enderecoBase, Context c) {
        this.enderecoBase = enderecoBase;
        //queue = Volley.newRequestQueue(c);
        this.context = c;

        Cache cache = new DiskBasedCache(c.getCacheDir(), 10 * 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        // Don't forget to start the volley request queue
        queue.start();

        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        cookieJar = cookieManager.getCookieStore();


    }

    public List<HttpCookie> getCookies() {
        List<HttpCookie> cookies = cookieJar.getCookies();

        for (HttpCookie cookie: cookies) {
            Log.d("WEBSERVER", "cookie name : "+cookie.getName().toString() + " - "+cookie.getValue());
        }
        return cookies;
    }

    public String getCookie(String nome) {
        List<HttpCookie> cookies = cookieJar.getCookies();

        for (HttpCookie cookie: cookies) {
            if(cookie.getName().equals(nome)) {
                return cookie.getValue();
            }
         }
        return "";
    }

    protected void postData(String caminho, final Map params, final WebServerCallback exec) {
        // Request a string response from the provided URL.
        String w = enderecoBase+"/"+caminho;
        System.out.println("Enviando req: "+w);



        //relativelayout.addView(progressbar);


        StringRequest sr = new StringRequest(Request.Method.POST,w, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                exec.executar(response);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                exec.executar("ERRO: "+error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };



// Add the request to the RequestQueue.
        queue.add(sr);
        System.out.println("REQ: "+sr);

        //queue.start();
    }


    protected void obterString(String caminho, final WebServerCallback exec) {
        // Request a string response from the provided URL.
        String w = enderecoBase+"/"+caminho;
        //System.out.println("Enviando req: "+w);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, w,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        exec.executar(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                exec.executar("ERRO: "+error);
            }

        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        //System.out.println("REQ: "+stringRequest);

        //queue.start();
    }


}
