package com.github.rubensousa.previewseekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

class PreviewAnimatorLollipopImpl extends PreviewAnimator {
    private Animator.AnimatorListener mShowListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mMorphView.animate().setListener(null);
            startReveal();
        }
    };

    private Animator.AnimatorListener mHideListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mMorphView.setVisibility(View.INVISIBLE);
            mMorphView.animate().setListener(null);
        }
    };

    public PreviewAnimatorLollipopImpl(PreviewSeekBarLayout previewSeekBarLayout) {
        super(previewSeekBarLayout);
    }

    @Override
    public void show() {
        mMorphView.setX(getPreviewCenterX(mMorphView.getWidth()));
        mMorphView.setY(mSeekBar.getY());
        mMorphView.setVisibility(View.VISIBLE);
        mMorphView.animate()
                .y(getShowY())
                .scaleY(4.0f)
                .scaleX(4.0f)
                .setDuration(MORPH_MOVE_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(mShowListener);
    }

    @Override
    public void hide() {
        mPreviewView.setVisibility(View.VISIBLE);
        mPreviewViewContainer.setVisibility(View.VISIBLE);
        mMorphView.setY(getShowY());
        mMorphView.setScaleX(4.0f);
        mMorphView.setScaleY(4.0f);
        mMorphView.setVisibility(View.INVISIBLE);
        startUnreveal();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startReveal() {
        Animator animator = ViewAnimationUtils.createCircularReveal(mPreviewViewContainer,
                getCenterX(mPreviewViewContainer),
                getCenterY(mPreviewViewContainer),
                mMorphView.getWidth() * 2,
                getRadius(mPreviewViewContainer));
        animator.setTarget(mPreviewViewContainer);
        animator.setDuration(MORPH_REVEAL_DURATION);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mPreviewView.setAlpha(1f);
                mPreviewViewContainer.setVisibility(View.VISIBLE);
                mPreviewView.setVisibility(View.VISIBLE);
                mMorphView.setVisibility(View.INVISIBLE);
                mPreviewView.animate().alpha(0f).setDuration(MORPH_REVEAL_DURATION);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPreviewViewContainer.animate().setListener(null);
                mPreviewView.setVisibility(View.INVISIBLE);
            }
        });
        animator.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startUnreveal() {
        Animator animator = ViewAnimationUtils.createCircularReveal(mPreviewViewContainer,
                getCenterX(mPreviewViewContainer),
                getCenterY(mPreviewViewContainer),
                getRadius(mPreviewViewContainer), mMorphView.getWidth() * 2);
        animator.setTarget(mPreviewViewContainer);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPreviewViewContainer.animate().setListener(null);
                mPreviewView.setVisibility(View.INVISIBLE);
                mPreviewViewContainer.setVisibility(View.INVISIBLE);
                mMorphView.setVisibility(View.VISIBLE);
                mMorphView.animate()
                        .y(getHideY())
                        .scaleY(0.5f)
                        .scaleX(0.5f)
                        .setDuration(UNMORPH_MOVE_DURATION)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(mHideListener);
            }
        });
        mPreviewView.animate()
                .alpha(1f)
                .setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator());
        animator.setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private int getRadius(View view) {
        return (int) Math.hypot(view.getWidth() / 2, view.getHeight() / 2);
    }

    private int getCenterX(View view) {
        return view.getWidth() / 2;
    }

    private int getCenterY(View view) {
        return view.getHeight() / 2;
    }
}
