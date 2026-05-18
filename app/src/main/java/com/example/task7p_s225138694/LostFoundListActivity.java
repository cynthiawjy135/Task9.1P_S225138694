package com.example.task7p_s225138694;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task7p_s225138694.data.DatabaseHelper;
import com.example.task7p_s225138694.data.ItemAdapter;
import com.example.task7p_s225138694.data.ItemDataModel;

import java.util.ArrayList;
import java.util.List;

public class LostFoundListActivity extends AppCompatActivity {

    ImageButton btnBackFromList;
    ItemAdapter adapter;
    SearchView searchbar;

    ArrayList<ItemDataModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lost_found_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBackFromList = findViewById(R.id.buttonBackList);

        btnBackFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 1. Get RecycleViewer from XML by id
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);

        // 2. Set the event list vertically
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. initialize adapter
        adapter = new ItemAdapter();
        recyclerView.setAdapter(adapter);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        new Thread(() -> {
            itemList = dbHelper.getAllItems();

            runOnUiThread(() -> {
                adapter.setItems(itemList);
            });
        }).start();

        //Spinner
        com.google.android.material.textfield.MaterialAutoCompleteTextView dropdown = findViewById(R.id.spinner);

        String[] options = {"Newest", "Oldest"};

        ArrayAdapter<String> adapterDropdown = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                options
        );

        dropdown.setAdapter(adapterDropdown);

        dropdown.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {

            String selected = adapterView.getItemAtPosition(i).toString();

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

            if(selected.equals("Newest")){
                java.util.Collections.sort(itemList, (a, b) -> {
                    try {
                        return sdf.parse(b.getDate()).compareTo(sdf.parse(a.getDate()));
                    } catch (Exception e) {
                        return 0;
                    }
                });
            } else {
                java.util.Collections.sort(itemList, (a, b) -> {
                    try {
                        return sdf.parse(a.getDate()).compareTo(sdf.parse(b.getDate()));
                    } catch (Exception e) {
                        return 0;
                    }
                });
            }

            adapter.setItems(itemList);

        });

        searchbar = findViewById(R.id.searchView);
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.searchFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.searchFilter(newText);
                return false;
            }
        });

        //When user click ImageButton Edit or Delete
        adapter.setOnEventButtonClickListener(new ItemAdapter.OnEventButtonClickListener() {
            @Override
            public void onDeleteClicked(ItemDataModel itm, int position) {
                // Delete the item from database via ViewModel
                DatabaseHelper dbHelper = new DatabaseHelper(LostFoundListActivity.this);

                new Thread(() -> {
                    dbHelper.deleteItem(itm.getId());

                    runOnUiThread(() -> {
                        itemList.remove(position);
                        adapter.setItems(itemList);
                    });
                }).start();
            }
        });

    }
}