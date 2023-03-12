package com.example.volunteers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView errorMessageTextView, register;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    PreparedStatement p = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        register = findViewById(R.id.register);
        //System.out.print(usernameString+pw);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameString = usernameEditText.getText().toString();
                String pw = passwordEditText.getText().toString();

                try {
                    conn = DriverManager.getConnection(ur1, user, password);
                    Class.forName("com.mysql.jdbc.Driver");
                    //System.out.println("连接成功·");
                    String sql="select * from user where id=? and password=?";
                    p = conn.prepareStatement(sql);
                    p.setString(1, usernameString);
                    p.setString(2, pw);
                    ResultSet rs = p.executeQuery();
                    if (rs.next()) {
                        String username=rs.getString("name");
                        int sn= Integer.parseInt(rs.getString("sn"));
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        SharedPreferences.Editor ed=getSharedPreferences("user",MODE_PRIVATE).edit();//存储用户数据
                        ed.putString("username",username);
                        ed.putInt("s_n",sn);
                        ed.commit();
                        startActivity(intent);
                        Toast toast = Toast.makeText(LoginActivity.this, "登陆成功！！！！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(LoginActivity.this, "输入的账号密码错误！！！！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.d("Login", "加载JDBC驱动失败！");
                }finally {
                    // 关闭资源
                    try {
                        if (p != null) p.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}
