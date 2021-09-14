package com.example.habitapp.Managers;

public class ActionsBarManager {

    public static final int DEFAULT = 1111;
    public static final int CREATE = 2222;
    public static final int EDIT_ACTIVE = 3333;
    public static final int EDIT_CONFIRM = 4444;

    public interface ActionsBarListener {
        void onActionsBarAffirm();
        void onActionsBarReject();
        void onActionsBarToggle();
    }

    public interface ActionsBarInteraction {
        void setActionsBarMode (int MODE);
    }
}

