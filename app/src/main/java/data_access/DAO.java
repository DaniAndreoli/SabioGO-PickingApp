package data_access;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import helpers.dbAccessHelper;
import helpers.dbOperationResponse;

public abstract class DAO {

    public static dbAccessHelper helper;
    public static SQLiteDatabase db;
    public static dbOperationResponse response;

    public static void initializeDAO(Context context) {
        //Initialize helper Object
        helper = helper.getInstance(context);

        //Open Database Connection and set database object value
        db = helper.open();
    }
}
