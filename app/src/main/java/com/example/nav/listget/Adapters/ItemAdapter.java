package com.example.nav.listget.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nav.listget.Activities.CheckableLinearLayout;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ItemObject;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<ItemObject> {
    private LayoutInflater layoutInflater_;

	/*
    public TaskAdapter(Context context, int textViewResourceId, List<ItemObject> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	 */

    public ItemAdapter(Context context, List<ItemObject> objects) {
        super(context, R.layout.adapter_item, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        ItemObject item = (ItemObject) getItem(position);
        if (row == null) {
            row = layoutInflater_.inflate(R.layout.adapter_item, parent, false);
            holder = new ViewHolder();
            holder.checkbox = (CheckableLinearLayout) row.findViewById(R.id.LinearLayout);
            holder.textPos = (TextView) holder.checkbox.getChildAt(1);
            if (holder.textPos == null) {
                Log.d("null", "textPos");
            }
            holder.textCircle = (TextView) row.findViewById(R.id.circle);
            holder.textView = (TextView) row.findViewById(R.id.text1);
            holder.paint = holder.textView.getPaint();

            holder.checkbox.setOnClickListener(new onCheckClicked());

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        //String colorS=toString(item.getColor(), row);
        //holder.textCircle.setTextColor(Color.parseColor(colorS));
        if (item.getChecked() != 0)

        {
            setStyleDone(holder.checkbox, holder.textCircle, holder.textView, holder.paint, row, item);
        } else

        {
            setStyleNotDone(holder.checkbox, holder.textCircle, holder.textView, holder.paint, row, item);
        }

        holder.textPos.setText("" + position);

        return (row);
    }

    public class onCheckClicked implements OnClickListener {
        public void onClick(View v) {
            LinearLayout row = (LinearLayout) v.getParent();
            TextView textView = null;
            CheckableLinearLayout checkbox = null;
            TextView textCircle = null;
            for (int i = 0; i < row.getChildCount(); i++) {
                if (row.getChildAt(i).getId() == R.id.text1) {
                    textView = (TextView) row.getChildAt(i);
                } else if (row.getChildAt(i).getId() == R.id.LinearLayout) {
                    checkbox = (CheckableLinearLayout) row.getChildAt(i);
                } else if (row.getChildAt(i).getId() == R.id.circle) {
                    textCircle = (TextView) row.getChildAt(i);
                }
            }
            TextPaint paint = textView.getPaint();
            TextView textPos = (TextView) checkbox.getChildAt(1);
            int position = Integer.parseInt(textPos.getText().toString());
            ItemObject item = (ItemObject) getItem(position);
            if (checkbox.isChecked()) {
                item.setChecked(0);
                setStyleNotDone(checkbox, textCircle, textView,  paint, row, item);
            } else {
                item.setChecked(1);
                setStyleDone(checkbox, textCircle, textView, paint, row, item);
            }
        }

    }


    public void setStyleDone(CheckableLinearLayout checkbox, TextView textCircle, TextView textView,  Paint paint, View row, ItemObject item) {
        checkbox.setChecked(true);
        textView.setText(Html.fromHtml("<font color='#aaa9a9'>" + item.getItem() + "</font>"));
        //色
        /*checkbox.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.check_done).substring(3)));
        drag.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.text_done).substring(3)));
        textView.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.text_done).substring(3)));
        textCircle.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.text_done).substring(3)));
        */
        paint.setFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        paint.setAntiAlias(true);
    }

    public void setStyleNotDone(CheckableLinearLayout checkbox, TextView textCircle, TextView textView,  Paint paint, View row, ItemObject item) {
        checkbox.setChecked(false);
        textView.setText(Html.fromHtml(item.getItem()));
        /*checkbox.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.check_notDone).substring(3)));
        drag.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.white).substring(3)));
        textView.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.white).substring(3)));
        textCircle.setBackgroundColor(Color.parseColor("#"+row.getResources().getString(R.color.white).substring(3)));
        */
        paint.setFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    /*
    // color
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
                str = "";
                break;
            default:
                str = view.getResources().getString(R.color.black).substring(3);
                break;
        }
        return "#"+str;
    }
    */

    public static class ViewHolder {
        CheckableLinearLayout checkbox;
        TextView textCircle;
        TextView textView;
        TextView textPos;
        TextPaint paint;
    }


}