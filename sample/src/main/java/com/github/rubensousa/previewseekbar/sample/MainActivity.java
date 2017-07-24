package com.github.rubensousa.previewseekbar.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import com.github.rubensousa.previewseekbar.PreviewSeekBar;
import com.github.rubensousa.previewseekbar.PreviewSeekBarLayout;
import com.github.rubensousa.previewseekbar.sample.exoplayer.ExoPlayerManager;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private PreviewSeekBar mSeekBar;            // 进度条
    private PreviewSeekBarLayout mSeekBarLayout;// 预览窗
    private ExoPlayerManager mExoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleExoPlayerView playerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        SimpleExoPlayerView previewPlayerView = (SimpleExoPlayerView) findViewById(R.id.previewPlayerView);
        mSeekBar = playerView.findViewById(R.id.exo_progress);
        mSeekBarLayout = (PreviewSeekBarLayout) findViewById(R.id.previewSeekBarLayout);

        mSeekBarLayout.setTintColorResource(R.color.colorPrimary);

        mSeekBar.addOnSeekBarChangeListener(this);
        mExoPlayerManager = new ExoPlayerManager(playerView, previewPlayerView, mSeekBarLayout);
        mExoPlayerManager.play(Uri.parse(getString(R.string.url_hls)));
        mSeekBarLayout.setup(mExoPlayerManager);

        View view = previewPlayerView.getVideoSurfaceView();

        if (view instanceof SurfaceView) {
            SurfaceView surfaceView = (SurfaceView) view;
            surfaceView.setZOrderMediaOverlay(true);
            surfaceView.setZOrderOnTop(true);
            surfaceView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mExoPlayerManager.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mExoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mExoPlayerManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mExoPlayerManager.onStop();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mExoPlayerManager.stopPreview();
    }
}
