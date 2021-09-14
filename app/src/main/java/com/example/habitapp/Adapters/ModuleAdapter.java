package com.example.habitapp.Adapters;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Utils.Module;
import com.example.habitapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.habitapp.Managers.ToDoManager.*;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> implements
        ModuleItemInteraction {

    private OnModuleListener moduleListener;
    private MainListener mainListener;

    private ArrayList<Module> modulesLocal;

    private long date;

    public ModuleAdapter(ArrayList<Module> modulesLocal, MainListener mainListener, OnModuleListener moduleListener) {
        this.modulesLocal = modulesLocal;
        this.mainListener = mainListener;
        this.moduleListener = moduleListener;
    }

    @NonNull
    @Override
    public ModuleAdapter.ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ModuleViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.module_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ModuleAdapter.ModuleViewHolder holder, final int position) {

        holder.moduleName.setText(modulesLocal.get(position).name);

        SimpleDateFormat weekDayFormatter = new SimpleDateFormat("EEEE", Locale.US);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd", Locale.US);

        String weekDayText = weekDayFormatter.format(date);
        String dateText = dateFormatter.format(date);

        holder.moduleDayInfo.setText(weekDayText);
        holder.moduleDayInfoNext.setText(dateText);

        holder.moduleItems.setText(String.format("%s items", modulesLocal.get(position).items.size()));

        holder.moduleProgress.setScaleX(1.6f);
        holder.moduleProgress.setScaleY(1.6f);

        holder.moduleCheck.setTranslationX(50);
        holder.moduleCircle.setTranslationX(50);
        holder.moduleProgress.setTranslationX(50);

        holder.moduleCircle.setScaleX(1.6f);
        holder.moduleCircle.setScaleY(1.6f);

        int moduleProgress = (int) (modulesLocal.get(position).getProgress() * 100f);

        if (modulesLocal.get(position).currentProgress != 100 || moduleProgress != 100) {

            if (position == mainListener.getActiveModule()) {

                ValueAnimator anim = ValueAnimator.ofInt(modulesLocal.get(position).currentProgress, moduleProgress);

                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                        int val = (Integer) valueAnimator.getAnimatedValue();
                        holder.moduleProgress.setProgress(val);

                        if (val == 100) {

                            if (!holder.complete) mainListener.vibratePhone(25);
                            holder.complete = true;

                            holder.moduleCircle.setAlpha(1.0f);
                            holder.moduleCircle.setScaleX(1.6f);
                            holder.moduleCircle.setScaleY(1.6f);

                            holder.moduleCircle.animate().alpha(0f).setDuration(250).start();
                            holder.moduleCircle.animate().scaleX(0f).setDuration(250).start();
                            holder.moduleCircle.animate().scaleY(0f).setDuration(250).start();

                            holder.moduleCheck.setAlpha(0f);
                            holder.moduleCheck.animate().alpha(1f).setDuration(400).start();

                            holder.moduleCheck.setRotation(-20f);
                            holder.moduleCheck.animate().rotation(0f).setDuration(400).start();

                            holder.moduleProgress.setAlpha(0f);
                            holder.moduleProgress.animate().alpha(1f).setDuration(200).start();

                            holder.moduleProgress.setAlpha(0f);
                            holder.moduleProgress.animate().alpha(1f).setDuration(250).start();

                            holder.moduleProgress.setAlpha(1f);
                            holder.moduleProgress.animate().alpha(0f).setDuration(250).start();

                            holder.moduleProgress.setScaleX(0f);
                            holder.moduleProgress.setScaleY(0f);
                            holder.moduleProgress.animate().scaleX(1.6f).setDuration(150).start();
                            holder.moduleProgress.animate().scaleY(1.6f).setDuration(150).start();

                        } else {

                            holder.moduleProgress.setAlpha(1f);
                            holder.moduleCheck.setAlpha(0f);
                            holder.moduleCircle.setAlpha(0f);

                        }
                    }
                });
                anim.setDuration(500);
                anim.start();
            }

        } else { // Progress is already at 100 percent.

            holder.moduleProgress.setProgress(100);
            holder.moduleCheck.setAlpha(1f);
            holder.moduleProgress.setAlpha(0f);
            holder.moduleCircle.setAlpha(0f);

        }

        // Set the current progress for the next iteration.
        modulesLocal.get(position).currentProgress = moduleProgress;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleListener.entireModuleSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modulesLocal.size();
    }

    @Override
    public void setDate(long date) {
        this.date = date;
        notifyDataSetChanged();
    }

    @Override
    public void refresh() {

    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {

        boolean complete;

        TextView moduleName;
        TextView moduleItems;
        TextView moduleDayInfo;
        TextView moduleDayInfoNext;
        ProgressBar moduleProgress;
        ImageView moduleCheck;
        ImageView moduleCircle;

        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);

            moduleName = itemView.findViewById(R.id.module_name);
            moduleItems = itemView.findViewById(R.id.module_items);
            moduleDayInfo = itemView.findViewById(R.id.module_day_info);
            moduleDayInfoNext = itemView.findViewById(R.id.module_day_info_next);
            moduleProgress = itemView.findViewById(R.id.module_progress);
            moduleCheck = itemView.findViewById(R.id.module_check);
            moduleCircle = itemView.findViewById(R.id.module_item_circle);
        }
    }
}
