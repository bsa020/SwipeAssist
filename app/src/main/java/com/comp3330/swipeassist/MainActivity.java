package com.comp3330.swipeassist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private Spinner spinner;
    private static final String[] paths = {"Fashion", "Fitness", "item 3"};
    private static final int RC_SIGN_IN = 443;
    private String userEmail = "";
    private String userName = "";
    private FirebaseAuth mAuth;

    TextView name;
    TextView email;

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
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        // Find our drawer view
        nvDrawer = findViewById(R.id.nav_view);

        // Setup drawer view

        int intentFragment = getIntent().getExtras().getInt("frg_to_load");
        userEmail = getIntent().getExtras().getString("userEmail");
        userName = getIntent().getExtras().getString("userName");

        setupDrawerContent(nvDrawer);


        Fragment fragment = null;
        Class fragmentClass;

        switch (intentFragment) {
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

        // set navigation header values
        View header = nvDrawer.getHeaderView(0);
        name = header.findViewById(R.id.nav_head_username);
        name.setText(userName);

        email = header.findViewById(R.id.nav_head_email);
        email.setText(userEmail);


    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.sign_out) {
                            FirebaseAuth.getInstance().signOut();
                            backtoStartup();
                        } else selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void backtoStartup() {
        Intent startUpIntent = new Intent(this, LoginActivity.class);
        this.startActivity(startUpIntent);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
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
        public static final int GET_FROM_GALLERY = 3330;
        ImageView imageView;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.get_fragment, container, false);
            // set spinner values
            Spinner spinner = v.findViewById(R.id.categorySpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item, paths);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            imageView = v.findViewById(R.id.imageView);

            Button imageButton = v.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GET_FROM_GALLERY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return v;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Detects request codes
            if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
                Button feedbackBut = returnView.findViewById(R.id.feedback_button);

                final RelativeLayout mainLayout = returnView.findViewById(R.id.relativeLayout);
                final RelativeLayout secondLayout = returnView.findViewById(R.id.relativeLayout2);
                feedbackBut.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
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
                                        if (!isUp) {
                                            mainLayout.animate().translationYBy(-secondLayout.getHeight());
                                            secondLayout.animate().translationYBy(-secondLayout.getHeight());
                                            isUp = !isUp;
                                        }
                                    } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                            && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                        if (isUp) {
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
                View v = inflater.inflate(R.layout.view_fragment, container, false);

                TextView upText = v.findViewById(R.id.upTextView);
                TextView downText = v.findViewById(R.id.downTextView);
                //upText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.thumbs_up, 0);
                //downText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.thumbs_down,0);
                return v;
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




