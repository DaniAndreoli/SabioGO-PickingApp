package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import entities.Comprobante;
import entities.Item;

import helpers.dbOperationResponse;
import object_mapping.ComprobanteMapper;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class ComprobanteDAO extends DAO {

    //Metodo que retorna todos los comprobantes (Sin listado de items, es decir sin detalle).
    //Si se necesita el detalle de un comprobante llamar al metodo getComprobanteDetalle() enviando un comprobante en particular por parametro
    public static List<Comprobante> getComprobantes(Context context) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Comprobante", null);
            List<Comprobante> lsComprobantes = ComprobanteMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            db.close();
            return lsComprobantes;

        }catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //Inserta un objeto Comprobante con los datos de su detalle
    public static dbOperationResponse insertComprobante(Context context, Comprobante comp)
    {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);

            //Seteamos los valores para insertar el comprobante
            ContentValues content = new ContentValues();
            content.put("numeroPick", comp.getNumeroPick());
            content.put("orden", comp.getOrden());
            content.put("observaciones", comp.getObservaciones());
            content.put("puedeUsuario", comp.getPuedeUsuario());

            db.insert("Comprobante", null, content);

            //Recorremos el listado de items del comprobante y los insertamos en la BD
            for (Item item : comp.getItems()) {
                content = new ContentValues();
                content.put("codigoArticulo", item.getCodigoArticulo());
                content.put("descripcion", item.getDescripcion());
                content.put("unidad", item.getUnidad());
                content.put("cantidad", item.getCantidad());
                content.put("kilos", item.getKilos());
                content.put("puedePickear", item.getPuedePickear());
                content.put("saldo", item.getSaldo());

                db.insert("Item", null, content);
            }

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

    //// TODO: 31/10/2017  Finalizar Comportamiento
    //Retorna el objeto comprobante pasado por parametro, con su detalle cargado (Listado de Items)
    public static Comprobante getComprobanteDetalle(Context context, Comprobante comp) {
        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursor = db.rawQuery("SELECT * FROM Item WHERE", null);
            List<Comprobante> lsComprobantes = ComprobanteMapper.mapList(cursor);

            //Closes the connection and makes a backup of db file
            db.close();
            return comp;

        }catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
