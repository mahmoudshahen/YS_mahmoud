package ysolution.ys_mahmoud;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;


public class Anime {
    public static void animate(RecyclerView.ViewHolder holder, boolean even)
    {
        ObjectAnimator animatorTranslateX = null;
        if(even)
            animatorTranslateX = ObjectAnimator.ofFloat(holder.itemView,"translationX",-400,0);
        else
            animatorTranslateX = ObjectAnimator.ofFloat(holder.itemView,"translationX", 400,0);
        animatorTranslateX.setDuration(1000);
        animatorTranslateX.start();
    }
}
