package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class ProfilePage extends AppCompatActivity {

    public String USERNAME;
    public String PASSWORD;
    public String FIRSTNAME;
    public String LASTNAME;
    public String FULLNAME;
    public String DATE;
    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

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

        System.out.println("username: " + USERNAME);

        populateProfilePage();
    }

    public void populateProfilePage()
    {
        new Thread(() -> {
            TextView username_text_view = (TextView) findViewById(R.id.profile_username_plain_text);
            TextView name_text_view = (TextView) findViewById(R.id.profile_name_plain_text);
            TextView password_text_view = (TextView) findViewById(R.id.profile_password_plain_text);
            TextView date_text_view = (TextView) findViewById(R.id.profile_date_joined_plain_text);
            TextView group_in_text_view = (TextView) findViewById(R.id.profile_groups_in_plain_text);

            username_text_view.setText(USERNAME);
            StringBuilder group_name_list = new StringBuilder();

            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                    // PROFILE DETAILS
                    String query = "SELECT password, first_name, last_name, date_joined FROM Users WHERE Users.username = '" + USERNAME + "';";
                    System.out.println(query);
                    Statement statement = connect.createStatement();
                    System.out.println("Created statement");

                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println("1:" + rs.getString(1));
                        System.out.println("2:" + rs.getString(2));
                        System.out.println("3:" + rs.getString(3));
                        System.out.println("4:" + rs.getString(4));
                        PASSWORD = rs.getString(1);
                        FIRSTNAME = rs.getString(2);
                        LASTNAME = rs.getString(3);
                        DATE = rs.getString(4);
                    }
                    System.out.println("EXECUTED QUERY");
                    FULLNAME = FIRSTNAME + " " + LASTNAME;
                    name_text_view.setText(FULLNAME);
                    password_text_view.setText(PASSWORD);
                    date_text_view.setText(DATE);

                    // USER IN GROUPS
                    query = "SELECT group_name, date_joined  FROM UserInGroup WHERE username = '" + USERNAME + "';";
                    System.out.println(query);
                    statement = connect.createStatement();
                    System.out.println("Created statement");

                    rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println("1:" + rs.getString(1));
                        group_name_list.append(rs.getString(1) + " - " + rs.getString(2) + '\n');
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
                    group_in_text_view.setText(group_name_list.toString());
                }
            });
        }).start();
    }
}