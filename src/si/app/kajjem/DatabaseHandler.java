package si.app.kajjem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




/**
 * This class takes care of SQLite accesses.
 * 
 * Class extends SQLiteOpenHelper class.
 * 
 * @author Matevž Pogačar
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	
	
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kajjem.db";
    
    
    
    /**
     * Class constructor.
     * 
     * @param context - Context of the application.
     */
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	
	
	/**
	 * Method onCreate takes care of tables creation in databes.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE content ( " +
				   "id_content INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				   "content_name VARCHAR(50) NOT NULL, " +
				   "content_description VARCHAR(500) NOT NULL, " +
				   "health_impact INTEGER NOT NULL, " +
				   "data_version INTEGER NOT NULL)");
		db.execSQL("INSERT INTO content (content_name, content_description, health_impact, data_version) VALUES ('snov 1', 'Prva lokalna snov', 200, 1)");
		db.execSQL("INSERT INTO content (content_name, content_description, health_impact, data_version) VALUES ('snov 2', 'Druga lokalna snov', 133, 1)");
	}
	


	/**
	 * Method onUpgrade takes care of database upgrades.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
		    switch (oldVersion) {
		        case 1:
		            // IN CASE OF DATABASE UPGRADE.
		    }
		}
	}

}
