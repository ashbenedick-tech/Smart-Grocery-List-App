package com.company.smartgrocerylist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class OpenSavedListActivity extends AppCompatActivity {

    private LinearLayout layoutHomeButton;
    private LinearLayout layoutSavedListsButton;
    private LinearLayout layoutAddToCurrentListButton;
    private LinearLayout layoutAllItems;

    private TextView textViewListDate;

    private DBHelper dbHelper;
    private long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_saved_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutHomeButton = findViewById(R.id.layoutHomeButton);
        layoutSavedListsButton = findViewById(R.id.layoutSavedListsButton);
        layoutAddToCurrentListButton = findViewById(R.id.layoutAddToCurrentListButton);
        layoutAllItems = findViewById(R.id.layoutAllItems);
        textViewListDate = findViewById(R.id.textViewListDate);

        dbHelper = new DBHelper(this);

        listId = getIntent().getLongExtra("listId", -1);

        layoutHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(OpenSavedListActivity.this, MainActivity.class);
            startActivity(intent);
        });

        layoutSavedListsButton.setOnClickListener(v -> {
            Intent intent = new Intent(OpenSavedListActivity.this, SavedListsActivity.class);
            startActivity(intent);
        });

        layoutAddToCurrentListButton.setOnClickListener(v -> addCheckedItemsToCurrentList());

        if (listId != -1) {
            loadSavedList();
        }
    }

    private void loadSavedList() {
        String dateCreated = dbHelper.getListDateById(listId);
        textViewListDate.setText("Date: " + dateCreated);

        Cursor cursor = dbHelper.getItemsForList(listId);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ITEM_NAME));
                    String quantity = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_QUANTITY));

                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(itemName + " (" + quantity + ")");
                    checkBox.setTextSize(22);
                    checkBox.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

                    layoutAllItems.addView(checkBox);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void addCheckedItemsToCurrentList() {
        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<String> itemQuantities = new ArrayList<>();

        for (int i = 0; i < layoutAllItems.getChildCount(); i++) {
            if (layoutAllItems.getChildAt(i) instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) layoutAllItems.getChildAt(i);

                if (checkBox.isChecked()) {
                    String text = checkBox.getText().toString();

                    int openParen = text.lastIndexOf("(");
                    int closeParen = text.lastIndexOf(")");

                    if (openParen != -1 && closeParen != -1 && openParen < closeParen) {
                        String itemName = text.substring(0, openParen).trim();
                        String quantity = text.substring(openParen + 1, closeParen).trim();

                        itemNames.add(itemName);
                        itemQuantities.add(quantity);
                    }
                }
            }
        }

        if (itemNames.isEmpty()) {
            Toast.makeText(this, "Select at least one item", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(OpenSavedListActivity.this, CreateListActivity.class);
        intent.putStringArrayListExtra("itemNames", itemNames);
        intent.putStringArrayListExtra("itemQuantities", itemQuantities);
        startActivity(intent);
    }
}