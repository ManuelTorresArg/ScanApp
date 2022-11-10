package com.example.scanapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.scanapp.databinding.ConfigActivityBinding;
import com.example.scanapp.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity {

    MainActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.search:
                        Toast.makeText(MainActivity.this, "Search!!", Toast.LENGTH_SHORT).show();
                        Log.d("Announcement", "Received Search");
                        break;
                    case R.id.config:
                        Intent myIntent = new Intent(MainActivity.this,ConfigActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                    case R.id.salir:

                }


                return false;
            }
        });

    }

}
