package com.sabiogo.pickingapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sabiogo.pickingapp.Adapters.EntradaSalidaAdapter;
import com.sabiogo.pickingapp.Fragments.ComprobanteEntradaSalidaFragment;
import com.sabiogo.pickingapp.Fragments.ConteoStockFragment;
import com.sabiogo.pickingapp.Fragments.SerialesEntradaSalidaFragment;
import com.sabiogo.pickingapp.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
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
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    public static final Integer SERIAL_AGREGADO = 1;
    public static final Integer SERIAL_REPETIDO = 2;
    public static final Integer SERIAL_INCORRECTO = 3;
    public static final Integer SERIAL_INEXISTENTE = 4;
    public static final Integer SALDO_INSUFICIENTE = 5;
    public static final Integer ARTICULO_FUERA_COMPROBANTE = 6;
    public static final String COMPROBANTE_ENTRADASALIDA = "Entrada/Salida";
    private Boolean waitingFlag = false;

    private String id_usuario;
    private Comprobante comprobante;
    private EntradaSalidaAdapter entradaSalidaAdapter;
    private List<CodigoBarra> listadoCodBarra;

    Button btn_salir, btn_grabar;
    FloatingActionButton btn_agregarManual;
    ListView lv_articulosComprobante;
    EditText txt_codigo;
    TextView tv_descripcionComprobante;
    private Vibrator vibrator;
    int cantidadAPickear;
    List<String> listaCodigos;

    //SLIDER
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_salida);

        //Seteamos el toolbar de la aplicacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Definimos los objetos de UI
        btn_grabar = (Button)findViewById(R.id.btn_grabarComprobante);
        btn_salir  = (Button)findViewById(R.id.btn_salirEntradaSalida);
        btn_agregarManual = (FloatingActionButton)findViewById(R.id.fab_agregarCodBarManualEntradaSalida);
        lv_articulosComprobante = (ListView)findViewById(R.id.lv_itemsComprobante);
        txt_codigo = (EditText)findViewById(R.id.txt_codigoArticulo);
        tv_descripcionComprobante = (TextView)findViewById(R.id.tv_descripcionComprobante);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);

        createTextListener();

        //En caso de que hubiera un comprobante en la bd local, lo obtenemos
        comprobante = ComprobanteDAO.getComprobanteUsuario(getApplicationContext(), id_usuario);

        //Si no existe ningun comprobante para este usuario en bd local, lo obtenemos del WebService
        if (comprobante == null) {
            setComprobante();

        } else {
            /*Por tratarse de una peticion asincrona, no sabremos si el comprobante fue seteado en el instante, por lo que seteamos el ListView
            * tanto en la peticion al WebService (dentro del metodo setComprobante), como en el caso en el que el Comprobante se encuentre en BD local*/
            if (comprobante.getItems() != null) {
                calcularCantidadAPickear();
                btn_grabar.setEnabled(cantidadAPickear == 0);
            }
            setTitulo();
        }

        //listaCodigos = new ArrayList<>();
        //agregarCodigos();

        btn_salir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                salir();
            }
        });

        btn_agregarManual.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                agregarManual();
            }
        });

        btn_grabar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                grabarComprobanteEntradaSalida();
            }
        });
        listadoCodBarra = CodigoBarraDAO.getCodigosBarra(getApplicationContext());

        if (comprobante != null) {
            mPagerAdapter = new EntradaSalidaPagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
        }
    }

    public void createTextListener(){
        txt_codigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (waitingFlag == false) {
                    //pDialog = new ProgressDialog(getApplicationContext(), Th)
                    //ProgressDialog.show(getApplicationContext(),"Leyendo...", "Aguarde un instante por favor.");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            leerArticulo(txt_codigo.getText().toString());
                            txt_codigo.setText("");
                            txt_codigo.requestFocus();

                            //pDialog.dismiss();
                        }
                    }, 4000);
                }
            }
        });
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

                                        // Instantiate a ViewPager and a PagerAdapter.
                                        mPager = (ViewPager) findViewById(R.id.pager);
                                        mPagerAdapter = new EntradaSalidaActivity.EntradaSalidaPagerAdapter(getSupportFragmentManager());
                                        mPager.setAdapter(mPagerAdapter);

//                                        entradaSalidaAdapter = new EntradaSalidaAdapter(activityThis, R.layout.listview_row,comprobante.getItems());
//                                        lv_articulosComprobante.setAdapter(entradaSalidaAdapter);

                                        calcularCantidadAPickear();
                                        btn_grabar.setEnabled(cantidadAPickear == 0);
                                        setTitulo();

                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradaSalidaActivity.this);
                                        alertDialogBuilder
                                                .setTitle("No existen comprobantes")
                                                .setMessage("No se han encontrado comprobantes de Entrada/Salida pendientes de Picking.")
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
            e.printStackTrace();

        }
    }

    private void salir(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradaSalidaActivity.this);
        alertDialogBuilder
                .setTitle("Salir")
                .setMessage("¿Está seguro que desea salir?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        borrarRegistros();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        ComprobanteDAO.borrarRegistros(getApplicationContext(),comprobante);
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

                                if(items.getFaltaPickear() > 0){
                                    Serial serialnuevo = new Serial(codArt, serial, COMPROBANTE_ENTRADASALIDA);
                                    serialnuevo.setIdItem(items.getId_item());

                                    SerialDAO.grabarSerialItem(getApplicationContext(), items, serialnuevo);
                                    comprobante = ComprobanteDAO.getComprobanteUsuario(getApplicationContext(),id_usuario);

                                    cantidadAPickear -= 1;

                                    mPagerAdapter = new EntradaSalidaPagerAdapter(getSupportFragmentManager());
                                    mPager.setAdapter(mPagerAdapter);

                                    vibrator.vibrate(SERIAL_AGREGADO);
                                    Toast.makeText(getApplicationContext(), "Artículo leído", Toast.LENGTH_LONG).show();
                                    if(cantidadAPickear == 0){
                                        btn_grabar.setEnabled(true);
                                    }

                                }else {
                                    vibrar(SALDO_INSUFICIENTE);
                                    Toast.makeText(getApplicationContext(), "Ya se han leído todos los artículos de este tipo", Toast.LENGTH_LONG).show();
                               }
                            }
                        }

                    } else {
                        vibrator.vibrate(ARTICULO_FUERA_COMPROBANTE);
                        Toast.makeText(getApplicationContext(), "El artículo no pertenece al comprobante", Toast.LENGTH_LONG).show();
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
        try {
            String url = "http://" + UserConfigDAO.getUserConfig( getApplicationContext()).getApiUrl() + getString(R.string.api_ingresarComprobanteEntradaSalida) + id_usuario;

            HashMap<String, String> headers = new HashMap<String, String>();
            helpers.GsonRequest request = new helpers.GsonRequest(url,comprobante,Comprobante.class,headers, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.toString().equals("true")){
                        borrarRegistros();
                        Toast.makeText(getApplicationContext(), "Grabado correctamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intent,0);

                    } else {
                        Toast.makeText(getApplicationContext(),"Error en la grabación", Toast.LENGTH_LONG).show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            long[] pattern = {0, 50, 50, 150, 50, 150};
            vibrator.vibrate(pattern,-1);

        } else if (codigoVibracion.equals(SALDO_INSUFICIENTE)){
            long[] pattern = {0, 100, 50, 100};
            vibrator.vibrate(pattern,-1);

        }else if (codigoVibracion.equals(ARTICULO_FUERA_COMPROBANTE)){
            vibrator.vibrate(600);

        }
    }

    public Boolean serialEsRepetido(String serial){
        Boolean esRepetido = false;
        List<Serial> listadoSeriales = SerialDAO.getSerialList(getApplicationContext(), COMPROBANTE_ENTRADASALIDA);
        for(Serial seriales : listadoSeriales){
            if(seriales.getNumero().equals(serial)){
                esRepetido = true;
            }
        }
        return esRepetido;
    }

    private void agregarManual(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText input = new EditText(getApplicationContext());
        input.setTextColor(getResources().getColor(R.color.colorBlack));
        input.setGravity(Gravity.CENTER);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertDialogBuilder
                .setTitle("Ingrese el código de barras del artículo")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        if(!input.getText().toString().equals("")){
                            leerArticulo(input.getText().toString());
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(input);
        alertDialog.show();
    }

    private void setTitulo() {
        //En esta instancia el comprobante ya no deberia ser nulo pero lo validamos para setear el titulo del toolbar
        if (comprobante != null) {
            tv_descripcionComprobante.setText("Comprobante: " + comprobante.getObservaciones());

        } else {
            tv_descripcionComprobante.setText("Sin Comprobantes");
        }
    }

    public void agregarCodigos(){

        //Listado de codigos para probar la app sin leer los codigos de barra necesarios para poder cerrar el comprobante.
        listaCodigos.add("12345670311123456789012345");
        listaCodigos.add("12345670311123456789012312");
        listaCodigos.add("12345670311123456789012323");
        listaCodigos.add("12345670311123456789012334");
        listaCodigos.add("12345670311123456789012389");

        listaCodigos.add("12345670322123456789012345");
        listaCodigos.add("12345670322123456789012389");

        listaCodigos.add("12345670666123456789012345");
        listaCodigos.add("12345670666123456789012312");
        listaCodigos.add("12345670666123456789012323");

        listaCodigos.add("12345670909123456789012345");
        listaCodigos.add("12345670909123456789012312");
    }

    public void calcularCantidadAPickear(){
        cantidadAPickear = 0;

        for(Item item: comprobante.getItems()){
            cantidadAPickear += item.getFaltaPickear();
        }
    }

    public void actualizarPager() {
        comprobante = ComprobanteDAO.getComprobanteUsuario(getApplicationContext(),id_usuario);
        calcularCantidadAPickear();
        mPagerAdapter = new EntradaSalidaPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class EntradaSalidaPagerAdapter extends FragmentStatePagerAdapter {
        public EntradaSalidaPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {

                case 0: return ComprobanteEntradaSalidaFragment.newInstance();
                case 1: return SerialesEntradaSalidaFragment.newInstance();
            }

            return new ConteoStockFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
