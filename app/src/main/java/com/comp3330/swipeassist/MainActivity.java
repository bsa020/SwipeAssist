package com.comp3330.swipeassist;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView nvDrawer;
    private Toolbar toolbar;

    //feedbackClick variables
    RelativeLayout layout2;
    RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        int intentFragment = getIntent().getExtras().getInt("frg_to_load");

        Fragment fragment = null;
        Class fragmentClass;

        switch (intentFragment){
            case 0:
                fragmentClass = GetFragment.class;
                setTitle(getString(R.string.get_advice));
                break;
            case 1:
                fragmentClass = GiveFragment.class;
                setTitle(getString(R.string.give_advice));
                break;
            case 2:
                fragmentClass = ViewFragment.class;
                setTitle(getString(R.string.view_advice));
                break;
            default:
                fragmentClass = GetFragment.class;
                setTitle(getString(R.string.get_advice));

        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();



    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_get_fragment:
                fragmentClass = GetFragment.class;
                break;
            case R.id.nav_give_fragment:
                fragmentClass = GiveFragment.class;
                break;
            case R.id.nav_view_fragment:
                fragmentClass = ViewFragment.class;
                break;
            case R.id.settings_fragment:
                fragmentClass = SettingsFragment.class;
                break;
            default:
                fragmentClass = GetFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }



    public static class GetFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.get_fragment, container, false);
        }
    }

    public static class GiveFragment extends Fragment {
        boolean isUp;

        // This is the gesture detector compat instance.
        private GestureDetectorCompat gestureDetectorCompat = null;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            isUp = false;

            View returnView = inflater.inflate(R.layout.give_fragment, container, false);
            Button feedbackBut = (Button) returnView.findViewById(R.id.feedback_button);

            final RelativeLayout mainLayout = returnView.findViewById(R.id.relativeLayout);
            final RelativeLayout secondLayout = returnView.findViewById(R.id.relativeLayout2);
            feedbackBut.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if (isUp) {
                        mainLayout.animate().translationYBy(secondLayout.getHeight());
                        secondLayout.animate().translationYBy(secondLayout.getHeight());

                    } else {
                        mainLayout.animate().translationYBy(-secondLayout.getHeight());
                        secondLayout.animate().translationYBy(-secondLayout.getHeight());
                    }
                    isUp = !isUp;
                }
            });

            final GestureDetector gesture = new GestureDetector(getActivity(),
                    new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onDown(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                               float velocityY) {
                            final int SWIPE_MIN_DISTANCE = 120;
                            final int SWIPE_MAX_OFF_PATH = 250;
                            final int SWIPE_THRESHOLD_VELOCITY = 200;
                            try {
                                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
                                    return false;
                                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                    if (!isUp){
                                        mainLayout.animate().translationYBy(-secondLayout.getHeight());
                                        secondLayout.animate().translationYBy(-secondLayout.getHeight());
                                        isUp = !isUp;
                                    }
                                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                    if (isUp){
                                        mainLayout.animate().translationYBy(secondLayout.getHeight());
                                        secondLayout.animate().translationYBy(secondLayout.getHeight());
                                        isUp = !isUp;
                                    }

                                }
                            } catch (Exception e) {
                                // nothing
                            }
                            return super.onFling(e1, e2, velocityX, velocityY);
                        }
                    });

            returnView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gesture.onTouchEvent(event);
                }
            });

            return returnView;
        }

    }


    public static class ViewFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.view_fragment, container, false);
        }
    }

    public static class SettingsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.settings_fragment, container, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    }
