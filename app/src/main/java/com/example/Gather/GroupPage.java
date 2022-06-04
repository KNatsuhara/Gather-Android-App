package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.security.acl.Group;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class GroupPage extends AppCompatActivity {

    public String USERNAME;
    public Connection connect;
    public ArrayList<GroupModel> groupModels = new ArrayList<>();

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

        new Thread(() -> {
            RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
            GM_RecyclerViewAdapter adapter = new GM_RecyclerViewAdapter(this, groupModels);
            recyclerView.setAdapter((adapter));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }).start();

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
                    RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
                    GM_RecyclerViewAdapter adapter = new GM_RecyclerViewAdapter(GroupPage.this, groupModels);
                    recyclerView.setAdapter((adapter));
                    recyclerView.setLayoutManager(new LinearLayoutManager(GroupPage.this));
                }
            });
        }).start();
    }
}