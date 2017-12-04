package data_access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.List;
import entities.Comprobante;
import entities.Item;
import entities.Serial;
import helpers.dbOperationResponse;
import object_mapping.ComprobanteMapper;
import object_mapping.ItemMapper;
import object_mapping.SerialMapper;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class ComprobanteDAO extends DAO {

    //Inserta un objeto Comprobante con los datos de su detalle
    public static dbOperationResponse insertComprobante(Context context, Comprobante comp, String idUsuario) {
        dbOperationResponse response = new dbOperationResponse();

        try {
            initializeDAO(context);

            //Seteamos los valores para insertar el comprobante
            ContentValues content = new ContentValues();
            content.put("numeroPick", comp.getNumeroPick());
            content.put("orden", comp.getOrden());
            content.put("observaciones", comp.getObservaciones());
            content.put("puedeUsuario", comp.getPuedeUsuario());
            content.put("idUsuario", idUsuario);

            db.insert("Comprobante", null, content);

            //Obtenemos el id insertado y se lo seteamos al comprobante
            Cursor cursorIdComprobante = db.rawQuery("SELECT last_insert_rowid()", null, null);
            cursorIdComprobante.moveToFirst();
            comp.setId_comprobante(cursorIdComprobante.getInt(0));
            cursorIdComprobante.close();

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
                content.put("faltaPickear", item.getFaltaPickear());

                //Seteamos el id_comprobante del item
                content.put("id_comprobante", comp.getId_comprobante());

                db.insert("Item", null, content);

                //Obtenemos el id insertado
                Cursor cursorIdItem = db.rawQuery("SELECT last_insert_rowid()", null, null);
                cursorIdItem.moveToFirst();
                item.setId_item(cursorIdItem.getInt(0));
                cursorIdItem.close();

                //Recorremos el listado de seriales para el item y los insertamos en la base de datos
                for (Serial serial : item.getSeriales()) {
                    content = new ContentValues();
                    content.put("codigoArticulo", item.getCodigoArticulo());
                    content.put("serial", serial.getNumero());
                    content.put("tipoComprobante", "Entrada/Salida");
                    content.put("id_item", item.getId_item());

                    db.insert("Seriales", null, content);
                }
            }

            response.Ok(true);

            return response;

        }catch (Exception e) {
            response.Ok(false);
            response.setException(e);
            response.setMessage("No se pudo insertar el Comprobante");

            return response;
        }
        finally {
            db.close();
        }
    }

    public static Comprobante getComprobanteUsuario(Context context, String idUsuario) {
        Comprobante comprobante = null;

        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            Cursor cursorComprobante = db.rawQuery("SELECT * FROM Comprobante WHERE idUsuario='" + idUsuario + "'", null, null);

            //Si el cursor trae algo, quiere decir que el Usuario actual posee un comprobante
            if (cursorComprobante.getCount() != 0) {
                comprobante = new Comprobante();
                comprobante = ComprobanteMapper.mapObject(cursorComprobante);

                //Hasta aca obtenemos la Cabecera del Comprobante. Ahora obtenemos los Items del mismo
                comprobante.setItems(getComprobanteDetalle(context, comprobante));
            }
        } catch (Error e) {
            throw e;

        } finally {
            return comprobante;
        }
    }

    public static void borrarRegistros(Context context, Comprobante comprobante) {

        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            //Eliminamos todos los seriales del comprobante
            db.execSQL("DELETE FROM Seriales WHERE tipoComprobante = ?", new String[] { "Entrada/Salida" });
            //db.delete("Seriales", "tipoComprobante = ?", new String[] { "Entrada/Salida" });

            //Eliminamos todos los items del comprobante
            db.delete("Item", "id_comprobante = ?", new String[] { Integer.toString(comprobante.getId_comprobante()) });

            //Eliminamos el comprobante
            db.delete("Comprobante", "id_comprobante = ?", new String[] { Integer.toString(comprobante.getId_comprobante()) });

            db.close();

        } catch (Error e) {
            throw e;

        }
    }

    //Retorna el objeto comprobante pasado por parametro, con su detalle cargado (Listado de Items) Solo para uso interno de la clase
    private static List<Item> getComprobanteDetalle(Context context, Comprobante comprobante) {
        Cursor cursorItems, cursorSeriales;

        try {
            //Initialize DAO for using Database (connection opened) and AccessHelper objects
            initializeDAO(context);

            //Obtenemos todos los items para el id_comprobante especificado
            cursorItems = db.rawQuery("SELECT * FROM Item WHERE id_comprobante=" + comprobante.getId_comprobante(),null, null);

            if (cursorItems.getCount()!= 0) {
                comprobante.setItems(ItemMapper.mapList(cursorItems));

                //Una vez obtenido el listado de items, lo recorremos para obtener los seriales de cada uno de ellos en este comprobante
                for (Item item: comprobante.getItems()) {
                    cursorSeriales = db.rawQuery("SELECT * FROM Seriales WHERE id_item = " + item.getId_item(), null);

                    if (cursorSeriales.getCount() != 0) {
                        item.setSeriales(SerialMapper.mapList(cursorSeriales));
                    }
                }
            }

            //Closes the connection and makes a backup of db file
            db.close();

        }catch (Exception e) {
            e.getMessage();
            return null;
        }
        finally {
            return comprobante.getItems();
        }
    }



}
