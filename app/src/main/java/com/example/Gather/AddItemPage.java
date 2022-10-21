package com.example.Gather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddItemPage extends AppCompatActivity {

    public Connection connect;
    public String USERNAME;
    public String[] item_categories = {"Grocery", "Electronics", "Clothing", "Home & Appliances", "Home Improvement", "Office", "Baby", "Patio & Garden", "Household Essentials", "Beauty", "Personal Care", "Pets", "Pharmacy"};
    public String[] num_to_ten = {"1","2","3","4","5","6","7","8","9","10"};
    public ArrayList<String> user_in_groups = new ArrayList<>();
    private Button register_btn;
    private String item_name, item_brand, item_group, item_category, item_expiration_date, item_updated_date;
    private Integer item_barcode, item_quantity, item_priority, item_rating;
    private Double item_percentage, item_price;
    private String insertQuery;
    DatePickerDialog.OnDateSetListener setListener;

    private String item_type = "item";

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate time = LocalDate.now();
            item_updated_date = time.toString();
        }

        // Populates arraylist with groups a user is in
        setUpGroupList();

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
                item_group = item;
                Toast.makeText(getApplicationContext(), "Group: "+item,Toast.LENGTH_SHORT).show();
            }
        });

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
                        AddItemPage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        item_expiration_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog (
                        AddItemPage.this, new DatePickerDialog.OnDateSetListener() {
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

        updateRegisterItemButton();
    }

    public void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(AddItemPage.this, toast, Toast.LENGTH_SHORT).show());
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

    private void updateRegisterItemButton() {

        EditText item_name_box = (EditText) findViewById(R.id.edit_item_name);
        EditText item_brand_box = (EditText) findViewById(R.id.edit_brand_name);
        EditText item_quantity_box = (EditText) findViewById(R.id.edit_quantity_text);
        EditText item_barcode_box = (EditText) findViewById(R.id.edit_barcode_text);
        EditText item_percentage_box = (EditText) findViewById(R.id.edit_percentage_text);
        EditText item_price_box = (EditText) findViewById(R.id.edit_price_text);
        EditText item_expiration_box = (EditText) findViewById(R.id.edit_expiration_date_text);

        System.out.println("Username: " + USERNAME);


        register_btn = findViewById(R.id.btn_register_item);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // CHECKING IF NAME FIELD IS EMPTY
                try {
                    item_name = item_name_box.getText().toString();
                } catch (Exception e)
                {
                    System.out.println(e);
                }

                // CHECKING IF BRAND FIELD IS EMPTY
                try {
                    item_brand = item_brand_box.getText().toString();
                } catch (Exception e)
                {
                    System.out.println(e);
                }

                // CHECKING IF QUANTITY FIELD IS EMPTY
                try {
                    item_quantity = Integer.parseInt(item_quantity_box.getText().toString());
                } catch (Exception e)
                {
                    System.out.println(e);
                }

                // CHECKING IF BARCODE FIELD IS EMPTY
                try {
                    item_barcode = Integer.parseInt(item_barcode_box.getText().toString());
                } catch (Exception e)
                {
                    System.out.println(e);
                }

                // CHECKING IF PERCENTAGE FIELD IS EMPTY
                try {
                    item_percentage = Double.parseDouble(item_percentage_box.getText().toString());
                } catch (Exception e)
                {
                    System.out.println(e);
                }

                // CHECKING IF PRICE FIELD IS EMPTY
                try {
                    item_price = Double.parseDouble(item_price_box.getText().toString());
                } catch (Exception e)
                {
                    System.out.println(e);
                }

                // CHECKING IF EXPIRATION DATE FIELD IS EMPTY
                try {
                    item_expiration_date = item_expiration_box.getText().toString();
                } catch (Exception e)
                {
                    System.out.println(e);
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDate time = LocalDate.now();
                    item_updated_date = time.toString();
                }

                // Item name is a primary key and must be filled
                if (item_name.isEmpty())
                {
                    showToast("Name field required");
                    return;
                }

                // Item must belong to a group
                if (item_group.isEmpty())
                {
                    showToast("Group field required");
                    return;
                }

                // Item percentage must be greater than 0 if not null
                if (item_percentage != null && (item_percentage < 0 || item_percentage > 100))
                {
                    showToast("Percentage field must be a valid percent");
                    return;
                }

                // Item price must be greater than 0 if not null
                if (item_price != null && item_price < 0)
                {
                    showToast("Price must be positive");
                    return;
                }

                // Item quantity must be greater than 0 and must be filled
                if (item_quantity != null && item_quantity < 0)
                {
                    showToast("Quantity must be positive");
                    return;
                }

                if (item_expiration_date.isEmpty())
                {
                    item_expiration_date = null;
                }

                if (item_brand.isEmpty())
                {
                    item_brand = null;
                }

                System.out.println("Item Name: " + item_name);
                System.out.println("Item Brand: " + item_brand);
                System.out.println("Item Group: " + item_group);
                System.out.println("Item Category: " + item_category);
                System.out.println("Item Quantity: " + item_quantity);
                System.out.println("Item Barcode: " + item_barcode);
                System.out.println("Item Priority: " + item_priority);
                System.out.println("Item Rating: " + item_rating);
                System.out.println("Item Percentage: " + item_percentage);
                System.out.println("Item Price: " + item_price);
                System.out.println("Item Expiration Date: " + item_expiration_date);
                System.out.println("Username: " + USERNAME);
                System.out.println("Update Date: " + item_updated_date);
                System.out.println("Item Type: " + item_type);


                // If the expiration date is empty we need to change the insert query for the item
                if (item_expiration_date == null)
                {
                    insertQuery = "INSERT INTO Item (item_name, brand_name, item_category, quantity, barcode, item_rating, percentage, price, expiration_date, last_updated_by, last_updated_date, type, item_priority, group_name) VALUES " +
                            "('" + item_name + "', '" + item_brand + "', '" + item_category + "', " + item_quantity + ", " + item_barcode + ", " + item_rating  + ", " + item_percentage + ", " + item_price + ", " + item_expiration_date +
                            ", '" + USERNAME + "', '" + item_updated_date + "', '" + item_type + "', " + item_priority + ", '" + item_group + "');";
                }
                else
                {
                    insertQuery = "INSERT INTO Item (item_name, brand_name, item_category, quantity, barcode, item_rating, percentage, price, expiration_date, last_updated_by, last_updated_date, type, item_priority, group_name) VALUES " +
                            "('" + item_name + "', '" + item_brand + "', '" + item_category + "', " + item_quantity + ", " + item_barcode + ", " + item_rating  + ", " + item_percentage + ", " + item_price + ", '" + item_expiration_date +
                            "', '" + USERNAME + "', '" + item_updated_date + "', '" + item_type + "', " + item_priority + ", '" + item_group + "');";
                }

                // If all fields and prerequisites pass then add the new item to the database
                addNewItem();
                showToast("Registered Item");
            }
        });
    }

    public void addNewItem()
    {
        new Thread(() -> {
            try {
                // Connect to the Gather Database
                Database db = new Database();
                connect = db.getConnection();

                if (connect != null)
                {
                    System.out.println("Create user Account function");
                    // Create SQL QUERY
                    System.out.println(insertQuery);
                    // Create insert item query into gatherdb
                    Statement insertStatement = connect.createStatement();
                    insertStatement.execute(insertQuery);
                    showToast("SUCCESSFULLY CREATED ACCOUNT");
                }
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // after inserting item reset all text fields to null
                    resetAllTextFields();
                }
            });
        }).start();
    }

    public void resetAllTextFields()
    {
        EditText item_name_box = (EditText) findViewById(R.id.edit_item_name);
        EditText item_brand_box = (EditText) findViewById(R.id.edit_brand_name);
        EditText item_quantity_box = (EditText) findViewById(R.id.edit_quantity_text);
        EditText item_barcode_box = (EditText) findViewById(R.id.edit_barcode_text);
        EditText item_percentage_box = (EditText) findViewById(R.id.edit_percentage_text);
        EditText item_price_box = (EditText) findViewById(R.id.edit_price_text);
        EditText item_expiration_box = (EditText) findViewById(R.id.edit_expiration_date_text);

        item_name_box.setText(null);
        item_brand_box.setText(null);
        item_quantity_box.setText(null);
        item_barcode_box.setText(null);
        item_percentage_box.setText(null);
        item_price_box.setText(null);
        item_expiration_box.setText(null);
        autoGroupText.setText(null);
        autoPriorityText.setText(null);
        autoRatingText.setText(null);
        autoCompleteText.setText(null);
    }
}