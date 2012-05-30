package me.pdthx.Adapters;

import java.util.LinkedHashMap;
import java.util.Map;

import me.pdthx.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class FriendListAdapter extends BaseAdapter 
{

	public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	public final ArrayAdapter<String> headers;
	public final static int TYPE_SECTION_HEADER = 0;

	public FriendListAdapter(Context context)
	{
		headers =  new ArrayAdapter<String>(context, R.layout.friends_header);
	}
	public void addFriend(String name, Adapter adapter)
	{
		this.headers.add(name);
		this.sections.put(name, adapter);
	}
	public Object getItem(int position)
	{
		for (Object section : this.sections.keySet())
			{
				Adapter adapter = sections.get(section);
				int size = adapter.getCount() + 1;

				// check if position inside this section
				if (position == 0) return section;
				if (position < size) return adapter.getItem(position - 1);

				// otherwise jump into next section
				position -= size;
			}
		return null;
	}

	public int getCount()
	{
		// total together all sections, plus one for each section header
		int total = 0;
		for (Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}
	@Override
	public long getItemId(int position)
		{
			return position;
		}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
		{
			int sectionnum = 0;
			for (Object section : this.sections.keySet())
				{
					Adapter adapter = sections.get(section);
					int size = adapter.getCount() + 1;

					// check if position inside this section
					if (position == 0) return headers.getView(sectionnum, convertView, parent);
					if (position < size) return adapter.getView(position - 1, convertView, parent);

					// otherwise jump into next section
					position -= size;
					sectionnum++;
				}
			return null;
		}

	public boolean areAllItemsSelectable()
		{
			return false;
		}
	@Override
	public boolean isEnabled(int position)
		{
			return (getItemViewType(position) != TYPE_SECTION_HEADER);
		}
}
