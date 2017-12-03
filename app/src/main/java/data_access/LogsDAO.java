package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import helpers.dbOperationResponse;
import object_mapping.LogsMapper;

/**
 * Created by Federico on 30/11/2017.
 */

public class LogsDAO extends DAO {

    public static String obtenerFechaUltimoLog(Context context, String id_usuario) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Logs WHERE id_usuario = ? AND actividad = ?", new String[] { id_usuario, "logout" });
            String fecha= LogsMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            close();
            return fecha;

        } catch (Exception e) {
            e.getMessage();
            close();
            return null;
        }
    }

    //Inserta un objeto articulo y retorna un objeto con datos sobre la transaccion
    public static dbOperationResponse insertarFecha(Context context, String id_usuario, String actividad)
    {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = sdf.format(Calendar.getInstance().getTime());

            ContentValues content = new ContentValues();
            content.put("id_usuario", id_usuario);
            content.put("actividad", actividad);
            content.put("fecha", fecha);

            db.insert("Logs", null, content);

            response.Ok(true);

            close();

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar el log");

            close();
            return response;
        }
        finally {
            close();
        }
    }
}
