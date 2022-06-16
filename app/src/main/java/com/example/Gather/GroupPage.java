package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GroupPage extends AppCompatActivity implements GM_RecyclerViewInterface{
    public String USERNAME;
    public Connection connect;
    public ArrayList<GroupModel> groupModels = new ArrayList<>();
    private Boolean COMPLETED = false;

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

//        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
//        GM_RecyclerViewAdapter adapter = new GM_RecyclerViewAdapter(this, groupModels, this);
//        recyclerView.setAdapter((adapter));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // groupModels.add(new GroupModel("BestGroup"));
    }

    private void setUpGroupModels() {
        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null) {
                    // USER IN GROUPS
                    String query = "SELECT group_name FROM UserInGroup WHERE username = '" + USERNAME + "';";
                    System.out.println(query);
                    Statement statement = connect.createStatement();

                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                        groupModels.add(new GroupModel(rs.getString(1)));
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
}