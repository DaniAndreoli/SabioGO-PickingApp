package object_mapping;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import entities.UserConfig;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class UserConfigMapper {

    //Constructor de un objeto UserConfig a partir de un Cursor
    public static UserConfig mapObject(Cursor cursor) {

        UserConfig userConfig = null;
        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Movemos el cursor a la primera posicion
                cursor.moveToLast();

                //Si el cursor trae la columna 'apiURL' no nula creamos el objeto
                if (!cursor.isNull(cursor.getColumnIndex("apiURL"))) {
                    userConfig = new UserConfig(cursor.getString(cursor.getColumnIndex("apiURL")));
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return userConfig;
        }
    }
}