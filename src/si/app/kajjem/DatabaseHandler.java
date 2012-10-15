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
    private static final String DATABASE_NAME = "vsebina";
	
    
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
		// TODO Auto-generated method stub
		
	}

	
	
	/**
	 * Method onUpgrade takes care of database upgrades.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
