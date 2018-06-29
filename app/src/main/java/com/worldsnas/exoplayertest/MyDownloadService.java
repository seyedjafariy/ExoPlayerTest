package com.worldsnas.exoplayertest;

import android.app.Notification;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class MyDownloadService extends DownloadService {

    protected MyDownloadService() {
        super(1000);
    }

    protected MyDownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval) {
        super(foregroundNotificationId, foregroundNotificationUpdateInterval);
    }

    protected MyDownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval, @Nullable String channelId, int channelName) {
        super(foregroundNotificationId, foregroundNotificationUpdateInterval, channelId, channelName);
    }

    @Override
    protected DownloadManager getDownloadManager() {
        return new DownloadManager(new DownloaderConstructorHelper(new SimpleCache(getCacheDir(), new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024)), new DefaultDataSourceFactory(this, "seyed")),
                getCacheDir(), new DownloadAction.Deserializer("test", 1) {
            @Override
            public DownloadAction readFromStream(int version, DataInputStream input) throws IOException {
                byte[] bytes = new byte[input.available()];
                input.readFully(bytes);
                return new ProgressiveDownloadAction(Uri.parse(input.readUTF()), false, bytes, null);
            }
        });
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return new PlatformScheduler(this, 100);
    }

    @Override
    protected Notification getForegroundNotification(DownloadManager.TaskState[] taskStates) {
        return new NotificationCompat.Builder(this, "seyed_channel")
                .setContentText("downloaded= " + taskStates[0].downloadPercentage + "%")
                .setContentTitle("downloading task id= " + taskStates[0].taskId)
                .build();
    }
}
