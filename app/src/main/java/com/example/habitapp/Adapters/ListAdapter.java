package com.example.habitapp.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.MainActivity;
import com.example.habitapp.Managers.ToDoManager.OnListItemListener;
import com.example.habitapp.R;
import com.example.habitapp.Utils.ToDoItem;

import java.util.List;

import static com.example.habitapp.Managers.ToDoManager.*;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ListItemInteraction {

    private List<ToDoItem> toDoItemsDisplayed;
    private OnListItemListener listListener;
    private MainListener mainListener;

    ListAdapter(List<ToDoItem> toDoItemsDisplayed, MainListener mainListener, OnListItemListener listListener) {
        this.toDoItemsDisplayed = toDoItemsDisplayed;
        this.listListener = listListener;
        this.mainListener = mainListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout container;
        container = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        new Handler().postDelayed(new Runnable() {
            public void run() {
            // Make changes to the most recently added or updated items.
            if (toDoItemsDisplayed.get(position).hasBeenUpdated())  {

                itemViewHolder.itemView.setTranslationX(-200f);
                itemViewHolder.itemView.animate().translationX(0f).setDuration(250).start();

                itemViewHolder.itemView.setAlpha(0f);
                itemViewHolder.itemView.animate().alpha(1f).setDuration(500).start();

                itemViewHolder.listBgOverlay.setAlpha(0.5f);
                itemViewHolder.listBgOverlay.animate().alpha(0f).setDuration(1000).start();
                toDoItemsDisplayed.get(position).updateItem();

            } else {
                itemViewHolder.listBgOverlay.setAlpha(0f);
            }
            }
        }, 50);

        // Set the to-do item name.
        itemViewHolder.toDoName.setText(toDoItemsDisplayed.get(position).getName());

        // Handle to-do item click.
        itemViewHolder.toDoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listListener.onToDoClick(toDoItemsDisplayed.get(position));
            }
        });

        // Handle Details display.
        if (!toDoItemsDisplayed.get(position).getDetails().equals("")) {

            itemViewHolder.toDoDetails.setText(toDoItemsDisplayed.get(position).getDetails());
            itemViewHolder.toDoDetails.setVisibility(View.VISIBLE);

        } else itemViewHolder.toDoDetails.setVisibility(View.GONE);

        if (toDoItemsDisplayed.get(position).isCompleted()) {
            itemViewHolder.toDoCheck.setImageResource(R.drawable.list_item_check);
            itemViewHolder.toDoName.setAlpha(0.4f);
            itemViewHolder.toDoDetails.setAlpha(0.4f);
            itemViewHolder.toDoBackground.setBackgroundResource(R.drawable.list_item_clear);
        } else {
            itemViewHolder.toDoCheck.setImageResource(R.drawable.circle_unchecked);
            itemViewHolder.toDoName.setAlpha(1f);
            itemViewHolder.toDoDetails.setAlpha(1f);
            itemViewHolder.toDoBackground.setBackgroundResource(R.drawable.list_item_opaque);
        }

        // Handle check box interaction.
        itemViewHolder.toDoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!toDoItemsDisplayed.get(position).isCompleted()) {

                    toDoItemsDisplayed.get(position).setCompleted(true);
                    listListener.onToDoCheck(toDoItemsDisplayed.get(position), true);

                } else {

                    toDoItemsDisplayed.get(position).setCompleted(false);
                    listListener.onToDoCheck(toDoItemsDisplayed.get(position), false);

                }

                updateCheckBox(itemViewHolder.toDoCheck, itemViewHolder.toDoName, itemViewHolder.toDoDetails, itemViewHolder.toDoBackground, position);
            }
        });
    }

    private void updateCheckBox (ImageButton button, TextView name, TextView details, ConstraintLayout background, int position) {

        if (toDoItemsDisplayed.get(position).isCompleted()) {

            mainListener.vibratePhone(10);

            button.setImageResource(R.drawable.list_item_check);
            button.setAlpha(0f);
            button.animate().alpha(1f).setDuration(150).start();
            name.animate().alpha(0.4f).setDuration(150).start();
            details.animate().alpha(0.4f).setDuration(150).start();
            background.setBackgroundResource(R.drawable.list_item_clear);

        } else {

            button.setImageResource(R.drawable.circle_unchecked);
            button.setScaleX(0f);
            button.setScaleY(0f);
            button.animate().scaleX(1f).setDuration(250).start();
            button.animate().scaleY(1f).setDuration(250).start();
            name.animate().alpha(1f).setDuration(150).start();
            details.animate().alpha(1f).setDuration(150).start();
            background.setBackgroundResource(R.drawable.list_item_opaque);
        }
    }

    @Override
    public int getItemCount() {
        return toDoItemsDisplayed.size();
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView toDoName;
        TextView toDoDetails;
        ImageButton toDoCheck;
        ConstraintLayout toDoContainer;
        ConstraintLayout listBgOverlay;
        ConstraintLayout toDoBackground;

        ItemViewHolder(@NonNull ConstraintLayout container) {
            super(container);

            toDoName = container.findViewById(R.id.todo_name);
            toDoDetails = container.findViewById(R.id.todo_details);
            toDoCheck = container.findViewById(R.id.todo_check);
            toDoContainer = container.findViewById(R.id.todo_item_text_container);
            listBgOverlay = container.findViewById(R.id.list_bg_overlay);
            toDoBackground = container.findViewById(R.id.todo_container);
        }
    }
}
