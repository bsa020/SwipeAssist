package com.comp3330.swipeassist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private Spinner spinner;
    private static final String[] paths = {"Cultural", "Fashion", "Formal", "Night out", "Outdoors", "Sports"};
    private static final int RC_SIGN_IN = 443;
    private static String userEmail = "";
    private String userName = "";
    private FirebaseAuth mAuth;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

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
        private Uri filePath;
        FirebaseStorage storage;
        StorageReference storageReference;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.get_fragment, container, false);
            // set spinner values
            final Spinner spinner = v.findViewById(R.id.categorySpinner);
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

            final EditText editNameText = v.findViewById(R.id.editNameText);
            final EditText editOccasionText = v.findViewById(R.id.editOccasionText);
            Button uploadButton = v.findViewById(R.id.uploadButton);
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Map<String, Object> user = new HashMap<>();
                        user.put("first_name", editNameText.getText().toString());
                        user.put("category", spinner.getSelectedItem().toString());
                        user.put("occasion", editOccasionText.getText().toString());
                        String path = uploadImage();
                        user.put("imagePath", path);
                        db.collection("swipe_assist").add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getActivity(), "Uploaded!", Toast.LENGTH_SHORT).show();
                                        editNameText.setText("");
                                        editOccasionText.setText("");
                                        imageView.setImageDrawable(null);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            return v;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Detects request codes
            if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
                filePath = data.getData();

                try {
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(filePath);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        private String uploadImage() {
            String randomUUID;
            String path;
            if(filePath != null) {
                randomUUID = UUID.randomUUID().toString();
                path = "images/"+ randomUUID;
                StorageReference ref = storageReference.child(path);
                ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                return path;
            }
            return null;
        }
    }

    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );

        return sb.toString();
    }

        public static class GiveFragment extends Fragment {
            boolean isUp;

            // pick random image
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            TextView nameText;
            TextView occasionText;
            ImageView giveImg;
            EditText detailedFeedbackInput;
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            // path reference to last and curr image
            DocumentSnapshot currDocSnap;
            DocumentSnapshot lastDocSnap;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                isUp = false;

                View returnView = inflater.inflate(R.layout.give_fragment, container, false);
                Button feedbackBut = returnView.findViewById(R.id.feedback_button);
                FloatingActionButton leftSkipButton = returnView.findViewById(R.id.skipLeftButton);
                FloatingActionButton rightSkipButton = returnView.findViewById(R.id.skipRightButton);

                nameText = returnView.findViewById(R.id.nameText);
                occasionText = returnView.findViewById(R.id.occasionText);
                giveImg = returnView.findViewById(R.id.giveImg);
                detailedFeedbackInput = returnView.findViewById(R.id.detailed_feedback_input);

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

                leftSkipButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Going back", Toast.LENGTH_SHORT).show();

                        if (isUp) {
                            mainLayout.animate().translationYBy(secondLayout.getHeight());
                            secondLayout.animate().translationYBy(secondLayout.getHeight());
                            isUp = !isUp;
                        }

                        // going back a picture
                        DocumentSnapshot documentToLoad = lastDocSnap;
                        currDocSnap = lastDocSnap;
                        // can only go back one picture, not multiple
                        lastDocSnap = null;

                        nameText.setText(documentToLoad.get("first_name").toString());
                        occasionText.setText(documentToLoad.get("occasion").toString());
                        StorageReference pathReference = storageReference.child(documentToLoad.get("imagePath").toString());
                        Glide.with(Objects.requireNonNull(getContext()))
                                .load(pathReference)
                                .into(giveImg);
                    }
                });

                rightSkipButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Skipping this picture", Toast.LENGTH_SHORT).show();

                        if (isUp) {
                            mainLayout.animate().translationYBy(secondLayout.getHeight());
                            secondLayout.animate().translationYBy(secondLayout.getHeight());
                            isUp = !isUp;
                        }
                        setNewPicture();
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
                                // swiping left or right
                                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH){
                                    // swiping left
                                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                        // TO DO: IMPLEMENT THUMBS UP AND DOWN LOGIC!!!
                                        Toast.makeText(getActivity(), "You gave a thumbs down", Toast.LENGTH_SHORT).show();
                                        setNewPicture();
                                    }
                                    // swiping right
                                    else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                        // TO DO: IMPLEMENT THUMBS UP AND DOWN LOGIC!!!
                                        Toast.makeText(getActivity(), "You gave a thumbs up", Toast.LENGTH_SHORT).show();
                                        setNewPicture();
                                    }
                                }
                                // swiping up or down with enough velocity
                                else {
                                    // swiping up
                                    if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                                            && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                        if (!isUp) {
                                            mainLayout.animate().translationYBy(-secondLayout.getHeight());
                                            secondLayout.animate().translationYBy(-secondLayout.getHeight());
                                            isUp = !isUp;
                                        }
                                    }
                                    // swiping down
                                    else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                            && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                        if (isUp) {
                                            mainLayout.animate().translationYBy(secondLayout.getHeight());
                                            secondLayout.animate().translationYBy(secondLayout.getHeight());
                                            isUp = !isUp;
                                        }
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
                setNewPicture();
                return returnView;
            }

            private void setNewPicture(){
                String randomID = randomString(20);
                CollectionReference collRef = db.collection("swipe_assist");
                Query queryRef = collRef.whereGreaterThan(FieldPath.documentId(), randomID);

                queryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && task.getResult().size() >= 1) {

                            // will only run once - only one document is queried
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
                            Random rand = new Random();
                            DocumentSnapshot documentPicked = list.get(rand.nextInt(list.size()));

                            nameText.setText(documentPicked.get("first_name").toString());
                            occasionText.setText(documentPicked.get("occasion").toString());

                            StorageReference pathReference = storageReference.child(documentPicked.get("imagePath").toString());

                            Glide.with(Objects.requireNonNull(getContext()))
                                    .load(pathReference)
                                    .into(giveImg);
                            nameText.setVisibility(View.VISIBLE);
                            occasionText.setVisibility(View.VISIBLE);
                            giveImg.setVisibility(View.VISIBLE);
                            detailedFeedbackInput.setText("");
                            lastDocSnap = currDocSnap;
                            currDocSnap = documentPicked;
                        }
                        // if queryRef is null still then restart the function call
                        else{
                            setNewPicture();
                        }
                    }
                });
            }
        }

        public static class ViewFragment extends Fragment {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View v = inflater.inflate(R.layout.view_fragment, container, false);

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




