package com.sabiogo.pickingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import data_access.ArticuloDAO;
import data_access.CodigoBarraDAO;
import data_access.UserConfigDAO;
import helpers.WSHelper;
import object_mapping.ArticuloMapper;
import object_mapping.CodigoBarraMapper;

/**
 * Created by Federico on 27/10/2017.
 */

public class OpcionesActivity extends AppCompatActivity {

    private static final String TAG = "OpcionesActivity";
    public static final String PREFS_NAME = "mPrefs";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private String id_usuario;
    private Boolean result = false;

    Button btn_entrada_salida, btn_stock, btn_sync, btn_logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        btn_entrada_salida = (Button)findViewById(R.id.btn_entradaSalida);
        btn_stock = (Button)findViewById(R.id.btn_stock);
        btn_sync = (Button)findViewById(R.id.btn_sync);
        btn_logout = (Button)findViewById(R.id.btn_logout);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        verificarSincronizacionPendiente();

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

        btn_entrada_salida.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                entradaSalida();
            }
        });

        btn_sync.setOnClickListener(new View.OnClickListener(){
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
        try
        {
            //Realizamos la consulta al web service para obtener el listado de codigos de barra
            if (UserConfigDAO.getUserConfig(getApplicationContext()) != null){
                JsonArrayRequest jsObjRequest = new JsonArrayRequest
                        (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/codigos/get/" + id_usuario, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                CodigoBarraDAO.insertCodigosBarra(getApplicationContext(), CodigoBarraMapper.mapList(response));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error al obtener los códigos de barra.", Toast.LENGTH_LONG).show();
                            }
                        });
                // Add the request to the RequestQueue.
                WSHelper.getInstance(this).addToRequestQueue(jsObjRequest);
            }
        } catch (Exception ex){
            throw ex;
        }
    }

    private void verificarSincronizacionPendiente() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,  "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/updates/pendientes" ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Obtenemos el response
                            if(response.equals("true")) {
                                btn_sync.setEnabled(true);
                                btn_entrada_salida.setEnabled(false);
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
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
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

    private void sincronizarArticulos() {
        try{
            final ProgressDialog progressDialog = new ProgressDialog(OpcionesActivity.this, R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sincronizando articulos...");
            progressDialog.show();

            if (UserConfigDAO.getUserConfig(getApplicationContext()) != null) {
                JsonArrayRequest jsObjRequest = new JsonArrayRequest
                        (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(OpcionesActivity.this).getApiUrl() + "/api/updates/articulos", null,
                                new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                ArticuloDAO.insertArticulos(getApplicationContext(), ArticuloMapper.mapList(response));
                                Toast.makeText(getApplicationContext(),"Articulos sincronizados con éxito.", Toast.LENGTH_LONG).show();
                                btn_entrada_salida.setEnabled(true);
                                btn_stock.setEnabled(true);
                                progressDialog.dismiss();
                            }
                        }
                        , new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error al sincronizar articulos.", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                WSHelper.getInstance(this).addToRequestQueue(jsObjRequest);
            }
            // Add the request to the RequestQueue.
        } catch (Exception ex){
                throw ex;
        }
    }
}
