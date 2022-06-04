package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainPage extends AppCompatActivity {

    public String USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

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

        CardView profileCard = (CardView) findViewById(R.id.profile_card);
        CardView group_card = (CardView) findViewById(R.id.group_card);
        CardView item_card = (CardView) findViewById(R.id.item_card);
        CardView chore_card = (CardView) findViewById(R.id.chore_card);
        CardView inventory_card = (CardView) findViewById(R.id.inventory_card);
        CardView todolist_card = (CardView) findViewById(R.id.to_do_list_card);
        CardView grocery_card = (CardView) findViewById(R.id.grocery_list_card);
        CardView last_updated_card = (CardView) findViewById(R.id.last_updated_card);
        CardView message_card = (CardView) findViewById(R.id.message_card);
        CardView help_card = (CardView) findViewById(R.id.help_card);

        // OPENING PROFILE PAGE
        profileCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Username: " + USERNAME);
                openProfilePage();
            }
        });

        // OPENING GROUP PAGE
        group_card.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Username: " + USERNAME);
                openGroupPage();
            }
        });
    }

    public void openProfilePage()
    {
        Intent intent = new Intent(this, ProfilePage.class);
        intent.putExtra("username", USERNAME);
        startActivity(intent);
    }

    public void openGroupPage()
    {
        Intent intent = new Intent(this, GroupPage.class);
        intent.putExtra("username", USERNAME);
        startActivity(intent);
    }
}