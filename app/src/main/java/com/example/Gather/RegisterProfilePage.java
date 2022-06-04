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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.time.LocalDate;

public class RegisterProfilePage extends AppCompatActivity {

    public String user, first, last, pass, pass2, date;
    Connection connect;
    public Button buttonShowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_page);

        Button create_btn = (Button) findViewById(R.id.create_account_button);
        EditText profile_user = (EditText) findViewById(R.id.create_profile_username);
        EditText profile_first = (EditText) findViewById(R.id.create_user_first_name);
        EditText profile_last = (EditText) findViewById(R.id.create_user_last_name);
        EditText profile_password = (EditText) findViewById(R.id.create_user_password);
        EditText profile_password2 = (EditText) findViewById(R.id.create_user_reenter_password);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate time = LocalDate.now();
            date = time.toString();
        }

        showCreateConfirmation();
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(RegisterProfilePage.this, toast, Toast.LENGTH_SHORT).show());
    }

    public boolean checkIfFieldsFilled()
    {
        if (user.isEmpty() || first.isEmpty() || last.isEmpty() || pass.isEmpty() || pass2.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean checkIfPasswordsMatch()
    {
        if (pass.equals(pass2))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void checkIfUsernameExists()
    {
        new Thread(() -> {
            String checkUser = "";
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                    String query = "SELECT username FROM Users WHERE Users.username = '" + user + "';";
                    System.out.println(query);
                    Statement statement = connect.createStatement();

                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                        checkUser = rs.getString(1);
                    }

                    System.out.println("EXECUTED QUERY");
                    System.out.println("Found user: " + checkUser);
                    System.out.println("User: " + user);

                    if (!checkUser.isEmpty() || user.isEmpty())
                    {
                        System.out.print("User already exists");
                        showToast("USERNAME ALREADY EXISTS");
                    }
                    else
                    {
                        // Create new profile
                        createNewProfile();
                    }
                }
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void createNewProfile()
    {
        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                        System.out.println("Create user Account function");
                        String insertQuery = "INSERT INTO Users (username, password, first_name, last_name, date_joined) VALUES ('" + user + "', '" + pass + "', '" + first + "', '" + last + "', '" + date + "');";
                        System.out.println(insertQuery);
                        // Create a user
                        Statement insertStatement = connect.createStatement();
                        insertStatement.execute(insertQuery);
                        showToast("SUCCESSFULLY CREATED ACCOUNT");
                        openMainPage(user);
                }
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showCreateConfirmation() {
        buttonShowDialog = findViewById(R.id.create_account_button);
        buttonShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonShowDialog_onClick(view);
            }
        });
    }

    private void buttonShowDialog_onClick(View view) {
        Button create_btn = (Button) findViewById(R.id.create_account_button);
        EditText profile_user = (EditText) findViewById(R.id.create_profile_username);
        EditText profile_first = (EditText) findViewById(R.id.create_user_first_name);
        EditText profile_last = (EditText) findViewById(R.id.create_user_last_name);
        EditText profile_password = (EditText) findViewById(R.id.create_user_password);
        EditText profile_password2 = (EditText) findViewById(R.id.create_user_reenter_password);

        user = profile_user.getText().toString();
        first = profile_first.getText().toString();
        last = profile_last.getText().toString();
        pass = profile_password.getText().toString();
        pass2 = profile_password2.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Create Profile " + user);
        builder.setMessage("Are you sure?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getApplicationContext(),"Ok", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();


                if (checkIfFieldsFilled())
                {
                    if (checkIfPasswordsMatch())
                    {
                        checkIfUsernameExists();
                    }
                    else
                    {
                        showToast("PASSWORDS DO NOT MATCH");
                    }
                }
                else
                {
                    showToast("NOT ALL FIELDS ARE FILLED");
                }
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

    public void openMainPage(String username)
    {
        Intent intent = new Intent(this, MainPage.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finishAffinity();
    }
}