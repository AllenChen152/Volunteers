package com.example.volunteers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.volunteers.adapter.join_taskadapter;
import com.example.volunteers.adapter.task;
import com.mysql.jdbc.Statement;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class join_activity extends AppCompatActivity {
    SharedPreferences sp;
    private ImageButton back;
    int sn;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    Statement stmt = null,stmt1=null;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        List<task> tasks = new ArrayList<>();
        back=findViewById(R.id.btn_back);
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        sn = sp.getInt("s_n",0);
        Set<String> participatedTasks = new HashSet<>();

        try {
            conn = DriverManager.getConnection(ur1, user, password);
            Class.forName("com.mysql.jdbc.Driver");
            String sql1 = "SELECT p_tasks FROM user WHERE sn='" + sn + "'";
            ResultSet rs1 = null;
            stmt1 = (Statement) conn.createStatement();
            rs1 = stmt1.executeQuery(sql1);
            if (rs1.next()) {
                String participatedTaskStr = rs1.getString(1);
                if (participatedTaskStr != null) {
                    String[] participatedTaskArray = participatedTaskStr.split(",");
                    participatedTasks.addAll(Arrays.asList(participatedTaskArray));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (String taskId : participatedTasks) {
            try {
                stmt = (Statement) conn.createStatement();
                String sql2="select * from task where t_id='"+taskId+"'";
                ResultSet rs = stmt.executeQuery(sql2);
                if(rs.next()){
                    String id=rs.getString("t_id");
                    String locate=rs.getString("t_loc");
                    String message=rs.getString("t_mes");
                    Date time=rs.getDate("t_time");
                    int sumtime=rs.getInt("t_ts");
                    int sump= Integer.parseInt(id);
                    boolean joined = participatedTasks.contains(id);
                    task t=new task(id,locate,message,time,sumtime,sump,joined);
                    tasks.add(t);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        ListView ls=(ListView) findViewById(R.id.ac_list);
        join_taskadapter ad= new join_taskadapter((Context) this,R.layout.join_items, tasks);
        ls.setAdapter(ad);

        ad.setOnItemClickListener(new join_taskadapter.OnItemClickListener() {
            @Override
            public void onItemClick(task tasks) throws SQLException {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date time=tasks.getTime();
                String d_time=dateFormat.format(time);
                AlertDialog.Builder builder = new AlertDialog.Builder(join_activity.this);
                //builder.setTitle("对话框标题");
                builder.setMessage("请准时签到："+d_time);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                builder.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
