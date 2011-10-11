package com.lgcns.nfc.secretnote.data;

import java.util.ArrayList;

import com.lgcns.nfc.secretnote.util.ByteUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Data Helper class
 * 
 * Provides Store/Update/Delete feature.
 */
public class DataResolver {
    
    private static final String TAG = "DataResolver";
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;
    private final Context context;

    private static final String PREF_FILENAME_KEY = "pref_file_count";
    private static final String DATABASE_NAME = "NfcSceretNote";
    private static final String TABLE_NAME = "Note";
    private static final String[] COLUMNS = {
            "_id", "type", "title","filename" , "date"
    };
    
    private static final int DATABASE_VERSION = 1;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = "create table " + TABLE_NAME + " ( " + COLUMNS[0]
                + " integer primary key autoincrement , " + COLUMNS[1] + " integer , " + COLUMNS[2] + " text , "
                +  COLUMNS[3] + " text , "+  COLUMNS[4] + " text );";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
        }
    }
    
    public DataResolver(Context _context) {
        this.context = _context;
        mDbHelper = null;
        mDb = null;
    }
    
    private void open() throws SQLException {
        open(false);
    }

    private void open(boolean readOnly) throws SQLException {
        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(context);
            if (readOnly) {
                mDb = mDbHelper.getReadableDatabase();
            } else {
                mDb = mDbHelper.getWritableDatabase();
            }
        }
    }

    private void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
        }
    }
    
    public ArrayList<Label> getDataList()
    {
        ArrayList<Label> result = new ArrayList<Label>();
        Cursor c = null;
        open();
        try{
            c = mDb.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
            if(c!=null&&c.getCount()>0)
            { 
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    result.add(new Label(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4)));
                    c.moveToNext();
                }
            }
        }finally
        {    
            if(c!=null&&!c.isClosed())c.close(); 
            close();
        }
        return result;
    }
    
    public Label getData(int id)
    {
        Label result = null;
        Cursor c = null;
        open();
        try{
            c = mDb.query(TABLE_NAME, COLUMNS, COLUMNS[0]+" = "+id, null, null, null, null);
            if(c!=null&&c.getCount()>0)
            { 
                c.moveToFirst();
                result = new Label(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4));                
            }
        }finally
        {
            if(c!=null&&!c.isClosed())c.close();
            close();
        }
        return result;
    }
    
    public boolean saveDataList(Label label, DataObject data) {        
        if(label.getFilename() == null||label.getFilename().equals(""))
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int count = prefs.getInt(PREF_FILENAME_KEY, 0);
            label.setFilename(Integer.toString(count));
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREF_FILENAME_KEY, count+1);
            editor.commit();
        }
        ContentValues content = new ContentValues();
        content.put(COLUMNS[1], label.getType());
        content.put(COLUMNS[2], label.getTitle());
        content.put(COLUMNS[3], label.getFilename());
        content.put(COLUMNS[4], label.getDate());
        open(true);
        try {
            Log.d(TAG, "Write label to DB");
            if (mDb.insert(TABLE_NAME, " ", content) != -1) {
                Log.d(TAG, "Write date to file");
                writeFile(label,data);
                return true;
            } else {
                return false;
            }
        } finally {
            close();
        }
    }
    
    public boolean updateDataList(Label label, DataObject data) {
        Log.d(TAG, "update data id : "+label.get_id());
        open(true);        
        ContentValues content = new ContentValues();
        content.put(COLUMNS[1], label.getType());
        content.put(COLUMNS[2], label.getTitle());
        content.put(COLUMNS[3], label.getFilename());
        content.put(COLUMNS[4], label.getDate());
        try {
            int update = mDb.update(TABLE_NAME, content, " _id = "+label.get_id(), null);
            Log.d(TAG,"updated : "+update);
            if (update == 1) {
                writeFile(label,data);
                return true;
            } else {
                return false;
            }
        } finally {
            close();
        }
    }

    public boolean deleteData(Label label) {
        open(true);        
        try {
            if (mDb.delete(TABLE_NAME, COLUMNS[0] + " = " + label.get_id(), null) == 1) {
                FileManager manager = new FileManager(context);
                switch (label.getType()) {
                    case Label.DATA_TYPE_IMAGE:
                        manager.deleteFile(label.getFilename()+"I.dat");
                    case Label.DATA_TYPE_TEXT:
                        manager.deleteFile(label.getFilename()+"T.dat");
                }
                return true;
            } else {
                return false;
            }
        } finally {
            close();
        }
    }
    
    public DataObject readFile(Label label)
    {
        DataObject result = null;
        FileManager manager = new FileManager(context);
        switch (label.getType()) {
            case Label.DATA_TYPE_IMAGE:
                result = new DataObject(Label.DATA_TYPE_IMAGE);
                result.setImageData(manager.readFromFile(label.getFilename()+"I.dat"));                
            case Label.DATA_TYPE_TEXT:
                if(result==null) result = new DataObject(Label.DATA_TYPE_TEXT);
                result.setTextData(manager.readFromFile(label.getFilename()+"T.dat"));
        }
        return result;
    }
    
    public void writeFile(Label label , DataObject data)
    {   
        FileManager manager = new FileManager(context);
        switch (label.getType()) {
            case Label.DATA_TYPE_IMAGE:
                manager.writeToFile(label.getFilename()+"I.dat", data.getImageData());
            case Label.DATA_TYPE_TEXT:
                manager.writeToFile(label.getFilename()+"T.dat", data.getTextData());                
        }
    }
    
    public int generagePasswordHash(String password)
    {
        int hash=0;
        if(password==null||password.equals("")) return 0;
        byte[] pw = ByteUtils.StringToByteArray(password);
        for(int i = 0 ; i<pw.length;i++)
        {
            hash |= ((pw[i]&0x3)<<(2*(i%16)));
        }
        return hash;
    }

}
