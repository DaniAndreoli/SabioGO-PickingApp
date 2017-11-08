package com.sabiogo.pickingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import data_access.UserConfigDAO;
import entities.UserConfig;

public class ConfigActivity extends AppCompatActivity {

    EditText txtApiUri;
    Button btnGuardar;
    Button btnCancelar;
    UserConfig userConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        this.txtApiUri = (EditText) findViewById(R.id.txtApiUri);
        this.btnGuardar = (Button) findViewById(R.id.btnGuardar);
        this.btnCancelar = (Button) findViewById(R.id.btnCancelar);

        this.txtApiUri.setText(UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl());

        //Definimos listener para btnGuardar
        this.btnGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userConfig = new UserConfig(txtApiUri.getText().toString());

                /*Toast t = new Toast(getBaseContext());
                t.setText("Guardando datos...");
                t.show();*/

                try {
                    //De esta forma enviamos la configuracion de usuario a traves del intent (SIN USAR BASE DE DATOS)
                    Intent resultado = new Intent();
                    resultado.putExtra("userConfig", userConfig);
                    setResult(RESULT_OK, resultado);

                    //De esta forma enviamos la configuracion de usuario a la base de datos local (PERSISTENCIA AUN SALIENDO DE LA APP)
                    UserConfigDAO.insertUserConfig(getApplicationContext(), userConfig);
                    //t.cancel();

                    Toast.makeText(getBaseContext(), "Datos guardados correctamente", Toast.LENGTH_LONG).show();

                    finish();
                } catch (Exception ex) {
                    //t.cancel();
                    throw ex;
                }
            }
        });

        //Definimos listener para btnCancelar
        this.btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //setResult(RESULT_CANCELED, null);
                finish();
            }
        });
    }
}
