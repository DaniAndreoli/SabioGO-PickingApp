package object_mapping;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import entities.Usuario;

/**
 * Created by Dani_ on 29/10/2017.
 */

public abstract class UsuarioMapper {

    //Constructor de un objeto Usuario a partir de un Cursor
    public static List<Usuario> mapList(Cursor cursor) {

        List<Usuario> lsUsuarios = new ArrayList<>();
        try {
            //Nos aseguramos de que el cursor traiga datos
            if (cursor != null && cursor.getCount() != 0) {

                //Movemos el cursor a la primera posicion
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    //Si el cursor trae la columna 'idUsuario' no nula creamos el objeto
                    if (!cursor.isNull(cursor.getColumnIndex("idUsuario"))) {
                        Usuario usuario = new Usuario();

                        usuario = new Usuario();
                        usuario.setIdUsuario(cursor.getInt(cursor.getColumnIndex("idUsuario")));
                        usuario.setLoginUsuario(cursor.getString(cursor.getColumnIndex("loginUsuario")));
                        usuario.setEstado(cursor.getInt(cursor.getColumnIndex("estado")));
                        usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                        usuario.setArea(cursor.getInt(cursor.getColumnIndex("area")));

                        lsUsuarios.add(usuario);
                    }
                    //Continuamos recorriendo el cursor
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            return null;
        } finally {
            return lsUsuarios;
        }
    }
}