package com.fc.mis.ngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.models.Case;

import java.util.List;

public class CaseListAdpater extends RecyclerView.Adapter<CaseListAdpater.CaseViewHolder> {
    private Context context;
    private List<Case> cases;

    public CaseListAdpater(Context context, List<Case> cases) {
        this.context = context;
        this.cases = cases;
    }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.case_single_layout, parent, false);

        return new CaseViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        final Case caseItem = cases.get(position);
        // attach data to holder
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CaseViewHolder extends RecyclerView.ViewHolder {

        public CaseViewHolder(@NonNull View itemView) {
            super(itemView);
            // attach views to variables
        }
    }
}
