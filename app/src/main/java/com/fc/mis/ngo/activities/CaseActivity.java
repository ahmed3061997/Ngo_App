package com.fc.mis.ngo.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.models.Case;
import com.fc.mis.ngo.models.SwipeDismissTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

public class CaseActivity extends AppCompatActivity {
    private static final int PICK_COVER_IMAGE = 100;
    private static final int PICK_IMAGE = 101;

    // toolbar
    private Toolbar mToolbar;

    private FloatingActionButton mFabBtn;
    private MaterialButton mAddCoverBtn;
    private MaterialButton mAddPicBtn;
    private TextInputEditText mTitle;
    private TextInputEditText mBody;
    private TextInputEditText mDonated;
    private TextInputEditText mNeeded;
    private AppCompatImageView mCoverImg;
    private LinearLayoutCompat mImagesList;

    private boolean mEditMode;
    private Case mCase;
    private String mCaseId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.case_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Case");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Intent options
        Intent intent = getIntent();
        mEditMode = intent.getBooleanExtra("EditMode", false);

        if (mEditMode && !intent.hasExtra("CaseId")) {
            showAlert("Unexpected Error", "No case id specified", new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            return;
        } else {
            mCaseId = intent.getStringExtra("CaseId");
            loadCase();
        }

        // UI
        mAddPicBtn = (MaterialButton) findViewById(R.id.case_add_picture_btn);
        mAddCoverBtn = (MaterialButton) findViewById(R.id.case_add_cover_btn);
        mFabBtn = (FloatingActionButton) findViewById(R.id.case_fab_btn);
        mTitle = (TextInputEditText) findViewById(R.id.case_title_field);
        mBody = (TextInputEditText) findViewById(R.id.case_body_field);
        mDonated = (TextInputEditText) findViewById(R.id.case_donated_field);
        mNeeded = (TextInputEditText) findViewById(R.id.case_needed_field);
        mCoverImg = (AppCompatImageView) findViewById(R.id.case_cover_img);
        mImagesList = (LinearLayoutCompat) findViewById(R.id.case_images_list);

        mAddPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        mAddCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCoverImage();
            }
        });
        mCoverImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCoverImage();
            }
        });

        mCoverImg.setOnTouchListener(new SwipeDismissTouchListener(mCoverImg, null,
                new SwipeDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view, Object token) {
                        removeCoverImage();
                    }
                }));

        if (mEditMode)
            mFabBtn.setTag("edit");

        mFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals("done")) {
                    saveCase();
                } else if (v.getTag().equals("edit")) {
                    enterEditMode();
                }
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Saving Case");
        mProgress.setMessage("Please wait while we upload your case");
        mProgress.setCanceledOnTouchOutside(false);
    }

    private ProgressDialog mProgress;

    private void saveCase() {
        mProgress.show();

        uploadImages(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Case caseRef = new Case();

                caseRef.setTitle(mTitle.getText().toString());
                caseRef.setBody(mTitle.getText().toString());
                caseRef.setDonated(Integer.valueOf(mDonated.getText().toString()));
                caseRef.setNeeded(Integer.valueOf(mNeeded.getText().toString()));

                if (task.isSuccessful()) {

                    mCaseId = Case.saveCase(mCaseId, caseRef, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mProgress.hide();
                            if (task.isSuccessful()) {
                                Snackbar.make(findViewById(R.id.case_coordinator_layout),
                                        "Case saved succussfully !", Snackbar.LENGTH_SHORT).show();
                                exitEditMode();
                            } else {
                                showAlert("Error", task.getException().getMessage(), null);
                            }
                        }
                    });
                } else {
                    mProgress.hide();
                    showAlert("Error", task.getException().getMessage(), null);
                }
            }
        });
    }

    private void uploadImages(OnCompleteListener<Void> listener) {
        listener.onComplete(new Task<Void>() {
            @Override
            public boolean isComplete() {
                return false;
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public Void getResult() {
                return null;
            }

            @Nullable
            @Override
            public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }
        });
    }

    private void loadCase() {
    }

    private void enterEditMode() {
        mEditMode = true;
        mTitle.setEnabled(true);
        mBody.setEnabled(true);
        mNeeded.setEnabled(true);
        mDonated.setEnabled(true);
        mAddCoverBtn.setVisibility(View.VISIBLE);
        mAddPicBtn.setVisibility(View.VISIBLE);
        mFabBtn.setTag("done");
        mFabBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
    }

    private void exitEditMode() {
        mEditMode = false;
        mTitle.setEnabled(false);
        mBody.setEnabled(false);
        mNeeded.setEnabled(false);
        mDonated.setEnabled(false);
        mAddCoverBtn.setVisibility(View.GONE);
        mAddPicBtn.setVisibility(View.GONE);
        mFabBtn.setTag("edit");
        mFabBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
    }

    private void addImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Picture");
        pickIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    private void addCoverImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Cover Picture");

        startActivityForResult(chooserIntent, PICK_COVER_IMAGE);
    }

    private void removeCoverImage() {
        mCoverImg.setVisibility(View.GONE);
        mAddCoverBtn.setVisibility(View.VISIBLE);
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.case_coordinator_layout),
                "Cover picture removed", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoverImg.setVisibility(View.VISIBLE);
                mAddCoverBtn.setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.case_coordinator_layout),
                        "Cover picture restored", Snackbar.LENGTH_SHORT).show();
            }
        });
        snackbar.show();
    }

    private void showAlert(String title, String message, DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_COVER_IMAGE) {
                Uri imageUri = data.getData();

                Picasso.get().load(imageUri).noPlaceholder().into(mCoverImg, new Callback() {
                    @Override
                    public void onSuccess() {
                        mCoverImg.setVisibility(View.VISIBLE);
                        mAddCoverBtn.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(CaseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (requestCode == PICK_IMAGE) {
                Uri imageUri = data.getData();
                addImageView(imageUri);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addImageView(Uri imageUri) {
        final AppCompatImageView imageView = new AppCompatImageView(this);

        LinearLayoutCompat.LayoutParams layout = new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        layout.bottomMargin = 10;

        imageView.setLayoutParams(layout);

        //imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setAdjustViewBounds(true);

        imageView.setTag(imageUri);

        int index = mImagesList.getChildCount() - 1;
        mImagesList.addView(imageView, index);

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                if (!mEditMode)
                    return false;

                final ViewGroup.LayoutParams lp = v.getLayoutParams();
                final int originalHeight = v.getHeight();

                ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(
                        v.getContext().getResources().getInteger(
                                android.R.integer.config_shortAnimTime));

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mImagesList.removeView(v);
                        // Reset view presentation
                        v.setAlpha(1f);
                        v.setTranslationX(0);
                        lp.height = originalHeight;
                        v.setLayoutParams(lp);
                    }
                });

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        lp.height = (Integer) valueAnimator.getAnimatedValue();
                        v.setLayoutParams(lp);
                    }
                });

                animator.start();
                return false;
            }
        });

        imageView.setOnTouchListener(new SwipeDismissTouchListener(imageView, null,
                new SwipeDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view, Object token) {
                        mImagesList.removeView(view);
                    }
                }));


        try {
            InputStream imgStream = getContentResolver().openInputStream(imageUri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inSampleSize = 3;

            Bitmap img = BitmapFactory.decodeStream(imgStream, null, options);

            imageView.setImageBitmap(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
