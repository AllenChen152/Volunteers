package com.example.volunteers;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.volunteers.tool.Map;
import com.mysql.jdbc.Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Page3Fragment extends Fragment {
    Button b_login;
    LinearLayout user_message,join_ac,sign;
    TextView t_u;
    String user1;

    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    Statement stmt = null;
    int sn;
    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person, container, false);
        user_message=view.findViewById(R.id.ll_info);
        join_ac=view.findViewById(R.id.ll_order);
        t_u=view.findViewById(R.id.tv_username);
        b_login = view.findViewById(R.id.btn_out);
        sign = view.findViewById(R.id.sign);
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        user1 = sp.getString("username", "");
        sn = sp.getInt("s_n",0);
        if (user1.equals("")) {
            t_u.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "请先登录！！", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            t_u.setText(user1);
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

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    checkActivity();

            }
        });

        return view;
    }
    private void checkActivity() {
        final EditText input = new EditText(getContext());
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("请输入活动编码");
        builder.setView(input);
        Set<String> participatedTasks = new HashSet<>();

        // 点击确定按钮后执行以下操作
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String activityCode = input.getText().toString();
                try {
                    conn = DriverManager.getConnection(ur1, user, password);
                    Class.forName("com.mysql.jdbc.Driver");
                    stmt = (Statement) conn.createStatement();
                    String sql1 = "SELECT p_tasks FROM user WHERE sn='" + sn + "'";
                    ResultSet rs = stmt.executeQuery(sql1);
                    if (rs.next()) {

                        String participatedTaskStr = rs.getString(1);
                        if (participatedTaskStr != null) {
                            String[] participatedTaskArray = participatedTaskStr.split(",");
                            participatedTasks.addAll(Arrays.asList(participatedTaskArray));
                        }
                    }

                    Set<String> newParticipatedTasks = new HashSet<>(participatedTasks);
                    //判断是否报名
                    if (newParticipatedTasks.contains(activityCode)) {
                        int code= Integer.parseInt(activityCode);
                        String sql2 = "SELECT * FROM task WHERE t_id=?";
                        PreparedStatement p = conn.prepareStatement(sql2);
                        p.setInt(1,code);
                        ResultSet rs1=p.executeQuery();
                        if(rs1.next()){
                            String loc=rs1.getString("t_loc");
                            Intent intent = new Intent(getActivity(), Map.class);
                            intent.putExtra("location", loc);
                            intent.putExtra("task_code",code);
                            startActivity(intent);
                        }

                    } else {
                        // 如果没有匹配的活动，显示错误消息
                        Toast.makeText(getActivity(), "未参加该活动", Toast.LENGTH_SHORT).show();
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                } catch (SQLException e) {
                    // 处理数据库连接错误
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "数据库连接错误", Toast.LENGTH_SHORT).show();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 创建并显示弹窗
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
