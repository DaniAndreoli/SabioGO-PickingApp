package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import entities.UserConfig;

import helpers.dbOperationResponse;
import object_mapping.UserConfigMapper;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class UserConfigDAO extends DAO {

    public static UserConfig getUserConfig(Context context) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM UserConfig", null);
            UserConfig userConfig = UserConfigMapper.mapObject(cursor);

            //Closes the connection and makes a backup of db file
            close();
            return userConfig;

        }catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //Inserta un objeto UserConfig y retorna un objeto con datos sobre la transaccion
    public static dbOperationResponse insertUserConfig(Context context, UserConfig userConfig)
    {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);

            //Eliminamos el contenido de la configuracion anterior
            db.delete("UserConfig", null, null);

            ContentValues content = new ContentValues();
            content.put("apiURL",userConfig.getApiUrl());

            db.insert("UserConfig", null, content);

            response.Ok(true);

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar la Configuracion de Usuario");

            return response;
        }
        finally {
            db.close();
        }
    }
}
