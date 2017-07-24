package com.github.rubensousa.previewseekbar.sample.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerMediaSourceBuilder {
    private DefaultBandwidthMeter mBandwidthMeter;
    private Context mContext;
    private Uri mUri;
    private int mStreamType;
    private Handler mHandler = new Handler();

    public ExoPlayerMediaSourceBuilder(Context context) {
        mContext = context;
        mBandwidthMeter = new DefaultBandwidthMeter();
    }

    public void setUri(Uri uri) {
        mUri = uri;
        mStreamType = Util.inferContentType(uri.getLastPathSegment());
    }

    public MediaSource getMediaSource(boolean preview) {
        switch (mStreamType) {
            case C.TYPE_SS:
                return new SsMediaSource(mUri,
                        new DefaultDataSourceFactory(mContext, null, getHttpDataSourceFactory(preview)),
                        new DefaultSsChunkSource.Factory(getDataSourceFactory(preview)),
                        mHandler,
                        null);
            case C.TYPE_DASH:
                return new DashMediaSource(mUri,
                        new DefaultDataSourceFactory(mContext, null, getHttpDataSourceFactory(preview)),
                        new DefaultDashChunkSource.Factory(getDataSourceFactory(preview)),
                        mHandler,
                        null);
            case C.TYPE_HLS:
                return new HlsMediaSource(mUri, getDataSourceFactory(preview), mHandler, null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(mUri, getDataSourceFactory(preview), new DefaultExtractorsFactory(), mHandler, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + mStreamType);
            }
        }
    }

    private DataSource.Factory getDataSourceFactory(boolean preview) {
        return new DefaultDataSourceFactory(mContext, preview ? null : mBandwidthMeter, getHttpDataSourceFactory(preview));
    }

    private DataSource.Factory getHttpDataSourceFactory(boolean preview) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(mContext, "ExoPlayerDemo"), preview ? null : mBandwidthMeter);
    }
}
