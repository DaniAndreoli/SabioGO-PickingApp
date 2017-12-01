package object_mapping;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import entities.Item;
import entities.Serial;

/**
 * Created by Dani on 30/11/2017.
 */

public class ItemMapper {

    public static List<Item> mapList(Cursor cursor) {

        List<Item> lsItems = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Movemos el cursor a la primera posicion
                cursor.moveToFirst();
                Item item;

                while (!cursor.isAfterLast()) {
                    item = new Item();

                    item.setId_item(cursor.getInt(cursor.getColumnIndex("id_item")));
                    item.setCodigoArticulo(cursor.getString(cursor.getColumnIndex("codigoArticulo")));
                    item.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
                    item.setUnidad(cursor.getInt(cursor.getColumnIndex("unidad")));
                    item.setCantidad(cursor.getInt(cursor.getColumnIndex("cantidad")));
                    item.setKilos(cursor.getDouble(cursor.getColumnIndex("kilos")));
                    item.setPuedePickear(cursor.getDouble(cursor.getColumnIndex("puedePickear")));
                    item.setSaldo(cursor.getDouble(cursor.getColumnIndex("saldo")));
                    item.setFaltaPickear(cursor.getInt(cursor.getColumnIndex("faltaPickear")));

                    lsItems.add(item);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return lsItems;
        }
    }

}
