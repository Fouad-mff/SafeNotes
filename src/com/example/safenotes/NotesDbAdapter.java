package com.example.safenotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDbAdapter {

    public static final String KEY_TITLE = "title"; // Colonne "Titre" dans la BD.
    public static final String KEY_DATE = "date"; // Colonne "Date" dans la BD.
    public static final String KEY_BODY = "body"; // Colonne "Texte" dans la BD.
    public static final String KEY_ROWID = "_id"; // Colonne de num�ro de ligne dans la BD.
    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_CREATE = "create table notes (_id integer primary key autoincrement, "
    											+ "title text not null, body text not null, date text not null);";
    			// Cr�ation d'une table "notes" dans la BD contenant les colonnes title, body et date.
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;
    
    
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        		// Lecture de la BD.
        return this;
    }

    public void close() {
        mDbHelper.close();
        		// Fermer la BD ouverte.
    }

    public long createNote(String title, String body, String date) {
    			// Cr�ation d'une nouvelle note Dans la BD.
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE, date);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteNote(long rowId) {
    			// Suppression de la note � partir de la BD.
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllNotes() {
    			// Obtenir toutes les notes enregist�es dans la BD.
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TITLE ,
        		KEY_BODY,KEY_DATE}, null, null, null, null, null);
    }

    public Cursor fetchNote(long rowId) throws SQLException {
    			// Obtenir la note dont le num�ro de ligne dans la BD est "rowId".
    	Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_TITLE
    			,KEY_BODY,KEY_DATE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public boolean rename(long rowId, String title) {
    			// Renommer la note dont le num�ro de ligne dans la BD est "rowId".
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateList(long rowId, String title, String date) {
    			/* Uilis� pour d�chiffrer (titre + date) apr�s l'identification
    			 * et les rechiffrer apr�s la fermeture de l'application.*/
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DATE, date);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateNote(long rowId, String title, String body,String date) {
    			// Enregistrer les modifications effectu�es � la note dans l'�diteur.
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_DATE, date);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
