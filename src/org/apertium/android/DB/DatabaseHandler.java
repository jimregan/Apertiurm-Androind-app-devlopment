/**
 *
 * @author Arink Verma
 */

package org.apertium.android.DB;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apertium.android.helper.AppPreference;
import org.apertium.android.helper.ConfigManager;
import org.apertium.android.languagepair.LanguagePackage;
import org.apertium.android.languagepair.TranslationMode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler {
	private static final String TAG = "DatabaseHandler";
	
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "ApertiumLanugManager.db";
 

    //Mode Table
    private static final String TABLE_MODE 		= "Mode";
    private static final String KEY_MODE_ID 		= "id";
    private static final String KEY_MODE_TITLE 	= "title";
    private static final String KEY_MODE_PACKAGE 		= "package_id";
    
    //Package Table
    private static final String TABLE_PACKAGE 		= "Package";
//    private static final String KEY_PACKAGE_TITLE 	= "title";
    private static final String KEY_PACKAGE_ID 		= "id";
//We dont support version at present
//    private static final String KEY_PACKAGE_VERSION 	= "version";
    private static final String KEY_PACKAGE_LASTMODIFIED = "LastDate";
    
    private Context context;
    
    public DatabaseHandler(Context context) {
        this.context = context;
    }
 

    public void closeDatabase(){
	      OpenHelper openHelper = new OpenHelper(this.context);
	      openHelper.close();
    }
    
 // Adding new contact
    public void addLanuagepair(LanguagePackage L) {
    	OpenHelper openHelper = new OpenHelper(this.context);
    	SQLiteDatabase db = openHelper.getWritableDatabase();  
    	ContentValues values = new ContentValues();
    	
    	//Inserting package details
    	values.put(KEY_PACKAGE_ID,L.getID()); 
    	values.put(KEY_PACKAGE_LASTMODIFIED,L.getLastDate());     	
    	db.insert(TABLE_PACKAGE, null, values);
    	values.clear();
    	
    	//Inserting Modes from above package
    	List<TranslationMode> TranslationModes = L.getModes();
        for (int i=0;i<TranslationModes.size();i++) {
        	TranslationMode m = TranslationModes.get(i);
        	values.put(KEY_MODE_ID , m.getID());
        	values.put(KEY_MODE_TITLE, m.getTitle()); 
        	values.put(KEY_MODE_PACKAGE, L.getID());
        	db.insert(TABLE_MODE, null, values);
        }	
    	db.close();
    }
     
     
    /***
     * Modes related queries
     * 
     */
    // Get all modes
    public List<TranslationMode> getAllModes() {
    	OpenHelper openHelper = new OpenHelper(this.context);
	    SQLiteDatabase db = openHelper.getReadableDatabase();
    	
        List<TranslationMode> LangList = new ArrayList<TranslationMode>();
        
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MODE + " ORDER BY "+KEY_MODE_TITLE+" ASC";
    	
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                
                String m 	= cursor.getString(0);	//Id
                String t 	= cursor.getString(1);	//Title
                String p 	= cursor.getString(2);	//Package_id
                TranslationMode M = new TranslationMode(m,t);
                M.setPackage(p);
                LangList.add(M);
            } while (cursor.moveToNext());
        }
        db.close();
        return LangList;    	
    }
    
    // Get mode id of translation language from , language to
	public String getModeID(String From,String To){
		Log.i(TAG,"From="+From+",To="+To);
		OpenHelper openHelper = new OpenHelper(this.context);
		SQLiteDatabase db = openHelper.getReadableDatabase();	       
		String ID = null;
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_MODE + " WHERE "+KEY_MODE_TITLE+" LIKE '"+From+"%"+To+"' ORDER BY "+KEY_MODE_TITLE+" ASC";
		Log.i("SQL",selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			ID = cursor.getString(0);	//Id
		}
		db.close();
		return ID;	   
	}
    
	// Get title of all language available which has translation from
	public String[] getModeTitlesOut(){
		List<String> TitleList = new ArrayList<String>();
		List<TranslationMode> LangList = this.getAllModes();
		    	 
		for(int i=0;i<LangList.size();i++){
			TranslationMode m = LangList.get(i);
			String []s = m.getTitle().split("[^\\w]+");
			if(!TitleList.contains(s[0])){
				TitleList.add(s[0]);
			}
		}    	 
		
		String[] Array = new String[TitleList.size()];
		for(int i=0;i<TitleList.size();i++){
			Array[i] = TitleList.get(i);
		}
		return Array;
	}
    
    // Get mode ids of incoming translation from argumented mode
   public String[] getModeTitlesInFrom(String ModeTile){
		OpenHelper openHelper = new OpenHelper(this.context);
	    SQLiteDatabase db = openHelper.getReadableDatabase();	   
	   	List<String> TitleList = new ArrayList<String>();
        
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MODE + " WHERE "+KEY_MODE_TITLE+" LIKE '"+ModeTile+"%' ORDER BY "+KEY_MODE_TITLE+" ASC";
    	Log.i("SQL",selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String []s 	= cursor.getString(1).split("[^\\w]+");	//Title
                //Ignoring first word
    			if(!TitleList.contains(s[1])){
    				TitleList.add(s[1]);
    			}
    		} while (cursor.moveToNext());
        }
        db.close();
        
        String[] Array = new String[TitleList.size()];
		for(int i=0;i<TitleList.size();i++){
			Array[i] = TitleList.get(i);
		}
		return Array;
   }
    
    //Get modes from Package id
    public List<TranslationMode> getModes(String Package_ID) {
    	OpenHelper openHelper = new OpenHelper(this.context);
	    SQLiteDatabase db = openHelper.getReadableDatabase();
    	
        List<TranslationMode> LangList = new ArrayList<TranslationMode>();
        
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MODE + " WHERE "+ KEY_MODE_PACKAGE+" = '"+Package_ID+"'";
    	
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                
                String m 	= cursor.getString(0);	//Id
                String t 	= cursor.getString(1);	//Title
                String p 	= cursor.getString(2);	//Package_id
                TranslationMode M = new TranslationMode(m,t);
                M.setPackage(p);
                LangList.add(M);
            } while (cursor.moveToNext());
        }
        db.close();
        return LangList;    	
    }
     
    //Get last modified date in seconds
    public String getLastModifiedDate(String Package_ID){
    	OpenHelper openHelper = new OpenHelper(this.context);
	    SQLiteDatabase db = openHelper.getReadableDatabase();
    	        
        // Select All Query
        String selectQuery = "SELECT  "+KEY_PACKAGE_LASTMODIFIED+" FROM " + TABLE_PACKAGE + " WHERE "+KEY_PACKAGE_ID +" = '"+Package_ID+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String M = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
        		M 	= cursor.getString(0);	//date              
        }
        db.close();
        return M;  
    }
    
    //Get mode by id
    public TranslationMode getMode(String Mode_ID) {
    	OpenHelper openHelper = new OpenHelper(this.context);
	    SQLiteDatabase db = openHelper.getReadableDatabase();
    	        
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MODE + " WHERE "+KEY_MODE_ID +" = '"+Mode_ID+"'";
    	
        Cursor cursor = db.rawQuery(selectQuery, null);
        TranslationMode M = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
        		String m 	= cursor.getString(0);	//Id
                String t 	= cursor.getString(1);	//Title
                String p 	= cursor.getString(2);	//Package_id
                M = new TranslationMode(m,t);
                M.setPackage(p);
        }
        db.close();
        return M;    	
    }
    
    //Get Package from modes 
    public LanguagePackage getPackage(String Package_ID) {
    	OpenHelper openHelper = new OpenHelper(this.context);
	    SQLiteDatabase db = openHelper.getReadableDatabase();
    	        
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PACKAGE + " WHERE "+KEY_PACKAGE_ID+" = '"+Package_ID+"'";
    	
        Cursor cursor = db.rawQuery(selectQuery, null);
        LanguagePackage L = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
           
                String l 	= cursor.getString(0);	//Id
                String v 	= cursor.getString(1);	//Last modified date
                L = new LanguagePackage(l);
                L.setLastDate(v);
          
        }
        db.close();
        return L;    	
    }
    
    // Get mode Count
    public int getModesCount() {
    	String countQuery = "SELECT  * FROM " + TABLE_MODE;
	      OpenHelper openHelper = new OpenHelper(this.context);
	      SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close(); 
        return cursor.getCount();    	
    }
    
    
    // Deleting a package
    public void deletePackage(String PackageID) {
    	OpenHelper openHelper = new OpenHelper(this.context);
    	SQLiteDatabase db = openHelper.getWritableDatabase();
    	db.delete(TABLE_PACKAGE, KEY_PACKAGE_ID + " = ?",new String[] { String.valueOf(PackageID) });
    	db.delete(TABLE_MODE, KEY_MODE_PACKAGE + " = ?",new String[] { String.valueOf(PackageID) });
    	db.close();
    }
    
    
    //Updating DB entries
    public void updateDB(){
    	Log.i(TAG, "Update database");
    	OpenHelper openHelper = new OpenHelper(this.context);
    	SQLiteDatabase db = openHelper.getWritableDatabase();
    	db.delete(TABLE_PACKAGE,null, null);
    	db.delete(TABLE_MODE,null,null);     	
    	File JARDIR = new File(AppPreference.JAR_DIR());
    	File[] files = JARDIR.listFiles();
    	for(int i=0;i<files.length;i++){
    		Log.i(TAG,files[i].getAbsolutePath());
    		ConfigManager config;
			try {
				config = new ConfigManager(files[i].getAbsolutePath(),files[i].getName());
				config.setModifiedDate(files[i].lastModified()+"");
				LanguagePackage pack = new LanguagePackage(config);
				addLanuagepair(pack);
			} catch (Exception e) {
				e.printStackTrace();
			}	
    	}
    	
    	db.close();
    }
    
    
    private static class OpenHelper extends SQLiteOpenHelper {
    	 
        OpenHelper(Context context) {
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
   
        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {	
            db.execSQL("CREATE TABLE " + TABLE_MODE + "("+KEY_MODE_ID+" TEXT PRIMARY KEY, "+KEY_MODE_TITLE+" TEXT,"+KEY_MODE_PACKAGE+" TEXT)");
            db.execSQL("CREATE TABLE " + TABLE_PACKAGE + "("+KEY_PACKAGE_ID+" TEXT PRIMARY KEY, "+KEY_PACKAGE_LASTMODIFIED+" TEXT)");
        }
     
        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKAGE);
     
            // Create tables again
            onCreate(db);
        }
     }
}