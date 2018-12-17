package com.khomutovandrey.exjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.khomutovandrey.exjournal.R;
import com.khomutovandrey.exjournal.entry.Zapis;

import java.util.ArrayList;

public class ToDayAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Zapis> zapisArray;
    LayoutInflater inflater;

    public ToDayAdapter(Context context, ArrayList<Zapis> array){
        mContext = context;
        zapisArray = array;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (zapisArray!=null) {
            return zapisArray.size();
        }else return 0;
    }

    @Override
    public Object getItem(int position) {
        return zapisArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.item_to_day, null);
        }

        Zapis zapis = (Zapis) getItem(position);
        ((TextView)view.findViewById(R.id.tvNamber)).setText(String.valueOf(position+1));
        ((TextView)view.findViewById(R.id.tvCount)).setText(String.valueOf(zapis.getCount()));
        ((TextView)view.findViewById(R.id.tvTime)).setText(zapis.getTime());
        return view;
    }
}
