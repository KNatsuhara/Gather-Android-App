package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GroupPage extends AppCompatActivity implements GM_RecyclerViewInterface {
    public String USERNAME;
    public Connection connect;
    public ArrayList<GroupModel> groupModels = new ArrayList<>();
    private final String SHOW = "show";
    private final String HIDE = "hide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                USERNAME= null;
            } else {
                USERNAME= extras.getString("username");
            }
        } else {
            USERNAME = (String) savedInstanceState.getSerializable("username");
        }

        setUpGroupModels();
    }

    private void setUpGroupModels() {
        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null) {
                    // USER IN GROUPS
                    String query = "SELECT group_name, view_status FROM UserInGroup WHERE username = '" + USERNAME + "' ORDER BY group_name ASC;";
                    System.out.println(query);
                    Statement statement = connect.createStatement();

                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                        groupModels.add(new GroupModel(rs.getString(1), rs.getString(2)));
                    }
                    System.out.println("EXECUTED QUERY");
                }
                connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // after the job is finished:
                    RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
                    GM_RecyclerViewAdapter adapter = new GM_RecyclerViewAdapter(GroupPage.this, groupModels, GroupPage.this);
                    recyclerView.setAdapter((adapter));
                    recyclerView.setLayoutManager(new LinearLayoutManager(GroupPage.this));
                }
            });

        }).start();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, GroupDetailsPage.class);

        intent.putExtra("NAME", groupModels.get(position).getGroupName());

        startActivity(intent);
    }

    @Override
    public void onToggleClick(int position) {
        String groupName = groupModels.get(position).getGroupName();

        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                    // UPDATE PROFILE DETAILS WITH NEW FIRST, LAST, AND PASSWORD
                    String query = "UPDATE UserInGroup SET view_status = '" + SHOW + "' WHERE username = '" + USERNAME + "' AND group_name = '" + groupName + "';";
                    System.out.println(query);
                    Statement statement = connect.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                }
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        showToast("Show " + groupName);


    }

    @Override
    public void offToggleClick(int position) {
        String groupName = groupModels.get(position).getGroupName();

        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                    // UPDATE PROFILE DETAILS WITH NEW FIRST, LAST, AND PASSWORD
                    String query = "UPDATE UserInGroup SET view_status = '" + HIDE + "' WHERE username = '" + USERNAME + "' AND group_name = '" + groupName + "';";
                    System.out.println(query);
                    Statement statement = connect.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                }
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        showToast("Hide " + groupName);
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(GroupPage.this, toast, Toast.LENGTH_SHORT).show());
    }
}