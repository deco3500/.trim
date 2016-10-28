package com.rhysmakesthings.discoverlocalprototype;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rhys on 27/10/2016.
 */
public class CheckAdapter extends ArrayAdapter<String> {
    Context context;
    String[] data;
    int check;
    public ArrayList<Integer> selectedFriends;
    public CheckAdapter(Context context, String[] data, int checked) {
        super(context, R.layout.listview2, data);
        this.context = context;
        this.data = data;
        selectedFriends = new ArrayList<Integer>();
        check = checked;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.listview2, parent, false);
            ((TextView)row.findViewById(R.id.label)).setText(data[position]);
            CheckBox c = (CheckBox) row.findViewById(R.id.checkBox);
            final int b = position;
            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (isChecked) {
                        selectedFriends.add(b);
                    } else {
                        if (selectedFriends.contains(b)){
                            selectedFriends.remove(selectedFriends.indexOf(b));
                        }
                    }
                }
            });
            if (position == check){
                c.setChecked(true);
            }
        }

        return row;
    }
}
