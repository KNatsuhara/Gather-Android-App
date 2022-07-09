package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class AddItemPage extends AppCompatActivity {

    public Connection connect;
    public String USERNAME;
    public String[] item_categories = {"Grocery", "Electronics", "Clothing", "Home & Appliances", "Home Improvement", "Office", "Baby", "Patio & Garden", "Household Essentials", "Beauty", "Personal Care", "Pets", "Pharmacy"};
    public String[] num_to_ten = {"1","2","3","4","5","6","7","8","9","10"};
    public ArrayList<String> user_in_groups = new ArrayList<>();

    AutoCompleteTextView autoCompleteText, autoRatingText, autoPriorityText, autoGroupText;
    ArrayAdapter<String> adapterItems, adapterRatings, adapterPriority, adapterGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_page);

        // Grabs the username that is using the app
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

        // Populates arraylist with groups a user is in
        setUpGroupList();

        // Sets the drop menu for the item categories
        autoCompleteText = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, item_categories);
        autoCompleteText.setAdapter(adapterItems);

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });

        // Sets the drop menu for the item rating
        autoRatingText = findViewById(R.id.auto_rate_text);
        adapterRatings = new ArrayAdapter<>(this, R.layout.list_item, num_to_ten);
        autoRatingText.setAdapter(adapterRatings);

        autoRatingText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Rating: "+item,Toast.LENGTH_SHORT).show();
            }
        });

        // Sets the drop menu for the item priority
        autoPriorityText = findViewById(R.id.auto_priority_text);
        adapterPriority = new ArrayAdapter<>(this, R.layout.list_item, num_to_ten);
        autoPriorityText.setAdapter(adapterPriority);

        autoPriorityText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Priority: "+item,Toast.LENGTH_SHORT).show();
            }
        });

        for (int i = 0; i < user_in_groups.size(); i++)
        {
            System.out.println(user_in_groups.get(i));
        }

        // Sets the drop menu for the item group
        autoGroupText = findViewById(R.id.auto_group_text);
        adapterGroup = new ArrayAdapter<>(this, R.layout.list_item, user_in_groups);
        autoGroupText.setAdapter(adapterGroup);

        autoGroupText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Group: "+item,Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpGroupList() {
        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null) {
                    // USER IN GROUPS
                    String query = "SELECT group_name FROM UserInGroup WHERE username = '" + USERNAME + "' ORDER BY group_name ASC;";
                    System.out.println(query);
                    Statement statement = connect.createStatement();

                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                        user_in_groups.add(rs.getString(1));
                    }
                    System.out.println("EXECUTED QUERY");
                }
                connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}