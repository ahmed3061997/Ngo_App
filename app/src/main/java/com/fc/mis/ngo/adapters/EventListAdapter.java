package com.fc.mis.ngo.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.activities.EventActivity;
import com.fc.mis.ngo.models.Event;
import com.fc.mis.ngo.models.GetTimeAgo;
import com.fc.mis.ngo.models.LanguageDetection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private Context mContext;
    private List<Event> mEvents;
    private boolean mDisplayOrg = false;
    private boolean mViewOnly = false;

    public EventListAdapter(Context context, List<Event> events, boolean displayOrg, boolean viewOnly) {
        this.mContext = context;
        this.mEvents = events;
        this.mDisplayOrg = displayOrg;
        this.mViewOnly = viewOnly;
    }

    public boolean isDisplayOrg() {
        return mDisplayOrg;
    }

    public boolean isViewOnly() {
        return mViewOnly;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_single_layout, parent, false);

        return new EventViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        final Event eventItem = mEvents.get(position);
        holder.bindEvent(eventItem);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView mCoverImg;
        private AppCompatImageView mOrgImg;
        private AppCompatTextView mOrgName;
        private AppCompatTextView mTitle;
        private AppCompatTextView mBody;
        private AppCompatTextView mTime;
        private AppCompatTextView mLocation;
        private AppCompatTextView mEventTime;
        private LinearLayoutCompat mContentLayout;
        private Event mEventRef;

        public EventViewHolder(@NonNull View view) {
            super(view);
            mCoverImg = (AppCompatImageView) view.findViewById(R.id.event_single_cover_img);
            mOrgImg = (AppCompatImageView) view.findViewById(R.id.event_single_org_thumb_img);
            mOrgName = (AppCompatTextView) view.findViewById(R.id.event_single_org_name_txt);
            mTitle = (AppCompatTextView) view.findViewById(R.id.event_single_title_txt);
            mBody = (AppCompatTextView) view.findViewById(R.id.event_single_body_txt);
            mTime = (AppCompatTextView) view.findViewById(R.id.event_single_time_stamp_txt);
            mLocation = (AppCompatTextView) view.findViewById(R.id.event_single_location_txt);
            mEventTime = (AppCompatTextView) view.findViewById(R.id.event_single_time_txt);
            mContentLayout = (LinearLayoutCompat) view.findViewById(R.id.event_single_content_layout);

            mContentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEvent(false);
                }
            });

            mContentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu();
                    return false;
                }
            });
        }

        public void bindEvent(Event eventRef) {
            this.mEventRef = eventRef;

            loadImage(mCoverImg, mEventRef.getThumbImg());

            if (isDisplayOrg()) {
                loadImage(mOrgImg, mEventRef.getOrgThumb());
                mOrgName.setText(mEventRef.getOrgName());
            } else {
                mOrgImg.setVisibility(View.GONE);
                mOrgName.setVisibility(View.GONE);
                itemView.findViewById(R.id.event_single_org_name_sep).setVisibility(View.GONE);
            }

            mTitle.setText(mEventRef.getTitle());
            mBody.setText(mEventRef.getBody());
            mLocation.setText(mEventRef.getLocation());

            mTime.setText(GetTimeAgo.getTimeAgo(mEventRef.getTimestamp(), itemView.getContext()));

            displayDateTime();
        }

        private void displayDateTime() {
            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(mEventRef.getTime());

            int dayNum = calendar.get(Calendar.DAY_OF_MONTH);
            String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

            int hour = calendar.get(Calendar.HOUR);
            String min = "" + calendar.get(Calendar.MINUTE);
            String dayNight = (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");

            if (dayNight.equals("AM")) // 0
                hour = 12; // 12 instead

            if (min.equals("0")) // :0
                min += "0"; // :00 instead

            mEventTime.setText(String.format("%s, %s %d %d:%s %s", day, month, dayNum, hour, min, dayNight));


            LanguageDetection.checkLanguageLayoutDirectionForAr(mTitle);
            LanguageDetection.checkLanguageLayoutDirectionForAr(mBody);
        }

        private void loadImage(final AppCompatImageView imageView, final String url) {
            if (url == null || TextUtils.isEmpty(url) || url.equals("default")) {
                imageView.setVisibility(View.GONE); // ensure image view is invisible
                return;
            }

            Log.d("EventListAdapter", "loading image for: " + mEventRef.getTitle() + ", " + url);

            imageView.setVisibility(View.VISIBLE);

            Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.image_place_holder).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(url).placeholder(R.drawable.image_place_holder).into(imageView);
                }
            });
        }

        public void showMenu() {
            if (isViewOnly())
                return;

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

            builder.setItems(new String[]{"View", "Edit", "Delete"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        showEvent(false);
                    } else if (which == 1) {
                        showEvent(true);
                    } else if (which == 2) {
                        removeEvent();
                    }
                }
            });

            builder.create().show();
        }

        private void showEvent(boolean edit) {
            if (isViewOnly())
                return;

            Intent intent = new Intent(mContext, EventActivity.class);
            intent.putExtra("EditMode", edit);
            intent.putExtra("Event", mEventRef);

            mContext.startActivity(intent);
        }

        private void removeEvent() {
            mEventRef.remove();

            notifyItemRemoved(mEvents.indexOf(mEventRef));

            mEvents.remove(mEventRef);
        }
    }
}
