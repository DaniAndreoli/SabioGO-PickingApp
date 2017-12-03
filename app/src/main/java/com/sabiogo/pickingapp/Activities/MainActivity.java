package com.sabiogo.pickingapp.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import android.location.LocationListener;
import com.jaredrummler.android.device.DeviceName;
import com.sabiogo.pickingapp.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import data_access.LogsDAO;
import data_access.UserConfigDAO;
import entities.DatosLOG;
import entities.UserConfig;
import helpers.*;


public class MainActivity extends AppCompatActivity{

    UserConfig userConfig;
    private static final int CODIGO_ACTIVITY_CONFIG = 1; //Utilizamos este codigo como identificador para iniciar el intent de configuracion
    public static final String PREFS_NAME = "mPrefs";
    private static final String TAG = "LoginActivity";
    private static final String LOGOUT = "logout";
    private static final String LOGIN = "login";


    EditText txtUserID;
    Button btnLogin;
    CoordinatorLayout coordLayout;

    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private String id_usuario;
    private DatosLOG datosLOG;


    double longitud = 0;
    double latitud = 0;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Seteamos el toolbar de la aplicacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        this.txtUserID = (EditText) findViewById(R.id.txtUserID);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.coordLayout = (CoordinatorLayout) findViewById(R.id.mainCoordLayout);

        //Configuramos el comportamiento del teclado
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        chequearLogs();

        if (!id_usuario.isEmpty())
            nextActivity();
        else{
            prepararLogueo();
        }

        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    // ask permissions here using below code
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            102);
                }
                prepararLogueo();
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

        if (GPSEncendido()) {
            //Obtenemos los datos de inicio de sesion
            id_usuario = txtUserID.getText().toString();
            this.userConfig = UserConfigDAO.getUserConfig(getApplicationContext());

            if (id_usuario.isEmpty()){
                Snackbar.make(this.coordLayout, "Ingrese el nombre de usuario", Snackbar.LENGTH_LONG).show();
                return;

            }else{
                try {
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme);
                    datosLOG = obtenerDatosLOG();

                    if (this.userConfig != null)
                    {
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Autenticando...");
                        progressDialog.show();

                        // Solicitamos un request de tipo string a la url provista por la configuracion
                        String url = "http://" + this.userConfig.getApiUrl() + "/api/session/login/" + id_usuario;
                        HashMap<String, String> headers = new HashMap<String, String>();
                        helpers.GsonRequest request = new helpers.GsonRequest(url,datosLOG,DatosLOG.class,headers, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Obtenemos el response
                                        if(response.toString().equals("true")){
                                            Toast.makeText(getApplicationContext(), "Bienvenido!", Toast.LENGTH_SHORT).show();
                                            savePreferences();
                                            nextActivity();
                                            LogsDAO.insertarFecha(MainActivity.this, id_usuario, LOGIN);
                                        }
                                        else{
                                            Snackbar.make(coordLayout, "Usuario incorrecto!", Snackbar.LENGTH_LONG).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //Obtenemos un error
                                        if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                                            Toast.makeText(getApplicationContext(), "Este usuario ya poseía una sesion abierta", Toast.LENGTH_SHORT).show();
                                            nextActivity();

                                        } else {
                                            Snackbar.make(coordLayout, "Error. Por favor, verifique las configuraciones", Snackbar.LENGTH_LONG).show();
                                        }

                                        progressDialog.dismiss();
                                    }
                                }){ @Override
                        public String getBodyContentType(){
                            return "application/json";
                        }

                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        ));
                        WSHelper.getInstance(getApplicationContext()).addToRequestQueue(request);
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {

                                    }
                                }, 3000);

                    } else {
                        Toast.makeText(getApplicationContext(), "Para iniciar sesión por primera vez, es necesario configurar la aplicación.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }
    }

    public void chequearLogs() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);
        try {
            Date fechaLogout = sdf.parse(LogsDAO.obtenerFechaUltimoLog(MainActivity.this, id_usuario));
            Date fechaActual = sdf.parse(sdf.format(new Date()));

            if(id_usuario != "" && fechaActual.getTime() > fechaLogout.getTime()){
                LogsDAO.insertarFecha(MainActivity.this, id_usuario, LOGOUT);
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(ID_USUARIO);
                editor.commit();
                id_usuario = "";
            }
        } catch (ParseException ex){
            ex.printStackTrace();
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

    public void nextActivity(){
        Log.d(TAG, "nextActivity: avanzando a la vista opciones");
        Intent intent = new Intent(getApplicationContext(), OpcionesActivity.class);
        startActivity(intent);
    }

    public DatosLOG obtenerDatosLOG(){
        DatosLOG datos = new DatosLOG();

        datos.setPorcentaje_bateria(obtenerBateria());
        datos.setModelo_dispositivo(DeviceName.getDeviceName());
        datos.setMarca_dispositivo(Build.BRAND);

        obtenerUbicacion();
        if (latitud != 0 && longitud != 0){
            datos.setUbicacion("Lat " + Double.toString(latitud)+ ", " + "Long " + Double.toString(longitud));
            mLocationManager.removeUpdates(locationListenerGPS);
        }
        return datos;
    }

    public String obtenerBateria(){
        int nivel, escala;
        float porcentajeBateria;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent estadoBateria = registerReceiver(null, intentFilter);
        nivel = estadoBateria.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        escala = estadoBateria.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        porcentajeBateria = (nivel * 100)/(float) escala;
        return (Float.toString(porcentajeBateria));
    }

    public void prepararLogueo(){
        chequearPermisos();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        GPSEncendido();
    }

    private Boolean GPSEncendido() {
        if (!localizacionActiva())
            mostrarAlerta();
        else
            obtenerUbicacion();

        return localizacionActiva();
    }

    public void obtenerUbicacion(){
        if (!(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setBearingRequired(true);
            criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setSpeedRequired(true);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            try{
                mLocationManager.requestSingleUpdate(criteria, locationListenerGPS, null);

            } catch (Exception ex){
                throw ex;
            }
        }
    }

    private boolean localizacionActiva() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void mostrarAlerta() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Activar GPS")
                .setMessage("Su ubicación esta desactivada, por favor actívela. ")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void chequearPermisos(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // se solicitan los permisos en caso de no tenerlos
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    102);
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            latitud = location.getLongitude();
            longitud = location.getLatitude();
            if(longitud != 0)
                mLocationManager.removeUpdates(locationListenerGPS);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setTitle("Salir")
                .setMessage("¿Está seguro que desea cerrar la aplicación?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

