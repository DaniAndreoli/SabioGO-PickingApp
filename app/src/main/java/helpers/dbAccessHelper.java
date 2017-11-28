package helpers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;

//Clase encargada de realizar las conexiones con la base de datos
public class dbAccessHelper {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static dbAccessHelper instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private dbAccessHelper(Context context) {
        this.openHelper = new dbCreationHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static dbAccessHelper getInstance(Context context) {
        if (instance == null) {
            instance = new dbAccessHelper(context);
        }
        return instance;
    }

    /**
     * Open connection and return DataBase object for interaction.
     */
    public SQLiteDatabase open() {
        if (this.database == null || !this.database.isOpen()) {
            this.database = openHelper.getWritableDatabase();
        }
        return this.database;
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public SQLiteDatabase database() {
        return this.database;
    }

}