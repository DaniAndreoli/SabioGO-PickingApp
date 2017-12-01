package com.sabiogo.pickingapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.android.device.DeviceName;
import com.sabiogo.pickingapp.R;
import org.json.JSONArray;
import data_access.ArticuloDAO;
import data_access.CodigoBarraDAO;
import data_access.LogsDAO;
import data_access.UserConfigDAO;
import entities.DatosLOG;
import helpers.WSHelper;
import object_mapping.ArticuloMapper;
import object_mapping.CodigoBarraMapper;

/**
 * Created by Federico on 27/10/2017.
 */

public class OpcionesActivity extends AppCompatActivity {

    private static final String TAG = "OpcionesActivity";
    public static final String PREFS_NAME = "mPrefs";
    private static final String LOGOUT = "logout";
    private final String ID_USUARIO = "id_usuario";
    private final String DefaultID = "";
    private String id_usuario;
    private Boolean result = false;


    private DatosLOG datosLOG;
    double longitud = 0;
    double latitud = 0;
    private LocationManager mLocationManager;

    Button btn_entrada_salida, btn_stock, btn_sync, btn_logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        //Seteamos el toolbar de la aplicacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        btn_entrada_salida = (Button)findViewById(R.id.btn_entradaSalida);
        btn_stock = (Button)findViewById(R.id.btn_stock);
        btn_sync = (Button)findViewById(R.id.btn_sync);
        btn_logout = (Button)findViewById(R.id.btn_logout);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        id_usuario = settings.getString(ID_USUARIO, DefaultID);

        verificarSincronizacionPendiente();


        chequearPermisos();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        GPSEncendido();


        btn_logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });

        btn_stock.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                stock();
            }
        });

        btn_entrada_salida.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                entradaSalida();
            }
        });

        btn_sync.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                sincronizarArticulos();
            }
        });

        //Una vez logueado el usuario, obtenemos los codigos de barra desde el WS
        if(!result){
            this.getCodigosBarra();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if(requestCode==0)
        {
            result = true;
        }

    }

    private void logout(){
        if(GPSEncendido()){
            RequestQueue queue = Volley.newRequestQueue(OpcionesActivity.this);
            try{
                LogsDAO.insertarFecha(OpcionesActivity.this,id_usuario, LOGOUT);
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(ID_USUARIO);
                editor.commit();
                datosLOG = obtenerDatosLOG();
                StringRequest stringRequest = new StringRequest(Request.Method.GET,   "http://" + UserConfigDAO.getUserConfig(OpcionesActivity.this).getApiUrl() + "/api/session/logout/" + id_usuario,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.toString().equals("true")) {
                                    //Obtenemos el response
                                    Log.d(TAG, "logout: usuario encontrado para desloguear.");
                                    Log.d(TAG, "logout: borrando id usuario de las preferencias.");
                                    SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.clear();
                                    editor.commit();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Obtenemos un error
                                Log.d(TAG,"logout: error.");
                            }
                        });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                setResult(99);
                finish();
            }catch (Exception ex){
                throw ex;
            }
        }
    }

    public void stock(){
        Log.d(TAG, "stock: avanzando a la vista stock");
        Intent intent = new Intent(getApplicationContext(), StockActivity.class);
        startActivity(intent);
    }

    public void entradaSalida(){
        Log.d(TAG, "entradaSalida: avanzando a la vista de entrada/salida");
        Intent intent = new Intent(getApplicationContext(), EntradaSalidaActivity.class);
        startActivity(intent);
    }

    public void getCodigosBarra(){
        try
        {
            //Realizamos la consulta al web service para obtener el listado de codigos de barra
            if (UserConfigDAO.getUserConfig(getApplicationContext()) != null){
                JsonArrayRequest jsObjRequest = new JsonArrayRequest
                        (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/codigos/get/" + id_usuario, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                CodigoBarraDAO.insertCodigosBarra(getApplicationContext(), CodigoBarraMapper.mapList(response));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error al obtener los códigos de barra.", Toast.LENGTH_LONG).show();
                            }
                        });
                // Add the request to the RequestQueue.
                WSHelper.getInstance(this).addToRequestQueue(jsObjRequest);
            }
        } catch (Exception ex){
            throw ex;
        }
    }

    private void verificarSincronizacionPendiente() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,  "http://" + UserConfigDAO.getUserConfig(getApplicationContext()).getApiUrl() + "/api/updates/pendientes" ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Obtenemos el response
                            if(response.equals("true")) {
                                btn_sync.setEnabled(true);
                                btn_entrada_salida.setEnabled(false);
                                btn_stock.setEnabled(false);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OpcionesActivity.this);
                                alertDialogBuilder
                                        .setTitle("Sincronizar articulos")
                                        .setMessage("Existen articulos que deben ser sincronizados. Por favor sincronice antes de continuar")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id){
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Articulos sincronizados.", Toast.LENGTH_SHORT);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error al verificar articulos sincronizados.", Toast.LENGTH_LONG).show();
                        }
                    });
            // Add the request to the RequestQueue.
            WSHelper.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void sincronizarArticulos() {
        try{
            final ProgressDialog progressDialog = new ProgressDialog(OpcionesActivity.this, R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sincronizando articulos...");
            progressDialog.show();

            if (UserConfigDAO.getUserConfig(getApplicationContext()) != null) {
                JsonArrayRequest jsObjRequest = new JsonArrayRequest
                        (Request.Method.GET, "http://" + UserConfigDAO.getUserConfig(OpcionesActivity.this).getApiUrl() + "/api/updates/articulos", null,
                                new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                ArticuloDAO.insertArticulos(getApplicationContext(), ArticuloMapper.mapList(response));
                                Toast.makeText(getApplicationContext(),"Articulos sincronizados con éxito.", Toast.LENGTH_LONG).show();
                                btn_entrada_salida.setEnabled(true);
                                btn_stock.setEnabled(true);
                                progressDialog.dismiss();
                            }
                        }
                        , new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error al sincronizar articulos.", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                WSHelper.getInstance(this).addToRequestQueue(jsObjRequest);
            }
            // Add the request to the RequestQueue.
        } catch (Exception ex){
                throw ex;
        }
    }

    //Se agrega el metodo vacio para que no salga de la actividad al presionar el boton back en el dispositivo.
    @Override
    public void onBackPressed() {
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

    private Boolean GPSEncendido() {
       /* Boolean gpsEncendido;
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        gpsEncendido = Settings.Secure.isLocationProviderEnabled(contentResolver,LocationManager.GPS_PROVIDER);*/

        //if(!gpsEncendido){

        // }
        //return gpsEncendido;

        if (!localizacionActiva())
            mostrarAlerta();
        else
            obtenerUbicacion();
        return localizacionActiva();
    }



    public void obtenerUbicacion(){
        if (!(ActivityCompat.checkSelfPermission(OpcionesActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OpcionesActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
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
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada. Por favor active su ubicación. ")
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
        int permissionCheck = ContextCompat.checkSelfPermission(OpcionesActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // pedimos los permisos si no los tenmemos
            ActivityCompat.requestPermissions(OpcionesActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    102);
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            latitud = location.getLongitude();
            longitud = location.getLatitude();
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

}
