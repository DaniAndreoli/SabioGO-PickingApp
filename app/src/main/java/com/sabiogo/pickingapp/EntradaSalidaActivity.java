package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import data_access.CodigoBarraDAO;
import data_access.ComprobanteDAO;
import data_access.SerialDAO;
import data_access.UserConfigDAO;
import entities.CodigoBarra;
import entities.Comprobante;
import entities.ItemStock;
import helpers.*;
import object_mapping.CodigoBarraMapper;
import object_mapping.ComprobanteMapper;

/**
 * Created by Federico on 23/11/2017.
 */

public class EntradaSalidaActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "mPrefs";
    public static final String COMPROBANTE_ENTRADA_SALIDA = "2";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";

    private String id_usuario;
    private Comprobante comprobante;
    private EntradaSalidaAdapter entradaSalidaAdapter;
    private List<CodigoBarra> listadoCodBarra;
    private Activity activityThis = this;

    Button btn_salir, btn_grabar, btn_agregarProducto;
    //FloatingActionButton btn_agregarManual;
    ListView lv_articulosComprobante;
    EditText txt_codigo;
    TextView tv_descripcionComprobante;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_salida);

        //Seteamos el toolbar de la aplicacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Definimos los objetos de UI
        btn_grabar = (Button)findViewById(R.id.btn_grabarComprobante);
        btn_salir  = (Button)findViewById(R.id.btn_salir);
        //btn_agregarProducto = (Button)findViewById(R.id.btn_agregarProductoStock);
        //btn_agregarManual = (FloatingActionButton)findViewById(R.id.fab_agregarCodBarManualStock);
        lv_articulosComprobante = (ListView)findViewById(R.id.lv_itemsComprobante);
        txt_codigo = (EditText)findViewById(R.id.txt_codigoArticulo);
        tv_descripcionComprobante = (TextView)findViewById(R.id.tv_descripcionComprobante);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        //Esta deshabilitado el boton hasta que se termine de pickear
        btn_grabar.setEnabled(false);

        //En caso de que hubiera un comprobante en la bd local, lo obtenemos
        comprobante = ComprobanteDAO.getComprobanteUsuario(getApplicationContext(), id_usuario);

        //Si no existe ningun comprobante para este usuario en bd local, lo obtenemos del WebService
        if (comprobante == null) {
            setComprobante();

        } else {
            /*Por tratarse de una peticion asincrona, no sabremos si el comprobante fue seteado en el instante, por lo que seteamos el ListView
            * tanto en la peticion al WebService (dentro del metodo setComprobante), como en el caso en el que el Comprobante se encuentre en BD local*/
            entradaSalidaAdapter = new EntradaSalidaAdapter(this, R.layout.listview_row,comprobante.getItems());
            lv_articulosComprobante.setAdapter(entradaSalidaAdapter);
        }

        //En esta instancia el comprobante ya no deberia ser nulo pero lo validamos para setear el titulo del toolbar
        if (comprobante != null) {
            tv_descripcionComprobante.setText("Comprobante: " + comprobante.getObservaciones());

        } else {
            tv_descripcionComprobante.setText("Sin Comprobantes");
        }

        btn_salir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                salir();
            }
        });
        listadoCodBarra = CodigoBarraDAO.getCodigosBarra(getApplicationContext());
    }

    public void setComprobante(){
        try {
            //Realizamos la consulta al web service para obtener el listado de codigos de barra
            if (UserConfigDAO.getUserConfig(getApplicationContext()) != null) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/comprobantes/get/" + id_usuario, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    /*Seteamos el objeto comprobante de la clase, y lo almacenamos en la base de datos*/
                                    if (response.getInt("numeroPick") > 0) {
                                        comprobante = ComprobanteMapper.mapObject(response);
                                        ComprobanteDAO.insertComprobante(getApplicationContext(), comprobante, id_usuario);

                                        entradaSalidaAdapter = new EntradaSalidaAdapter(activityThis, R.layout.listview_row,comprobante.getItems());
                                        lv_articulosComprobante.setAdapter(entradaSalidaAdapter);

                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradaSalidaActivity.this);
                                        alertDialogBuilder
                                                .setTitle("No existen Comprobantes")
                                                .setMessage("No se han encontrado Comprobantes de Entrada/Salida pendientes de Picking")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                                                    public void onClick(DialogInterface dialog, int id){
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                } catch (JSONException e) {

                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradaSalidaActivity.this);
                                alertDialogBuilder
                                        .setTitle("Error")
                                        .setMessage("No hay conexión con el servidor.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id){
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
                // Add the request to the RequestQueue.
                WSHelper.getInstance(this).addToRequestQueue(jsObjRequest);
            }
        } catch (Exception e) {

        }
    }

    private void salir(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradaSalidaActivity.this);
        alertDialogBuilder
                .setTitle("Salir")
                .setMessage("¿Esta seguro que desea salir?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        borrarRegistros();
                        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
                        startActivityForResult(intent,0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void borrarRegistros() {
        SerialDAO.borrarSeriales(getApplicationContext(), COMPROBANTE_ENTRADA_SALIDA);
        //AGREGAR BORRAR ITEMS DEL COMPROBANTE
    }

    private void leerArticulo(String serial) {
        if (!serial.equals("")) {
            CodigoBarra codigoBarra = verificarCodigoBarra(serial);

            if(codigoBarra != null) {
                String codArt = Integer.toString(codigoBarra.getCodigoArticulo(serial));

                if(comprobante.perteneceAlComprobante(codArt)) {
                    //TODO Desarrollar logica de picking para el item
                }
            }
        }
    }

    private void grabarComprobanteEntradaSalida() {
        JSONObject jsonBody;
        try {
            String url = "http://" + UserConfigDAO.getUserConfig( getApplicationContext()).getApiUrl() + getString(R.string.api_ingresarComprobanteEntradaSalida) + id_usuario;

            HashMap<String, String> headers = new HashMap<String, String>();
            //headers.put("Content-Type","application/json");
            helpers.GsonRequest request = new helpers.GsonRequest(url,comprobante,Comprobante.class,headers, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d(TAG, response);
                    borrarRegistros();
                    Toast.makeText(getApplicationContext(), "Grabado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
                    startActivityForResult(intent,0);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }){ @Override
            public String getBodyContentType(){
                return "application/json";
            }

            };
            request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            WSHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CodigoBarra verificarCodigoBarra(String serial){
        for (CodigoBarra codBar : listadoCodBarra) {
            if(serial.length() == codBar.getLargoTotal()){
                return codBar;
            }
        }
        return null;
    }
}
