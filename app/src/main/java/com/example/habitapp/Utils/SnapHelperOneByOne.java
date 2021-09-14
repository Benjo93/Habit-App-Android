package com.example.habitapp.Utils;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Managers.ToDoManager;

import static com.example.habitapp.Managers.ToDoManager.*;

public class SnapHelperOneByOne extends LinearSnapHelper {

    private SnapListener snapListener;

    public SnapHelperOneByOne (SnapListener snapListener) {
        this.snapListener = snapListener;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY){

        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final View currentView = findSnapView(layoutManager);

        if(currentView == null){
            return RecyclerView.NO_POSITION;
        }

        LinearLayoutManager myLayoutManager = (LinearLayoutManager) layoutManager;

        int position1 = myLayoutManager.findFirstVisibleItemPosition();
        int position2 = myLayoutManager.findLastVisibleItemPosition();

        int currentPosition = layoutManager.getPosition(currentView);

        if (velocityX > 400) {
            snapListener.onItemSnapped(position2);
            currentPosition = position2;
        } else if (velocityX < 400) {
            snapListener.onItemSnapped(position1);
            currentPosition = position1;
        }

        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        return currentPosition;
    }
}