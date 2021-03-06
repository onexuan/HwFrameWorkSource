package com.android.server.notification;

import android.content.Context;

public class NotificationChannelExtractor implements NotificationSignalExtractor {
    private static final boolean DBG = false;
    private static final String TAG = "ChannelExtractor";
    private RankingConfig mConfig;

    public void initialize(Context ctx, NotificationUsageStats usageStats) {
    }

    /* JADX WARNING: Missing block: B:9:0x002e, code skipped:
            return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public RankingReconsideration process(NotificationRecord record) {
        if (record == null || record.getNotification() == null || this.mConfig == null) {
            return null;
        }
        record.updateNotificationChannel(this.mConfig.getNotificationChannel(record.sbn.getPackageName(), record.sbn.getUid(), record.getChannel().getId(), false));
        return null;
    }

    public void setConfig(RankingConfig config) {
        this.mConfig = config;
    }

    public void setZenHelper(ZenModeHelper helper) {
    }
}
