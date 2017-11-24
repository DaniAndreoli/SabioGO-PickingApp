package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private boolean result;

    Button btn_entradaSalida, btn_stock, btn_logout, btn_sincronizar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        btn_entradaSalida = (Button)findViewById(R.id.btn_entradaSalida);
        btn_stock = (Button)findViewById(R.id.btn_stock);
        btn_sincronizar = (Button) findViewById(R.id.btn_sincronizar);
        btn_logout = (Button)findViewById(R.id.btn_logout);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        //verificarSincronizacionPendiente();

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

        btn_entradaSalida.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                entradaSalida();
            }
        });

        btn_sincronizar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                sincronizarArticulos();
            }
        });

        //Una vez logueado el usuario, obtenemos los codigos de barra desde el WS
        if(!result){
            this.getCodigosBarra();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if(requestCode==0)
        {
            result = true;
        }

    }
    private void logout(){
        RequestQueue queue = Volley.newRequestQueue(OpcionesActivity.this);
        try{
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(ID_USUARIO);
            editor.commit();
            StringRequest stringRequest = new StringRequest(Request.Method.GET,   "http://" + UserConfigDAO.getUserConfig(OpcionesActivity.this).getApiUrl() + "/api/session/logout/" + id_usuario,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.toString().equals("true")) {
                                //Obtenemos el response
                                Log.d(TAG, "logout: usuario encontrado para desloguear.");
                                Log.d(TAG, "logout: borrando id usuario de las preferencias.");
                                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                            }
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

    public void entradaSalida(){
        Log.d(TAG, "entradaSalida: avanzando a la vista de entrada/salida");
        Intent intent = new Intent(getApplicationContext(), EntradaSalidaActivity.class);
        startActivity(intent);
    }

    public void getCodigosBarra(){
        //Realizamos la consulta al web service para obtener el listado de codigos de barra
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(OpcionesActivity.this).getApiUrl() + "/api/codigos/get/" + id_usuario, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        CodigoBarraDAO.insertCodigosBarra(OpcionesActivity.this, CodigoBarraMapper.mapList(response));
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


    private void verificarSincronizacionPendiente() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,  "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/updates/pendientes" ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Obtenemos el response
                            if(response.equals("true")){
                                btn_sincronizar.setEnabled(true);
                                btn_entradaSalida.setEnabled(false);
                                btn_stock.setEnabled(false);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OpcionesActivity.this);
                                alertDialogBuilder
                                        .setTitle("Sincronizar articulos")
                                        .setMessage("Existen articulos que deben ser sincronizados. Por favor sincronice antes de continuar")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id){
                                                dialog.cancel();
                                            }
                                        });
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Articulos sincronizados.", Toast.LENGTH_SHORT);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error al verificar articulos sincronizados.", Toast.LENGTH_LONG).show();
                        }
                    });
            // Add the request to the RequestQueue.
            WSHelper.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void sincronizarArticulos(){
       // JsonArrayRequest jsObjectRequest = new JsonArrayRequest
         //       (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(OpcionesActivity.this).getApiUrl() + "/api/codigos/get/" + id_usuario,null)
    }
}
