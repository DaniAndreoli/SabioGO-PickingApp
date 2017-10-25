package com.sabiogo.pickingapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import entities.UserConfig;

public class MainActivity extends AppCompatActivity {

    UserConfig userConfig;
    private static final int CODIGO_ACTIVITY_CONFIG = 1; //Utilizamos este codigo como identificador para iniciar el intent de configuracion

    EditText txtUserID;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.txtUserID = (EditText) findViewById(R.id.txtUserID);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);

        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Agregamos los botones presentes en el menu del Toolbar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Controlamos click en los botones del menu
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, ConfigActivity.class);
            startActivityForResult(i, CODIGO_ACTIVITY_CONFIG);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CODIGO_ACTIVITY_CONFIG:
                if (resultCode == Activity.RESULT_OK) {
                    Uri dato = data.getData();
                    this.userConfig = (UserConfig) data.getSerializableExtra("userConfig");
                }
                break;
        }
    }

    public void login() {
        // Instanciamos el objeto request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            // Solicitamos un request de tipo string a la url provista por la configuracion
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "192.168.0.105/api/session/login/" + this.txtUserID.getText(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Obtenemos el response
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Obtenemos un error
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception ex) {
            throw ex;
        }
    }
}
