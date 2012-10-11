package si.app.kajjem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;




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

    private ArrayList<String> contents_list; /* Items are stored in this ArrayList variable. */
    private ArrayAdapter<String> contents_adapter; /* ArrayAdapter for setting items to ListView. */

    
    
	/**
	 * Function onCreate is called when the application is started.
	 * 
	 * This method overrides existing method in Activity class.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        lv = (ListView)this.findViewById(R.id.content_list);
        
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

    }
    
    
    
    /**
     * Method onActivityResult is called after the picture has been taken or tha camera function has been canceled.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == CAMERA_REQUEST) {
        	if (resultCode == RESULT_OK) {
        		this.contents_list = new ArrayList<String>();
        		this.contents_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contents_list);
        		this.lv.setAdapter(this.contents_adapter);
        		
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
		new PerformOCR(view).execute();
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
		
		
		
		/**
		 * Constructor receives picture as a View parameter and creates an instance of JsonObject. 
		 * @param view - Picture passed to the class.
		 */
		public PerformOCR(View view) {
			sendDataJson = new JsonObject();
			this.view = view;
		}
		
		
		
		/**
		 * Method doInBackground is function, that takes care of the operation we want this class to perform.
		 * 
		 * This method overrides existing method in AsyncTask class.
		 */
		@Override
		protected JsonObject doInBackground(String... params) {
			
			//OCR ocr = new OCR();
			/*
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			this.photo.compress(Bitmap.CompressFormat.PNG, 100, output);
			byte[] bytes = output.toByteArray();
			byte[] base64Image = Base64.encode(bytes, Base64.DEFAULT);
			*/
			//jsonImage.put("image", base64Image);
			//JsonPrimitive jsp = new JsonPrimitive(Base64.encodeToString(bytes, Base64.NO_WRAP));
			
			
			/*
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOut = null;
			File file = new File(path, "tmp.png");
			
			
			fOut = new FileOutputStream(file);
			this.photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();

			MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
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
	protected class PerformQuery extends AsyncTask<String, Void, List<Item>> {

		
		/**
		 * Method doInBackground is function, that takes care of the operation we want this class to perform.
		 * 
		 * This method overrides existing method in AsyncTask class.
		 */
		@Override
		protected List<Item> doInBackground(String... params) {
			String url = params[0];
			String jsonData = params[1];
			
			List<Item> items = null;
			
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
			
				items = new ArrayList<Item>();
				
				/* Goes through all received items and parses them into List items. */
				while(iterator.hasNext()){
					JsonElement json2 = (JsonElement)iterator.next();
					Gson gson = new Gson();
					Item item = gson.fromJson(json2, Item.class);
					items.add(item);
				}
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return items;
		}
		

		
		/**
		 * Method onPostExecute is function, that takes care of the operation we want run after the main operation of this class has been completed.
		 * 
		 * This method overrides existing method in AsyncTask class.
		 */
		protected void onPostExecute(List<Item> items) {
			if (items == null) {
				return;
			}
			
			for (int i = 0 ; i < items.size() ; i++) {
				contents_list.add(items.get(i).itemName + "\n" + items.get(i).description + "\n" + items.get(i).healthImpact);
                contents_adapter.notifyDataSetChanged();
			}
        }
	}
	
	
	
	
	
	
	/**
	 * Class used to parse the data from string into a list.
	 * @author Matevž Pogačar
	 *
	 */
	public class Item {
		private String itemName;
		private String description;
		private int healthImpact;
	}
	
}


