package com.sabiogo.pickingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import data_access.CodigoBarraDAO;
import data_access.UserConfigDAO;
import entities.CodigoBarra;
import entities.UserConfig;
import helpers.WSHelper;
import object_mapping.CodigoBarraMapper;

/**
 * Created by Federico on 27/10/2017.
 */

public class OpcionesActivity extends Activity {

    private static final String TAG = "OpcionesActivity";
    public static final String PREFS_NAME = "mPrefs";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private String id_usuario;

    Button btn_entradas, btn_salidas, btn_stock, btn_logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        btn_entradas = (Button)findViewById(R.id.btn_entradas);
        btn_salidas = (Button)findViewById(R.id.btn_salidas);
        btn_stock = (Button)findViewById(R.id.btn_stock);
        btn_logout = (Button)findViewById(R.id.btn_logout);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });

        btn_stock.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                stock();
            }
        });

        //Una vez logueado el usuario, obtenemos los codigos de barra desde el WS
        this.getCodigosBarra();
    }

    private void logout(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try{
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/session/logout/" + id_usuario,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Obtenemos el response
                            Log.d(TAG, "logout: usuario encontrado para desloguear.");
                            Log.d(TAG, "logout: borrando id usuario de las preferencias.");
                            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.commit();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Obtenemos un error
                            Log.d(TAG,"logout: error.");
                        }
                    });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            setResult(99);
            finish();
        }catch (Exception ex){
            throw ex;
        }
    }

    public void stock(){
        Log.d(TAG, "stock: avanzando a la vista stock");
        Intent intent = new Intent(getApplicationContext(), StockActivity.class);
        startActivity(intent);
    }

    public void getCodigosBarra(){
        //Realizamos la consulta al web service para obtener el listado de codigos de barra
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/codigos/get/" + id_usuario, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        CodigoBarraDAO.insertCodigosBarra(getApplicationContext(), CodigoBarraMapper.mapList(response));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        // Add the request to the RequestQueue.
        WSHelper.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
