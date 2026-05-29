package com.company.smartgrocerylist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private LinearLayout layoutHomeButton;
    private LinearLayout layoutSearchButton;
    private LinearLayout layoutAddToCurrentListButton;
    private LinearLayout layoutFavoriteItems;

    private EditText editTextSearchMonth;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutHomeButton = findViewById(R.id.layoutHomeButton);
        layoutSearchButton = findViewById(R.id.layoutSearchButton);
        layoutAddToCurrentListButton = findViewById(R.id.layoutAddToCurrentListButton);
        layoutFavoriteItems = findViewById(R.id.layoutFavoriteItems);

        editTextSearchMonth = findViewById(R.id.editTextSearchMonth);

        dbHelper = new DBHelper(this);

        layoutHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        layoutSearchButton.setOnClickListener(v -> searchMonthlyFavorites());

        layoutAddToCurrentListButton.setOnClickListener(v -> addCheckedFavoritesToCurrentList());
    }

    private void searchMonthlyFavorites() {
        String monthYear = editTextSearchMonth.getText().toString().trim();

        if (monthYear.isEmpty()) {
            editTextSearchMonth.setError("Enter MM/YYYY");
            return;
        }

        loadFavoriteItems(monthYear);
    }

    private void loadFavoriteItems(String monthYear) {
        layoutFavoriteItems.removeAllViews();

        Cursor cursor = dbHelper.getMonthlyFavoriteItems(monthYear);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ITEM_NAME));
                    int count = cursor.getInt(cursor.getColumnIndexOrThrow("item_count"));

                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(itemName + " (" + count + ")");
                    checkBox.setTextSize(20);
                    checkBox.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

                    layoutFavoriteItems.addView(checkBox);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "No favorites found for that month", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void addCheckedFavoritesToCurrentList() {
        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<String> itemQuantities = new ArrayList<>();

        for (int i = 0; i < layoutFavoriteItems.getChildCount(); i++) {
            if (layoutFavoriteItems.getChildAt(i) instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) layoutFavoriteItems.getChildAt(i);

                if (checkBox.isChecked()) {
                    String text = checkBox.getText().toString();
                    int index = text.lastIndexOf("(");

                    if (index != -1) {
                        String itemName = text.substring(0, index).trim();
                        itemNames.add(itemName);
                        itemQuantities.add("1");
                    }
                }
            }
        }

        if (itemNames.isEmpty()) {
            Toast.makeText(this, "Select at least one favorite item", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(FavoritesActivity.this, CreateListActivity.class);
        intent.putStringArrayListExtra("itemNames", itemNames);
        intent.putStringArrayListExtra("itemQuantities", itemQuantities);
        startActivity(intent);
    }
}