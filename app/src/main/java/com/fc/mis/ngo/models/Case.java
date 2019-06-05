package com.fc.mis.ngo.models;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;

public class Case {
    private String mTitle;
    private String mBody;
    private String mThumbImg;
    private long mTimestamp;
    private String mOrgName;
    private String mOrgThumb;
    private int mNeeded;
    private int mDonated;
    private ArrayList<String> mImages;

    public ArrayList<String> getImages() {
        return mImages;
    }

    public void setImages(ArrayList<String> mImages) {
        this.mImages = mImages;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public String getThumbImg() {
        return mThumbImg;
    }

    public void setThumbImg(String mThumbImg) {
        this.mThumbImg = mThumbImg;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public String getOrgName() {
        return mOrgName;
    }

    public void setOrgName(String mOrgName) {
        this.mOrgName = mOrgName;
    }

    public String getOrgThumb() {
        return mOrgThumb;
    }

    public void setOrgThumb(String mOrgThumb) {
        this.mOrgThumb = mOrgThumb;
    }

    public int getNeeded() {
        return mNeeded;
    }

    public void setNeeded(int mNeeded) {
        this.mNeeded = mNeeded;
    }

    public int getDonated() {
        return mDonated;
    }

    public void setDonated(int mDonated) {
        this.mDonated = mDonated;
    }

    public static String saveCase(String caseId, final Case caseRef, final OnCompleteListener<Void> completeListener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid(); // ngo id

        final DatabaseReference databaseRef = (caseId == null ? FirebaseDatabase.getInstance().getReference()
                .child("Cases")
                .child(userId) // ngo id
                .push() /* case id (generated) */ : FirebaseDatabase.getInstance().getReference()
                .child("Cases")
                .child(userId) // ngo id
                .child(caseId)); // case id (given)

        HashMap<String, Object> caseMap = new HashMap<>();
        caseMap.put("title", caseRef.getTitle());
        caseMap.put("body", caseRef.getBody());
        caseMap.put("timestamp", ServerValue.TIMESTAMP);
        caseMap.put("needed", caseRef.getNeeded());
        caseMap.put("donated", String.valueOf(caseRef.getDonated()));
        caseMap.put("thumb_img", caseRef.getThumbImg());

        databaseRef.updateChildren(caseMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ArrayList imgList = caseRef.getImages();
                    if (imgList != null) {
                        HashMap<String, Object> imgMap = new HashMap<>();
                        for (String url : caseRef.getImages()) {
                            imgMap.put(url, "default");
                        }
                        // put images.. forward completed callback
                        databaseRef.child("Images").updateChildren(imgMap).addOnCompleteListener(completeListener);
                    } else {
                        completeListener.onComplete(task); // No images.. now completed
                    }
                } else {
                    completeListener.onComplete(task); // failed..
                }
            }
        });

        return databaseRef.getKey(); // forward case id again (generated or given)
    }
}
