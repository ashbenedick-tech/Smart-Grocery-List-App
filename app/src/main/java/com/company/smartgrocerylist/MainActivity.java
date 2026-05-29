package com.company.smartgrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layoutCreateList;
    private LinearLayout layoutSavedLists;
    private LinearLayout layoutFavorites;
    private LinearLayout layoutRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutCreateList = findViewById(R.id.layoutCreateList);
        layoutSavedLists = findViewById(R.id.layoutSavedLists);
        layoutFavorites = findViewById(R.id.layoutFavorites);
        layoutRecipes = findViewById(R.id.layoutRecipes);

        layoutCreateList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateListActivity.class);
            startActivity(intent);
        });

        layoutSavedLists.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedListsActivity.class);
            startActivity(intent);
        });

        layoutFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        layoutRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipeSuggestionsActivity.class);
            startActivity(intent);
        });
    }
}