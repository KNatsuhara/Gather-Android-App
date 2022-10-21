package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class ItemDetailsPage extends AppCompatActivity {

    String ITEM_NAME, GROUP_NAME, USERNAME;
    public String[] item_categories = {"Grocery", "Electronics", "Clothing", "Home & Appliances", "Home Improvement", "Office", "Baby", "Patio & Garden", "Household Essentials", "Beauty", "Personal Care", "Pets", "Pharmacy"};
    public String[] num_to_ten = {"1","2","3","4","5","6","7","8","9","10"};
    public ArrayList<String> user_in_groups = new ArrayList<>();

    private String insertQuery, item_category, item_group;
    private int item_rating, item_priority;
    public String item_updated_date;
    DatePickerDialog.OnDateSetListener setListener;

    AutoCompleteTextView autoCompleteText, autoRatingText, autoPriorityText, autoGroupText;
    ArrayAdapter<String> adapterItems, adapterRatings, adapterPriority, adapterGroup;

    public String BRAND_NAME, ITEM_CATEGORY;
    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details_page);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                ITEM_NAME= null;
                USERNAME = null;
            } else {
                ITEM_NAME= extras.getString("ITEM_NAME");
                USERNAME= extras.getString("USERNAME");
                GROUP_NAME = extras.getString("GROUP_NAME");
            }
        } else {
            ITEM_NAME = (String) savedInstanceState.getSerializable("ITEM_NAME");
            USERNAME = (String) savedInstanceState.getSerializable("USERNAME");
            GROUP_NAME = (String) savedInstanceState.getSerializable("GROUP_NAME");
        }

        // Gets the current date
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate time = LocalDate.now();
            item_updated_date = time.toString();
        }

        System.out.println("THIS IS THE ITEM NAME: " + ITEM_NAME);
        System.out.println("THIS IS THE USERNAME: " + USERNAME);
        System.out.println("THIS IS THE GROUP NAME: " + GROUP_NAME);

        // Populates arraylist with groups a user is in
        setUpGroupList();

        // Sets up the auto menus for categories, rating, priority, and groups
        setupItemCategoriesAutoMenu();
        setupItemRatingAutoMenu();
        setupItemPriorityAutoMenu();
        setupItemGroupAutoMenu();

        // Creates date dialog selector for expiration date
        setupExpirationDateDialog(ItemDetailsPage.this);

        // Finds the item details from Gather Database and fills in corresponding fields
        //populateItemPage();
    }

    private void setupItemCategoriesAutoMenu()
    {
        // Sets the drop menu for the item categories
        autoCompleteText = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, item_categories);
        autoCompleteText.setAdapter(adapterItems);

        // OnClickListener for the drop down menu
        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                item_category = item;
                Toast.makeText(getApplicationContext(), "Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupItemRatingAutoMenu()
    {
        // Sets the drop menu for the item rating
        autoRatingText = findViewById(R.id.auto_rate_text);
        adapterRatings = new ArrayAdapter<>(this, R.layout.list_item, num_to_ten);
        autoRatingText.setAdapter(adapterRatings);

        autoRatingText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                item_rating = Integer.parseInt(item);
                Toast.makeText(getApplicationContext(), "Rating: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupItemPriorityAutoMenu()
    {
        // Sets the drop menu for the item priority
        autoPriorityText = findViewById(R.id.auto_priority_text);
        adapterPriority = new ArrayAdapter<>(this, R.layout.list_item, num_to_ten);
        autoPriorityText.setAdapter(adapterPriority);

        autoPriorityText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                item_priority = Integer.parseInt(item);
                Toast.makeText(getApplicationContext(), "Priority: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupItemGroupAutoMenu()
    {
        // Sets the drop menu for the item group
        autoGroupText = findViewById(R.id.auto_group_text);
        adapterGroup = new ArrayAdapter<>(this, R.layout.list_item, user_in_groups);
        autoGroupText.setAdapter(adapterGroup);

        autoGroupText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                item_group = item;
                Toast.makeText(getApplicationContext(), "Group: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupExpirationDateDialog(Context context)
    {
        // SETTING UP CALENDAR DATE DIALOG FOR EXPIRATION DATE
        EditText item_expiration_box = (EditText) findViewById(R.id.edit_expiration_date_text);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        item_expiration_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        item_expiration_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog (
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String exprDate = year + "/" + month + "/" + day;
                        System.out.println("expiration date: " + exprDate);
                        item_expiration_box.setText(exprDate);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(ItemDetailsPage.this, toast, Toast.LENGTH_SHORT).show());
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

//    public void populateItemPage()
//    {
//        new Thread(() -> {
//            EditText item_name_text_view = (EditText) findViewById(R.id.edit_item_name);
//            EditText brand_text_view = (EditText) findViewById(R.id.edit_brand_name);
//            TextView category_text_view = (TextView) findViewById(R.id.item_category_text_view);
//            EditText quantity_text_view = (EditText) findViewById(R.id.edit_quantity_text);
//            EditText barcode_text_view = (EditText) findViewById(R.id.edit_barcode_text);
//            EditText rating_text_view = (EditText) findViewById(R.id.rating_text);
//            EditText percentage_text_view = (EditText) findViewById(R.id.edit_percentage_text);
//            EditText price_text_view = (EditText) findViewById(R.id.edit_price_text);
//            EditText expiration_date_text_view = (EditText) findViewById(R.id.edit_expiration_date_text);
//            TextView last_updated_by_text_view = (TextView) findViewById(R.id.last_updated_text);
//            TextView last_updated_date_text_view = (TextView) findViewById(R.id.last_updated_date);
//            EditText priority_text_view = (EditText) findViewById(R.id.priority_text);
//            EditText group_text_view = (EditText) findViewById(R.id.group_text);
//
//            StringBuilder group_name_list = new StringBuilder();
//
//            try {
//                Database db = new Database();
//                connect = db.getConnection();
//
//                if (connect != null)
//                {
//                    // PROFILE DETAILS
//                    String query = "SELECT password, first_name, last_name, date_joined FROM Users WHERE Users.username = '" + USERNAME + "';";
//                    System.out.println(query);
//                    Statement statement = connect.createStatement();
//                    System.out.println("Created statement");
//
////                    ResultSet rs = statement.executeQuery(query);
////                    while (rs.next()) {
////                        PASSWORD = rs.getString(1);
////                        FIRSTNAME = rs.getString(2);
////                        LASTNAME = rs.getString(3);
////                        DATE = rs.getString(4);
////                    }
////                    System.out.println("EXECUTED QUERY");
////
////                    System.out.println(PASSWORD);
////                    System.out.println(DATE);
////                    System.out.println(FIRSTNAME + " " + LASTNAME);
//
//
//                    // USER IN GROUPS
//                    query = "SELECT group_name, date_joined  FROM UserInGroup WHERE username = '" + USERNAME + "';";
//                    System.out.println(query);
//                    statement = connect.createStatement();
//                    System.out.println("Created statement");
//
//                    rs = statement.executeQuery(query);
//                    while (rs.next()) {
//                        System.out.println("1:" + rs.getString(1));
//                        group_name_list.append(rs.getString(1) + " - " + rs.getString(2) + '\n');
//                    }
//                    System.out.println("EXECUTED QUERY");
//                }
//                connect.close();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
////            runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    // after the job is finished:
////                    name_text_view.setText(FIRSTNAME + " " + LASTNAME);
////                    password_text_view.setText(PASSWORD);
////                    date_text_view.setText(DATE);
////                    group_in_text_view.setText(group_name_list.toString());
////                }
//            });
//        }).start();
//    }
}