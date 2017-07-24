package com.github.rubensousa.previewseekbar.sample.exoplayer;

import android.net.Uri;
import android.view.SurfaceView;
import android.view.View;

import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.PreviewSeekBarLayout;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerManager implements ExoPlayer.EventListener, PreviewLoader {
    private static final int ROUND_DECIMALS_THRESHOLD = 60 * 1000;

    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayerView mPreviewPlayerView;
    private PreviewSeekBarLayout mSeekBarLayout;
    private ExoPlayerMediaSourceBuilder mMediaSourceBuilder;

    private SimpleExoPlayer mPlayer;
    private SimpleExoPlayer mPreviewPlayer;

    public ExoPlayerManager(SimpleExoPlayerView playerView, SimpleExoPlayerView previewPlayerView, PreviewSeekBarLayout seekBarLayout) {
        mPlayerView = playerView;
        mPreviewPlayerView = previewPlayerView;
        mSeekBarLayout = seekBarLayout;
        mMediaSourceBuilder = new ExoPlayerMediaSourceBuilder(playerView.getContext());
    }

    public void play(Uri uri) {
        mMediaSourceBuilder.setUri(uri);
    }

    public void onStart() {
        if (Util.SDK_INT > 23) {
            createPlayers();
        }
    }

    public void onResume() {
        if (Util.SDK_INT <= 23) {
            createPlayers();
        }
    }

    public void onPause() {
        if (Util.SDK_INT <= 23) {
            releasePlayers();
        }
    }

    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayers();
        }
    }

    private void createPlayers() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        if (mPreviewPlayer != null) {
            mPreviewPlayer.release();
        }
        mPlayer = createFullPlayer();
        mPlayerView.setPlayer(mPlayer);
        mPreviewPlayer = createPreviewPlayer();
        mPreviewPlayerView.setPlayer(mPreviewPlayer);
    }

    private SimpleExoPlayer createFullPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(mPlayerView.getContext(), trackSelector, loadControl);
        player.setPlayWhenReady(true);
        player.prepare(mMediaSourceBuilder.getMediaSource(false));
        player.addListener(this);
        return player;
    }

    private SimpleExoPlayer createPreviewPlayer() {
        TrackSelection.Factory videoTrackSelectionFactory = new WorstVideoTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new PreviewLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(mPreviewPlayerView.getContext(), trackSelector, loadControl);
        player.setPlayWhenReady(false);
        player.setVolume(0f);
        player.prepare(mMediaSourceBuilder.getMediaSource(true));
        return player;
    }

    private void releasePlayers() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mPreviewPlayer != null) {
            mPreviewPlayer.release();
            mPreviewPlayer = null;
        }
    }

    public void stopPreview() {
        mPlayer.setPlayWhenReady(true);
        View view = mPreviewPlayerView.getVideoSurfaceView();
        if (view instanceof SurfaceView) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /*ExoPlayer.EventListener*/

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            mSeekBarLayout.hidePreview();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    /*PreviewLoader*/

    @Override
    public void loadPreview(long currentPosition, long max) {
        float offset = (float) currentPosition / max;
        int scale = mPlayer.getDuration() >= ROUND_DECIMALS_THRESHOLD ? 2 : 1;
        float offsetRounded = roundOffset(offset, scale);
        mPlayer.setPlayWhenReady(false);
        mPreviewPlayer.seekTo((long) (offsetRounded * mPreviewPlayer.getDuration()));
        mPreviewPlayer.setPlayWhenReady(false);
        View view = mPreviewPlayerView.getVideoSurfaceView();
        if (view instanceof SurfaceView) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private float roundOffset(float offset, int scale) {
        return (float) (Math.round(offset * Math.pow(10, scale)) / Math.pow(10, scale));
    }
}
