package com.koakh.androidrealmobjectserverstartup.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mario on 15/01/2017.
 */

public class RecyclerViewWithEmptyViewSupport extends RecyclerView {
  private int mMediumAnimationDuration;
  private int mShortAnimationDuration;
  private View emptyView;

  private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
    @Override
    public void onChanged() {
      Adapter<?> adapter = getAdapter();
      if (adapter != null && emptyView != null) {
        if (adapter.getItemCount() == 0) {
          if (emptyView.getVisibility() != VISIBLE) {
            emptyView.setAlpha(0f);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.animate()
                .alpha(1f)
                .setDuration(mMediumAnimationDuration)
                .setListener(null);
          }
        } else {
          if (emptyView.getVisibility() != GONE) {
            // Animate the "hide" view to 0% opacity. After the animation ends, set its visibility
            // to GONE as an optimization step (it won't participate in layout passes, etc.)
            emptyView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                  @Override
                  public void onAnimationEnd(Animator animation) {
                    emptyView.setVisibility(View.GONE);
                  }
                });

          }
        }
      }
    }
  };

  public RecyclerViewWithEmptyViewSupport(Context context) {
    super(context);
    // Retrieve and cache the system's default "short" animation time.
    // Retrieve and cache the system's default "short" animation time.
    mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
  }

  public RecyclerViewWithEmptyViewSupport(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RecyclerViewWithEmptyViewSupport(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void setAdapter(Adapter adapter) {
    super.setAdapter(adapter);

    if (adapter != null) {
      adapter.registerAdapterDataObserver(emptyObserver);
    }

    emptyObserver.onChanged();
  }

  public void setEmptyView(View emptyView) {
    this.emptyView = emptyView;
  }
}