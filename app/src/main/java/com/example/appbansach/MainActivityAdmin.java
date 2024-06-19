package com.example.appbansach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivityAdmin extends AppCompatActivity {
    private FrameLayout infoSach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        // Initialize infoSach FrameLayout correctly
        infoSach = findViewById(R.id.infoSach);

        // Set onClickListener for infoSach FrameLayout
        infoSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityAdmin.this, ListBookActivity.class);
                startActivity(intent);
            }
        });
    }
}
