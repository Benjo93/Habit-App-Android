package com.example.habitapp.Fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.habitapp.Managers.ToDoManager;
import com.example.habitapp.Managers.TopBarManager;
import com.example.habitapp.Utils.Module;
import com.example.habitapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TopBarModuleFragment extends Fragment implements
        TopBarManager.TopBarInteraction {

    private ToDoManager.MainListener toDoListener;
    private Module module;

    private ProgressBar topBarProgress;
    private int currentProgress;

    public static TopBarModuleFragment newInstance(Module module) {
        TopBarModuleFragment topBarModuleFragment = new TopBarModuleFragment();
        Bundle args = new Bundle();
        args.putParcelable("module", module);
        topBarModuleFragment.setArguments(args);
        return topBarModuleFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try { toDoListener = (ToDoManager.MainListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnToDoListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            module = getArguments().getParcelable("module");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView topBarName = view.findViewById(R.id.top_bar_name);
        topBarName.setText(module.name);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMMM dd", Locale.US);
        TextView topBarDate = view.findViewById(R.id.top_bar_date);
        topBarDate.setText(dateFormatter.format(module.dueDate));

        topBarProgress = view.findViewById(R.id.top_bar_progress);
        topBarProgress.setProgress((int) (module.getProgress() * 100), true);
        currentProgress = (int) (module.getProgress() * 100);

        view.findViewById(R.id.top_bar_progress).setVisibility(View.GONE);

    }

    @Override
    public void refresh() {

        ValueAnimator anim = ValueAnimator.ofInt(currentProgress, (int) (module.getProgress() * 100));

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                currentProgress = val;
                topBarProgress.setProgress(val);
            }
        });
        anim.setDuration(400);
        anim.start();
    }
}
