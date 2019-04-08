package com.comp3330.swipeassist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StartupScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_screen);
    }

    public void buttonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        if (view.getId() == R.id.get_button){
            intent.putExtra("frg_to_load", 0);

        } else if (view.getId() == R.id.give_button){
            intent.putExtra("frg_to_load", 1);


        } else if (view.getId() == R.id.view_button){
            intent.putExtra("frg_to_load", 2);
        }

        this.startActivity(intent);

    }
}
