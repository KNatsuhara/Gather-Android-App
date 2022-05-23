package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        CardView profileCard = (CardView) findViewById(R.id.profile_card);

        profileCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openProfilePage();
            }
        });
    }

    public void openProfilePage()
    {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }
}