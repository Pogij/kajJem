package si.custom.widgets;

import java.util.ArrayList;

import si.app.data.classes.Item;
import si.app.kajjem.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;




/**
 * Adapter with which we enable setting the background of individual item in list.
 * 
 * Class extends ArrayAdapter.
 *  
 * @author Matevž Pogačar
 *
 */
public class ColorfulAdapter extends ArrayAdapter<Item> {

	
	private final Context context;
	private final ArrayList<Item> items;
	
	
	
	/**
	 * Constructor of this class.
	 * 
	 * Calls the superclass constructor and sets the data.
	 * 
	 * @param context - Application context.
	 * @param layout - Layout for this adapter.
	 * @param items - Data.
	 */
	public ColorfulAdapter(Context context, int layout, ArrayList<Item> items) {
		super(context, layout, items);
		this.context = context;
		this.items = items;
	}
	
	
	
	/**
	 * Fuction prepares the data to be presented.
	 * 
	 * Overrides the ArrayAdapter's method.
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.listview_row, parent, false);

		//ImageView imageView = (ImageView) rowView.findViewById(R.id.row_image);
		TextView itemName = (TextView) rowView.findViewById(R.id.row_text);
		TextView description = (TextView) rowView.findViewById(R.id.row_description);
		
		Item item = items.get(position);
		itemName.setText(item.getItemName());
		description.setText(item.getDescription());

		if (item.getHealthImpact() > 5) {
			rowView.setBackgroundColor(Color.BLUE);
		} else {
			rowView.setBackgroundColor(Color.RED);
		}
 
		return rowView;
	}
}
