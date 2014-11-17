package com.example.nav.listget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nav.listget.parcelable.ListObject;


public class MyDialogFragment extends DialogFragment {

	private ListObject selectedCat = null;
	private OnMyClickListener listener = null;
	private EditText editText = null;
	//GridViewAdapter adapter = null;

	public interface OnMyClickListener {
		public void onClose();
		public void onCancelClose();
	}
	
	private String[] marks ={"●","■"	};

	public void setOnCloseListener(OnMyClickListener listener) {
		this.listener = listener;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.fragment_my_dialog);

		if(selectedCat.getCategory().equals("Add List")){
			Button b_delete = (Button) dialog.findViewById(R.id.b_delete);
			b_delete.setVisibility(View.GONE);
			selectedCat.setCategory("");
		    //selectedCat.setColor(1);
			insertButton(dialog);

		}else{
			updateButton(dialog);
			deleteButton(dialog);
		}

		cancelButton(dialog);
		//色用gridview
        /*
		GridView gridView1 = (GridView) dialog.findViewById(R.id.gridView1);
		gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {			
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				GridView gridView = (GridView)parent;
				ListObject cBefore = (ListObject)gridView.getItemAtPosition(selectedCat.getColor()-1);
				ListObject colorS = (ListObject)gridView.getItemAtPosition(position);
				cBefore.setCategory(marks[1]);
				colorS.setCategory(marks[0]);
				selectedCat.setColor(colorS.getColor());
				adapter.notifyDataSetChanged();
			}
		});
		createAdapter();
		gridView1.setAdapter(adapter);
		*/
		editText = (EditText)dialog.findViewById(R.id.category);
		editText.setText(selectedCat.getCategory());

		return dialog;
	}
	/**
	 * create adapter
	 * @return adapter
	 */
    /*
	private void createAdapter(){
		List<ListObject> objects = new ArrayList<ListObject>();
		for(int i = 1; i<=7;i++){
			if(selectedCat.getColor()==i){
				objects.add(new ListObject(0,marks[0],i));
			}else{
				objects.add(new ListObject(0,marks[1],i));
			}
		}
		adapter = new GridViewAdapter(getActivity(),0, objects);
	}
	*/

	/**
	 * insert to database
	 * @param dialog
	 */
	private void insertButton(Dialog dialog){
		Button b_save = (Button) dialog.findViewById(R.id.b_save);
		b_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!(editText.getText().toString().equals(""))){
					selectedCat.setCategory(editText.getText().toString());
					DBHelper helper = new DBHelper(getActivity());
					SQLiteDatabase db = helper.getReadableDatabase();
					ContentValues cv = new ContentValues();
					cv.put("category", selectedCat.getCategory() );
					//cv.put("color", selectedCat.getColor() );
					db.insert("categories", null, cv);
					db.close();
					listener.onClose();
					dismiss();
				}
			}
		});
	}
	/**
	 * update database
	 * @param dialog
	 */
	private void updateButton(Dialog dialog){
		Button b_save = (Button) dialog.findViewById(R.id.b_save);
		b_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!(editText.getText().toString().equals(""))){
					selectedCat.setCategory(editText.getText().toString());

					DBHelper helper = new DBHelper(getActivity());
					SQLiteDatabase db = helper.getReadableDatabase();
					ContentValues cv = new ContentValues();
					cv.put("category", selectedCat.getCategory() );
					//cv.put("color", selectedCat.getColor() );
					db.update("categories", cv, "categoryId = "+selectedCat.getCategoryId(), null);
					db.close();
					//onCloselistenerを呼ぶ
					listener.onClose();
					dismiss();
				}
			}
		});
	}
	/**
	 * delete from database
	 * @param dialog
	 */
	private void deleteButton(Dialog dialog){
		Button b_delete = (Button) dialog.findViewById(R.id.b_delete);
		b_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DBHelper helper = new DBHelper(getActivity());
				SQLiteDatabase db = helper.getReadableDatabase();
				db.delete("categories", "categoryId = "+selectedCat.getCategoryId(), null);
				db.close();
				listener.onClose();
				dismiss();
			}
		});

	}
	/**
	 * cancel button listener (x)
	 * @param dialog
	 */
	private void cancelButton(Dialog dialog){
		TextView b_cancel = (TextView) dialog.findViewById(R.id.b_cancel);
		b_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onCancelClose();
				dismiss();
			}
		});
	}

	/**
	 * set cat to selectedCat
	 * @param c
	 */
	public void setCategory(ListObject c) {
		selectedCat = c;
	}
}