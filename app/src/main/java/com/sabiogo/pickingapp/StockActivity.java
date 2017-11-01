package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
import helpers.WSHelper;

/**
 * Created by Federico on 28/10/2017.
 */

public class StockActivity extends Activity {

    private static final String TAG = "StockActivity";
    public static final String PREFS_NAME = "mPrefs";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private final float UNO = 1;
    public StockAdapter stockAdapter;
    private String id_usuario;

    Button btn_salir, btn_grabar, btn_agregarProducto;
    FloatingActionButton btn_agregarManual;
    ListView lv_articulos;
    EditText txt_codigo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        btn_grabar = (Button)findViewById(R.id.btn_grabarStock);
        btn_salir  = (Button)findViewById(R.id.btn_salirStock);
        btn_agregarProducto = (Button)findViewById(R.id.btn_agregarProductoStock);
        btn_agregarManual = (FloatingActionButton)findViewById(R.id.fab_agregarCodBarManualStock);
        lv_articulos = (ListView)findViewById(R.id.lv_productosStock);
        txt_codigo = (EditText)findViewById(R.id.txt_CodigoStock);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        createTextListener();


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
    }

    private void salir(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder
                .setTitle("Salir")
                .setMessage("¿Esta seguro que desea salir?")
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener(){
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
        List<ItemStock> listadoItemStock;
        if (!serial.equals("")){
            CodigoBarra codigoBarra = verificarCodigoBarra(listadoCodBarra, serial);
            if (codigoBarra != null){
                Integer codArt = codigoBarra.getCodigoArticulo(serial);
                kilos = codigoBarra.getKilos(serial);
                ItemStock item = new ItemStock(codArt, UNO, UNO, kilos);

                listadoItemStock = StockDAO.leerItemStock(getApplicationContext(),item);

                Toast.makeText(getBaseContext(), R.string.producto_agregado, Toast.LENGTH_LONG).show();
                stockAdapter = new StockAdapter(this, R.layout.listview_row,listadoItemStock);
                lv_articulos.setAdapter(stockAdapter);

                Serial serialNuevo = new Serial(codArt, serial);
                SerialDAO.grabarSerial(getApplicationContext(), serialNuevo);
            }
            result = true;
        }
        else{
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


    private Comprobante armarComprobanteStock() {
        Comprobante comprobante = new Comprobante();
        List<ItemStock> listadoItemsStock;
        List<Item> listadoItem = new ArrayList<>();

        listadoItemsStock = StockDAO.getStockList(getApplicationContext());
        if (listadoItemsStock.size() > 0){
            for (ItemStock itemStock: listadoItemsStock) {
                Item item = new Item();
                item.setCodigoArticulo(Integer.toString(itemStock.getCodigoArticulo()));
                item.setDescripcion("");
                item.setUnidad((int)itemStock.getUnidad());
                item.setCantidad(itemStock.getCantidad());
                item.setKilos(itemStock.getKilos());
                item.setPuedePickear(0);
                item.setSaldo(0);

                listadoItem.add(item);
            }

            for (Item item: listadoItem) {
                List<Serial> seriales= SerialDAO.getSerialesArticulo(getApplicationContext(), item.getCodigoArticulo());
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

    public void grabarComprobanteStock(Comprobante comprobante){
        JSONObject jsonBody;
        try {
            jsonBody = new JSONObject();
            jsonBody.put("", comprobante);

            StringRequest stringRequest = new StringRequest(1, "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + getString(R.string.api_ingresarStock) + id_usuario,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.toString().equals("true")){
                        borrarRegistros();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                        alertDialogBuilder
                                .setTitle("Comprobante")
                                .setMessage("Comprobante guardado con éxito")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int id){
                                        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
                                        startActivityForResult(intent,0);
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                    alertDialogBuilder
                            .setTitle("Error")
                            .setMessage(error.toString())
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                }
            });
            WSHelper.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void borrarRegistros(){
        StockDAO.borrarItemStock(getApplicationContext());
        SerialDAO.borrarSeriales(getApplicationContext());
    }
}
