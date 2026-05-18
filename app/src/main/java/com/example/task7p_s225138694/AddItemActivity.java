package com.example.task7p_s225138694;

import static java.security.AccessController.getContext;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Preconditions;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.task7p_s225138694.data.DatabaseHelper;
import com.example.task7p_s225138694.data.ItemDataModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    ImageButton btnBack;
    Button btnSave, btnUpload;
    EditText txtName, txtPhone, txtDesc, txtDate, txtLoc;
    RadioGroup rgType;

    ImageView ivPreview;

    private DatabaseHelper dbHelper;

    String imagePath = "";

    private double lat = 0.0;
    private double longt = 0.0;

    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;


    private final ActivityResultLauncher<Intent> placeAutocompleteResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode()
                                == AutocompleteActivity.RESULT_OK
                                && result.getData() != null) {

                            Intent intent = result.getData();

                            Place place = Autocomplete.getPlaceFromIntent(intent);

                            txtLoc.setText(place.getAddress());

                            // get and store lat and long
                            if (place.getLatLng() != null) {

                                lat = place.getLatLng().latitude;
                                longt = place.getLatLng().longitude;

                                Log.d("PLACE",
                                        "Lat: " + lat +
                                                ", Long: " + longt);
                            }

                        } else if (result.getResultCode()
                                == AutocompleteActivity.RESULT_CANCELED) {

                            Log.d("PLACE", "User canceled");

                        }
                    }
            );

    private final ActivityResultLauncher<String> imgPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    //Grant permission for uri
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    imagePath = uri.toString();
                    ivPreview.setVisibility(View.VISIBLE);
                    Glide.with(AddItemActivity.this)
                            .load(uri)
                            .centerCrop()
                            .into(ivPreview);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

        btnSave = findViewById(R.id.btnSave);
        btnUpload = findViewById(R.id.btnUploadImg);
        txtName = findViewById(R.id.editTextName);
        txtPhone = findViewById(R.id.editTextPhone);
        txtDesc = findViewById(R.id.editTextDesc);
        txtDate = findViewById(R.id.editTextDate);
        ivPreview = findViewById(R.id.ivPreview);

        //  DatePickerDialog when EditTextDate is clicked
        txtDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddItemActivity.this,
                    (view1, year1, month1, dayOfMonth1) -> {
                        // Format date that is chosen
                        String selectedDate = (month1 + 1) + "/" + dayOfMonth1 + "/" + year1;
                        txtDate.setText(selectedDate); // Show selected Date in EditText
                    },
                    year, month, dayOfMonth
            );

            // Show dialog
            datePickerDialog.show();
        });

        txtLoc = findViewById(R.id.editTextLoc);
        rgType = findViewById(R.id.radioGroupLostFound);

        dbHelper = new DatabaseHelper(this);

        btnBack = findViewById(R.id.buttonBack);

        txtLoc.setFocusable(false);
        txtLoc.setClickable(true);
        txtLoc.setOnClickListener(v -> {
            launchPlaceAutocomplete();
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUpload.setOnClickListener(v -> imgPickerLauncher.launch("image/*"));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtName.getText().toString().isEmpty() ||
                        txtPhone.getText().toString().isEmpty() ||
                        txtDesc.getText().toString().isEmpty() ||
                        txtDate.getText().toString().isEmpty() ||
                        txtLoc.getText().toString().isEmpty()||
                        imagePath.isEmpty()
                )
                {
                    Toast.makeText(AddItemActivity.this, "All input fields should be filled!", Toast.LENGTH_SHORT).show();
                }else{
                    int selectedRadioButtonId = rgType.getCheckedRadioButtonId();
                    if (selectedRadioButtonId == -1) {
                        Toast.makeText(AddItemActivity.this, "Please select an item type", Toast.LENGTH_SHORT).show();
                    } else {
                        // get selected radio button
                        String selectedType = ((RadioButton) findViewById(selectedRadioButtonId)).getText().toString();
                        String name = txtName.getText().toString();
                        String phone = txtPhone.getText().toString();
                        String desc = txtDesc.getText().toString();
                        String date = txtDate.getText().toString();
                        String loc = txtLoc.getText().toString();

                        ItemDataModel newItem = new ItemDataModel(name, selectedType, phone, desc, date, loc,imagePath, lat, longt);

                        dbHelper.addItem(newItem);

                        // Back to previous Activity
                        finish();
                    }
                }
            }
        });
    }

    private void launchPlaceAutocomplete() {

        java.util.List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields
        ).build(this);

        placeAutocompleteResultLauncher.launch(intent);
    }

}