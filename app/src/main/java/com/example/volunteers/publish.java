package com.example.volunteers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class publish extends Activity {
    private EditText editText;
    private Button button;
    private ImageView imageView;
    private Button chooseImageButton;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";

    private static final int CHOOSE_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish);

        // 获取控件对象
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.imageView);
        chooseImageButton = findViewById(R.id.button);
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String user1 = sp.getString("username", "");
        //生成每条评论的随机ID
        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, 8).toUpperCase();


        // 设置选择图片按钮点击事件
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
            }
        });

        // 设置上传按钮点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(publish.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (bitmap == null) {
                    Toast.makeText(publish.this, "请选择一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 将图片转换成Base64编码格式的字符串
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                // 将图片压缩并转换成 Base64 编码格式的字符串
                byte[] compressedImageBytes = compressBitmap(bitmap, 80); // 使用 80 的质量参数进行压缩
                String encodedImage = Base64.encodeToString(compressedImageBytes, Base64.DEFAULT);


                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm");
                String formattedDateTime = now.format(formatter);

                // 保存数据到MySQL数据库
                try {
                    Connection conn = DriverManager.getConnection(ur1, user, password);
                    Class.forName("com.mysql.jdbc.Driver");
                    String sql = "INSERT INTO pyq (u_name,p_text,p_time,p_picture,p_id) VALUES (?,?,?,?,?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, user1);
                    pstmt.setString(2, text);
                    pstmt.setString(3,formattedDateTime);
                    pstmt.setBytes(4,compressedImageBytes);
                    pstmt.setString(5,id);

                    pstmt.executeUpdate();
                    conn.commit();
                    pstmt.close();
                    conn.close();



                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    /*Log.e("publish", "ClassNotFoundException: " + e.getMessage());
                    Toast.makeText(publish.this, "数据库驱动加载失败，请联系管理员", Toast.LENGTH_SHORT).show();*/
                } catch (SQLException e) {
                    e.printStackTrace();
                    /*Log.e("publish", "SQLException: " + e.getMessage());
                    Toast.makeText(publish.this, "数据库操作失败，请联系管理员", Toast.LENGTH_SHORT).show();*/
                }
                Toast.makeText(publish.this, "发布成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 处理选择图片后的结果
        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                // 从返回的数据中获取所选图片的位图对象
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                // 将位图对象设置到 ImageView 控件中显示出来
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 压缩位图对象 bitmap，返回压缩后的字节数组
    public static byte[] compressBitmap(Bitmap bitmap, int quality) {
        // 创建一个新的字节数组输出流，用于存储压缩后的数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 将位图对象压缩为 JPEG 格式，并将其写入字节数组输出流中
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        // 返回压缩后的字节数组
        return byteArrayOutputStream.toByteArray();
    }


}
