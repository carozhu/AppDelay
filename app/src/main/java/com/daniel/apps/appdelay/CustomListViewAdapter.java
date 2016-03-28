package com.daniel.apps.appdelay;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daniel on 1/7/2016.
 */
/*This Custom ArrayAdapter class organizes/creates the list in ListActivity*/
public class CustomListViewAdapter extends ArrayAdapter<AppDetail> {

    Context context;

    //this is needed to be accessible in other classes and for getting the context
    public CustomListViewAdapter(Context context, int resourceId, List<AppDetail> items) {
        super(context, resourceId, items);

        this.context = context;
    }

    /*private view holder class*/
private class ViewHolder {
    ImageView imageView;//will hold icon
    TextView txtTitle;//will hold app label
}
    //assigns the label and icon the their Viewholders
    public View getView(int position, View convertView, ViewGroup parent) {
        //Viewholder
        ViewHolder holder;

        AppDetail rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item, null);//inflate the item

            holder = new ViewHolder();//new holder
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);//get the viewHolder
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);//get the viewHolder

            convertView.setTag(holder);//setTag

        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(rowItem.name);//assign app label
        holder.imageView.setImageDrawable(rowItem.icon);//assign ap icon

        return convertView;//return the convertView
    }
}