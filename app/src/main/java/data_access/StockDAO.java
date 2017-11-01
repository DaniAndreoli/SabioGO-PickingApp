package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import entities.ItemStock;
import object_mapping.StockMapper;

/**
 * Created by Dani_ on 31/10/2017.
 */

public class StockDAO extends DAO {
    //Este metodo se llama si se apaga el celular, o se quiere obtener la lista para cargar la grilla sin ingresar un valor.
    public static List<ItemStock> getStockList(Context context) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Stock", null);
            List<ItemStock> lsItemsStock = StockMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            close();
            return lsItemsStock;

        }catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //Permite leer los datos de un item en un Picking de Stock. El metodo agrega el item si no existe, y si existe, incrementa el campo cantidad en 1
    //Devuelve el listado de Stock actualizado
    public static List<ItemStock> leerItemStock(Context context, ItemStock item) {

        try {
            initializeDAO(context);

            //Realizamos la busqueda en la base de datos por codigo de articulo
            Cursor cursor = db.rawQuery("SELECT * FROM Stock WHERE codigoArticulo = ?", new String[] { Integer.toString(item.getCodigoArticulo()) });

            //Si el cursor trae datos quiere decir que el item ya existe en el listado
            if (cursor.getCount() != 0) {

                //Por lo tanto realizaremos un update en la base de datos con el nuevo valor de la cantidad, que seria la cantidad anterior +1
                ItemStock itemExistente = StockMapper.mapObject(cursor);
                db.rawQuery("UPDATE Stock set cantidad = " + itemExistente.getCantidad() + 1 + " WHERE codigoArticulo = ?", new String[] { Integer.toString(item.getCodigoArticulo()) });
            } else {

                //Si el cursor no trae datos significa que el articulo no existe en el listado de stock por lo tanto lo insertamos
                ContentValues content = new ContentValues();
                content.put("codigoArticulo", item.getCodigoArticulo());
                content.put("cantidad", item.getCantidad());
                content.put("unidad", item.getUnidad());
                content.put("kilos", item.getKilos());

                db.insert("Stock", null, content);
            }

            //Una vez finalizado el proceso de insercion o actualizacion segun haya correspondido, consultamos nuevamente la tabla stock para traer el listado actualizado
            cursor = db.rawQuery("SELECT * FROM Stock", null, null);
            close();
            return StockMapper.mapList(cursor);

        }catch (Exception e) {
            return null;
        }
    }


    public static void borrarItemStock(Context context){
        try{
            initializeDAO(context);
            db.execSQL("DELETE FROM Stock");
            close();
        }catch (Exception ex){
            throw ex;
        }
    }
}