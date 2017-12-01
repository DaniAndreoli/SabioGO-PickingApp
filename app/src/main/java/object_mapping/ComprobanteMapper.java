package object_mapping;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import entities.CodigoBarra;
import entities.Comprobante;
import entities.Item;
import entities.Serial;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class ComprobanteMapper implements List<Comprobante> {

    //Constructor de Lista de Comprobantes a partir de un Cursor
    public static List<Comprobante> mapList(Cursor cursor) {
        //Declaramos Lista de Comprobantes
        List<Comprobante> lsComprobantes = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Recorremos el cursor
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    //Si el cursor trae la columna 'numero' no nula creamos el objeto
                    if (!cursor.isNull(cursor.getColumnIndex("id_comprobante"))){
                        Comprobante comp = new Comprobante();

                        //Seteamos los datos propios del comprobante
                        comp.setId_comprobante(cursor.getInt(cursor.getColumnIndex("id_comprobante")));
                        comp.setNumeroPick(cursor.getInt(cursor.getColumnIndex("numeroPick")));
                        comp.setOrden(cursor.getInt(cursor.getColumnIndex("orden")));
                        comp.setObservaciones(cursor.getString(cursor.getColumnIndex("observaciones")));
                        comp.setPuedeUsuario(cursor.getInt(cursor.getColumnIndex("puedeUsuario")));

                        /* Para setear el atributo Lizt<Item> items, sera necesario realizar una busqueda de todos los
                         * items que pertenezcan a este comprobante. Este metodo solo devuelve la cabecera del comprobante */

                        //Agregamos el comprobabte a la lista
                        lsComprobantes.add(comp);
                    }
                    //Continuamos recorriendo el cursor
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        }
        finally {
            return lsComprobantes;
        }
    }

    //Constructor de Lista de Codigos de Barra a partir de un JSONArray
    public static Comprobante mapObject(JSONObject jsonObject) {
        //Declaramos Objeto Comprobante
        Comprobante comprobante = new Comprobante();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (jsonObject!= null && jsonObject.length() != 0) {

                //Recorremos el objeto JSONObject para obtener el Comprobante

                //Seteamos los datos del objeto Comprobante - Cabecera
                comprobante.setNumeroPick(jsonObject.getInt("numeroPick"));
                comprobante.setOrden(jsonObject.getInt("orden"));
                comprobante.setObservaciones(jsonObject.getString("observaciones"));
                comprobante.setPuedeUsuario(jsonObject.getInt("puedeUsuario"));

                //Obtenemos el JsonArray en la posicion "items" del JsonObject Comprobante
                JSONArray itemsJsonArray = jsonObject.getJSONArray("items");
                List<Item> lsItemsComprobante = new ArrayList<Item>();

                if (itemsJsonArray != null && itemsJsonArray.length() != 0) {

                    //Recorremos el objeto JsonArray con los items del Comprobante
                    for (int i = 0; i < itemsJsonArray.length(); i++) {

                        //Declaramos los JsonObjects del recorrido y los items
                        JSONObject itemJson = itemsJsonArray.getJSONObject(i);
                        Item item = new Item();

                        if (itemJson != null && itemJson.length() != 0) {
                            item.setCodigoArticulo(itemJson.getString("codigoArticulo"));
                            item.setDescripcion(itemJson.getString("descripcion"));
                            item.setUnidad(itemJson.getInt("unidad"));
                            item.setCantidad(itemJson.getInt("cantidad"));
                            item.setKilos(itemJson.getInt("kilos"));
                            item.setPuedePickear(itemJson.getInt("puedePickear"));
                            item.setSaldo(itemJson.getInt("saldo"));
                            item.setFaltaPickear(itemJson.getInt("cantidad"));

                            //Obtenemos el JsonArray en la posicion "seriales" del JsonObject Item
                            JSONArray serialesJsonArray = itemJson.getJSONArray("seriales");
                            List<Serial> lsSerialesItem = new ArrayList<Serial>();

                            if (serialesJsonArray != null && serialesJsonArray.length() != 0) {

                                //Recorremos el objeto JsonArray con los seriales del item
                                for (int j=0; j < serialesJsonArray.length(); j++) {

                                    //Declaramos los JsonObjects del recorrido y los seriales
                                    JSONObject serialJson = serialesJsonArray.getJSONObject(j);
                                    Serial serial = new Serial();

                                    if (serialJson != null && serialJson.length() != 0) {
                                        serial.setSerial(serialJson.getString("numero"));
                                    }

                                    //Agregamos el serial al listado de seriales del item
                                    lsSerialesItem.add(serial);
                                }

                                //Seteamos la lista de seriales del item
                                item.setSeriales(lsSerialesItem);
                            }
                        }

                        //Agregamos el item al listado de items del comprobante
                        lsItemsComprobante.add(item);
                    }

                    //Seteamoos el listado de items del objeto Comprobante
                    comprobante.setItems(lsItemsComprobante);
                }
            }
        } catch (Exception e) {
            return null;
        }
        finally {
            return comprobante;
        }
    }

    public static Comprobante mapObject(Cursor cursor) {
        //Declaramos Objeto Comprobante
        Comprobante comprobante = new Comprobante();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Inicializamos el cursor con la primera posicion, y la unica
                cursor.moveToFirst();

                //Seteamos los datos del objeto Comprobante - Cabecera
                comprobante.setId_comprobante(cursor.getInt(cursor.getColumnIndex("id_comprobante")));
                comprobante.setNumeroPick(cursor.getInt(cursor.getColumnIndex("numeroPick")));
                comprobante.setOrden(cursor.getInt(cursor.getColumnIndex("orden")));
                comprobante.setObservaciones(cursor.getString(cursor.getColumnIndex("observaciones")));
                comprobante.setPuedeUsuario(cursor.getInt(cursor.getColumnIndex("puedeUsuario")));
                comprobante.setIdUsuario(cursor.getString(cursor.getColumnIndex("idUsuario")));
            }
        } catch (Exception e) {
            return null;
        }
        finally {
            return comprobante;
        }
    }

}
