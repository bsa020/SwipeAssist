package com.comp3330.swipeassist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class NewSignIn extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1337;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser firebaseUser;
    // private AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    //signed in
                    Intent intent = new Intent(NewSignIn.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    //signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, MainActivity.class));
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                accessToken = AccessToken.getCurrentAccessToken();
//                addToDatabase();
                finish();
                return;
            }

            // Sign in canceled
            if (resultCode == RESULT_CANCELED) {
                // showSnackbar("Sign in is required to use this app.");
                return;
            }

            // No network
//            if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
//                showSnackbar("Conexiune internet inactiva");
//                return;
//            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
