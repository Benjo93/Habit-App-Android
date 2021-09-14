package com.example.habitapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.habitapp.Managers.ToDoManager;
import com.example.habitapp.R;

import static com.example.habitapp.Managers.ActionsBarManager.*;

public class ActionsBarFragment extends Fragment implements
        View.OnClickListener,
        ActionsBarInteraction {

    private ActionsBarListener actionsBarListener;
    private ToDoManager.MainListener mainListener;

    private boolean ACTION_BAR_READY;
    private int ACTION_BAR_MODE = DEFAULT;

    private ImageButton actionsBarToggle;
    private ImageButton actionsBarAffirm;
    private ImageButton actionsBarReject;

    private boolean actionsBarOpen;

    public ActionsBarFragment() { }

    public static ActionsBarFragment newInstance() {
        return new ActionsBarFragment();
    }

    public static ActionsBarFragment newInstance(int MODE) {
        ActionsBarFragment actionsBarFragment = new ActionsBarFragment();
        Bundle args = new Bundle();
        args.putInt("MODE", MODE);
        actionsBarFragment.setArguments(args);
        return actionsBarFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.ACTION_BAR_MODE = getArguments().getInt("MODE");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            actionsBarListener = (ActionsBarListener) context;
            mainListener = (ToDoManager.MainListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ActionsBarListener and MainListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actions_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actionsBarAffirm = view.findViewById(R.id.action_bar_affirm);
        actionsBarReject = view.findViewById(R.id.action_bar_reject);
        actionsBarToggle = view.findViewById(R.id.actions_bar_toggle);

        actionsBarAffirm.setOnClickListener(this);
        actionsBarReject.setOnClickListener(this);
        actionsBarToggle.setOnClickListener(this);

        actionsBarToggle.setVisibility(View.GONE);

        ACTION_BAR_READY = true;

        if (getArguments() != null) setActionsBarMode(getArguments().getInt("MODE", DEFAULT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_affirm :
                //mainListener.vibratePhone(10);
                actionsBarListener.onActionsBarAffirm();
                break;
            case R.id.action_bar_reject :
                //mainListener.vibratePhone(10);
                actionsBarListener.onActionsBarReject();
                break;
            case R.id.actions_bar_toggle :
                actionsBarToggle.animate().rotation(actionsBarOpen ? 180 : 0).setDuration(100).start();
                actionsBarOpen = !actionsBarOpen;
                actionsBarListener.onActionsBarToggle();
                break;
        }
    }

    @Override
    public void setActionsBarMode (int MODE) {

        if (!ACTION_BAR_READY) return;

        ACTION_BAR_MODE = MODE;

        switch (ACTION_BAR_MODE) {

            case DEFAULT :

                actionsBarAffirm.setImageResource(R.drawable.actions_bar_add_naked);
                actionsBarReject.setImageResource(R.drawable.actions_bar_back);

                actionsBarAffirm.setAlpha(0f);
                actionsBarAffirm.animate().alpha(1f).setDuration(200).start();

                //actionsBarAffirm.setTranslationX(-75f);
                //actionsBarAffirm.animate().translationX(0f).setDuration(200).start();

                actionsBarAffirm.setRotation(-45);
                actionsBarAffirm.animate().rotation(0).setDuration(200).start();

/*
                actionsBarReject.setVisibility(View.GONE);
*/

                break;

            case EDIT_ACTIVE:

                actionsBarAffirm.setImageResource(R.drawable.actions_bar_down);
                actionsBarReject.setImageResource(R.drawable.actions_bar_close);

                actionsBarAffirm.setAlpha(0f);
                actionsBarAffirm.animate().alpha(1f).setDuration(200).start();

                actionsBarAffirm.setRotation(-45);
                actionsBarAffirm.animate().rotation(0).setDuration(200).start();

                actionsBarToggle.setAlpha(0f);
                actionsBarToggle.animate().alpha(1f).setDuration(500).start();

                actionsBarReject.setAlpha(0f);
                actionsBarReject.animate().alpha(1f).setDuration(100).start();

                actionsBarReject.setVisibility(View.VISIBLE);

                break;

            case EDIT_CONFIRM:

                actionsBarAffirm.setImageResource(R.drawable.actions_bar_check);
                actionsBarReject.setImageResource(R.drawable.actions_bar_close);

//                actionsBarAffirm.setRotation(0);
//                actionsBarAffirm.animate().rotation(45).setDuration(200).start();

                actionsBarAffirm.setAlpha(0f);
                actionsBarAffirm.animate().alpha(1f).setDuration(200).start();

                actionsBarToggle.setAlpha(0f);
                actionsBarToggle.animate().alpha(1f).setDuration(500).start();

                actionsBarReject.setAlpha(0f);
                actionsBarReject.animate().alpha(1f).setDuration(100).start();

                actionsBarReject.setVisibility(View.VISIBLE);

                break;

            case CREATE :

                actionsBarAffirm.setImageResource(R.drawable.actions_bar_send);
                actionsBarReject.setImageResource(R.drawable.actions_bar_close);

                actionsBarAffirm.setAlpha(0f);
                actionsBarAffirm.animate().alpha(1f).setDuration(200).start();

                actionsBarAffirm.setRotation(-45);
                actionsBarAffirm.animate().rotation(0).setDuration(200).start();

                actionsBarReject.setAlpha(0f);
                actionsBarReject.animate().alpha(1f).setDuration(100).start();

                actionsBarReject.setVisibility(View.VISIBLE);

                break;
        }
    }
}
