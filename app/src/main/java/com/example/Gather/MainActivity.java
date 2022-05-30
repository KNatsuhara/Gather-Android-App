package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    public static final String database = "postgres";
    public static final String host = "gather-database.cydodwb7rnr2.us-west-2.rds.amazonaws.com";
    public static final int port = 5432;
    public static final String usernameDB = "postgres";
    public static final String passwordDB = "AnfieldNight2666$";
    public static final String url = "jdbc:postgresql://gather-database.cydodwb7rnr2.us-west-2.rds.amazonaws.com:6432/gatherdb";

    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        TextView register = (TextView) findViewById(R.id.register_here);

        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        // On click listener for login button
        loginbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkForUserPassword();
            }
        });

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openRegisterProfilePage();
            }
        });


    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show());
    }

    public void checkForUserPassword()
    {
        new Thread(() -> {
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        String userAccount = username.getText().toString();
        String passAccount = password.getText().toString();

        String userPassword = "";

        try {
            System.out.println("BEFORE DATABASE");
            Database db = new Database();
            System.out.println("AFTER DATABASE");
            connect = db.getConnection();

            if (connect != null)
            {
                System.out.println("VALIDATING CREDENTIALS...");
                String query = "SELECT password FROM Users WHERE Users.username = '" + userAccount + "';";
                System.out.println(query);
                Statement statement = connect.createStatement();
                System.out.println("Created statement");

                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                    userPassword = rs.getString(1);
                }

                System.out.println("EXECUTED QUERY");
                System.out.println("Expected: " + userPassword);
                System.out.println("Input: " + passAccount);

                if (userPassword.equals(passAccount) && !userAccount.equals(""))
                {
                    // Admin Login
                    showToast("LOGIN SUCCESSFUL");
                    openMainPage(userAccount);
                } else {
                    showToast("LOGIN FAILED");
                }
            }
            connect.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        }).start();
    }


    public void openMainPage(String username)
    {
        Intent intent = new Intent(this, MainPage.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void openRegisterProfilePage()
    {
        Intent intent = new Intent(this, RegisterProfilePage.class);
        startActivity(intent);
    }

}