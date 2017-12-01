package com.sabiogo.pickingapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.sabiogo.pickingapp.Adapters.EntradaSalidaAdapter;
import com.sabiogo.pickingapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import data_access.CodigoBarraDAO;
import data_access.ComprobanteDAO;
import data_access.SerialDAO;
import data_access.UserConfigDAO;
import entities.CodigoBarra;
import entities.Comprobante;
import entities.Item;
import entities.Serial;
import helpers.*;
import object_mapping.ComprobanteMapper;

/**
 * Created by Federico on 23/11/2017.
 */

public class EntradaSalidaActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "mPrefs";
    public static final String COMPROBANTE_ENTRADA_SALIDA = "2";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    public static final Integer SERIAL_AGREGADO = 1;
    public static final Integer SERIAL_REPETIDO = 2;
    public static final Integer SERIAL_INCORRECTO = 3;
    public static final Integer SERIAL_INEXISTENTE = 4;
    public static final Integer SALDO_INSUFICIENTE = 5;
    public static final Integer ARTICULO_FUERA_COMPROBANTE = 6;
    public static final String COMPROBANTE_ENTRADASALIDA = "Entrada/Salida";

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
    private Vibrator vibrator;


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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
        //SerialDAO.borrarSeriales(getApplicationContext(), COMPROBANTE_ENTRADA_SALIDA);
        ComprobanteDAO.borrarRegistros(getApplicationContext(),comprobante);
        //AGREGAR BORRAR ITEMS DEL COMPROBANTE
    }

    private void leerArticulo(String serial) {
        if (!serial.equals("")) {
            if(!serialEsRepetido(serial)){
                CodigoBarra codigoBarra = verificarCodigoBarra(serial);

                if(codigoBarra != null) {
                    String codArt = Integer.toString(codigoBarra.getCodigoArticulo(serial));

                    if(comprobante.perteneceAlComprobante(codArt)) {

                        for (Item items: comprobante.getItems()) {

                            if(codArt.equals(items.getCodigoArticulo())){

                                if(items.getSaldo() > 0){
                                    Serial serialnuevo = new Serial(codArt, serial, COMPROBANTE_ENTRADASALIDA);
                                    SerialDAO.grabarSerialItem(getApplicationContext(), items, serialnuevo );
                                    comprobante = ComprobanteDAO.getComprobanteUsuario(getApplicationContext(),id_usuario);

                                    entradaSalidaAdapter = new EntradaSalidaAdapter(this, R.layout.listview_row,comprobante.getItems());
                                    lv_articulosComprobante.setAdapter(entradaSalidaAdapter);

                                }else {
                                    vibrar(SALDO_INSUFICIENTE);
                                    Toast.makeText(getApplicationContext(), "Saldo completado", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    } else {
                        vibrator.vibrate(ARTICULO_FUERA_COMPROBANTE);
                        Toast.makeText(getApplicationContext(), "El articulo no pertenece al comprobante", Toast.LENGTH_LONG).show();

                    }

                } else {
                    vibrar(SERIAL_INCORRECTO);
                    Toast.makeText(getBaseContext(), R.string.serial_invalido, Toast.LENGTH_SHORT).show();
                }

            } else {
                vibrar(SERIAL_REPETIDO);
                Toast.makeText(getBaseContext(),"Serial repetido",Toast.LENGTH_SHORT).show();
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

    public void vibrar(Integer codigoVibracion){
        if (codigoVibracion.equals(SERIAL_AGREGADO)) {
            vibrator.vibrate(200);

        } else if (codigoVibracion.equals(SERIAL_REPETIDO)) {
            long[] pattern = {0, 150, 100, 150, 100, 50}; //delay, duracion primera vibracion, segundo delay, duracion segunda vibracion
            vibrator.vibrate(pattern, -1); //-1 es para que vibre exactamente como el patron/pattern

        } else if (codigoVibracion.equals(SERIAL_INCORRECTO)) {
            vibrator.vibrate(600);

        } else if (codigoVibracion.equals(SERIAL_INEXISTENTE)) {
            vibrator.vibrate(600);

        } else if (codigoVibracion.equals(SALDO_INSUFICIENTE)){
            vibrator.vibrate(800);

        }else if (codigoVibracion.equals(ARTICULO_FUERA_COMPROBANTE)){
            vibrator.vibrate(600);
        }
    }

    public Boolean serialEsRepetido(String serial){
        Boolean esRepetido = false;
        List<Serial> listadoSeriales = SerialDAO.getSerialList(getApplicationContext(), COMPROBANTE_ENTRADA_SALIDA);
        for(Serial seriales : listadoSeriales){
            if(seriales.getSerial().equals(serial)){
                esRepetido = true;
            }
        }
        return esRepetido;
    }
}
