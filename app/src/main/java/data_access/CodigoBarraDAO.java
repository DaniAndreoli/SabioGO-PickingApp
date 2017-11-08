package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import entities.CodigoBarra;

import helpers.dbOperationResponse;
import object_mapping.CodigoBarraMapper;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class CodigoBarraDAO extends DAO {

    public static List<CodigoBarra> getCodigosBarra(Context context) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM CodigoBarra", null);
            List<CodigoBarra> list = CodigoBarraMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            close();
            return list;

        } catch (Exception e) {
            e.getMessage();
            close();
            return null;
        }
    }

    //Inserta un objeto CodigoBarra y retorna un objeto con datos sobre la transaccion
    public static dbOperationResponse insertCodigoBarra(Context context, CodigoBarra codBarra)
    {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);

            ContentValues content = new ContentValues();
            content.put("numero",codBarra.getNumero());
            content.put("nombre",codBarra.getNombre());
            content.put("descripcion",codBarra.getDescripcion());
            content.put("largoTotal",codBarra.getLargoTotal());
            content.put("ubicacionCodProd",codBarra.getUbicacionCodProd());
            content.put("largoCodProd",codBarra.getLargoCodProd());
            content.put("ubicacionCantidad",codBarra.getUbicacionCantidad());
            content.put("largoCantidad",codBarra.getLargoCantidad());
            content.put("ubicacionPeso",codBarra.getUbicacionPeso());
            content.put("largoPeso",codBarra.getLargoPeso());
            content.put("ubicacionPrecio",codBarra.getUbicacionPrecio());
            content.put("largoPrecio",codBarra.getLargoPrecio());
            content.put("ubicacionFechaElab",codBarra.getUbicacionFechaElab());
            content.put("largoFechaElab",codBarra.getLargoFechaElab());
            content.put("ubicacionFechaVenc",codBarra.getUbicacionFechaVenc());
            content.put("largoFechaVenc",codBarra.getLargoFechaVenc());
            content.put("ubicacionDigitoVer",codBarra.getUbicacionDigitoVer());
            content.put("largoDigitoVer",codBarra.getLargoDigitoVer());
            content.put("ubicacionIdUsuario",codBarra.getUbicacionIdUsuario());
            content.put("largoIdUsuario",codBarra.getLargoIdUsuario());
            content.put("cantidadDecPeso",codBarra.getCantidadDecPeso());

            db.insert("CodigoBarra", null, content);

            response.Ok(true);

            close();

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar el Codigo de Barra");

            close();

            return response;
        }
        finally {
            close();
        }
    }

    //Metodo que permite insertar un listado completo de objetos CodigoBarra
    public static dbOperationResponse insertCodigosBarra(Context context, List<CodigoBarra> lsCodigosBarra) {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);
            db.beginTransaction();

            //Si vamos a obtener un listado completo de todos los codigos de barra posibles, eliminamos los anteriores
            db.delete("CodigoBarra","numero > ?", new String[]{ "0" });
            //db.execSQL("delete from CodigoBarra");

            for (CodigoBarra codBarra: lsCodigosBarra) {

                ContentValues content = new ContentValues();
                content.put("numero",codBarra.getNumero());
                content.put("nombre",codBarra.getNombre());
                content.put("descripcion",codBarra.getDescripcion());
                content.put("largoTotal",codBarra.getLargoTotal());
                content.put("ubicacionCodProd",codBarra.getUbicacionCodProd());
                content.put("largoCodProd",codBarra.getLargoCodProd());
                content.put("ubicacionCantidad",codBarra.getUbicacionCantidad());
                content.put("largoCantidad",codBarra.getLargoCantidad());
                content.put("ubicacionPeso",codBarra.getUbicacionPeso());
                content.put("largoPeso",codBarra.getLargoPeso());
                content.put("ubicacionPrecio",codBarra.getUbicacionPrecio());
                content.put("largoPrecio",codBarra.getLargoPrecio());
                content.put("ubicacionFechaElab",codBarra.getUbicacionFechaElab());
                content.put("largoFechaElab",codBarra.getLargoFechaElab());
                content.put("ubicacionFechaVenc",codBarra.getUbicacionFechaVenc());
                content.put("largoFechaVenc",codBarra.getLargoFechaVenc());
                content.put("ubicacionDigitoVer",codBarra.getUbicacionDigitoVer());
                content.put("largoDigitoVer",codBarra.getLargoDigitoVer());
                content.put("ubicacionIdUsuario",codBarra.getUbicacionIdUsuario());
                content.put("largoIdUsuario",codBarra.getLargoIdUsuario());
                content.put("cantidadDecPeso",codBarra.getCantidadDecPeso());

                db.insert("CodigoBarra", null, content);
            }

            response.Ok(true);

            //Si surge un error durante la transaccion, sin haber ejecutado setTransactionSuccessful(), la transaccion no sera commiteada
            //El rollback se llevara a cabo cuando se ejectute endTransaction() sin haber sido comiteada con setTransactionSuccessful()
            db.setTransactionSuccessful();

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar el Codigo de Barra");

            return response;
        }
        finally {
            db.endTransaction();
            close();
        }
    }

}
