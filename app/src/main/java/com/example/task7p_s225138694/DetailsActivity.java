package com.example.task7p_s225138694;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.task7p_s225138694.data.DatabaseHelper;
import com.example.task7p_s225138694.data.ItemDataModel;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    ImageButton btnBackFromDetail;

    TextView txtTitle, txtTime, txtDescrption, txtLoc, txtType;
    ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int itemId = getIntent().getIntExtra("item_id", -1);

        btnBackFromDetail = findViewById(R.id.buttonBackDetail);

        btnBackFromDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgView = findViewById(R.id.imgViewDetail);

        txtTitle = findViewById(R.id.textViewTitleDetail);
        txtDescrption = findViewById(R.id.textViewDescDetail);
        txtTime = findViewById(R.id.textViewTimeDetail);
        txtLoc = findViewById(R.id.textViewLocDetail);
        txtType = findViewById(R.id.textViewTypeDetail);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ItemDataModel itm = dbHelper.getItemByID(itemId);

        if(itm != null) {
            android.net.Uri imageUri = android.net.Uri.parse(itm.getImagePath());
            imgView.setImageURI(imageUri);

            txtTitle.setText(itm.getName());
            txtDescrption.setText(itm.getDescription());
            txtTime.setText(itm.getDate());
            txtLoc.setText(itm.getLocation());
            txtType.setText(itm.getType());

        }
    }
}