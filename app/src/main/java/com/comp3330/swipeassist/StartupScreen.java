package com.comp3330.swipeassist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StartupScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_screen);
    }

    public void buttonClicked(View view) {

        if (view.getId() == R.id.get_button){

        } else if (view.getId() == R.id.give_button){

        } else if (view.getId() == R.id.view_button){

        }
    }
}
