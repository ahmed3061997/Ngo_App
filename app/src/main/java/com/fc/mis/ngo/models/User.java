package com.fc.mis.ngo.models;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {
    private static User mCurrentUser;

    public static String getCurrentUserId() {
        if (mCurrentUser == null)
            mCurrentUser = getCurrentUser();

        return mCurrentUser.getUserId();
    }


    public static User getCurrentUser() {
        if (mCurrentUser == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null)
                return null;

            mCurrentUser = new User(user);
        }

        return mCurrentUser;
    }

    FirebaseUser mUser;

    private User(FirebaseUser user) {
        mUser = user;
    }

    public FirebaseUser getFirebaseUser() {
        return mUser;
    }

    public String getUserId() {
        return mUser.getUid();
    }

    public boolean isEmailVerified() {
        return mUser.isEmailVerified();
    }

    public void modifyCounter(String catagory, final int modifier) {
        String childName;
        if (catagory.equalsIgnoreCase("Cases")) {
            childName = "cases_num";
        } else if (catagory.equalsIgnoreCase("Events")) {
            childName = "events_num";
        } else
            return;

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child("Ngos")
                .child(getUserId())
                .child(childName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int num = 0;
                try {
                    num = dataSnapshot.getValue(Integer.class);
                } catch (Exception e) {
                    if (modifier < 0)
                        num = 1;
                }
                dataSnapshot.getRef().setValue(num + modifier);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }
}
