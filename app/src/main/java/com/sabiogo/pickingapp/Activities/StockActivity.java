package com.sabiogo.pickingapp.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.os.Vibrator;
import com.android.volley.DefaultRetryPolicy;
import com.sabiogo.pickingapp.Fragments.ConteoStockFragment;
import com.sabiogo.pickingapp.Fragments.SerialesEntradaSalidaFragment;
import com.sabiogo.pickingapp.Fragments.SerialesStockFragment;
import com.sabiogo.pickingapp.R;
import data_access.ArticuloDAO;
import data_access.CodigoBarraDAO;
import data_access.SerialDAO;
import data_access.StockDAO;
import data_access.UserConfigDAO;
import entities.CodigoBarra;
import entities.Comprobante;
import entities.Item;
import entities.ItemStock;
import entities.Serial;
import helpers.GsonRequest;
import helpers.WSHelper;

/**
 * Created by Federico on 28/10/2017.
 */

public class StockActivity extends AppCompatActivity {

    //Declaracion de Variables de Clase
    private static final String TAG = "StockActivity";
    public static final String PREFS_NAME = "mPrefs";
    public static final String COMPROBANTE_STOCK = "Stock";
    private String id_usuario;
    private List<ItemStock> listadoItemStock;
    private List<CodigoBarra> listadoCodBarra;

    private Boolean waitingFlag = false;

    public static final Integer SERIAL_AGREGADO = 1;
    public static final Integer SERIAL_REPETIDO = 2;
    public static final Integer SERIAL_INCORRECTO = 3;
    public static final Integer SERIAL_INEXISTENTE = 4;
    public ProgressDialog pDialog;
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private final float UNO = 1;

    //Declaracion de Controles Visuales
    private Button btn_salir, btn_grabar;
    private FloatingActionButton btn_agregarManual;
    private ListView lv_articulos;
    private EditText txt_codigo;
    private Vibrator vibrator;

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
        setContentView(R.layout.activity_stock);

        //Seteamos el toolbar de la aplicacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Inicializamos objetos visuales
        btn_grabar = (Button)findViewById(R.id.btn_grabarStock);
        btn_salir  = (Button)findViewById(R.id.btn_salirStock);
        btn_agregarManual = (FloatingActionButton)findViewById(R.id.fab_agregarCodBarManualStock);
        lv_articulos = (ListView)findViewById(R.id.lv_itemsStock);
        txt_codigo = (EditText)findViewById(R.id.txt_CodigoStock);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

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

        btn_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comprobante comprobante = armarComprobanteStock();
                if(comprobante != null){
                    grabarComprobanteStock(comprobante);
                }
                else{
                    Toast.makeText(getBaseContext(), "Error en la creación del comprobante", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Una vez seteados los valores de la interfaz, verificamos si existe un conteo de stock pendiente y de ser asi lo mostramos en pantalla
        listadoItemStock = StockDAO.getStockList(getApplicationContext());

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new StockPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        /*mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagerAdapter = new StockPagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    agregarArticulo(txt_codigo.getText().toString());
                    txt_codigo.setText("");
                    txt_codigo.requestFocus();
                }
                return true;
            }
        };

        txt_codigo.setOnEditorActionListener(exampleListener);



        listadoCodBarra = CodigoBarraDAO.getCodigosBarra(getApplicationContext());
        txt_codigo.requestFocus();
    }

    private void salir(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Salir")
                .setMessage("¿Está seguro que desea salir?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        borrarRegistros();
                        Log.d(TAG, "nextActivity: avanzando a la vista opciones");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intent,0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                })
        ;

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                            agregarArticulo(input.getText().toString());
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(input);
        alertDialog.show();
    }

    public void agregarArticulo(String serial){
        Float kilos;
        if (!serial.equals("")){
            if(!serialEsRepetido(serial)){
                CodigoBarra codigoBarra = verificarCodigoBarra(serial);

                if (codigoBarra != null){
                    Integer codArt = codigoBarra.getCodigoArticulo(serial);

                    if(!ArticuloDAO.getDescripcionArticulo(getApplicationContext(), Integer.toString(codArt)).equals("")){
                        kilos = codigoBarra.getKilos(serial);

                        //Definimos el item que estamos leyendo, con sus datos
                        ItemStock item = new ItemStock(Integer.toString(codArt), UNO, UNO, kilos);

                    /*Ejecutamos metodo DAO que verifica si el item creado existe
                    para aumentar su cantidad, o crearlo de lo contrario, y luego devuelve el listado actualizado*/
                        listadoItemStock = StockDAO.leerItemStock(getApplicationContext(),item);

                        //Insertamos el objeto Serial en la bd Sqlite
                        Serial serialNuevo = new Serial(item.getCodigoArticulo(), serial, COMPROBANTE_STOCK);
                        if (SerialDAO.grabarSerial(getApplicationContext(), serialNuevo, COMPROBANTE_STOCK)){
                            mPagerAdapter = new StockPagerAdapter(getSupportFragmentManager());
                            mPager.setAdapter(mPagerAdapter);

                            vibrar(SERIAL_AGREGADO);
                            Toast.makeText(getBaseContext(), R.string.producto_agregado, Toast.LENGTH_SHORT).show();

                        } else {
                        vibrar(SERIAL_REPETIDO);
                        Toast.makeText(getApplicationContext(), "Serial repetido", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        vibrar(SERIAL_INEXISTENTE);
                        Toast.makeText(getApplicationContext(), "Artículo inexistente", Toast.LENGTH_LONG).show();

                    }
                } else {
                    vibrar(SERIAL_INCORRECTO);
                    Toast.makeText(getBaseContext(),"Serial incorrecto",Toast.LENGTH_LONG).show();

                }
            } else {
                vibrar(SERIAL_REPETIDO);
                Toast.makeText(getBaseContext(), "Serial repetido", Toast.LENGTH_LONG).show();

            }
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

    public Boolean serialEsRepetido(String serial){
        Boolean esRepetido = false;
        List<Serial> listadoSeriales = SerialDAO.getSerialList(getApplicationContext(), COMPROBANTE_STOCK);
        for(Serial seriales : listadoSeriales){
            if(seriales.getNumero().equals(serial)){
                esRepetido = true;
            }
        }
        return esRepetido;
    }

    private Comprobante armarComprobanteStock() {
        Comprobante comprobante = new Comprobante();
        List<ItemStock> listadoItemsStock;
        List<Item> listadoItem = new ArrayList<>();

        listadoItemsStock = StockDAO.getStockList(getApplicationContext());
        if (listadoItemsStock.size() > 0){
            for (ItemStock itemStock: listadoItemsStock) {
                Item item = new Item();
                item.setCodigoArticulo(itemStock.getCodigoArticulo());
                item.setDescripcion("");
                item.setUnidad((int)itemStock.getUnidad());
                item.setCantidad(itemStock.getCantidad());
                item.setKilos(itemStock.getKilos());
                item.setPuedePickear(0);
                item.setSaldo(0);

                listadoItem.add(item);
            }

            for (Item item: listadoItem) {
                List<Serial> seriales= SerialDAO.getSerialesArticulo(getApplicationContext(), item.getCodigoArticulo(), COMPROBANTE_STOCK);
                item.setSeriales(seriales);
            }

            comprobante.setItems(listadoItem);
            comprobante.setNumeroPick(0);
            comprobante.setObservaciones(getString(R.string.mov_stock));
            comprobante.setOrden(0);
            comprobante.setPuedeUsuario(0);

            return comprobante;
        }else {
            return null;
        }
    }

    public void grabarComprobanteStock(final Comprobante comprobante){
        try {
            String url = "http://" + UserConfigDAO.getUserConfig( getApplicationContext()).getApiUrl() + getString(R.string.api_ingresarStock) + id_usuario;

            HashMap<String, String> headers = new HashMap<String, String>();
            GsonRequest request = new GsonRequest(url,comprobante,Comprobante.class,headers, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.toString().equals("true")){
                        Log.d(TAG, response);
                        borrarRegistros();
                        Toast.makeText(getApplicationContext(), "Grabado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intent,0);

                    } else {
                        Toast.makeText(getApplicationContext(),"Error en la grabación", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
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

    private void borrarRegistros(){
        StockDAO.borrarItemStock(getApplicationContext());
        SerialDAO.borrarSeriales(getApplicationContext(), COMPROBANTE_STOCK);
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
        }
    }

    public void actualizarPager() {
        mPagerAdapter = new StockPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class StockPagerAdapter extends FragmentStatePagerAdapter {
        public StockPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {

                case 0: return ConteoStockFragment.newInstance();
                case 1: return SerialesStockFragment.newInstance();
            }

            return new ConteoStockFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}


