package com.example.nav.listget.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
            holder.textView = (TextView) row.findViewById(R.id.text);


            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
//        holder.textView.setText(Html.fromHtml(item.getCategory() + " (" + item.getNumTask() + ") "));
        return (row);
    }



    public static class ViewHolder {
        TextView textView;
    }
}