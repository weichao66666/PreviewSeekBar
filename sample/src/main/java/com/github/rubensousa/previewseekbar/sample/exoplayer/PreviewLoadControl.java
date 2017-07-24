package com.github.rubensousa.previewseekbar.sample.exoplayer;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.Util;

public class PreviewLoadControl implements LoadControl {
    private DefaultAllocator mAllocator;

    public PreviewLoadControl() {
        mAllocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
    }

    @Override
    public void onTracksSelected(Renderer[] renderers, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        int targetBufferSize = 0;
        for (int i = 0; i < renderers.length; i++) {
            if (trackSelections.get(i) != null) {
                targetBufferSize += Util.getDefaultBufferSize(renderers[i].getTrackType());
            }
        }
        mAllocator.setTargetBufferSize(targetBufferSize);
    }

    @Override
    public Allocator getAllocator() {
        return mAllocator;
    }

    @Override
    public boolean shouldStartPlayback(long bufferedDurationUs, boolean rebuffering) {
        return bufferedDurationUs >= 1000L * 50; // around 1 frame
    }

    @Override
    public boolean shouldContinueLoading(long bufferedDurationUs) {
        return true;
    }

    @Override
    public void onPrepared() {
    }

    @Override
    public void onStopped() {
    }

    @Override
    public void onReleased() {
    }
}
