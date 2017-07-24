package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class PreviewSeekBarLayout extends RelativeLayout implements SeekBar.OnSeekBarChangeListener {
    private View mMorphView;                // 小球
    private View mPreviewView;              // 预览窗
    private PreviewSeekBar mSeekBar;
    private FrameLayout mPreviewViewContainer;
    private PreviewDelegate mDelegate;
    private PreviewLoader mLoader;

    private int mTintColor;

    private boolean mLayoutFlag = false;

    public PreviewSeekBarLayout(Context context) {
        super(context);
        init(context);
    }

    public PreviewSeekBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreviewSeekBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        mTintColor = ContextCompat.getColor(context, outValue.resourceId);

        mMorphView = new View(getContext());
        mMorphView.setBackgroundResource(R.drawable.previewseekbar_morph);

        mPreviewView = new View(getContext());

        mDelegate = new PreviewDelegate(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        } else if (!mLayoutFlag) {
            if (!checkChilds()) {
                throw new IllegalStateException("You need to add a PreviewSeekBar and a FrameLayout as direct childs");
            }

            setSeekbarMargins();

            setColors();

            mDelegate.setup();

            if (mLoader != null) {
                setup(mLoader);
            }

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, 0);
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.previewseekbar_indicator_width);
            layoutParams.height = layoutParams.width;
            addView(mMorphView, layoutParams);

            FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayoutParams.gravity = Gravity.CENTER;
            mPreviewViewContainer.addView(mPreviewView, frameLayoutParams);

            mLayoutFlag = true;
        }
    }

    /**
     * 检查子 View 是否包含 PreviewSeekBar 和 FrameLayout
     *
     * @return
     */
    private boolean checkChilds() {
        int childCount = getChildCount();

        if (childCount < 2) {
            return false;
        }

        boolean hasSeekbar = false;
        boolean hasFrameLayout = false;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child instanceof PreviewSeekBar) {
                mSeekBar = (PreviewSeekBar) child;
                hasSeekbar = true;
            } else if (child instanceof FrameLayout) {
                mPreviewViewContainer = (FrameLayout) child;
                hasFrameLayout = true;
            }

            if (hasSeekbar && hasFrameLayout) {
                return true;
            }
        }

        return false;
    }

    /**
     * 改变 seekbar 位置（左、右均留出预览窗一般的宽度）
     */
    private void setSeekbarMargins() {
        LayoutParams layoutParams = (LayoutParams) mSeekBar.getLayoutParams();
        layoutParams.rightMargin = (int) (mPreviewViewContainer.getWidth() / 2 - mSeekBar.getThumb().getIntrinsicWidth() * 0.9f);
        layoutParams.leftMargin = layoutParams.rightMargin;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginEnd(layoutParams.leftMargin);
            layoutParams.setMarginStart(layoutParams.leftMargin);
        }
        mSeekBar.setLayoutParams(layoutParams);
        requestLayout();
        invalidate();
    }

    public void setup(PreviewLoader loader) {
        mLoader = loader;
        if (mLoader != null && mSeekBar != null) {
            mSeekBar.addOnSeekBarChangeListener(this);
        }
    }

    private void setColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList list = mSeekBar.getThumbTintList();
            if (list != null) {
                mTintColor = list.getDefaultColor();
            }
        }
        setTintColor(mTintColor);
    }

    public void setTintColorResource(@ColorRes int color) {
        setTintColor(ContextCompat.getColor(getContext(), color));
    }

    public void setTintColor(@ColorInt int color) {
        mTintColor = color;
        Drawable drawable = DrawableCompat.wrap(mMorphView.getBackground());
        DrawableCompat.setTint(drawable, color);
        mMorphView.setBackground(drawable);
        mPreviewView.setBackgroundColor(color);
    }

    public boolean isShownPreview() {
        return mDelegate.isShown();
    }

    public void showPreview() {
        mDelegate.show();
    }

    public void hidePreview() {
        mDelegate.hide();
    }

    public FrameLayout getPreviewViewContainer() {
        return mPreviewViewContainer;
    }

    public PreviewSeekBar getSeekBar() {
        return mSeekBar;
    }

    View getPreviewView() {
        return mPreviewView;
    }

    View getMorphView() {
        return mMorphView;
    }

    /*SeekBar.OnSeekBarChangeListener*/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mLoader != null) {
                mLoader.loadPreview(progress, seekBar.getMax());
            }
            showPreview();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        hidePreview();
    }
}
