package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class PreviewSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {
    private List<OnSeekBarChangeListener> mListenerList;

    public PreviewSeekBar(Context context) {
        super(context);
        init();
    }

    public PreviewSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mListenerList = new ArrayList<>();
        super.setOnSeekBarChangeListener(this);
    }

    public void setTintColorResource(@ColorRes int colorResource) {
        setTintColor(ContextCompat.getColor(getContext(), colorResource));
    }

    /**
     * 设置滑动块和进度条的颜色
     *
     * @param color
     */
    public void setTintColor(@ColorInt int color) {
        // 设置滑动块的颜色
        Drawable drawable = DrawableCompat.wrap(getThumb());
        DrawableCompat.setTint(drawable, color);
        setThumb(drawable);
        // 设置进度条的颜色
        drawable = DrawableCompat.wrap(getProgressDrawable());
        DrawableCompat.setTint(drawable, color);
        setProgressDrawable(drawable);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        addOnSeekBarChangeListener(listener);
    }

    public void addOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    public void removeOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        mListenerList.remove(listener);
    }

    public void removeAllOnSeekBarChangeListener() {
        for (OnSeekBarChangeListener listener : mListenerList) {
            mListenerList.remove(listener);
        }
    }

    /*SeekBar.OnSeekBarChangeListener*/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        for (OnSeekBarChangeListener listener : mListenerList) {
            listener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        for (OnSeekBarChangeListener listener : mListenerList) {
            listener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        for (OnSeekBarChangeListener listener : mListenerList) {
            listener.onStopTrackingTouch(seekBar);
        }
    }
}
