package com.github.rubensousa.previewseekbar;

import android.os.Build;
import android.view.View;
import android.widget.SeekBar;

class PreviewDelegate implements SeekBar.OnSeekBarChangeListener {
    private PreviewSeekBarLayout mSeekBarLayout;
    private PreviewAnimator mAnimator;          // 小球弹出、收回动画
    private boolean mShowFlag;
    private boolean mStartTouchFlag;

    public PreviewDelegate(PreviewSeekBarLayout seekBarLayout) {
        mSeekBarLayout = seekBarLayout;
    }

    public void setup() {
        if (mSeekBarLayout != null) {
            mSeekBarLayout.getPreviewViewContainer().setVisibility(View.INVISIBLE);
            mSeekBarLayout.getMorphView().setVisibility(View.INVISIBLE);
            mSeekBarLayout.getPreviewView().setVisibility(View.INVISIBLE);
            mSeekBarLayout.getSeekBar().addOnSeekBarChangeListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAnimator = new PreviewAnimatorLollipopImpl(mSeekBarLayout);
            } else {
                mAnimator = new PreviewAnimatorImpl(mSeekBarLayout);
            }
        }
    }

    public boolean isShown() {
        return mShowFlag;
    }

    public void show() {
        if (mAnimator != null) {
            if (!mShowFlag) {
                mAnimator.show();
                mShowFlag = true;
            }
        }
    }

    public void hide() {
        if (mAnimator != null) {
            if (mShowFlag) {
                mAnimator.hide();
                mShowFlag = false;
            }
        }
    }

    /*SeekBar.OnSeekBarChangeListener*/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mAnimator != null) {
            mAnimator.move();
            if (!mShowFlag && !mStartTouchFlag && fromUser) {
                mAnimator.show();
                mShowFlag = true;
            }
            mStartTouchFlag = false;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mStartTouchFlag = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mAnimator != null) {
            if (mShowFlag) {
                mAnimator.hide();
            }
            mShowFlag = false;
            mStartTouchFlag = false;
        }
    }
}
