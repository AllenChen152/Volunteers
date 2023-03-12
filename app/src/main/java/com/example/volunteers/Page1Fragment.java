package com.example.volunteers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.volunteers.adapter.task;
import com.example.volunteers.adapter.taskadapter;
import com.example.volunteers.tool.RefreshableView;
import com.mysql.jdbc.Statement;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

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

public class Page1Fragment extends Fragment {

    List<Integer> list = new ArrayList();
    String user = "root";
    String password = "Sql1234.";
    String ur1 = "jdbc:mysql://sh-cynosdbmysql-grp-m20d79p8.sql.tencentcdb.com:21440/volunteer";
    Connection conn = null;
    Statement stmt = null,stmt1=null;
    PreparedStatement p = null;
    SharedPreferences sp;
    int sn;
    private RefreshableView refreshableView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home, container, false);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        sn = sp.getInt("s_n",0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 执行网络操作...
                Set<String> participatedTasks = new HashSet<>();


                final List<task> tasks = new ArrayList<>();
                try {
                    conn = DriverManager.getConnection(ur1, user, password);
                    Class.forName("com.mysql.jdbc.Driver");
                    stmt = (Statement) conn.createStatement();
                    String sql2="select * from task";
                    ResultSet rs = stmt.executeQuery(sql2);

                    while(rs.next()){
                        String id=rs.getString("t_id");
                        String locate=rs.getString("t_loc");
                        String message=rs.getString("t_mes");
                        Date time=rs.getDate("t_time");
                        int sumtime=rs.getInt("t_ts");
                        int sump=rs.getInt("t_sum");
                        boolean joined = participatedTasks.contains(id);
                        task t=new task(id,locate,message,time,sumtime,sump,joined);
                        tasks.add(t);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView ls=(ListView) getView().findViewById(R.id.task_listView);
                            taskadapter ad=new taskadapter((Context) getActivity(),R.layout.news_item, tasks);
                            ls.setAdapter(ad);

                            //点击事件
                            ad.setOnItemClickListener(new taskadapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(task tasks) {
                                    // 处理点击事件
                                    String sql1 = "SELECT p_tasks FROM user WHERE sn='" + sn + "'";
                                    ResultSet rs1 = null;
                                    try {
                                        conn = DriverManager.getConnection(ur1, user, password);
                                        Class.forName("com.mysql.jdbc.Driver");
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
                                    } finally {
                                        // 关闭资源
                                        try {
                                            if (stmt1 != null) stmt1.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //弹出报名成功提示框
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    //builder.setTitle("对话框标题");
                                    builder.setMessage("是否报名该志愿活动");
                                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    });
                                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //保存报名记录
                                            String taskId = tasks.getId();
                                            Set<String> newParticipatedTasks = new HashSet<>(participatedTasks);
                                            if (newParticipatedTasks.contains(taskId)) {
                                                //已报名，弹出提示框
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setMessage("您已报名该活动！");
                                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                });
                                                builder.show();
                                            }else {
                                                //未报名，添加记录
                                                newParticipatedTasks.add(taskId);
                                                String joinedParticipatedTasks = String.join(",", newParticipatedTasks);
                                                String sql = "UPDATE user SET p_tasks=? WHERE sn=?";
                                                try {
                                                    conn = DriverManager.getConnection(ur1, user, password);
                                                    Class.forName("com.mysql.jdbc.Driver");
                                                    p = conn.prepareStatement(sql);
                                                    p.setString(1,joinedParticipatedTasks);
                                                    p.setInt(2,sn);
                                                    p.executeUpdate();
                                                } catch (SQLException e) {
                                                    throw new RuntimeException(e);
                                                } catch (ClassNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                //弹出报名成功提示框
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setMessage("报名成功");
                                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                });
                                                builder.show();
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });

                            ad.notifyDataSetChanged();
                        }
                    });
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 关闭资源
                    try {
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return view;
    }


    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        /*try {
            conn = DriverManager.getConnection(ur1, user, password);
            Class.forName("com.mysql.jdbc.Driver");
            stmt = (Statement) conn.createStatement();
            stmt1 = (Statement) conn.createStatement();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/

        //图片轮播
        initDate();
        Banner mbanner=(Banner) getView().findViewById(R.id.banner);
        Banner banner = mbanner.setAdapter(new BannerImageAdapter<Integer>(list) {
            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
            }

        });
        //是否允许自动轮播
        mbanner.isAutoLoop(true);
        //设置指示器
        mbanner.setIndicator(new CircleIndicator(getContext()));
        //开始轮播
        mbanner.start();
        Set<String> participatedTasks = new HashSet<>();
        /*String sql1 = "SELECT p_tasks FROM user WHERE id='" + user1 + "'";
        ResultSet rs1 = null;
        try {
            conn = DriverManager.getConnection(ur1, user, password);
            Class.forName("com.mysql.jdbc.Driver");
            //stmt = (Statement) conn.createStatement();
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
        } finally {
            // 关闭资源
            try {
                if (stmt1 != null) stmt1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/



        ListView ls=(ListView) getView().findViewById(R.id.task_listView);
        List<task> tasks=new ArrayList<>();

        /*try {
            //System.out.println("连接成功·");
            conn = DriverManager.getConnection(ur1, user, password);
            Class.forName("com.mysql.jdbc.Driver");
            stmt = (Statement) conn.createStatement();
            stmt1 = (Statement) conn.createStatement();
            String sql2="select * from task";
            ResultSet rs = stmt.executeQuery(sql2);

            while(rs.next()){
                String id=rs.getString("t_id");
                String locate=rs.getString("t_loc");
                String message=rs.getString("t_mes");
                Date time=rs.getDate("t_time");
                int sumtime=rs.getInt("t_ts");
                int sump=rs.getInt("t_sum");
                boolean joined = participatedTasks.contains(id);
                task t=new task(id,locate,message,time,sumtime,sump,joined);
                tasks.add(t);
            }
            taskadapter ad=new taskadapter((Context) getActivity(),R.layout.news_item, tasks);
            ls.setAdapter(ad);
            // 设置点击事件
            ad.setOnItemClickListener(new taskadapter.OnItemClickListener() {
                @Override
                public void onItemClick(task tasks) throws SQLException {
                    // 处理点击事件
                    if(tasks.isJoined()) {
                        //已经报名过该活动
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("您已经报名过该活动");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    } else {
                        //弹出报名成功提示框
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        //builder.setTitle("对话框标题");
                        builder.setMessage("是否报名该志愿活动");
                        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//保存报名记录
                                String taskId = tasks.getId();
                                Set<String> newParticipatedTasks = new HashSet<>(participatedTasks);
                                newParticipatedTasks.add(taskId);
                                String joinedParticipatedTasks = String.join(",", newParticipatedTasks);

                                String sql = "UPDATE user SET p_tasks=? WHERE id=?";
                                try {
                                    conn = DriverManager.getConnection(ur1, user, password);
                                    Class.forName("com.mysql.jdbc.Driver");
                                    stmt = (Statement) conn.createStatement();
                                    stmt1 = (Statement) conn.createStatement();
                                    p = conn.prepareStatement(sql);
                                    p.setString(1,joinedParticipatedTasks);
                                    p.setString(2,user1);
                                    p.executeUpdate();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        });
                        builder.show();
                    }


                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭资源

            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
//页面刷新
        refreshableView = (RefreshableView) getView().findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(1000);
                    //重新查询数据库
                    List<task> tasks = new ArrayList<>();
                    conn = DriverManager.getConnection(ur1, user, password);
                    Class.forName("com.mysql.jdbc.Driver");
                    stmt = (Statement) conn.createStatement();
                    String sql = "select * from task";
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next()) {
                        String id = rs.getString("t_id");
                        String locate = rs.getString("t_loc");
                        String message = rs.getString("t_mes");
                        Date time = rs.getDate("t_time");
                        int sumtime = rs.getInt("t_ts");
                        int sump = rs.getInt("t_sum");
                        boolean joined = participatedTasks.contains(id);
                        task t = new task(id, locate, message, time, sumtime,sump,joined);
                        tasks.add(t);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            taskadapter ad = new taskadapter((Context) getActivity(), R.layout.news_item, tasks);
                            ls.setAdapter(ad);
                            //点击事件
                            ad.setOnItemClickListener(new taskadapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(task tasks) {
                                    // 处理点击事件
                                    String sql1 = "SELECT p_tasks FROM user WHERE sn='" + sn + "'";
                                    ResultSet rs1 = null;
                                    try {
                                        conn = DriverManager.getConnection(ur1, user, password);
                                        Class.forName("com.mysql.jdbc.Driver");
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
                                    } finally {
                                        // 关闭资源
                                        try {
                                            if (stmt1 != null) stmt1.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //弹出报名成功提示框
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    //builder.setTitle("对话框标题");
                                    builder.setMessage("是否报名该志愿活动");
                                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    });
                                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //保存报名记录
                                            String taskId = tasks.getId();
                                            Set<String> newParticipatedTasks = new HashSet<>(participatedTasks);
                                            if (newParticipatedTasks.contains(taskId)) {
                                                //已报名，弹出提示框
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setMessage("您已报名该活动！");
                                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                });
                                                builder.show();
                                            }else {
                                                //未报名，添加记录
                                                newParticipatedTasks.add(taskId);
                                                String joinedParticipatedTasks = String.join(",", newParticipatedTasks);
                                                String sql = "UPDATE user SET p_tasks=? WHERE sn=?";
                                                try {
                                                    conn = DriverManager.getConnection(ur1, user, password);
                                                    Class.forName("com.mysql.jdbc.Driver");
                                                    p = conn.prepareStatement(sql);
                                                    p.setString(1,joinedParticipatedTasks);
                                                    p.setInt(2,sn);
                                                    p.executeUpdate();
                                                } catch (SQLException e) {
                                                    throw new RuntimeException(e);
                                                } catch (ClassNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                //弹出报名成功提示框
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setMessage("报名成功");
                                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                });
                                                builder.show();
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                            ad.notifyDataSetChanged();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
    }
    private void initDate() {
        list.add(R.drawable.a);
        list.add(R.drawable.b);
        list.add(R.drawable.c);
        list.add(R.drawable.d);
    }


}
