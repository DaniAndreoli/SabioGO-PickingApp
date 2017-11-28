package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import entities.Articulo;
import helpers.dbOperationResponse;
import object_mapping.ArticuloMapper;

/**
 * Created by Federico on 28/11/2017.
 */

public class ArticuloDAO extends DAO {

    public static List<Articulo> getArticulo(Context context) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Articulos", null);
            List<Articulo> list = ArticuloMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            close();
            return list;

        } catch (Exception e) {
            e.getMessage();
            close();
            return null;
        }
    }

    //Inserta un objeto articulo y retorna un objeto con datos sobre la transaccion
    public static dbOperationResponse insertArticulos(Context context, Articulo articulo)
    {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);

            ContentValues content = new ContentValues();
            content.put("codigo", articulo.getCodigo());
            content.put("descripcion", articulo.getDescripcion());

            db.insert("Articulo", null, content);

            response.Ok(true);

            close();

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar el Articulo");

            close();

            return response;
        }
        finally {
            close();
        }
    }

    //Metodo que permite insertar un listado completo de objetos articulo
    public static dbOperationResponse insertArticulos(Context context, List<Articulo> lsArticulos) {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);
            db.beginTransaction();

            //Si vamos a obtener un listado completo de todos los articulos posibles, eliminamos los anteriores
            db.delete("Articulo","codigo >?",new String[]{"0"});

            for (Articulo articulo: lsArticulos) {

                ContentValues content = new ContentValues();
                content.put("codigo",articulo.getCodigo());
                content.put("descripcion",articulo.getDescripcion());

                db.insert("Articulo", null, content);
            }

            response.Ok(true);

            //Si surge un error durante la transaccion, sin haber ejecutado setTransactionSuccessful(), la transaccion no sera commiteada
            //El rollback se llevara a cabo cuando se ejectute endTransaction() sin haber sido comiteada con setTransactionSuccessful()
            db.setTransactionSuccessful();

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar el Articulo");

            return response;
        }
        finally {
            db.endTransaction();
            close();
        }
    }
}
