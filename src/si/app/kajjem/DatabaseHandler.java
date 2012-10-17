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
		db.execSQL("CREATE TABLE snov ( " +
				   "id_snov INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				   "naziv_snovi VARCHAR(50) NOT NULL, " +
				   "opis VARCHAR(500) NOT NULL, " +
				   "vpliv_zdravje INTEGER NOT NULL)");
		db.execSQL("INSERT INTO snov (naziv_snovi, opis, vpliv_zdravje) VALUES ('snov 1', 'Prva lokalna snov', 200)");
		db.execSQL("INSERT INTO snov (naziv_snovi, opis, vpliv_zdravje) VALUES ('snov 2', 'Druga lokalna snov', 133)");
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
