package com.example.volunteers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.volunteers.R;

import java.util.List;

public class pyqAdapter extends ArrayAdapter<pyq> {
    public pyqAdapter(@NonNull Context context, int resource, @NonNull List<pyq> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        pyq p = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.p_items, parent, false);
        TextView u_name=view.findViewById(R.id.textView);
        TextView text=view.findViewById(R.id.textView3);
        TextView time=view.findViewById(R.id.textView4);
        ImageView image=view.findViewById(R.id.imageView5);
        ImageButton imgb=view.findViewById(R.id.comment);

        u_name.setText(p.getName());
        text.setText(p.getText());
        time.setText(p.getTime());;
        image.setImageBitmap(p.getPicture());


        view.setTag(p.getP_id());

        return view;
    }
}
