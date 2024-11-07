package com.example.yeschef.utils;

import android.content.Context;
import android.util.Log;

import com.example.yeschef.fragments.AddFragment;
import com.example.yeschef.models.Recipe;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonUtils {
    // Convert Recipe object to JSON string
    public static String convertRecipeToJson(Recipe recipe) {
        Gson gson = new Gson();
        return gson.toJson(recipe); // Converts the Recipe object to JSON string
    }

    // Method to write JSON to a text file
    public static void writeJsonToFile(Context context, Recipe recipe, String fileName) {
        String json = convertRecipeToJson(recipe); // Convert Recipe to JSON string

       File file = new File(context.getFilesDir(), fileName); // Create a file in internal storage
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(json.getBytes()); // Write JSON string to file
            fos.flush(); // Ensure all data is written
            System.out.println("JSON file created: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace(); // Handle any exceptions
        }
    }

    // This allows us to log the json file data in logcat so we don't have to search through the phones files
    public static void logJson(Recipe recipe) {
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        Log.e("JsonUtils", "Recipe JSON: " + json); // stored in alphabetical order
    }

    public static Recipe readJsonFromFile(Context context, String fileName)
    {
        Gson gson = new Gson();

        File file = new File(context.getFilesDir(), fileName);

        try (FileInputStream fis = new FileInputStream(file); // reading strings of raw bytes
             InputStreamReader isr = new InputStreamReader(fis); // reads bytes and decodes them into characters
             BufferedReader reader = new BufferedReader(isr)) { // reads text from character input stream

            // Parse the JSON content back into a Recipe object
            return gson.fromJson(reader, Recipe.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if there was an error
        }
    }
}
