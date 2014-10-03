package com.example.nav.listget;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<ItemObject> {
    private LayoutInflater layoutInflater_;


    public ItemAdapter(Context context, List<ItemObject> objects) {
        super(context, R.layout.object_item, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        ItemObject item = (ItemObject) getItem(position);
        if (row == null) {
            row = layoutInflater_.inflate(R.layout.object_item, parent, false);
            holder = new ViewHolder();
            holder.checkbox = (CheckableLinearLayout) row.findViewById(R.id.LinearLayout);
            holder.textPos = (TextView) holder.checkbox.getChildAt(1);
            holder.textView = (TextView) row.findViewById(R.id.text1);
            holder.paint = holder.textView.getPaint();
            holder.checkbox.setOnClickListener(new onCheckClicked());
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if (item.getChecked() != 0) {
            setStyleChecked(holder.checkbox, holder.textView,holder.paint, row, item);
        } else {
            setStyleNotChecked(holder.checkbox, holder.textView, holder.paint, row, item);
        }
        holder.textPos.setText("" + position);

        return (row);
    }

    public class onCheckClicked implements OnClickListener{
        public void onClick(View v){
            LinearLayout row = (LinearLayout) v.getParent();
            TextView textView = null;
            CheckableLinearLayout checkbox = null;
            for (int i = 0; i < row.getChildCount(); i++) {
                if (row.getChildAt(i).getId() == R.id.text1) {
                    textView = (TextView) row.getChildAt(i);
                } else if (row.getChildAt(i).getId() == R.id.LinearLayout) {
                    checkbox = (CheckableLinearLayout) row.getChildAt(i);
                }
            }
            TextPaint paint = textView.getPaint();
            TextView textPos = (TextView) checkbox.getChildAt(1);
            int position = Integer.parseInt(textPos.getText().toString());
            ItemObject item = (ItemObject) getItem(position);
            if (checkbox.isChecked()) {
                item.setChecked(0);
                setStyleNotChecked(checkbox, textView, paint, row, item);
            } else {
                item.setChecked(1);
                setStyleChecked(checkbox, textView,  paint, row, item);
            }
        }
    }


    public void setStyleChecked(CheckableLinearLayout checkbox, TextView textView, Paint paint, View row, ItemObject item) {
        checkbox.setChecked(true);
        textView.setText(Html.fromHtml("<font color='#aaa9a9'>" + item.getItem() + "</font>"));
        //checkbox.setBackgroundColor(Color.parseColor("#" + row.getResources().getString(R.color.check_checked).substring(3)));
        //textView.setBackgroundColor(Color.parseColor(textChecked));
        //textCircle.setBackgroundColor(Color.parseColor(textChecked));
        paint.setFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        paint.setAntiAlias(true);
    }

    public void setStyleNotChecked(CheckableLinearLayout checkbox, TextView textView, Paint paint, View row, ItemObject item) {
        checkbox.setChecked(false);
        textView.setText(Html.fromHtml(item.getItem()));
        //checkbox.setBackgroundColor(Color.parseColor("#" + row.getResources().getString(R.color.check_notChecked).substring(3)));
        //textView.setBackgroundColor(Color.parseColor(textNotChecked));
        //textCircle.setBackgroundColor(Color.parseColor(textNotChecked));
        paint.setFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }


    public static class ViewHolder {
        CheckableLinearLayout checkbox;
        TextView textView;
        TextView textPos;
        TextPaint paint;
    }


}