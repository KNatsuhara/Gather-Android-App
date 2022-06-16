package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GroupDetailsPage extends AppCompatActivity {

    public String GROUP_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details_page);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                GROUP_NAME= null;
            } else {
                GROUP_NAME= extras.getString("NAME");
            }
        } else {
            GROUP_NAME = (String) savedInstanceState.getSerializable("NAME");
        }

        System.out.println("THIS IS THE GROUP NAME: " + GROUP_NAME);
    }
}