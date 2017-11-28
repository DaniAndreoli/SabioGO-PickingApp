package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import entities.Usuario;

import helpers.dbOperationResponse;
import object_mapping.UsuarioMapper;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class UsuarioDAO extends DAO {

    public static List<Usuario> getUsuarios(Context context) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Usuario", null);
            List<Usuario> lsUsuarios = UsuarioMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            db.close();

            return lsUsuarios;

        }catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //Inserta un objeto UserConfig y retorna un objeto con datos sobre la transaccion
    public static dbOperationResponse insertUser(Context context, Usuario usuario)
    {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);

            ContentValues content = new ContentValues();
            content.put("idUsuario",usuario.getIdUsuario());
            content.put("loginUsuario",usuario.getLoginUsuario());
            content.put("estado",usuario.getEstado());
            content.put("nombre",usuario.getNombre());
            content.put("area",usuario.getArea());

            db.insert("Usuario", null, content);

            response.Ok(true);

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar el Usuario");

            return response;
        }
        finally {
            db.close();
        }
    }
}
