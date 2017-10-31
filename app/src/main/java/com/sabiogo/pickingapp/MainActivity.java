package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import data_access.UserConfigDAO;
import entities.UserConfig;

public class MainActivity extends AppCompatActivity {

    UserConfig userConfig;
    private static final int CODIGO_ACTIVITY_CONFIG = 1; //Utilizamos este codigo como identificador para iniciar el intent de configuracion
    public static final String PREFS_NAME = "mPrefs";
    private static final String TAG = "LoginActivity";

    EditText txtUserID;
    Button btnLogin;

    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private String id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.txtUserID = (EditText) findViewById(R.id.txtUserID);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        if (!id_usuario.isEmpty()){
            nextActivity();
        }

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
        if(resultCode == 99) {
            finish();
        }
    }

    public void login() {
        Log.d(TAG, "Ingresar: comienzo.");
        id_usuario = txtUserID.getText().toString();

        // Instanciamos el objeto request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        if (id_usuario.isEmpty()){
            Log.d(TAG, "Ingresar: id usuario vacío.");
            txtUserID.setError("Ingrese un id válido");
            Toast.makeText(getBaseContext(), "Fallo al ingresar", Toast.LENGTH_LONG).show();
            return;
        }else{
            try {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Autenticando...");
                progressDialog.show();

                //Obtenemos la direccion URL de la API desde la base de datos local
                this.userConfig = UserConfigDAO.getUserConfig(getApplicationContext());

                // Solicitamos un request de tipo string a la url provista por la configuracion
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + userConfig.getApiUrl() + "/api/session/login/" + id_usuario,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Obtenemos el response
                                if(response.toString().equals("true")){
                                    Log.d(TAG, "login: acceso concedido.");
                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                    savePreferences();
                                    nextActivity();
                                }
                                else{
                                    Log.d(TAG,"login: usuario no existente.");
                                    txtUserID.setError("El usuario ingresado no existe.");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Obtenemos un error
                                Log.d(TAG,"login: error de acceso.");
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                txtUserID.setError("El usuario " + id_usuario +" posee una sesion abierta");
                            }
                        });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                            }
                        }, 3000);

            } catch (Exception ex) {
                throw ex;
            }
        }
    }


    //VER SI onPause y onResume HACE FALTA EN LA MAIN ACTIVITY O EN LAS SIGUIENTES!
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: se minimiza la aplicación.");
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: se maximiza la aplicación.");
        loadPreferences();
    }

    private void savePreferences(){
        Log.d(TAG,"savePreferences: se almacena el id del usuario como 'variable de sesion'.");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        id_usuario = txtUserID.getText().toString();
        editor.putString(ID_USUARIO, id_usuario);
        editor.commit();
    }

    private void loadPreferences(){
        Log.d(TAG,"loadPreferences: se leen el id del usuario si existe como 'variable de sesion'.");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);
    }

    public void nextActivity(){
        Log.d(TAG, "nextActivity: avanzando a la vista opciones");
        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
        startActivity(intent);
    }
}
