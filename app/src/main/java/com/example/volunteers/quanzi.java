package com.example.volunteers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunteers.adapter.comment;
import com.example.volunteers.adapter.commentAdapter;
import com.mysql.jdbc.Statement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class quanzi extends AppCompatActivity {
    TextView u_name,c_text,c_time;
    EditText com;
    Button btn;
    ImageView pic;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    Statement stmt = null;
    SharedPreferences sp;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_pyq);
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        String name = bundle.getString("name");
        String text = bundle.getString("text");
        String time = bundle.getString("time");
        String filename = bundle.getString("picture_file");
        FileInputStream fis = null;
        Bitmap bitmap=null;
        try {
            fis = openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        u_name=findViewById(R.id.textView);
        c_text=findViewById(R.id.textView3);
        c_time=findViewById(R.id.textView4);
        pic=findViewById(R.id.imageView5);
        com=findViewById(R.id.comment_input);
        btn=findViewById(R.id.comment_button);

        u_name.setText(name);
        c_text.setText(text);
        c_time.setText(time);
        pic.setImageBitmap(bitmap);
        List<comment> coms = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(ur1, user, password);
            Class.forName("com.mysql.jdbc.Driver");
            stmt = (Statement) conn.createStatement();
            String sql2="select * from comment where p_id='"+id+"'";
            ResultSet rs = stmt.executeQuery(sql2);
            while(rs.next()){
                String u_n=rs.getString("u_name");
                String t=rs.getString("c_text");
                String tim=rs.getString("c_time");
                comment c=new comment(u_n,t,tim);
                coms.add(c);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ListView ls=findViewById(R.id.comment_list);
        commentAdapter ad=new commentAdapter(quanzi.this,R.layout.c_items, coms);
        ls.setAdapter(ad);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edittext=com.getText().toString();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm");
                String formattedDateTime = now.format(formatter);

                sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                String user1 = sp.getString("username", "");
                try {
                    conn = DriverManager.getConnection(ur1, user, password);
                    Class.forName("com.mysql.jdbc.Driver");
                    String sql = "INSERT INTO comment (p_id,u_name,c_time,c_text) VALUES (?,?,?,?)";
                    PreparedStatement pst=conn.prepareStatement(sql);
                    pst.setString(1,id);
                    pst.setString(2,user1);
                    pst.setString(3,formattedDateTime);
                    pst.setString(4,edittext);
                    pst.executeUpdate();
                    Toast.makeText(quanzi.this, "发布成功", Toast.LENGTH_SHORT).show();

                    // 清空评论输入框
                    com.setText("");

                    // 重新查询并显示评论列表
                    coms.clear();
                    try {
                        String sql2="select * from comment where p_id='"+id+"'";
                        ResultSet rs = stmt.executeQuery(sql2);
                        while(rs.next()){
                            String u_n=rs.getString("u_name");
                            String t=rs.getString("c_text");
                            String tim=rs.getString("c_time");
                            comment c=new comment(u_n,t,tim);
                            coms.add(c);
                        }
                        ad.notifyDataSetChanged(); // 通知ListView更新显示
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    public void back1(View view){
        finish();

    }
}
