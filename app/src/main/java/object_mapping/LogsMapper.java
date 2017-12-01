package object_mapping;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import entities.ItemStock;

/**
 * Created by Federico on 30/11/2017.
 */

public class LogsMapper {

    //Constructor de un Listado de objetos ItemStock a partir de un Cursor
    public static String mapList(Cursor cursor) {
        String fecha = "";

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {
                //Movemos el cursor a la ultima posicion, para ver la fecha del ultimo logout
                cursor.moveToLast();
                while (!cursor.isAfterLast()) {
                    fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return fecha;
        }
    }
}
