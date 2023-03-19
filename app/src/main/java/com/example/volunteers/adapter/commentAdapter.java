package com.example.volunteers.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import com.example.volunteers.R;

import java.util.List;

public class commentAdapter extends ArrayAdapter<comment> {
    public commentAdapter(@NonNull Context context, int resource, @NonNull List<comment> objects) {
        super(context, resource, objects);
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        comment c=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.c_items, parent, false);
        TextView u_name=view.findViewById(R.id.c_name);
        TextView text=view.findViewById(R.id.c_text);
        TextView time=view.findViewById(R.id.c_time);


        u_name.setText(c.getU_name());
        text.setText(c.getC_text());
        time.setText(c.getC_time());;



        view.setTag(c.getP_id());
        return view;
    }
}
