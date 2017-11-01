package object_mapping;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import entities.Item;
import entities.ItemStock;
import entities.UserConfig;

/**
 * Created by Dani_ on 31/10/2017.
 */

public class StockMapper {

    //Constructor de un Listado de objetos ItemStock a partir de un Cursor
    public static List<ItemStock> mapList(Cursor cursor) {

        List<ItemStock> lsItems = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Movemos el cursor a la primera posicion
                cursor.moveToFirst();
                ItemStock item;
                while (!cursor.isAfterLast()) {
                    item = new ItemStock();
                    item.setCodigoArticulo(cursor.getInt(cursor.getColumnIndex("codigoArticulo")));
                    item.setCantidad(cursor.getInt(cursor.getColumnIndex("cantidad")));
                    item.setUnidad(cursor.getInt(cursor.getColumnIndex("unidad")));
                    item.setKilos(cursor.getInt(cursor.getColumnIndex("kilos")));

                    lsItems.add(item);
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return lsItems;
        }
    }

    //Constructor de un objeto ItemStock a partir de un cursor
    public static ItemStock mapObject(Cursor cursor) {

        ItemStock item = null;
        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Movemos el cursor a la primera posicion
                cursor.moveToFirst();

                //Si el cursor trae la columna 'codigoArticulo' no nula creamos el objeto
                if (!cursor.isNull(cursor.getColumnIndex("codigoArticulo"))) {
                    item = new ItemStock();
                    item.setCodigoArticulo(cursor.getInt(cursor.getColumnIndex("codigoArticulo")));
                    item.setCantidad(cursor.getInt(cursor.getColumnIndex("cantidad")));
                    item.setUnidad(cursor.getInt(cursor.getColumnIndex("unidad")));
                    item.setKilos(cursor.getInt(cursor.getColumnIndex("kilos")));

                                    }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return item;
        }
    }
}
