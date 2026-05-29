package com.company.smartgrocerylist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SavedListsActivity extends AppCompatActivity {

    private LinearLayout layoutHomeButton;
    private LinearLayout layoutSearchButton;
    private LinearLayout layoutOpenButton1;
    private LinearLayout layoutOpenButton2;


    private LinearLayout layoutResultCard1;
    private LinearLayout layoutResultCard2;

    private TextView textViewListDate1;
    private TextView textViewListDate2;

    private CheckBox checkBoxItem1a;
    private CheckBox checkBoxItem1b;
    private CheckBox checkBoxItem1c;

    private CheckBox checkBoxItem2a;
    private CheckBox checkBoxItem2b;
    private CheckBox checkBoxItem2c;

    private EditText editTextSearchDate;

    private DBHelper dbHelper;

    private long listId1 = -1;
    private long listId2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_lists);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutHomeButton = findViewById(R.id.layoutHomeButton);
        layoutSearchButton = findViewById(R.id.layoutSearchButton);
        layoutOpenButton1 = findViewById(R.id.layoutOpenButton1);
        layoutOpenButton2 = findViewById(R.id.layoutOpenButton2);


        layoutResultCard1 = findViewById(R.id.layoutResultCard1);
        layoutResultCard2 = findViewById(R.id.layoutResultCard2);

        textViewListDate1 = findViewById(R.id.textViewListDate1);
        textViewListDate2 = findViewById(R.id.textViewListDate2);

        checkBoxItem1a = findViewById(R.id.checkBoxItem1a);
        checkBoxItem1b = findViewById(R.id.checkBoxItem1b);
        checkBoxItem1c = findViewById(R.id.checkBoxItem1c);

        checkBoxItem2a = findViewById(R.id.checkBoxItem2a);
        checkBoxItem2b = findViewById(R.id.checkBoxItem2b);
        checkBoxItem2c = findViewById(R.id.checkBoxItem2c);

        editTextSearchDate = findViewById(R.id.editTextSearchDate);

        dbHelper = new DBHelper(this);

        layoutHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SavedListsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        layoutSearchButton.setOnClickListener(v -> searchSavedLists());

        layoutOpenButton1.setOnClickListener(v -> {
            if (listId1 != -1) {
                Intent intent = new Intent(SavedListsActivity.this, OpenSavedListActivity.class);
                intent.putExtra("listId", listId1);
                startActivity(intent);
            }
        });

        layoutOpenButton2.setOnClickListener(v -> {
            if (listId2 != -1) {
                Intent intent = new Intent(SavedListsActivity.this, OpenSavedListActivity.class);
                intent.putExtra("listId", listId2);
                startActivity(intent);
            }
        });



        loadLatestLists();
    }

    private void loadLatestLists() {
        Cursor cursor = dbHelper.getAllLists();
        displayLists(cursor);
    }

    private void searchSavedLists() {
        String searchDate = editTextSearchDate.getText().toString().trim();

        if (searchDate.isEmpty()) {
            editTextSearchDate.setError("Enter MM/YYYY");
            return;
        }

        Cursor cursor = dbHelper.getListsByMonthYear(searchDate);
        displayLists(cursor);
    }

    private void displayLists(Cursor cursor) {
        listId1 = -1;
        listId2 = -1;

        layoutResultCard1.setVisibility(View.GONE);
        layoutResultCard2.setVisibility(View.GONE);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int cardNumber = 1;

                do {
                    long listId = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LIST_ID));
                    String dateCreated = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DATE_CREATED));

                    if (cardNumber == 1) {
                        listId1 = listId;
                        textViewListDate1.setText("Grocery List / " + dateCreated);
                        loadItemsIntoCard(listId, 1);
                        layoutResultCard1.setVisibility(View.VISIBLE);
                    } else if (cardNumber == 2) {
                        listId2 = listId;
                        textViewListDate2.setText("Grocery List / " + dateCreated);
                        loadItemsIntoCard(listId, 2);
                        layoutResultCard2.setVisibility(View.VISIBLE);
                    }

                    cardNumber++;
                } while (cursor.moveToNext() && cardNumber <= 2);

            } else {
                Toast.makeText(this, "No saved lists found", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void loadItemsIntoCard(long listId, int cardNumber) {
        Cursor cursor = dbHelper.getItemsForList(listId);

        CheckBox[] boxes;

        if (cardNumber == 1) {
            boxes = new CheckBox[]{checkBoxItem1a, checkBoxItem1b, checkBoxItem1c};
        } else {
            boxes = new CheckBox[]{checkBoxItem2a, checkBoxItem2b, checkBoxItem2c};
        }

        for (CheckBox box : boxes) {
            box.setVisibility(View.GONE);
            box.setChecked(false);
        }

        try {
            int index = 0;

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (index >= 3) {
                        break;
                    }

                    String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ITEM_NAME));
                    String quantity = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_QUANTITY));

                    boxes[index].setText(itemName + " (" + quantity + ")");
                    boxes[index].setVisibility(View.VISIBLE);

                    index++;
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }



            }


