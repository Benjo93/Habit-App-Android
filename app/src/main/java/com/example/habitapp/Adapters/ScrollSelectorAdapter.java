package com.example.habitapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.habitapp.Managers.ToDoManager.*;

public class ScrollSelectorAdapter extends RecyclerView.Adapter<ScrollSelectorAdapter.ScrollSelectorViewHolder> implements
        ScrollSelectorInteraction {

    private ArrayList<String> scrollItems;
    private OnScrollSelectorListener scrollSelectorListener;
    private List<TextView> textList = new ArrayList<>();

    public ScrollSelectorAdapter(ArrayList<String> scrollItems, OnScrollSelectorListener scrollSelectorListener, int width) {
        this.scrollItems = scrollItems;
        this.scrollSelectorListener = scrollSelectorListener;
    }

    @NonNull
    @Override
    public ScrollSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout container = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scroll_selector_item, parent, false);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollSelectorListener.scrollItemSelected(view);
            }
        });
        return new ScrollSelectorViewHolder(container);
    }

    private boolean editingData;

    @Override
    public void onBindViewHolder(@NonNull final ScrollSelectorViewHolder holder, int position) {

        holder.scrollData.setText(scrollItems.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

/*
                if (!editingData) {
                    holder.scrollDataEdit.setVisibility(View.VISIBLE);
                    holder.scrollData.setVisibility(View.GONE);
                } else {
                    holder.scrollDataEdit.setVisibility(View.GONE);
                    holder.scrollData.setVisibility(View.VISIBLE);
                }
*/

                return true;
            }
        });

        textList.add(holder.scrollData);
    }

    public void saveData () {

    }

    @Override
    public void selectItem(int position) {

        if (position >= textList.size() || position < 0) return;

        for (TextView text : textList) {
            //text.setTextColor(Color.WHITE);
            text.setBackgroundResource(R.color.transparent);
            //text.setScaleX(0.75f);
            //ext.setScaleY(0.75f);
            //text.animate().scaleX(1f).setDuration(100).start();
            //text.animate().scaleY(1f).setDuration(100).start();

        }

        //textList.get(position).setTextColor(MainActivity.PRIMARY_COLOR);
        textList.get(position).setBackgroundResource(R.drawable.category_item_bg);
        //textList.get(position).setScaleX(1.2f);
        //textList.get(position).setScaleY(1.2f);
        //textList.get(position).animate().scaleX(1.5f).setDuration(250).start();
        //textList.get(position).animate().scaleY(1.5f).setDuration(250).start();

    }

    @Override
    public int getItemCount() {
        return scrollItems.size();
    }

    static class ScrollSelectorViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout bounds;
        TextView scrollData;
        EditText scrollDataEdit;

        ScrollSelectorViewHolder(@NonNull ConstraintLayout container) {
            super(container);

            scrollData = container.findViewById(R.id.scroll_data);
            bounds = container.findViewById(R.id.scroll_item_container);

        }
    }
}
