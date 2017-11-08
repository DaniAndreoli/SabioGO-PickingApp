package object_mapping;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import entities.CodigoBarra;
import entities.Comprobante;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class ComprobanteMapper implements List<Comprobante> {

    //Constructor de Lista de Codigos de Barra a partir de un Cursor
    public static List<Comprobante> mapList(Cursor cursor) {
        //Declaramos Lista de Codigos de Barra
        List<Comprobante> lsComprobantes = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Recorremos el cursor
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    //Si el cursor trae la columna 'numero' no nula creamos el objeto
                    if (!cursor.isNull(cursor.getColumnIndex("numero"))){
                        Comprobante comp = new Comprobante();

                        //Seteamos los datos propios del comprobante
                        comp.setNumeroPick(cursor.getInt(cursor.getColumnIndex("numeroPick")));
                        comp.setOrden(cursor.getInt(cursor.getColumnIndex("orden")));
                        comp.setObservaciones(cursor.getString(cursor.getColumnIndex("observaciones")));
                        comp.setPuedeUsuario(cursor.getInt(cursor.getColumnIndex("puedeUsuario")));

                        /* Para setear el atributo Lizt<Item> items, sera necesario realizar una busqueda de todos los
                         * items que pertenezcan a este comprobante */

                        //Agregamos el comprobabte a la lista
                        lsComprobantes.add(comp);
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
            return lsComprobantes;
        }
    }
}
