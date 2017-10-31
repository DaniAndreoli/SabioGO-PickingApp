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
            db.close();
            return list;

        } catch (Exception e) {
            e.getMessage();
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

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se puede insertar el Codigo de Barra");

            return response;
        }
        finally {
            db.close();
        }
    }

}
