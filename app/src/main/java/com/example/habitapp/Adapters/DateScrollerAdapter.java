package com.example.habitapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.MainActivity;
import com.example.habitapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.habitapp.Managers.ToDoManager.*;

public class DateScrollerAdapter extends RecyclerView.Adapter<DateScrollerAdapter.ScrollSelectorViewHolder> implements
        DateScrollerInteraction {

    private MainListener mainListener;

    private List<String> scrollItems;
    private OnScrollSelectorListener scrollSelectorListener;

    private int start;
    private int end;

    private int currentModule;

    public DateScrollerAdapter(ArrayList<String> scrollItems, OnScrollSelectorListener scrollSelectorListener, MainListener mainListener) {
        this.scrollItems = scrollItems;
        this.scrollSelectorListener = scrollSelectorListener;
        this.mainListener = mainListener;
    }

    @NonNull
    @Override
    public ScrollSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout container = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_scroller_item, parent, false);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollSelectorListener.scrollItemSelected(view);
            }
        });
        return new ScrollSelectorViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollSelectorViewHolder holder, int position) {

/*
        int color = mainListener.getModuleColor(currentModule);

        holder.dateSelectedLeft.setBackgroundColor(color);
        holder.dateSelectedRight.setBackgroundColor(color);
        DrawableCompat.setTint(holder.dateSelectedSingle.getDrawable(), color);
*/

        holder.scrollData.setText(scrollItems.get(position));

        holder.dateSelectedSingle.setScaleX(0f);
        holder.dateSelectedSingle.setScaleY(0f);
        holder.dateSelectedLeft.setAlpha(0f);
        holder.dateSelectedRight.setAlpha(0f);

        if (position >= start && position <= end) {

            if (start == end) { // Interval is one day.

                holder.dateSelectedSingle.animate().scaleX(1f).setDuration(150).start();
                holder.dateSelectedSingle.animate().scaleY(1f).setDuration(150).start();

            } else if (position == start) {

                holder.dateSelectedSingle.setScaleX(1f);
                holder.dateSelectedSingle.setScaleY(1f);
                holder.dateSelectedRight.setAlpha(1f);

            } else if (position == end) {
                holder.dateSelectedSingle.setScaleX(1f);
                holder.dateSelectedSingle.setScaleY(1f);
                holder.dateSelectedLeft.setAlpha(1f);

            } else {
                holder.dateSelectedRight.setAlpha(1f);
                holder.dateSelectedLeft.setAlpha(1f);
            }
        }
    }

    @Override
    public int getItemCount() {
        return scrollItems.size();
    }

    @Override
    public void setInterval(int start, int end) {
        this.start = start;
        this.end = end;

        notifyDataSetChanged();
    }

    @Override
    public void setModule(int module) {
        this.currentModule = module;
    }

    static class ScrollSelectorViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout bounds;
        TextView scrollData;

        ImageView dateSelectedSingle;
        ImageView dateSelectedLeft;
        ImageView dateSelectedRight;

        ScrollSelectorViewHolder(@NonNull ConstraintLayout container) {
            super(container);

            scrollData = container.findViewById(R.id.scroll_data);
            bounds = container.findViewById(R.id.scroll_item_container);

            dateSelectedSingle = container.findViewById(R.id.date_selected_single);
            dateSelectedLeft = container.findViewById(R.id.date_selected_left);
            dateSelectedRight = container.findViewById(R.id.date_selected_right);

        }
    }
}
