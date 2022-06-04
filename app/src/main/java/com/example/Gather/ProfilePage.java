package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class ProfilePage extends AppCompatActivity {

    public String USERNAME;
    public String PASSWORD;
    public String FIRSTNAME;
    public String LASTNAME;
    public String DATE;
    Connection connect;
    private Button buttonShowDialog;

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

        populateProfilePage(); // Populate Profile Attribute Fields
        showUpdateConfirmation(); // Update Button Trigger
        showDeleteConfirmation(); // Delete Button Trigger
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(ProfilePage.this, toast, Toast.LENGTH_SHORT).show());
    }

    public void deleteProfile()
    {
        new Thread(() -> {
            TextView username_text_view = (TextView) findViewById(R.id.profile_username_plain_text);

            USERNAME = username_text_view.getText().toString();

            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                    // DELETE PROFILE WITH USERNAME
                    String query = "DELETE FROM Users WHERE username = '"+ USERNAME +"';";
                    System.out.println(query);
                    Statement statement = connect.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                }
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        showToast("DELETED PROFILE");
        openSignUpPage();
    }

    public void updateProfile()
    {
        new Thread(() -> {
            TextView username_text_view = (TextView) findViewById(R.id.profile_username_plain_text);
            EditText name_text_view = (EditText) findViewById(R.id.profile_name_plain_text);
            EditText password_text_view = (EditText) findViewById(R.id.profile_password_plain_text);

            USERNAME = username_text_view.getText().toString();
            String FULL = name_text_view.getText().toString();
            String NAMES[] = FULL.split(" ", 2);
            FIRSTNAME = NAMES[0];
            LASTNAME = NAMES[1];
            PASSWORD = password_text_view.getText().toString();

            if (PASSWORD.isEmpty())
            {
                showToast("Password field cannot be empty!");
                return;
            }

            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                    // UPDATE PROFILE DETAILS WITH NEW FIRST, LAST, AND PASSWORD
                    String query = "UPDATE Users SET password = '" + PASSWORD + "', first_name = '"+ FIRSTNAME + "', last_name = '"+ LASTNAME +"' WHERE username = '"+ USERNAME +"';";
                    System.out.println(query);
                    Statement statement = connect.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                }
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        showToast("UPDATED PROFILE");
    }

    public void populateProfilePage()
    {
        new Thread(() -> {
            TextView username_text_view = (TextView) findViewById(R.id.profile_username_plain_text);
            EditText name_text_view = (EditText) findViewById(R.id.profile_name_plain_text);
            EditText password_text_view = (EditText) findViewById(R.id.profile_password_plain_text);
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
                        PASSWORD = rs.getString(1);
                        FIRSTNAME = rs.getString(2);
                        LASTNAME = rs.getString(3);
                        DATE = rs.getString(4);
                    }
                    System.out.println("EXECUTED QUERY");

                    System.out.println(PASSWORD);
                    System.out.println(DATE);
                    System.out.println(FIRSTNAME + " " + LASTNAME);


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
                    name_text_view.setText(FIRSTNAME + " " + LASTNAME);
                    password_text_view.setText(PASSWORD);
                    date_text_view.setText(DATE);
                    group_in_text_view.setText(group_name_list.toString());
                }
            });
        }).start();
    }

    public void openSignUpPage()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void showDeleteConfirmation() {
        buttonShowDialog = findViewById(R.id.delete_button);
        buttonShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonShowDialog_onClick(view);
            }
        });
    }

    private void buttonShowDialog_onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete Profile");
        builder.setMessage("Are you sure?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getApplicationContext(),"Ok", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                deleteProfile();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void showUpdateConfirmation() {
        buttonShowDialog = findViewById(R.id.button);
        buttonShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateShowDialog(view);
            }
        });
    }

    private void updateShowDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Update Profile");
        builder.setMessage("Are you sure?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getApplicationContext(),"Ok", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                updateProfile();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

}