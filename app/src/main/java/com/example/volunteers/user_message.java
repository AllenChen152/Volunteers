package com.example.volunteers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mysql.jdbc.Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class user_message extends AppCompatActivity {
    private EditText et_nicke,  et_email, et_major;
    private TextView et_phone,et_idetity;
    private Button btn_update;
    private SharedPreferences sp;
    private int userId;
    private ImageButton back;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement p;

    String name,school,major;
    int tel;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_message);

        // 获取用户id
        sp = getSharedPreferences("user", MODE_PRIVATE);
        userId = sp.getInt("s_n", 0);
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/


        // 获取各个EditText控件的实例
        et_nicke = findViewById(R.id.et_nicke);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        et_idetity = findViewById(R.id.et_idetity);
        et_major = findViewById(R.id.et_major);
        back=findViewById(R.id.btn_back);
        try {
            conn = DriverManager.getConnection(ur1, user, password);
            Class.forName("com.mysql.jdbc.Driver");
            stmt = (Statement) conn.createStatement();
            String sql1 = "SELECT * FROM user WHERE sn='" + userId + "'";
            ResultSet rs = stmt.executeQuery(sql1);
            if(rs.next()){
                name=rs.getString("name");
                school=rs.getString("school");
                major=rs.getString("major");
                tel=rs.getInt("id");

            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String tel1= String.valueOf(tel);
        String userId1= String.valueOf(userId);
        et_nicke.setText(name);
        et_phone.setText(tel1);
        et_email.setText(school);
        et_major.setText(major);
        et_idetity.setText(userId1);



        // 设置修改按钮的点击事件
        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(user_message.this);
                //builder.setTitle("对话框标题");
                builder.setMessage("是否更新信息");
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sql = "UPDATE user SET name=?,school=?,major=? WHERE sn=?";
                        String name1=et_nicke.getText().toString();
                        String school1=et_email.getText().toString();
                        String major1=et_major.getText().toString();
                        try {
                            p=conn.prepareStatement(sql);
                            p.setString(1,name1);
                            p.setString(2,school1);
                            p.setString(3,major1);
                            p.setInt(4, userId);
                            p.executeUpdate();
                            finish();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
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

