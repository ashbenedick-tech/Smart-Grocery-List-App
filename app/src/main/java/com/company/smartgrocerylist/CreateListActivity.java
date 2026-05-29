package com.company.smartgrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CreateListActivity extends AppCompatActivity {

    private LinearLayout layoutHomeButton;
    private LinearLayout layoutAddItemButton;
    private LinearLayout layoutSaveListButton;

    private EditText editTextItem;
    private EditText editTextQuantity;
    private TextView textViewCurrentList;

    private DBHelper dbHelper;

    private ArrayList<String> itemNames;
    private ArrayList<String> itemQuantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutHomeButton = findViewById(R.id.layoutHomeButton);
        layoutAddItemButton = findViewById(R.id.layoutAddItemButton);
        layoutSaveListButton = findViewById(R.id.layoutSaveListButton);

        editTextItem = findViewById(R.id.editTextItem);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        textViewCurrentList = findViewById(R.id.textViewCurrentList);

        dbHelper = new DBHelper(this);

        itemNames = new ArrayList<>();
        itemQuantities = new ArrayList<>();

        textViewCurrentList.setText("");

        loadIncomingItems();

        layoutHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateListActivity.this, MainActivity.class);
            startActivity(intent);
        });

        layoutAddItemButton.setOnClickListener(v -> addItemToCurrentList());

        layoutSaveListButton.setOnClickListener(v -> saveCurrentList());
    }

    private void loadIncomingItems() {
        ArrayList<String> incomingNames = getIntent().getStringArrayListExtra("itemNames");
        ArrayList<String> incomingQuantities = getIntent().getStringArrayListExtra("itemQuantities");

        if (incomingNames != null && incomingQuantities != null) {
            for (int i = 0; i < incomingNames.size(); i++) {
                itemNames.add(incomingNames.get(i));
                itemQuantities.add(incomingQuantities.get(i));
            }
            updateCurrentListDisplay();
        }
    }

    private void addItemToCurrentList() {
        String item = editTextItem.getText().toString().trim();
        String quantity = editTextQuantity.getText().toString().trim();

        if (item.isEmpty()) {
            editTextItem.setError("Enter an item");
            return;
        }

        if (quantity.isEmpty()) {
            quantity = "1";
        }

        itemNames.add(item);
        itemQuantities.add(quantity);

        updateCurrentListDisplay();

        editTextItem.setText("");
        editTextQuantity.setText("");
        editTextItem.requestFocus();
    }

    private void updateCurrentListDisplay() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < itemNames.size(); i++) {
            builder.append(itemNames.get(i))
                    .append(" (")
                    .append(itemQuantities.get(i))
                    .append(")");

            if (i < itemNames.size() - 1) {
                builder.append("\n");
            }
        }

        textViewCurrentList.setText(builder.toString());
    }

    private void saveCurrentList() {
        if (itemNames.isEmpty()) {
            Toast.makeText(this, "Add at least one item first", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateCreated = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());

        long listId = dbHelper.addGroceryList(dateCreated);

        for (int i = 0; i < itemNames.size(); i++) {
            dbHelper.addGroceryItem(listId, itemNames.get(i), itemQuantities.get(i));
        }

        Toast.makeText(this, "List saved", Toast.LENGTH_SHORT).show();

        itemNames.clear();
        itemQuantities.clear();
        textViewCurrentList.setText("");
        editTextItem.setText("");
        editTextQuantity.setText("");
    }
}