package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import entities.Serial;
import object_mapping.SerialMapper;

/**
 * Created by Federico 01/11/2017.
 */

public class SerialDAO extends DAO {

    public static boolean grabarSerial(Context context, Serial serial, String codigoArticulo, String tipoComprobante) {

        try {
            initializeDAO(context);
            //Realizamos la busqueda en la base de datos por codigo de articulo
            Cursor cursor = db.rawQuery("SELECT * FROM Seriales WHERE serial = ?", new String[] { serial.getNumero()});

            //Si el cursor trae datos quiere decir que el serial ya existe en el listado
            if (cursor.getCount() != 0) {
                return false;
            } else {
                //Si el cursor no trae datos significa que el serial no fue cargado en la tabla Seriales, por lo tanto lo insertamos
                ContentValues content = new ContentValues();
                content.put("codigoArticulo", codigoArticulo);
                content.put("serial", serial.getNumero());
                content.put("tipoComprobante", tipoComprobante);

                db.insert("Seriales", null, content);
                return true;
            }
        }catch (Exception e) {
            return false;
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
}