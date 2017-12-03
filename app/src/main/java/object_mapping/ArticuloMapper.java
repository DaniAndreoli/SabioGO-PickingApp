package object_mapping;

import android.database.Cursor;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import entities.Articulo;

/**
 * Created by Federico on 28/11/2017.
 */

public abstract class ArticuloMapper implements List<Articulo>{

    public static List<Articulo> mapList(Cursor cursor) {
        //Declaramos Lista de articulos
        List<Articulo> lsArticulos = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Recorremos el cursor
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    //Si el cursor trae la columna 'codigo' no nula creamos el objeto
                    if (!cursor.isNull(cursor.getColumnIndex("codigo"))){
                        Articulo articulo = new Articulo();

                        //Seteamos los datos del objeto codBarra
                        articulo.setCodigo(cursor.getString(cursor.getColumnIndex("codigo")));
                        articulo.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));

                        lsArticulos.add(articulo);
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
            return lsArticulos;
        }
    }

    //Constructor de Lista de Articulos a partir de un JSONArray
    public static List<Articulo> mapList(JSONArray jsonArray) {
        List<Articulo> lsArticulos = new ArrayList<>();

        try {
            //Nos aseguramos de que el cursor traiga datos
            if (jsonArray != null && jsonArray.length() != 0) {

                //Recorremos el objeto JSONArray para obtener cada articulo por separado
                for (int i=0; i<jsonArray.length(); i++) {

                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    Articulo articulo = new Articulo();

                    //Seteamos los datos del articulo
                    articulo.setCodigo(jsonObj.getString("codigo"));
                    articulo.setDescripcion(jsonObj.getString("descri"));

                    lsArticulos.add(articulo);
                }
            }
        } catch (Exception e) {
            return null;
        }
        finally {
            return lsArticulos;
        }
    }
}
