package com.example.volunteers.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import com.example.volunteers.R;
import com.example.volunteers.quanzi;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 显示图片并允许放大查看
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                ImageView imageView = new ImageView(getContext());
                imageView.setImageBitmap(p.getPicture());
                builder.setView(imageView);
                builder.show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动详情页并传递数据
                try {
                    String filename = "image_" + p.getP_id() + ".png";
                    FileOutputStream fos = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    p.getPicture().compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Intent intent = new Intent(getContext(), quanzi.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", p.getP_id());
                    bundle.putString("name", p.getName());
                    bundle.putString("text", p.getText());
                    bundle.putString("time", p.getTime());
                    bundle.putString("picture_file", filename);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        view.setTag(p.getP_id());

        return view;
    }
}
