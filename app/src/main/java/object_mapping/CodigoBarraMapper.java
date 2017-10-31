package object_mapping;

import android.database.Cursor;

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
                        codBarra.setUbicacionPrecio(cursor.getInt(cursor.getColumnIndex("ubicacionPeso")));
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
}
