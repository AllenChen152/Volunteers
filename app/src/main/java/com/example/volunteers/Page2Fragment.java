package com.example.volunteers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.volunteers.adapter.pyq;
import com.example.volunteers.adapter.pyqAdapter;
import com.example.volunteers.adapter.task;
import com.example.volunteers.tool.BaseActivity;
import com.example.volunteers.tool.Map;
import com.mysql.jdbc.Statement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Page2Fragment extends Fragment {
    private Button btn;
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    Statement stmt = null;
    SharedPreferences sp;
    int sn;
    List<pyq> pyqs = new ArrayList<>(); // 用于保存查询结果

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.sign, container, false);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        sn = sp.getInt("s_n",0);

        ImageButton imb=view.findViewById(R.id.publish1);
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), publish.class);
                startActivity(intent);
            }
        });

        // 创建新线程来执行数据库查询操作
        Thread queryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    conn = DriverManager.getConnection(ur1, user, password);
                    Class.forName("com.mysql.jdbc.Driver");
                    stmt = (Statement) conn.createStatement();
                    String sql2="select * from pyq ";
                    ResultSet rs = stmt.executeQuery(sql2);

                    while(rs.next()){
                        String id=rs.getString("p_id");
                        String name=rs.getString("u_name");
                        String text=rs.getString("p_text");
                        String time=rs.getString("p_time");
                        byte[] pic=rs.getBytes("p_picture");
                        // 保存图像并获取其路径
                        String fileName = "image_" + id + ".jpg"; // 在此使用id作为文件名，以便区分不同的图片
                        String imagePath = saveImage(pic, fileName);

                        Bitmap bitmap = null;
                        if (imagePath != null) {
                            bitmap = Glide.with(getActivity()).asBitmap().load(new File(imagePath)).submit().get();
                        }
                        pyq p = new pyq(name, id, text, time, bitmap);
                        pyqs.add(p);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // 更新UI元素等操作需要在UI线程中执行
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView ls=view.findViewById(R.id.listp);
                        pyqAdapter ad=new pyqAdapter((Context) getActivity(),R.layout.p_items, pyqs);
                        ls.setAdapter(ad);
                    }
                });
            }
        });

        queryThread.start();

        return view;
    }

    private String saveImage(byte[] pic, String fileName) {
        File dir = getActivity().getFilesDir(); // 获取应用程序缓存目录
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File imageFile = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(pic);
            fos.flush();
            fos.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

