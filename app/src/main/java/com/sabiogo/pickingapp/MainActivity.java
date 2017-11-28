package com.sabiogo.pickingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import data_access.UserConfigDAO;
import entities.UserConfig;
import helpers.WSHelper;

public class MainActivity extends AppCompatActivity {

    UserConfig userConfig;
    private static final int CODIGO_ACTIVITY_CONFIG = 1; //Utilizamos este codigo como identificador para iniciar el intent de configuracion
    public static final String PREFS_NAME = "mPrefs";
    private static final String TAG = "LoginActivity";

    EditText txtUserID;
    Button btnLogin;
    CoordinatorLayout coordLayout;

    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private String id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Seteamos el toolbar de la aplicacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Obtenemos la instancia de los elementos de la UI
        this.txtUserID = (EditText) findViewById(R.id.txtUserID);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.coordLayout = (CoordinatorLayout) findViewById(R.id.mainCoordLayout);

        //Configuramos el comportamiento del teclado
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Buscamos el id_usuario almacenado en las preferencias
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

        //Hardcodeamos el user por defecto para pruebas
        this.txtUserID.setText("pistola");
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

    public void login() {
        //Obtenemos los datos de inicio de sesion
        id_usuario = txtUserID.getText().toString();
        this.userConfig = UserConfigDAO.getUserConfig(getApplicationContext());

        if (id_usuario.isEmpty()){
            Snackbar.make(this.coordLayout, "Ingrese el nombre de usuario", Snackbar.LENGTH_LONG).show();
            return;

        }else{
            try {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme);

                if (this.userConfig != null)
                {
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Autenticando...");
                    progressDialog.show();

                    // Solicitamos un request de tipo string a la url provista por la configuracion
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + this.userConfig.getApiUrl() + "/api/session/login/" + id_usuario,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Obtenemos el response
                                    if(response.toString().equals("true")){
                                        Toast.makeText(getApplicationContext(), "Bienvenido!", Toast.LENGTH_SHORT).show();
                                        savePreferences();
                                        nextActivity();
                                    }
                                    else{
                                        Snackbar.make(coordLayout, "Nombre de Usuario incorrecto!", Snackbar.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Obtenemos un error
                                    if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                                        Toast.makeText(getApplicationContext(), "Este usuario ya poseia una sesion abierta", Toast.LENGTH_SHORT).show();
                                        //Snackbar.make(coordLayout, "", Snackbar.LENGTH_LONG).show();
                                        nextActivity();

                                    } else {
                                        Snackbar.make(coordLayout, "Error. Por favor, verifique las configuraciones", Snackbar.LENGTH_LONG).show();
                                    }

                                    progressDialog.dismiss();
                                }
                            });
                    // Add the request to the RequestQueue.
                    WSHelper.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {

                                }
                            }, 3000);

                } else {
                    Toast.makeText(getApplicationContext(), "Para iniciar sesion por primera vez, es necesario configurar la aplicacion.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                throw ex;
            }
        }
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

    private void restartPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(ID_USUARIO, null);
        editor.commit();
    }

    public void nextActivity(){
        Log.d(TAG, "nextActivity: avanzando a la vista opciones");
        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
        startActivity(intent);
    }
}
