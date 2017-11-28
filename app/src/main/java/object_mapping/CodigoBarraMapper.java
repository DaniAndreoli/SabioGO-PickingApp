package object_mapping;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import entities.CodigoBarra;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class CodigoBarraMapper implements List<CodigoBarra> {

    //Constructor de Lista de Codigos de Barra a partir de un Cursor
    public static List<CodigoBarra> mapList(Cursor cursor) {
        //Declaramos Lista de Codigos de Barra
        List<CodigoBarra> lsCodigosBarra = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Recorremos el cursor
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    //Si el cursor trae la columna 'numero' no nula creamos el objeto
                    if (!cursor.isNull(cursor.getColumnIndex("numero"))){
                        CodigoBarra codBarra = new CodigoBarra();

                        //Seteamos los datos del objeto codBarra
                        codBarra.setNumero(cursor.getInt(cursor.getColumnIndex("numero")));
                        codBarra.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                        codBarra.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
                        codBarra.setLargoTotal(cursor.getInt(cursor.getColumnIndex("largoTotal")));
                        codBarra.setUbicacionCodProd(cursor.getInt(cursor.getColumnIndex("ubicacionCodProd")));
                        codBarra.setLargoCodProd(cursor.getInt(cursor.getColumnIndex("largoCodProd")));
                        codBarra.setUbicacionCantidad(cursor.getInt(cursor.getColumnIndex("ubicacionCantidad")));
                        codBarra.setLargoCantidad(cursor.getInt(cursor.getColumnIndex("largoCantidad")));
<<<<<<< HEAD
                        codBarra.setUbicacionPrecio(cursor.getInt(cursor.getColumnIndex("ubicacionPeso")));
=======
                        codBarra.setUbicacionPeso(cursor.getInt(cursor.getColumnIndex("ubicacionPeso")));
>>>>>>> Fede/master
                        codBarra.setLargoPeso(cursor.getInt(cursor.getColumnIndex("largoPeso")));
                        codBarra.setUbicacionPrecio(cursor.getInt(cursor.getColumnIndex("ubicacionPrecio")));
                        codBarra.setLargoPrecio(cursor.getInt(cursor.getColumnIndex("largoPrecio")));
                        codBarra.setUbicacionFechaElab(cursor.getInt(cursor.getColumnIndex("ubicacionFechaElab")));
                        codBarra.setLargoFechaElab(cursor.getInt(cursor.getColumnIndex("largoFechaElab")));
                        codBarra.setUbicacionFechaVenc(cursor.getInt(cursor.getColumnIndex("ubicacionFechaVenc")));
                        codBarra.setLargoFechaVenc(cursor.getInt(cursor.getColumnIndex("largoFechaVenc")));
                        codBarra.setUbicacionDigitoVer(cursor.getInt(cursor.getColumnIndex("ubicacionDigitoVer")));
                        codBarra.setLargoDigitoVer(cursor.getInt(cursor.getColumnIndex("largoDigitoVer")));
                        codBarra.setUbicacionIdUsuario(cursor.getInt(cursor.getColumnIndex("ubicacionIdUsuario")));
                        codBarra.setLargoIdUsuario(cursor.getInt(cursor.getColumnIndex("largoIdUsuario")));
                        codBarra.setCantidadDecPeso(cursor.getInt(cursor.getColumnIndex("cantidadDecPeso")));

                        //Agregamos el codigo a la lista
                        lsCodigosBarra.add(codBarra);
                    }
                    //Continuamos recorriendo el cursor
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        }
        finally {
            return lsCodigosBarra;
        }
    }

    //Constructor de Lista de Codigos de Barra a partir de un JSONArray
    public static List<CodigoBarra> mapList(JSONArray jsonArray) {
        //Declaramos Lista de Codigos de Barra
        List<CodigoBarra> lsCodigosBarra = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (jsonArray != null && jsonArray.length() != 0) {

                //Recorremos el objeto JSONArray para obtener cada codigo de barra por separado
                for (int i=0; i<jsonArray.length(); i++) {

                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    CodigoBarra codBarra = new CodigoBarra();

                    //Seteamos los datos del objeto codBarra
                    codBarra.setNumero(jsonObj.getInt("numero"));
                    codBarra.setNombre(jsonObj.getString("numero"));
                    codBarra.setDescripcion(jsonObj.getString("descripcion"));
                    codBarra.setLargoTotal(jsonObj.getInt("largoTotal"));
                    codBarra.setUbicacionCodProd(jsonObj.getInt("ubicacionCodProd"));
                    codBarra.setLargoCodProd(jsonObj.getInt("largoCodProd"));
                    codBarra.setUbicacionCantidad(jsonObj.getInt("ubicacionCantidad"));
                    codBarra.setLargoCantidad(jsonObj.getInt("largoCantidad"));
<<<<<<< HEAD
                    codBarra.setUbicacionPrecio(jsonObj.getInt("ubicacionPeso"));
=======
                    codBarra.setUbicacionPeso(jsonObj.getInt("ubicacionPeso"));
>>>>>>> Fede/master
                    codBarra.setLargoPeso(jsonObj.getInt("largoPeso"));
                    codBarra.setUbicacionPrecio(jsonObj.getInt("ubicacionPrecio"));
                    codBarra.setLargoPrecio(jsonObj.getInt("largoPrecio"));
                    codBarra.setUbicacionFechaElab(jsonObj.getInt("ubicacionFechaElab"));
                    codBarra.setLargoFechaElab(jsonObj.getInt("largoFechaElab"));
                    codBarra.setUbicacionFechaVenc(jsonObj.getInt("ubicacionFechaVenc"));
                    codBarra.setLargoFechaVenc(jsonObj.getInt("largoFechaVenc"));
                    codBarra.setUbicacionDigitoVer(jsonObj.getInt("ubicacionDigitoVer"));
                    codBarra.setLargoDigitoVer(jsonObj.getInt("largoDigitoVer"));
                    codBarra.setUbicacionIdUsuario(jsonObj.getInt("ubicacionIdUsuario"));
                    codBarra.setLargoIdUsuario(jsonObj.getInt("largoIdUsuario"));
                    codBarra.setCantidadDecPeso(jsonObj.getInt("cantidadDecPeso"));

                    //Agregamos el codigo a la lista
                    lsCodigosBarra.add(codBarra);
                }
            }
        } catch (Exception e) {
            return null;
        }
        finally {
            return lsCodigosBarra;
        }
    }

}
