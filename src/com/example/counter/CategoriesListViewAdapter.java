package com.example.counter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CategoriesListViewAdapter extends BaseAdapter {
	private ArrayList<Category> list = new ArrayList<Category>();
	private Context context;

	public CategoriesListViewAdapter(ArrayList<Category> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getId();
	}

	@Override
	public View getView(final int position, View convert_view, final ViewGroup parent)
	{
		View view = convert_view;

		//if convert_view is null we inflate it
		if(view==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.category_list_row,null);
		}

		Category cat = (Category) getItem(position);

		if(cat != null)
		{
			EditText txt_name = (EditText) view.findViewById(R.id.txt_row_category_name);
			Button btn_edit = (Button) view.findViewById(R.id.btn_row_category_edit);
			Button btn_delete = (Button) view.findViewById(R.id.btn_row_category_delete);
			Button btn_reset = (Button) view.findViewById(R.id.btn_row_category_reset);

			txt_name.setText(cat.getName());

			//OnClick edit
			btn_edit.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					editName(v);
				}
			});

			//OnClick delete 
			btn_delete.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					deleteRow(v,position);
				}
			});

			//OnClick reset 
			btn_reset.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					resetCategory(v,position);
				}
			});
		}



		return view;
	}

	public void editName(View v)
	{
		Debug.log("Edit name");
		View row_view = (View) v.getParent();

		EditText txt_name = (EditText) row_view.findViewById(R.id.txt_row_category_name);

		//if not yet editable
		if(!txt_name.isEnabled())
		{
			txt_name.setEnabled(true);
			v.setBackgroundResource(android.R.drawable.ic_menu_save);
		}else{
			txt_name.setEnabled(false);
			v.setBackgroundResource(android.R.drawable.ic_menu_edit);
			//Todo: save name
		}

		notifyDataSetChanged();
	}

	public void deleteRow(View v, int position)
	{
		Debug.log("Delete Row");
		list.remove(position);
		//Todo: remove item from DB
		notifyDataSetChanged();
	}

	public void resetCategory(View v, int position)
	{
		Debug.log("Reset Category");
		//Todo: reset counter for this category
		notifyDataSetChanged();
	}


	public View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition ) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}
}