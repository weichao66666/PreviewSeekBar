package com.github.rubensousa.previewseekbar;

import android.view.View;

abstract class PreviewAnimator {
    static final int MORPH_REVEAL_DURATION = 250;
    static final int MORPH_MOVE_DURATION = 200;
    static final int UNMORPH_MOVE_DURATION = 200;
    static final int UNMORPH_UNREVEAL_DURATION = 250;

    PreviewSeekBarLayout mSeekBarLayout;
    PreviewSeekBar mSeekBar;
    View mPreviewViewContainer;
    View mPreviewView;
    View mMorphView;

    public PreviewAnimator(PreviewSeekBarLayout previewSeekBarLayout) {
        mSeekBarLayout = previewSeekBarLayout;
        mSeekBar = previewSeekBarLayout.getSeekBar();
        mPreviewViewContainer = previewSeekBarLayout.getPreviewViewContainer();
        mPreviewView = previewSeekBarLayout.getPreviewView();
        mMorphView = previewSeekBarLayout.getMorphView();
    }

    public void move() {
        mMorphView.setX(getPreviewCenterX(mMorphView.getWidth()));
        mPreviewViewContainer.setX(getPreviewX());
    }

    float getPreviewCenterX(int width) {
        return getPreviewX() + (mPreviewViewContainer.getWidth() - width) / 2f;
    }

    float getPreviewX() {
        return (mSeekBarLayout.getWidth() - mPreviewViewContainer.getWidth()) * getWidthOffset(mSeekBar.getProgress());
    }

    float getWidthOffset(int progress) {
        return (float) progress / mSeekBar.getMax();
    }

    float getHideY() {
        return mSeekBar.getY() + mSeekBar.getThumbOffset();
    }

    float getShowY() {
        return (int) (mPreviewViewContainer.getY() + mPreviewViewContainer.getHeight() / 2f);
    }

    public abstract void show();

    public abstract void hide();
}
