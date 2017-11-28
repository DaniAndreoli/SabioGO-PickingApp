package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;
import data_access.SerialDAO;
import entities.Comprobante;
import entities.ItemStock;

/**
 * Created by Federico on 23/11/2017.
 */

public class EntradaSalidaActivity extends Activity{

    public static final String PREFS_NAME = "mPrefs";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    public static final String COMPROBANTE_ENTRADA_SALIDA = "2";
    public StockAdapter stockAdapter;
    List<ItemStock> listadoItemStock;
    private String id_usuario;

    Button btn_salir, btn_grabar, btn_agregarProducto;
    //FloatingActionButton btn_agregarManual;
    ListView lv_articulos;
    EditText txt_codigo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_salida);

        btn_grabar = (Button)findViewById(R.id.btn_grabarStock);
        btn_salir  = (Button)findViewById(R.id.btn_salirStock);
        btn_agregarProducto = (Button)findViewById(R.id.btn_agregarProductoStock);
        //btn_agregarManual = (FloatingActionButton)findViewById(R.id.fab_agregarCodBarManualStock);
        lv_articulos = (ListView)findViewById(R.id.lv_itemsStock);
        txt_codigo = (EditText)findViewById(R.id.txt_CodigoStock);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        //Esta deshabilitado el boton hasta que se termine de pickear
        btn_grabar.setEnabled(false);

        Comprobante comprobante = getComprobante();

        btn_salir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                salir();
            }
        });
    }


    public Comprobante getComprobante(){
        //Borrar esto y devolver el comprobante formado por lo obtenido del request
        Comprobante comprobante = new Comprobante();
        return comprobante;
    }

    private void salir(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradaSalidaActivity.this);
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
                        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
                        startActivityForResult(intent,0);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void borrarRegistros() {
<<<<<<< HEAD
        //SerialDAO.borrarSeriales(getApplicationContext(), COMPROBANTE_ENTRADA_SALIDA);
        //AGREGAR BORRAR ITEMS DEL COMPROBANTE
    }
}
=======
        SerialDAO.borrarSeriales(getApplicationContext(), COMPROBANTE_ENTRADA_SALIDA);
        //AGREGAR BORRAR ITEMS DEL COMPROBANTE
    }
}
>>>>>>> Fede/master
