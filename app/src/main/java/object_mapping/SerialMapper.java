package object_mapping;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import entities.Serial;

/**
 * Created by Federico on 01/11/2017.
 */

public class SerialMapper {
    public static List<Serial> mapList(Cursor cursor) {

        List<Serial> lsSerial = new ArrayList<>();
        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Movemos el cursor a la primera posicion
                cursor.moveToFirst();
                Serial serial;
                while (!cursor.isAfterLast()) {
                    serial = new Serial();
                    serial.setCodigoArticulo(cursor.getString(cursor.getColumnIndex("codigoArticulo")));
                    serial.setSerial(cursor.getString(cursor.getColumnIndex("serial")));
                    serial.setTipoComprobante(cursor.getString(cursor.getColumnIndex("tipoComprobante")));
                    serial.setIdItem(cursor.getInt(cursor.getColumnIndex("id_item")));
                    serial.setId_serial(cursor.getInt(cursor.getColumnIndex("id_serial")));

                    lsSerial.add(serial);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return lsSerial;
        }
    }

    //Constructor de un objeto ItemStock a partir de un cursor
    public static Serial mapObject(Cursor cursor) {

        Serial serial = null;
        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Movemos el cursor a la primera posicion
                cursor.moveToFirst();

                //Si el cursor trae la columna 'codigoArticulo' no nula creamos el objeto
                if (!cursor.isNull(cursor.getColumnIndex("codigoArticulo"))) {
                    serial = new Serial();
                    //serial.setCodigoArticulo(cursor.getInt(cursor.getColumnIndex("codigoArticulo")));
                    serial.setSerial(cursor.getString(cursor.getColumnIndex("serial")));
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return serial;
        }
    }
}
