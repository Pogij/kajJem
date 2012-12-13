package si.app.kajjem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import si.app.data.classes.Item;
import si.custom.widgets.ColorfulAdapter;
import si.custom.widgets.SemiClosedSlidingDrawer;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.tesseract.android.TessBaseAPI;



/**
 * Class represents the main activity. Takes care of main application window.
 * 
 * Class extends Activity class.
 * 
 * Class implements OnClickListener. Click listener function is called when button b_performQuery is pressed.
 * 
 * @author Matevž Pogačar
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	
	private static final int CAMERA_REQUEST = 1;	//random number >= 0 which is returned when activity exits. 
	private Bitmap photo;
	private ImageView iv;
	private ListView lv;
	private SemiClosedSlidingDrawer scsd;

	private ArrayList<Item> items; /* Items are stored in this ArrayList variable. */
    private ColorfulAdapter items_adapter; /* ArrayAdapter for setting items to ListView. */
    
    private DatabaseHandler dbh;

    
    
	/**
	 * Function onCreate is called when the application is started.
	 * 
	 * This method overrides existing method in Activity class.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.items = null;
        
        this.lv = (ListView)this.findViewById(R.id.content_list);
        this.scsd = (SemiClosedSlidingDrawer) findViewById(R.id.list_sliding_drawer);
        
        this.photo = null;
        this.iv = (ImageView)this.findViewById(R.id.iv);
        
        /* Set up button for starting the activity. */
        final Button b_takePicture = (Button) this.findViewById(R.id.b_takePicture);
        b_takePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        
        /* Set up button for communication with server. */
        final Button b_performQuery = (Button) this.findViewById(R.id.b_performQuery);
        b_performQuery.setOnClickListener(this);
        b_performQuery.setEnabled(false);
        
        /* Prepares all necessary variables to fill listView with new data. */
        this.items = new ArrayList<Item>();
		this.items_adapter = new ColorfulAdapter(this, R.layout.listview_row, items);
		this.lv.setAdapter(this.items_adapter);
        
		/* Prepares database access object. */
        this.dbh = new DatabaseHandler(this);
    }
    
    
    
    /**
     * Method onActivityResult is called after the picture has been taken or tha camera function has been canceled.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == CAMERA_REQUEST) {
        	if (resultCode == RESULT_OK) {
        		
        		this.photo = (Bitmap) data.getExtras().get("data"); 
	            iv.setImageBitmap(photo);
        		
	            final Button b_performQuery = (Button) this.findViewById(R.id.b_performQuery);
	            b_performQuery.setEnabled(true);
        	}
        }
    }

    
    
    /**
     * This method overrides existing method in Activity class.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    

    
    /**
     * This method takes care of click function (it is present in this class because of 'implements' statement in the class definition).
     */
	public void onClick(View view) {
		new PerformOCR(view, this.photo).execute();
	}
	
	
	
	
	
	
	/**
	 * Inner class PerformOCR is asynchronous operation. It is created after user has confirmed the photo taken and wants for the app to parse the data.
	 *  
	 * Class extends AsyncTask which takes care of creating new Thread.
	 * 
	 * @author Matevž Pogačar
	 *
	 */
	protected class PerformOCR extends AsyncTask<String, Void, JsonObject> {

		
		JsonObject sendDataJson;
		View view;
		Bitmap photo;
		
		
		/**
		 * Constructor receives picture as a View parameter and creates an instance of JsonObject. 
		 * @param view - Picture passed to the class.
		 */
		public PerformOCR(View view, Bitmap photo) {
			sendDataJson = new JsonObject();
			this.view = view;
			this.photo = photo;
		}
		
		
		
		/**
		 * Method doInBackground is function, that takes care of the operation we want this class to perform.
		 * 
		 * This method overrides existing method in AsyncTask class.
		 */
		@Override
		protected JsonObject doInBackground(String... params) {
			
			String trainedDataDirectory ="/mnt/sdcard/";
			TessBaseAPI tess_api = new TessBaseAPI();
			tess_api.init(trainedDataDirectory, "eng");
			tess_api.setImage(this.photo);
			String text = tess_api.getUTF8Text();

			tess_api.end();
			
			SQLiteDatabase qdb = dbh.getReadableDatabase();
			Cursor result = qdb.rawQuery("SELECT content_name, content_description, health_impact, data_version FROM content ORDER BY health_impact DESC", null);
			
			items = new ArrayList<Item>();
			
			items.add(new Item("OCR", text, 255, 1));
			
			if (result.moveToFirst() == true) {
			   do {
				   String name = result.getString(result.getColumnIndex("content_name"));
				   String description = result.getString(result.getColumnIndex("content_description"));
				   int healthImpact = result.getInt(result.getColumnIndex("health_impact"));
				   int dataVersion = result.getInt(result.getColumnIndex("data_version"));
				   items.add(new Item(name, description, healthImpact, dataVersion));
			   } while(result.moveToNext() == true);
			}
			result.close();

			
			/*
			   GET THE REST OF THE ITEMS AND QUERY THE SERVER FOR THE DATA.
			*/
			
			
			sendDataJson.addProperty("webservice", "Get product contents");
			return sendDataJson;
		}
		
		
		
		/**
		 * Method onPostExecute is function, that takes care of the operation we want run after the main operation of this class has been completed.
		 * 
		 * This method overrides existing method in AsyncTask class.
		 */
		protected void onPostExecute(JsonObject sendDataJson) {
			String sendDataJsonString = sendDataJson.toString();
			new PerformQuery().execute("http://193.95.242.213:8192/kajJemWebService/content", sendDataJsonString);
			
			/* Here we present all data we got from database. */
			items_adapter.clear();
			items_adapter.addAll(items);
			items_adapter.notifyDataSetChanged();
			/* Last command is necessary to show contents of sliding drawer right away. */
			scsd.closeDrawer();
			this.cancel(true);
        }
	}
	
	
	
	
	
	
	/**
	 * Inner class PerformQuery is asynchronous operation. It is created when main application wants to connect to server via Internet.
	 * 
	 * Class extends AsyncTask which takes care of creating new Thread.
	 * 
	 * @author Matevž Pogačar
	 *
	 */
	protected class PerformQuery extends AsyncTask<String, Void, Void> {

		
		/**
		 * Method doInBackground is function, that takes care of the operation we want this class to perform.
		 * 
		 * This method overrides existing method in AsyncTask class.
		 */
		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];
			String jsonData = params[1];
			
			HttpPost post = new HttpPost();
			post.setHeader("Accept", "application/json; charset=UTF-8");
		    post.setHeader("Content-type", "application/json; charset=UTF-8");
		    post.setHeader("dataType", "json");

			try {
				post.setURI(new URI(url));
				HttpClient client = new DefaultHttpClient();
				
		        StringEntity entityPostJson = new StringEntity(jsonData);  
                entityPostJson.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(entityPostJson);

                /* Start communication with server. Sends json data received as a second parameter (params[1]) and receives json array. */
				HttpResponse response = client.execute(post);
				
				/* Prepares data for iteration that will parse data from string into a List. */
				String receivedResult = EntityUtils.toString(response.getEntity());
				JsonElement jElement = new JsonParser().parse(receivedResult);
				JsonArray jArray = jElement.getAsJsonArray();	 
				Iterator<JsonElement> iterator = jArray.iterator();
				
				/* Goes through all received items and parses them into List items. */
				/* It also writes new data into database. */
				//SQLiteDatabase qdb = dbh.getWritableDatabase();
				items = new ArrayList<Item>();
				while(iterator.hasNext()){
					JsonElement json2 = (JsonElement)iterator.next();
					Gson gson = new Gson();
					Item item = gson.fromJson(json2, Item.class);
					items.add(item);
					
					/* Database insert. */
					/*ContentValues insertValues = new ContentValues();
					insertValues.put("naziv_snovi",item.getItemName());
					insertValues.put("opis",item.getDescription());
					insertValues.put("vpliv_zdravje",item.getHealthImpact());
					qdb.insert("snov", null, insertValues);*/
				}
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		

		
		/**
		 * Method onPostExecute is function, that takes care of the operation we want run after the main operation of this class has been completed.
		 * 
		 * This method overrides existing method in AsyncTask class.
		 */
		protected void onPostExecute(Void none) {
			
			/* Here the application goes through all Internet received items and places them into position according to healthImpact value. */
			for (int i = 0 ; i < items.size() ; i++) {
				int j = 0;
				for (j = 0 ; j < items_adapter.getCount() ; j++) {
					if (items.get(i).getHealthImpact() > items_adapter.getItem(j).getHealthImpact()) {
						break;
					}
				}
				
				items_adapter.insert(items.get(i), j);
				items_adapter.notifyDataSetChanged();
			}
			this.cancel(true);
        }
	}
}


