package com.example.volunteers;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterActivity extends AppCompatActivity {
    private EditText mPhoneNumberEditText;
    private EditText mNameEditText;
    private EditText mPasswordEditText,msn;
    private Button mRegisterButton;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    PreparedStatement p = null,pt=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());

        mPhoneNumberEditText = (EditText) findViewById(R.id.et_phone_number);
        mNameEditText = (EditText) findViewById(R.id.et_name);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mRegisterButton = (Button) findViewById(R.id.btn_register);
        msn = (EditText) findViewById(R.id.et_sn);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
                String name = mNameEditText.getText().toString().trim();
                String pw = mPasswordEditText.getText().toString().trim();
                int sn= Integer.parseInt(msn.getText().toString().trim());

                if (phoneNumber.isEmpty() || name.isEmpty() || pw.isEmpty()) {
                    Toast toast = Toast.makeText(RegisterActivity.this, "不能为空！！！！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                    return;
                }else{
                    try {
                        conn = DriverManager.getConnection(ur1, user, password);
                        Class.forName("com.mysql.jdbc.Driver");
                        //System.out.println("连接成功·");
                        String sql;
                        sql="select * from user where sn = ? and id = ?";
                        p = conn.prepareStatement(sql);
                        p.setInt(1, sn);
                        p.setString(2, phoneNumber);
                        ResultSet resultSet = p.executeQuery();
                        if(resultSet.next()){
                            Toast toast = Toast.makeText(RegisterActivity.this, "该账号已经注册", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                            return;
                        }else{
                            String ss = "INSERT INTO user (name,id,password,sn) VALUES (?,?,?,?)";
                            pt = conn.prepareStatement(ss);
                            pt.setString(1, name);
                            pt.setString(2, phoneNumber);
                            pt.setString(3, pw);
                            pt.setInt(4,sn);
                            pt.executeUpdate();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast toast = Toast.makeText(RegisterActivity.this, "注册成功！！！！", Toast.LENGTH_SHORT);
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

                // perform register
                // ...

                // navigate to home page
                // ...
            }
        });
    }
    public void fin1(View view){
        finish();
    }
}
