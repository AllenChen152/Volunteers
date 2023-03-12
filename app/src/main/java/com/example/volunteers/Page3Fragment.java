package com.example.volunteers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Page3Fragment extends Fragment {
    Button b_login;
    LinearLayout user_message,join_ac;
    TextView t_u;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person, container, false);
        user_message=view.findViewById(R.id.ll_info);
        join_ac=view.findViewById(R.id.ll_order);
        t_u=view.findViewById(R.id.tv_username);
        b_login = view.findViewById(R.id.btn_out);
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String user = sp.getString("username", "");
        if (user.equals("")) {
            t_u.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "请先登录！！", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            t_u.setText(user);
            b_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    //builder.setTitle("对话框标题");
                    builder.setMessage("是否退出当前用户登录");
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.clear();
                            editor.apply();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }

        user_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), user_message.class);
                startActivity(intent);
            }
        });

        join_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), join_activity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
