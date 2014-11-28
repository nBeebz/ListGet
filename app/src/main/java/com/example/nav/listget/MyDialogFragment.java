package com.example.nav.listget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nav.listget.Interfaces.MongoInterface;
import com.example.nav.listget.parcelable.ListObject;


public class MyDialogFragment extends DialogFragment implements MongoInterface {

	private ListObject selectedList = null;
	private OnMyClickListener listener = null;
	private EditText editText = null;
    private MongoInterface m = null;

    @Override
    public void processResult(String result) {

    }

    public interface OnMyClickListener {
		public void onDelete(ListObject removedList);
		public void onSave(ListObject addedList);
	}


	public void setOnCloseListener(OnMyClickListener listener) {
		this.listener = listener;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
        m = this;
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.fragment_my_dialog);

        editText = (EditText)dialog.findViewById(R.id.category);

        if(selectedList.getName().equals("")){
			Button b_delete = (Button) dialog.findViewById(R.id.b_delete);
		    b_delete.setVisibility(View.GONE);
			insertButton(dialog);
		}else{
            editText.setText(selectedList.getName());
            updateButton(dialog);
			deleteButton(dialog);
    	}

        cancelButton(dialog);

		return dialog;
	}
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
                    selectedList.setName(editText.getText().toString());
                    Mongo.getMongo(m).post( Mongo.COLL_LISTS, selectedList.getJSON() );
                    listener.onSave(selectedList);
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
                    selectedList.setName(editText.getText().toString());
                    Mongo.getMongo(m).putById(Mongo.COLL_LISTS, selectedList.getId(), Mongo.KEY_NAME, selectedList.getName());

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
                Mongo.getMongo(m).delete(Mongo.COLL_LISTS,selectedList.getId());
				listener.onDelete(selectedList);
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
				dismiss();
			}
		});
	}

	/**
	 * set cat to selectedCat called before this fragment is called
	 * @param c
	 */
	public void setCategory(ListObject c) {
		selectedList = c;
	}
}