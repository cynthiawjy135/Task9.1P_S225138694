package com.example.task7p_s225138694.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.task7p_s225138694.AddItemActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LostAndFoundDB3";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LONG = "long";


    private String formatDate(String rawDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            return outputFormat.format(inputFormat.parse(rawDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return rawDate;
        }
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_LAT + " TEXT, "
                + COLUMN_LONG + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addItem(ItemDataModel itm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, itm.getName());
        values.put(COLUMN_TYPE, itm.getType());
        values.put(COLUMN_PHONE, itm.getPhone());
        values.put(COLUMN_DESCRIPTION, itm.getDescription());
        values.put(COLUMN_DATE, itm.getDate());
        values.put(COLUMN_LOCATION, itm.getLocation());
        values.put(COLUMN_IMAGE, itm.getImagePath());
        values.put(COLUMN_LAT, itm.getLat());
        values.put(COLUMN_LONG, itm.getLong());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<ItemDataModel> getAllItems() {
        ArrayList<ItemDataModel> itemLists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if(cursor.moveToFirst()){
            do {
                // Get data by calling it using cursor
                String name = cursor.getString(1);
                String type = cursor.getString(2);
                String phone = cursor.getString(3);
                String description = cursor.getString(4);
                String date =  formatDate(cursor.getString(5));
                String location = cursor.getString(6);
                String image = cursor.getString(7);
                double lat = cursor.getDouble(8);
                double longt = cursor.getDouble(9);

                // Create Item Object with the parameter
                ItemDataModel itm = new ItemDataModel(name, type, phone, description, date, location, image, lat, longt) ;
                itm.setId(cursor.getInt(0));  // Set object ID

                itemLists.add(itm);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemLists;
    }

    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ItemDataModel getItemByID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        if(cursor != null && cursor.moveToFirst()){
            String name = cursor.getString(1);
            String type = cursor.getString(2);
            String phone = cursor.getString(3);
            String description = cursor.getString(4);
            String date =  formatDate(cursor.getString(5));
            String location = cursor.getString(6);
            String image = cursor.getString(7);
            double lat = cursor.getDouble(8);
            double longt = cursor.getDouble(9);

            ItemDataModel itm = new ItemDataModel(
                    name, type, phone, description, date, location, image, lat, longt
            );

            itm.setId(cursor.getInt(0));

            cursor.close();
            db.close();

            return itm;
        }

        if(cursor != null){
            cursor.close();
        }
        db.close();

        return null;
    }
}
