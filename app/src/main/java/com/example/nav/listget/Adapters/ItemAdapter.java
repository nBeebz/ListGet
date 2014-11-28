package com.example.nav.listget.Adapters;

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

import com.example.nav.listget.Activities.CheckableLinearLayout;
import com.example.nav.listget.R;
import com.example.nav.listget.parcelable.ItemObject;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<ItemObject> {
    private LayoutInflater layoutInflater_;


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
            holder.textView = (TextView) row.findViewById(R.id.text1);
            holder.paint = holder.textView.getPaint();

            holder.checkbox.setOnClickListener(new onCheckClicked());

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

       if (item.getCompleter().equals("") )
        {
            setStyleNotDone(holder.checkbox, holder.textView, holder.paint, row, item);

        } else

        {
            setStyleDone(holder.checkbox, holder.textView, holder.paint, row, item);

       }

        holder.textPos.setText("" + position);

        return (row);
    }

    public class onCheckClicked implements OnClickListener {
        public void onClick(View v) {
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
  //              item.setChecked(0);
                setStyleNotDone(checkbox,  textView,  paint, row, item);
            } else {
   //             item.setChecked(1);
                setStyleDone(checkbox, textView, paint, row, item);
            }
        }

    }


    public void setStyleDone(CheckableLinearLayout checkbox , TextView textView,  Paint paint, View row, ItemObject item) {
        checkbox.setChecked(true);
        textView.setText(Html.fromHtml("<font color='#aaa9a9'>" + item.getName() + "</font>"));

        paint.setFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        paint.setAntiAlias(true);
    }

    public void setStyleNotDone(CheckableLinearLayout checkbox, TextView textView,  Paint paint, View row, ItemObject item) {
        checkbox.setChecked(false);
        textView.setText(Html.fromHtml(item.getName()));

        paint.setFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }


    public static class ViewHolder {
        CheckableLinearLayout checkbox;
        TextView textView;
        TextView textPos;
        TextPaint paint;
    }


}