package com.github.rubensousa.previewseekbar;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

class PreviewAnimatorImpl extends PreviewAnimator {

    private Animator.AnimatorListener showListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mMorphView.animate().setListener(null);
            startReveal();
        }
    };

    private Animator.AnimatorListener hideListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mMorphView.setVisibility(View.INVISIBLE);
            mMorphView.animate().setListener(null);
        }
    };

    public PreviewAnimatorImpl(PreviewSeekBarLayout previewSeekBarLayout) {
        super(previewSeekBarLayout);
    }

    @Override
    public void show() {
        mPreviewViewContainer.setScaleX(getScaleXStart());
        mPreviewViewContainer.setScaleY(getScaleYStart());
        mMorphView.setX(getPreviewCenterX(mMorphView.getWidth()));
        mMorphView.setY(mSeekBar.getY());
        mMorphView.setVisibility(View.VISIBLE);
        mMorphView.animate()
                .y(getShowY())
                .scaleY(4.0f)
                .scaleX(4.0f)
                .setDuration(MORPH_MOVE_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(showListener);
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

    private void startReveal() {
        mPreviewViewContainer.animate()
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(MORPH_REVEAL_DURATION)
                .scaleX(1)
                .scaleY(1)
                .setListener(new AnimatorListenerAdapter() {
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
                        mPreviewViewContainer.animate().setListener(null);
                        mPreviewView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void startUnreveal() {
        mPreviewView.animate().alpha(1f).setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator());

        mPreviewViewContainer.animate()
                .setDuration(UNMORPH_UNREVEAL_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .scaleX(getScaleXStart())
                .scaleY(getScaleYStart())
                .setListener(new AnimatorListenerAdapter() {
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
                                .setListener(hideListener);
                    }
                });
    }

    private float getScaleXStart() {
        return mMorphView.getWidth() / mPreviewViewContainer.getWidth();
    }

    private float getScaleYStart() {
        return (mMorphView.getWidth() * 2) / mPreviewViewContainer.getWidth();
    }

}
