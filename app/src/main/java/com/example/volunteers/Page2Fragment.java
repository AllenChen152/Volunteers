package com.example.volunteers;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.volunteers.tool.BaseActivity;
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

public class Page2Fragment extends Fragment {
    private Button btn;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    Statement stmt = null;
    SharedPreferences sp;
    int sn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.sign, container, false);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        sn = sp.getInt("s_n",0);
        btn=view.findViewById(R.id.sign);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkActivity();
            }
        });
        return view;
    }
    private void checkActivity() {
        final EditText input = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
