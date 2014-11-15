package com.example.nav.listget.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ListObject;

import java.util.List;

public class ListAdapter2 extends ArrayAdapter<ListObject> {
    private LayoutInflater layoutInflater_;

    public ListAdapter2(Context context, List<ListObject> objects) {
        super(context, R.layout.adapter_list, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        ListObject item = (ListObject) getItem(position);
        if (null == row) {
            holder = new ViewHolder();
            row = layoutInflater_.inflate(R.layout.adapter_list, parent, false);
            //disable drag_sort listview
            /*if(item.getColor() <1){
                row=layoutInflater_.inflate(R.layout.object_list, parent, false);
            }*/
            holder.textView = (TextView) row.findViewById(R.id.text);
            holder.edit_button = (ImageView) row.findViewById(R.id.edit_button);
            holder.edit_button.setOnClickListener(new onCheckClicked());


            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        //String colorS=toString(item.getColor(), row);
        holder.textView.setText(Html.fromHtml(item.getCategory() + " (" + item.getNumTask() + ") "));
        return (row);
    }

    public class onCheckClicked implements View.OnClickListener {
        public void onClick(View v) {
           // Log.d("yaaaay", "editimg clicked");
            /*
            LinearLayout row = (LinearLayout) v.getParent();

            TextView textView = null;
            ImageView edit = null;
            for (int i = 0; i < row.getChildCount(); i++) {
                if (row.getChildAt(i).getId() == R.id.text) {
                    textView = (TextView) row.getChildAt(i);
                } else if (row.getChildAt(i).getId() == R.id.edit_button) {
                    edit = (ImageView) row.getChildAt(i);
                }
            }

            TextView textPos = (TextView) ((edit != null) ? edit.getChildAt(1) : null);
            int position = Integer.parseInt(textPos.getText().toString());
            ListObject item = (ListObject) getItem(position);

            final MyDialogFragment dialog = new MyDialogFragment();
            dialog.setCategory(item);
            dialog.setOnCloseListener(new MyDialogFragment.OnMyClickListener() {
                @Override
                public void onClose() {
                    setAdapterForCatList();
                }
                //cancelボタンでdialogを閉じた時
                public void onCancelClose(){
                }
            });
            dialog.show(getActivity().getSupportFragmentManager(), "");
            */


        }

    }

    /*
    @Override
    public boolean isEnabled(int position) {
        ListObject item = (ListObject) getItem(position);
        if (item.getColor() > 0 || item.getCategory().equals("Add List")) {
            return true;
        } else {
            return false;
        }
    }
    */
/*
    // change color of the circle depends on int color
	public String toString(int color,View view){
		String str;
		switch(color){
		case 1:	
			str = view.getResources().getString(R.color.cat1).substring(3);
			break;
		case 2:	
			str = view.getResources().getString(R.color.cat2).substring(3);
			break;
		case 3:	
			str = view.getResources().getString(R.color.cat3).substring(3);
			break;
		case 4:	
			str = view.getResources().getString(R.color.cat4).substring(3);
			break;
		case 5:	
			str = view.getResources().getString(R.color.cat5).substring(3);
			break;
		case 6:	
			str = view.getResources().getString(R.color.cat6).substring(3);
			break;
		case 7:
			str = view.getResources().getString(R.color.cat7).substring(3);
			break;
		case -1:	
			str = view.getResources().getString(R.color.cat7).substring(3);
			break;
		default:	
			str = view.getResources().getString(R.color.black).substring(3);
			break;
		}
		str = "<font color='#"+str+"'>●</font>";
		if(color == -1){
			str = "◯";
		}
		return str;
	}
*/


    public static class ViewHolder {
        TextView textView;
        ImageView edit_button;
    }
}