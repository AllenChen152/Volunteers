package com.example.volunteers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.volunteers.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class join_taskadapter extends ArrayAdapter<task> {
    private OnItemClickListener listener;

    public join_taskadapter(@NonNull Context context, int resource, @NonNull List<task> objects) {
        super(context, resource, objects);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        task tasks = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.join_items, parent, false);
        TextView mes = view.findViewById(R.id.new_title);
        TextView loc = view.findViewById(R.id.new_context);
        TextView time = view.findViewById(R.id.new_date);
        TextView sum = view.findViewById(R.id.viewsNumber);
        mes.setText(tasks.getMessage());
        loc.setText(tasks.getLocate());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        time.setText(format.format(tasks.getTime()));
        sum.setText(String.valueOf(tasks.getSump()));

        // 将任务的 ID 存储在 Tag 中
        view.setTag(tasks.getId());
        // 设置点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    try {
                        listener.onItemClick(tasks);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
/*// 如果该任务已经报名，将按钮状态设置为不可点击，并将背景色改为灰色
        if (tasks.isJoined()) {
            joinBtn.setEnabled(false);
            joinBtn.setBackgroundColor(Color.GRAY);
        }
        // 否则，将按钮状态设置为可点击，并将背景色改为白色
        else {
            joinBtn.setEnabled(true);
            joinBtn.setBackgroundColor(Color.WHITE);
        }*/
        return view;
    }

    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(task tasks) throws SQLException;
    }
}
