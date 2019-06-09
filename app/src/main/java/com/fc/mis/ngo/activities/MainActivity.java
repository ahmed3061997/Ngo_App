package com.fc.mis.ngo.activities;

import androidx.annotation.NonNull;

import com.fc.mis.ngo.fragments.ChatFragment;
import com.fc.mis.ngo.fragments.MoreFragment;
import com.fc.mis.ngo.fragments.MyAccountFragment;
import com.fc.mis.ngo.fragments.NGOsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {
    // toolbar
    private Toolbar mToolbar;

    // bottom navigation bar
    private BottomNavigationView mBottomNavigationView;

    // firebase Database
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    // firebase storage ...
    private StorageReference mImageStorage;

    // progress dialog
    private ProgressDialog mProgressDialog;

    private String download_url;
    private String thumb_download_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);

        mAuth = FirebaseAuth.getInstance();

        mImageStorage = FirebaseStorage.getInstance().getReference();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        // bottom navigation bar
        mBottomNavigationView = findViewById(R.id.main_bottom_nav_bar);
        mBottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new HomeFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if the user is signed in (not-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        } else {
            if (!currentUser.isEmailVerified()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your account isn't verified.");
                builder.setPositiveButton("Contact Us", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendToStart();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setActionBarShadow(boolean enabled) {
        mToolbar.setElevation((enabled ? 4 : 0));
    }


    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setTitle("Uploading Image ... ");
                mProgressDialog.setMessage("Please wait while upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                // we gonna make this to get the image path to compress it ...
                File thumb_filePath = new File(resultUri.getPath());

                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String Current_User_Id = mCurrentUser.getUid();

                Bitmap thumb_bitmap = null;  // <<<< to avoid the error and also make try and catch ...
                try {
                    thumb_bitmap = new Compressor(MainActivity.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // this converts bitmap data into bytes so we could upload it to storage
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_bytes = baos.toByteArray();


                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_User_Id);

                final StorageReference profile_filepath = mImageStorage.child("profile_images").child(Current_User_Id + ".jpg");
                // create another storage reference for our thumbnail images ...
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(Current_User_Id + ".jpg");


                profile_filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            // to get the image URL and store it to current user data
                            profile_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    download_url = uri.toString();
                                }
                            });


                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_bytes);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    // to get the thumbnail image URL and store it to current user data
                                    thumb_task.getResult().getStorage()
                                            .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            if (task.isSuccessful()) {

                                                thumb_download_url = task.getResult().toString();

                                                Map update_hashMap = new HashMap();
                                                update_hashMap.put("profile_image", download_url);
                                                update_hashMap.put("thumb_image", thumb_download_url);

                                                mDatabase.updateChildren(update_hashMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mProgressDialog.dismiss();
                                                                    Toast.makeText(MainActivity.this,
                                                                            "Success Uploading",
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                Toast.makeText(MainActivity.this,
                                                        "Error in uploading Thumbnail ", Toast.LENGTH_LONG).show();
                                                mProgressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Error in uploading ! ", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                showAlert("Error", error.getMessage());
            }
        }

    }

    // bottom navigation bar listener
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.nav_ngo:
                    selectedFragment = new NGOsFragment();
                    break;

                case R.id.nav_chat:
                    selectedFragment = new ChatFragment();
                    break;

                case R.id.nav_my_account:
                    selectedFragment = new MyAccountFragment();
                    break;

                case R.id.nav_more:
                    selectedFragment = new MoreFragment();
                    break;
            }

            if (selectedFragment != null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, selectedFragment).commit();

            return true;
        }
    };
}
