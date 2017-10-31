package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Federico on 28/10/2017.
 */

public class StockActivity extends Activity {

    private static final String TAG = "StockActivity";
    public static final String PREFS_NAME = "mPrefs";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private String id_usuario;

    Button btn_salir, btn_grabar;
    FloatingActionButton btn_agregarManual;
    RecyclerView rv_articulos;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        btn_grabar = (Button)findViewById(R.id.btn_grabarStock);
        btn_salir  = (Button)findViewById(R.id.btn_salirStock);
        btn_agregarManual = (FloatingActionButton)findViewById(R.id.fab_agregarCodBarManualStock);
        rv_articulos = (RecyclerView)findViewById(R.id.rv_articulosStock);

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
    }

    private void salir(){
        //SI EL RECYCLEVIEW NO ESTA VACIO, PREGUNTAR
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
                        Log.d(TAG, "nextActivity: avanzando a la vista opciones");
                        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
                        startActivityForResult(intent,0);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void agregarManual(){
        long serial = 0;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        final EditText input = new EditText(getApplicationContext());
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
                            //serial = Long.parseLong(input.getText().toString());
                            //AGREGAR EL LLAMADO A LA VERIFICACION SI EL SERIAL INGRESADO ES CORRECTO O NO.
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(input);
        alertDialog.show();
    }
}
