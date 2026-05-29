package com.company.smartgrocerylist;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecipeSuggestionsActivity extends AppCompatActivity {

    private EditText editTextIngredient;
    private ImageButton buttonSearchRecipes;
    private ImageButton buttonViewRecipe1;
    private ImageButton buttonViewRecipe2;
    private LinearLayout layoutHome;

    private TextView textViewRecipe1;
    private TextView textViewRecipe2;

    private String recipeUrl1 = "";
    private String recipeUrl2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_suggestions);

        editTextIngredient = findViewById(R.id.editTextIngredient);
        buttonSearchRecipes = findViewById(R.id.buttonSearchRecipes);
        buttonViewRecipe1 = findViewById(R.id.buttonViewRecipe1);
        buttonViewRecipe2 = findViewById(R.id.buttonViewRecipe2);
        layoutHome = findViewById(R.id.layoutHome);

        textViewRecipe1 = findViewById(R.id.textViewRecipe1);
        textViewRecipe2 = findViewById(R.id.textViewRecipe2);

        buttonSearchRecipes.setOnClickListener(view -> searchRecipes());

        buttonViewRecipe1.setOnClickListener(view -> openRecipeUrl(recipeUrl1));
        buttonViewRecipe2.setOnClickListener(view -> openRecipeUrl(recipeUrl2));

        layoutHome.setOnClickListener(view -> {
            Intent intent = new Intent(RecipeSuggestionsActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    public void searchRecipes() {
        String ingredient = editTextIngredient.getText().toString().trim();

        if (ingredient.isEmpty()) {
            Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        textViewRecipe1.setText("Searching...");
        textViewRecipe2.setText("Searching...");
        recipeUrl1 = "";
        recipeUrl2 = "";

        AsyncTask.execute(() -> {
            HttpURLConnection connection = null;

            try {
                String urlString = "https://www.themealdb.com/api/json/v1/1/filter.php?i="
                        + ingredient.replace(" ", "%20");

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Smart Grocery List App");

                InputStream stream = new BufferedInputStream(connection.getInputStream());
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader streamReader = new BufferedReader(reader);

                String output = "";
                String line = streamReader.readLine();

                while (line != null) {
                    output += line;
                    line = streamReader.readLine();
                }

                JSONObject json = new JSONObject(output);
                JSONArray meals = json.optJSONArray("meals");

                if (meals == null || meals.length() == 0) {
                    runOnUiThread(() -> {
                        textViewRecipe1.setText("No recipes found");
                        textViewRecipe2.setText("Try another ingredient");
                        Toast.makeText(RecipeSuggestionsActivity.this,
                                "No recipes found", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                String recipeName1 = "";
                String recipeName2 = "";
                String tempUrl1 = "";
                String tempUrl2 = "";

                JSONObject meal1 = meals.getJSONObject(0);
                recipeName1 = meal1.getString("strMeal");
                String mealId1 = meal1.getString("idMeal");
                tempUrl1 = "https://www.themealdb.com/meal/" + mealId1;

                if (meals.length() > 1) {
                    JSONObject meal2 = meals.getJSONObject(1);
                    recipeName2 = meal2.getString("strMeal");
                    String mealId2 = meal2.getString("idMeal");
                    tempUrl2 = "https://www.themealdb.com/meal/" + mealId2;
                } else {
                    recipeName2 = "Only one recipe found";
                    tempUrl2 = "";
                }

                String finalRecipeName1 = recipeName1;
                String finalRecipeName2 = recipeName2;
                String finalTempUrl1 = tempUrl1;
                String finalTempUrl2 = tempUrl2;

                runOnUiThread(() -> {
                    textViewRecipe1.setText(finalRecipeName1);
                    textViewRecipe2.setText(finalRecipeName2);
                    recipeUrl1 = finalTempUrl1;
                    recipeUrl2 = finalTempUrl2;
                });

            } catch (Exception ex) {
                runOnUiThread(() -> {
                    textViewRecipe1.setText("Error getting recipes");
                    textViewRecipe2.setText("Please try again");
                    Toast.makeText(RecipeSuggestionsActivity.this,
                            "Error getting recipe data", Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void openRecipeUrl(String recipeUrl) {
        if (recipeUrl == null || recipeUrl.isEmpty()) {
            Toast.makeText(this, "No recipe link available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipeUrl));
        startActivity(intent);
    }
}