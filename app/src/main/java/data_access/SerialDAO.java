package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.List;
import entities.Item;
import entities.ItemStock;
import entities.Serial;
import object_mapping.SerialMapper;
import object_mapping.StockMapper;

/**
 * Created by Federico 01/11/2017.
 */

public class SerialDAO extends DAO {

    public static boolean grabarSerial(Context context, Serial serial) {

        try {
            initializeDAO(context);
            //Realizamos la busqueda en la base de datos por codigo de articulo
            Cursor cursor = db.rawQuery("SELECT * FROM Seriales WHERE serial=?", new String[]{serial.getSerial()});

            //Si el cursor trae datos quiere decir que el serial ya existe en el listado
            if (cursor.getCount() != 0) {
                return false;
            } else {
                //Si el cursor no trae datos significa que el serial no fue cargado en la tabla Seriales, por lo tanto lo insertamos
                ContentValues content = new ContentValues();
                content.put("codigoArticulo", serial.getCodigoArticulo());
                content.put("serial", serial.getSerial());
                content.put("tipoComprobante", serial.getTipoComprobante());

                db.insert("Seriales", null, content);
                return true;
            }
        }catch (Exception e) {
            return false;
        }
    }

    public static void grabarSerialItem(Context context, Item itemComprobante, Serial serial) {
        try {
            initializeDAO(context);

            List<Serial> lsSerialesItem = SerialMapper.mapList(db.rawQuery("SELECT * FROM Seriales " +
                    "WHERE serial = 'null' AND tipoComprobante = ? AND id_item = ?", new String[] { serial.getTipoComprobante(), Integer.toString(serial.getIdItem()) }, null));

            ContentValues content = new ContentValues();

            if (lsSerialesItem != null && lsSerialesItem.size() != 0) {
                Serial primerSerialNulo = lsSerialesItem.get(0);
                primerSerialNulo.setSerial(serial.getSerial());

                content.put("serial", primerSerialNulo.getSerial());

                db.update("Seriales", content, "id_serial = ?", new String[] { Integer.toString(primerSerialNulo.getId_serial()) });
            }

            content = new ContentValues();
            content.put("faltaPickear", itemComprobante.getFaltaPickear() - 1);

            db.update("Item", content, "id_item = ?", new String[] { Integer.toString(itemComprobante.getId_item()) });

            db.close();

        }catch (Exception e) {
            throw e;
        }
    }

    public static List<Serial> getSerialList(Context context, String tipoComprobante) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Seriales WHERE tipoComprobante =?", new String[]{ tipoComprobante });
            List<Serial> seriales= SerialMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            close();
            return seriales;

        }catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static List<Serial> getSerialesArticulo(Context context, String nroArt, String tipoComprobante){
        try{
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Seriales WHERE codigoArticulo = ? AND tipoComprobante = ?", new String[] { nroArt, tipoComprobante });
            List<Serial> seriales = SerialMapper.mapList(cursor);

            close();
            return seriales;
        }catch (Exception ex){
            ex.getMessage();
            return null;
        }
    }

    public static void borrarSeriales(Context context, String tipoComprobante){
        try{
            initializeDAO(context);
            //db.execSQL("DELETE FROM Seriales");
            db.delete("Seriales","tipoComprobante = ?", new String[] { tipoComprobante });
            close();
        }catch (Exception ex){
            throw ex;
        }
    }

    public static void borrarSerial(Context context, String serial, String tipoComprobante, String codArticulo) {
        try{
            initializeDAO(context);

            //Eliminamos el serial correspondiente
            db.delete("Seriales","serial = ? and tipoComprobante = ?", new String[] { serial, tipoComprobante });

            //Verificamos la cantidad de articulos pickeados para el codigo de articulo al cual pertenece el serial
            Cursor cursorItemStock = db.rawQuery("SELECT * FROM Stock WHERE codigoArticulo = ?", new String[] { codArticulo });
            ItemStock itemStock = StockMapper.mapObject(cursorItemStock);

            //Si la cantidad de items que se correspondan con este serial, es mayor que 1, descontamos su cantidad en 1
            if (itemStock.getCantidad() > 1) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("cantidad", itemStock.getCantidad() -1);

                db.update("Stock",contentValues,"codigoArticulo = ?", new String[] { codArticulo });
//                db.rawQuery("UPDATE Stock SET cantidad = ? WHERE codigoArticulo = ?", new String[] { Float.toString(itemStock.getCantidad() - 1), itemStock.getCodigoArticulo() });

            } else if (itemStock.getCantidad() == 1) {
                //Si la cantidad de items que es 1, eliminamos el registro
                db.delete("Stock", "codigoArticulo = ?", new String[] { codArticulo });

            }

            close();
        }catch (Exception ex){
            throw ex;
        }
    }
}