package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class InventoryPage extends AppCompatActivity implements IM_RecyclerViewInterface{

    public String USERNAME;
    public Connection connect;
    public ArrayList<ItemModel> itemModels = new ArrayList<>();
    public ArrayList<GroupModel> groupModels = new ArrayList<>();
    public ArrayList<String> strModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_page);

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

        getShownGroups();
    }

    private void printSetUpItemModels()
    {
        System.out.println("SETTING UP ITEM MODELS");
        setUpItemModels();
    }

    private void printItemModel()
    {
        System.out.println("PRINTING ITEM MODELS");
        for (int i = 0; i < itemModels.size(); i++)
        {
            System.out.println(itemModels.get(i).getName());
        }
    }

    private void getShownGroups() {
        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null) {
                    // INCLUDE GROUPS THAT HAVE view_status = show
                    String query = "SELECT group_name, view_status FROM UserInGroup WHERE username = '" + USERNAME + "' AND view_status = 'show' ORDER BY group_name ASC;";
                    System.out.println(query);
                    Statement statement = connect.createStatement();

                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                        groupModels.add(new GroupModel(rs.getString(1), rs.getString(2)));
                    }
                    System.out.println("EXECUTED QUERY");
                }
                connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // After adding groups to which users will view
            // Create item models and add them to the arraylist
            printSetUpItemModels();

        }).start();

    }

    private void setUpItemModels() {
        new Thread(() -> {
            try {
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null) {
                    // USER IN GROUPS
                    String query = "SELECT item_name, brand_name, item_category, last_updated_by, group_name, expiration_date, last_updated_date, quantity, barcode, item_rating, item_priority, percentage, price FROM Item ";

                    for (int i = 0; i < groupModels.size(); i++)
                    {
                        if (i == 0)
                        {
                            // Will show the items corresponding to that group name
                            query = query + "WHERE group_name = '" + groupModels.get(i).getGroupName() + "' ";
                        }
                        else
                        {
                            // Will show more items belonging to a different group
                            query =  query + "OR group_name = '" + groupModels.get(i).getGroupName() + "' ";
                        }
                    }

                    // Will sort the items by item_name in alphabetical order increasing
                    query = query + "ORDER BY item_name ASC;";

                    System.out.println(query);
                    Statement statement = connect.createStatement();

                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                        itemModels.add(new ItemModel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                                rs.getDate(6), rs.getDate(7),rs.getInt(8),rs.getInt(9),rs.getInt(10),rs.getInt(11),
                                rs.getDouble(12),rs.getDouble(13)));
                    }
                    System.out.println("EXECUTED QUERY");
                }
                connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Setting up recycler view
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // after the job is finished:
                    RecyclerView recyclerView = findViewById(R.id.inventory_recycler_view);
                    IM_RecyclerViewAdapter adapter = new IM_RecyclerViewAdapter(InventoryPage.this, itemModels, InventoryPage.this);
                    recyclerView.setAdapter((adapter));
                    recyclerView.setLayoutManager(new LinearLayoutManager(InventoryPage.this));
                }
            });

             printItemModel();
        }).start();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ItemDetailsPage.class);

        intent.putExtra("ITEM_NAME", itemModels.get(position).getName());
        intent.putExtra("USERNAME", USERNAME);

        startActivity(intent);
    }
}