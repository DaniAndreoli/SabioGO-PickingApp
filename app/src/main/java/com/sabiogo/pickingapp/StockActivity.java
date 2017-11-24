package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Vibrator;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class StockActivity extends Activity {

    private static final String TAG = "StockActivity";
    public static final String PREFS_NAME = "mPrefs";
    public static final String COMPROBANTE_STOCK = "1";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private final float UNO = 1;
    public static final Integer SERIAL_AGREGADO = 1;
    public static final Integer SERIAL_REPETIDO = 2;
    public static final Integer SERIAL_INCORRECTO = 3;
    public StockAdapter stockAdapter;
    List<ItemStock> listadoItemStock;

    //public MyItemStockRecyclerViewAdapter adapter;
    private String id_usuario;

    Button btn_salir, btn_grabar, btn_agregarProducto;
    //FloatingActionButton btn_agregarManual;
    ListView lv_articulos;
    EditText txt_codigo;
    Vibrator vibrator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        btn_grabar = (Button)findViewById(R.id.btn_grabarStock);
        btn_salir  = (Button)findViewById(R.id.btn_salirStock);
        btn_agregarProducto = (Button)findViewById(R.id.btn_agregarProductoStock);
        //btn_agregarManual = (FloatingActionButton)findViewById(R.id.fab_agregarCodBarManualStock);
        lv_articulos = (ListView)findViewById(R.id.lv_itemsStock);
        txt_codigo = (EditText)findViewById(R.id.txt_CodigoStock);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        createTextListener();

        btn_salir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                salir();
            }
        });

        /*btn_agregarManual.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                agregarManual();
            }
        });*/

        txt_codigo.setText("12345678911234567891123456");

        btn_agregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarArticulo(txt_codigo.getText().toString());
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

        stockAdapter = new StockAdapter(this, R.layout.listview_row,listadoItemStock);
        lv_articulos.setAdapter(stockAdapter);
    }

    private void salir(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StockActivity.this);
        alertDialogBuilder
                .setTitle("Salir")
                .setMessage("¿Esta seguro que desea salir?")
                .setPositiveButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Si", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        borrarRegistros();
                        Log.d(TAG, "nextActivity: avanzando a la vista opciones");
                        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
                        startActivityForResult(intent,0);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void agregarManual(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        final EditText input = new EditText(getApplicationContext());
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertDialogBuilder
                .setTitle("Ingrese el codigo de barra del articulo")
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
                //ESTE METODO SE VA A IMPLEMENTAR UNA VEZ QUE TENGAN EL ANILLO, CUANDO CARGUEN AUTOMATICAMENTE LOS CODIGO DE BARRA. EL USUARIO NO VA A TENER ACCESO DE FORMA MANUAL A ESTE CAMPO, Y SI INGRESA DE FORMA MANUAL ES A TRAVES DE OTRO CAMPO

                if(!txt_codigo.getText().toString().equals("")){
                    //LLAMAR AL METODO QUE COMPRUEBA SI EL CODIGO INGRESADO ES VALIDO O NO (TRUE O FALSE)
                    //IF(CODIGO ES VALIDO) ENTONCES TOAST = CODIGO AGREGADO ELSE CODIGOERRONEO

                    //TXT.CODIGO.SETTEXT("");
                }
            }
        });
    }

    public Boolean agregarArticulo(String serial){
        boolean result;
        Float kilos;
        List<CodigoBarra> listadoCodBarra = CodigoBarraDAO.getCodigosBarra(getApplicationContext());

        if (!serial.equals("")){
            if(!serialEsRepetido(serial)){
                CodigoBarra codigoBarra = verificarCodigoBarra(listadoCodBarra, serial);

                if (codigoBarra != null){
                    Integer codArt = codigoBarra.getCodigoArticulo(serial);
                    kilos = codigoBarra.getKilos(serial);

                    //TODO Modificar tipo
                    ItemStock item = new ItemStock(Integer.toString(codArt), UNO, UNO, kilos);

                    listadoItemStock = StockDAO.leerItemStock(getApplicationContext(),item);

                    Toast.makeText(getBaseContext(), R.string.producto_agregado, Toast.LENGTH_LONG).show();
                    //adapter = new MyItemStockRecyclerViewAdapter(listadoItemStock, null);

                    stockAdapter = new StockAdapter(this, R.layout.listview_row,listadoItemStock);
                    lv_articulos.setAdapter(stockAdapter);

                    //rv_articulos.setAdapter(adapter);
                    Serial serialNuevo = new Serial(serial);
                    SerialDAO.grabarSerial(getApplicationContext(), serialNuevo, item.getCodigoArticulo(), COMPROBANTE_STOCK);
                }
                vibrar(SERIAL_AGREGADO);
                result = true;
            }else
            {
                vibrar(SERIAL_REPETIDO);
                Toast.makeText(getBaseContext(),"Serial repetido",Toast.LENGTH_LONG).show();
                result = false;
            }
        }
        else{
            vibrar(SERIAL_INCORRECTO);
            Toast.makeText(getBaseContext(), R.string.serial_invalido, Toast.LENGTH_LONG).show();
            result =  false;
        }
        txt_codigo.setText("98765432109876543210123456");
        return result;
    }

    public CodigoBarra verificarCodigoBarra(List<CodigoBarra> listadoCodBarra, String serial){
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
        JSONObject jsonBody;
        try {
            String url = "http://" + UserConfigDAO.getUserConfig( getApplicationContext()).getApiUrl() + getString(R.string.api_ingresarStock) + id_usuario;

            HashMap<String, String> headers = new HashMap<String, String>();
            //headers.put("Content-Type","application/json");
            GsonRequest request = new GsonRequest(url,comprobante,Comprobante.class,headers, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                    borrarRegistros();
                    Toast.makeText(getApplicationContext(), "Grabado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
                    startActivityForResult(intent,0);
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

        }
    }
}
