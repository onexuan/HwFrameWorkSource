package android.media.session;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.media.AudioAttributes;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaMetadata.Builder;
import android.media.Rating;
import android.media.VolumeProvider;
import android.media.session.ISessionController.Stub;
import android.media.session.MediaSessionManager.RemoteUserInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import com.android.internal.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public final class MediaSession {
    public static final int FLAG_EXCLUSIVE_GLOBAL_PRIORITY = 65536;
    @Deprecated
    public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
    @Deprecated
    public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
    public static final int INVALID_PID = -1;
    public static final int INVALID_UID = -1;
    private static final String TAG = "MediaSession";
    private boolean mActive;
    private final ISession mBinder;
    private CallbackMessageHandler mCallback;
    private final CallbackStub mCbStub;
    private final MediaController mController;
    private final Object mLock;
    private final int mMaxBitmapSize;
    private PlaybackState mPlaybackState;
    private final Token mSessionToken;
    private VolumeProvider mVolumeProvider;

    public static abstract class Callback {
        private CallbackMessageHandler mHandler;
        private boolean mMediaPlayPauseKeyPending;
        private MediaSession mSession;

        public void onCommand(String command, Bundle args, ResultReceiver cb) {
        }

        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            if (!(this.mSession == null || this.mHandler == null || !Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonIntent.getAction()))) {
                KeyEvent ke = (KeyEvent) mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (ke != null && ke.getAction() == 0) {
                    PlaybackState state = this.mSession.mPlaybackState;
                    long validActions = state == null ? 0 : state.getActions();
                    int keyCode = ke.getKeyCode();
                    if (keyCode != 79 && keyCode != 85) {
                        handleMediaPlayPauseKeySingleTapIfPending();
                        keyCode = ke.getKeyCode();
                        switch (keyCode) {
                            case 86:
                                if ((1 & validActions) != 0) {
                                    onStop();
                                    return true;
                                }
                                break;
                            case 87:
                                if ((validActions & 32) != 0) {
                                    onSkipToNext();
                                    return true;
                                }
                                break;
                            case 88:
                                if ((16 & validActions) != 0) {
                                    onSkipToPrevious();
                                    return true;
                                }
                                break;
                            case 89:
                                if ((8 & validActions) != 0) {
                                    onRewind();
                                    return true;
                                }
                                break;
                            case 90:
                                if ((64 & validActions) != 0) {
                                    onFastForward();
                                    return true;
                                }
                                break;
                            default:
                                switch (keyCode) {
                                    case 126:
                                        if ((4 & validActions) != 0) {
                                            onPlay();
                                            return true;
                                        }
                                        break;
                                    case 127:
                                        if ((2 & validActions) != 0) {
                                            onPause();
                                            return true;
                                        }
                                        break;
                                }
                                break;
                        }
                    }
                    if (ke.getRepeatCount() > 0) {
                        handleMediaPlayPauseKeySingleTapIfPending();
                    } else if (this.mMediaPlayPauseKeyPending) {
                        this.mHandler.removeMessages(23);
                        this.mMediaPlayPauseKeyPending = false;
                        if ((validActions & 32) != 0) {
                            onSkipToNext();
                        }
                    } else {
                        this.mMediaPlayPauseKeyPending = true;
                        this.mSession.dispatchMediaButtonDelayed(this.mSession.getCurrentControllerInfo(), mediaButtonIntent, (long) ViewConfiguration.getDoubleTapTimeout());
                    }
                    return true;
                }
            }
            return false;
        }

        private void handleMediaPlayPauseKeySingleTapIfPending() {
            if (this.mMediaPlayPauseKeyPending) {
                boolean canPause = false;
                this.mMediaPlayPauseKeyPending = false;
                this.mHandler.removeMessages(23);
                PlaybackState state = this.mSession.mPlaybackState;
                long validActions = state == null ? 0 : state.getActions();
                boolean isPlaying = state != null && state.getState() == 3;
                boolean canPlay = (516 & validActions) != 0;
                if ((514 & validActions) != 0) {
                    canPause = true;
                }
                if (isPlaying && canPause) {
                    onPause();
                } else if (!isPlaying && canPlay) {
                    onPlay();
                }
            }
        }

        public void onPrepare() {
        }

        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
        }

        public void onPrepareFromSearch(String query, Bundle extras) {
        }

        public void onPrepareFromUri(Uri uri, Bundle extras) {
        }

        public void onPlay() {
        }

        public void onPlayFromSearch(String query, Bundle extras) {
        }

        public void onPlayFromMediaId(String mediaId, Bundle extras) {
        }

        public void onPlayFromUri(Uri uri, Bundle extras) {
        }

        public void onSkipToQueueItem(long id) {
        }

        public void onPause() {
        }

        public void onSkipToNext() {
        }

        public void onSkipToPrevious() {
        }

        public void onFastForward() {
        }

        public void onRewind() {
        }

        public void onStop() {
        }

        public void onSeekTo(long pos) {
        }

        public void onSetRating(Rating rating) {
        }

        public void onCustomAction(String action, Bundle extras) {
        }
    }

    private static final class Command {
        public final String command;
        public final Bundle extras;
        public final ResultReceiver stub;

        public Command(String command, Bundle extras, ResultReceiver stub) {
            this.command = command;
            this.extras = extras;
            this.stub = stub;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SessionFlags {
    }

    private class CallbackMessageHandler extends Handler {
        private static final int MSG_ADJUST_VOLUME = 21;
        private static final int MSG_COMMAND = 1;
        private static final int MSG_CUSTOM_ACTION = 20;
        private static final int MSG_FAST_FORWARD = 16;
        private static final int MSG_MEDIA_BUTTON = 2;
        private static final int MSG_NEXT = 14;
        private static final int MSG_PAUSE = 12;
        private static final int MSG_PLAY = 7;
        private static final int MSG_PLAY_MEDIA_ID = 8;
        private static final int MSG_PLAY_PAUSE_KEY_DOUBLE_TAP_TIMEOUT = 23;
        private static final int MSG_PLAY_SEARCH = 9;
        private static final int MSG_PLAY_URI = 10;
        private static final int MSG_PREPARE = 3;
        private static final int MSG_PREPARE_MEDIA_ID = 4;
        private static final int MSG_PREPARE_SEARCH = 5;
        private static final int MSG_PREPARE_URI = 6;
        private static final int MSG_PREVIOUS = 15;
        private static final int MSG_RATE = 19;
        private static final int MSG_REWIND = 17;
        private static final int MSG_SEEK_TO = 18;
        private static final int MSG_SET_VOLUME = 22;
        private static final int MSG_SKIP_TO_ITEM = 11;
        private static final int MSG_STOP = 13;
        private Callback mCallback;
        private RemoteUserInfo mCurrentControllerInfo;

        public CallbackMessageHandler(Looper looper, Callback callback) {
            super(looper, null, true);
            this.mCallback = callback;
            this.mCallback.mHandler = this;
        }

        public void post(RemoteUserInfo caller, int what, Object obj, Bundle data, long delayMs) {
            Message msg = obtainMessage(what, Pair.create(caller, obj));
            msg.setData(data);
            if (delayMs > 0) {
                sendMessageDelayed(msg, delayMs);
            } else {
                sendMessage(msg);
            }
        }

        public void handleMessage(Message msg) {
            this.mCurrentControllerInfo = (RemoteUserInfo) ((Pair) msg.obj).first;
            Command obj = ((Pair) msg.obj).second;
            VolumeProvider vp;
            switch (msg.what) {
                case 1:
                    Command cmd = obj;
                    this.mCallback.onCommand(cmd.command, cmd.extras, cmd.stub);
                    break;
                case 2:
                    this.mCallback.onMediaButtonEvent((Intent) obj);
                    break;
                case 3:
                    this.mCallback.onPrepare();
                    break;
                case 4:
                    this.mCallback.onPrepareFromMediaId((String) obj, msg.getData());
                    break;
                case 5:
                    this.mCallback.onPrepareFromSearch((String) obj, msg.getData());
                    break;
                case 6:
                    this.mCallback.onPrepareFromUri((Uri) obj, msg.getData());
                    break;
                case 7:
                    this.mCallback.onPlay();
                    break;
                case 8:
                    this.mCallback.onPlayFromMediaId((String) obj, msg.getData());
                    break;
                case 9:
                    this.mCallback.onPlayFromSearch((String) obj, msg.getData());
                    break;
                case 10:
                    this.mCallback.onPlayFromUri((Uri) obj, msg.getData());
                    break;
                case 11:
                    this.mCallback.onSkipToQueueItem(((Long) obj).longValue());
                    break;
                case 12:
                    this.mCallback.onPause();
                    break;
                case 13:
                    this.mCallback.onStop();
                    break;
                case 14:
                    this.mCallback.onSkipToNext();
                    break;
                case 15:
                    this.mCallback.onSkipToPrevious();
                    break;
                case 16:
                    this.mCallback.onFastForward();
                    break;
                case 17:
                    this.mCallback.onRewind();
                    break;
                case 18:
                    this.mCallback.onSeekTo(((Long) obj).longValue());
                    break;
                case 19:
                    this.mCallback.onSetRating((Rating) obj);
                    break;
                case 20:
                    this.mCallback.onCustomAction((String) obj, msg.getData());
                    break;
                case 21:
                    synchronized (MediaSession.this.mLock) {
                        vp = MediaSession.this.mVolumeProvider;
                    }
                    if (vp != null) {
                        vp.onAdjustVolume(((Integer) obj).intValue());
                        break;
                    }
                    break;
                case 22:
                    synchronized (MediaSession.this.mLock) {
                        vp = MediaSession.this.mVolumeProvider;
                    }
                    if (vp != null) {
                        vp.onSetVolumeTo(((Integer) obj).intValue());
                        break;
                    }
                    break;
                case 23:
                    this.mCallback.handleMediaPlayPauseKeySingleTapIfPending();
                    break;
            }
            this.mCurrentControllerInfo = null;
        }
    }

    public static final class QueueItem implements Parcelable {
        public static final Creator<QueueItem> CREATOR = new Creator<QueueItem>() {
            public QueueItem createFromParcel(Parcel p) {
                return new QueueItem(p, null);
            }

            public QueueItem[] newArray(int size) {
                return new QueueItem[size];
            }
        };
        public static final int UNKNOWN_ID = -1;
        private final MediaDescription mDescription;
        private final long mId;

        public QueueItem(MediaDescription description, long id) {
            if (description == null) {
                throw new IllegalArgumentException("Description cannot be null.");
            } else if (id != -1) {
                this.mDescription = description;
                this.mId = id;
            } else {
                throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
            }
        }

        private QueueItem(Parcel in) {
            this.mDescription = (MediaDescription) MediaDescription.CREATOR.createFromParcel(in);
            this.mId = in.readLong();
        }

        public MediaDescription getDescription() {
            return this.mDescription;
        }

        public long getQueueId() {
            return this.mId;
        }

        public void writeToParcel(Parcel dest, int flags) {
            this.mDescription.writeToParcel(dest, flags);
            dest.writeLong(this.mId);
        }

        public int describeContents() {
            return 0;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MediaSession.QueueItem {Description=");
            stringBuilder.append(this.mDescription);
            stringBuilder.append(", Id=");
            stringBuilder.append(this.mId);
            stringBuilder.append(" }");
            return stringBuilder.toString();
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof QueueItem)) {
                return false;
            }
            QueueItem item = (QueueItem) o;
            if (this.mId == item.mId && Objects.equals(this.mDescription, item.mDescription)) {
                return true;
            }
            return false;
        }
    }

    public static final class Token implements Parcelable {
        public static final Creator<Token> CREATOR = new Creator<Token>() {
            public Token createFromParcel(Parcel in) {
                return new Token(Stub.asInterface(in.readStrongBinder()));
            }

            public Token[] newArray(int size) {
                return new Token[size];
            }
        };
        private ISessionController mBinder;

        public Token(ISessionController binder) {
            this.mBinder = binder;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStrongBinder(this.mBinder.asBinder());
        }

        public int hashCode() {
            return (31 * 1) + (this.mBinder == null ? 0 : this.mBinder.asBinder().hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Token other = (Token) obj;
            if (this.mBinder == null) {
                if (other.mBinder != null) {
                    return false;
                }
            } else if (!this.mBinder.asBinder().equals(other.mBinder.asBinder())) {
                return false;
            }
            return true;
        }

        ISessionController getBinder() {
            return this.mBinder;
        }
    }

    public static class CallbackStub extends ISessionCallback.Stub {
        private WeakReference<MediaSession> mMediaSession;

        public CallbackStub(MediaSession session) {
            this.mMediaSession = new WeakReference(session);
        }

        private static RemoteUserInfo createRemoteUserInfo(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            return new RemoteUserInfo(packageName, pid, uid, caller != null ? caller.asBinder() : null);
        }

        public void onCommand(String packageName, int pid, int uid, ISessionControllerCallback caller, String command, Bundle args, ResultReceiver cb) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchCommand(createRemoteUserInfo(packageName, pid, uid, caller), command, args, cb);
            }
        }

        public void onMediaButton(String packageName, int pid, int uid, Intent mediaButtonIntent, int sequenceNumber, ResultReceiver cb) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                try {
                    session.dispatchMediaButton(createRemoteUserInfo(packageName, pid, uid, null), mediaButtonIntent);
                } catch (Throwable th) {
                    if (cb != null) {
                        cb.send(sequenceNumber, null);
                    }
                }
            }
            if (cb != null) {
                cb.send(sequenceNumber, null);
            }
        }

        public void onMediaButtonFromController(String packageName, int pid, int uid, ISessionControllerCallback caller, Intent mediaButtonIntent) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchMediaButton(createRemoteUserInfo(packageName, pid, uid, caller), mediaButtonIntent);
            }
        }

        public void onPrepare(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepare(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onPrepareFromMediaId(String packageName, int pid, int uid, ISessionControllerCallback caller, String mediaId, Bundle extras) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepareFromMediaId(createRemoteUserInfo(packageName, pid, uid, caller), mediaId, extras);
            }
        }

        public void onPrepareFromSearch(String packageName, int pid, int uid, ISessionControllerCallback caller, String query, Bundle extras) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepareFromSearch(createRemoteUserInfo(packageName, pid, uid, caller), query, extras);
            }
        }

        public void onPrepareFromUri(String packageName, int pid, int uid, ISessionControllerCallback caller, Uri uri, Bundle extras) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrepareFromUri(createRemoteUserInfo(packageName, pid, uid, caller), uri, extras);
            }
        }

        public void onPlay(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlay(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onPlayFromMediaId(String packageName, int pid, int uid, ISessionControllerCallback caller, String mediaId, Bundle extras) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlayFromMediaId(createRemoteUserInfo(packageName, pid, uid, caller), mediaId, extras);
            }
        }

        public void onPlayFromSearch(String packageName, int pid, int uid, ISessionControllerCallback caller, String query, Bundle extras) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlayFromSearch(createRemoteUserInfo(packageName, pid, uid, caller), query, extras);
            }
        }

        public void onPlayFromUri(String packageName, int pid, int uid, ISessionControllerCallback caller, Uri uri, Bundle extras) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPlayFromUri(createRemoteUserInfo(packageName, pid, uid, caller), uri, extras);
            }
        }

        public void onSkipToTrack(String packageName, int pid, int uid, ISessionControllerCallback caller, long id) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchSkipToItem(createRemoteUserInfo(packageName, pid, uid, caller), id);
            }
        }

        public void onPause(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPause(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onStop(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchStop(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onNext(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchNext(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onPrevious(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchPrevious(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onFastForward(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchFastForward(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onRewind(String packageName, int pid, int uid, ISessionControllerCallback caller) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchRewind(createRemoteUserInfo(packageName, pid, uid, caller));
            }
        }

        public void onSeekTo(String packageName, int pid, int uid, ISessionControllerCallback caller, long pos) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchSeekTo(createRemoteUserInfo(packageName, pid, uid, caller), pos);
            }
        }

        public void onRate(String packageName, int pid, int uid, ISessionControllerCallback caller, Rating rating) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchRate(createRemoteUserInfo(packageName, pid, uid, caller), rating);
            }
        }

        public void onCustomAction(String packageName, int pid, int uid, ISessionControllerCallback caller, String action, Bundle args) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchCustomAction(createRemoteUserInfo(packageName, pid, uid, caller), action, args);
            }
        }

        public void onAdjustVolume(String packageName, int pid, int uid, ISessionControllerCallback caller, int direction) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchAdjustVolume(createRemoteUserInfo(packageName, pid, uid, caller), direction);
            }
        }

        public void onSetVolumeTo(String packageName, int pid, int uid, ISessionControllerCallback caller, int value) {
            MediaSession session = (MediaSession) this.mMediaSession.get();
            if (session != null) {
                session.dispatchSetVolumeTo(createRemoteUserInfo(packageName, pid, uid, caller), value);
            }
        }
    }

    public MediaSession(Context context, String tag) {
        this(context, tag, UserHandle.myUserId());
    }

    public MediaSession(Context context, String tag, int userId) {
        this.mLock = new Object();
        this.mActive = false;
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        } else if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag cannot be null or empty");
        } else {
            this.mMaxBitmapSize = context.getResources().getDimensionPixelSize(R.dimen.config_mediaMetadataBitmapMaxSize);
            this.mCbStub = new CallbackStub(this);
            try {
                this.mBinder = ((MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE)).createSession(this.mCbStub, tag, userId);
                this.mSessionToken = new Token(this.mBinder.getController());
                this.mController = new MediaController(context, this.mSessionToken);
            } catch (RemoteException e) {
                throw new RuntimeException("Remote error creating session.", e);
            }
        }
    }

    public void setCallback(Callback callback) {
        setCallback(callback, null);
    }

    public void setCallback(Callback callback, Handler handler) {
        synchronized (this.mLock) {
            if (this.mCallback != null) {
                this.mCallback.mCallback.mSession = null;
                this.mCallback.removeCallbacksAndMessages(null);
            }
            if (callback == null) {
                this.mCallback = null;
                return;
            }
            if (handler == null) {
                handler = new Handler();
            }
            callback.mSession = this;
            this.mCallback = new CallbackMessageHandler(handler.getLooper(), callback);
        }
    }

    public void setSessionActivity(PendingIntent pi) {
        try {
            this.mBinder.setLaunchPendingIntent(pi);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setLaunchPendingIntent.", e);
        }
    }

    public void setMediaButtonReceiver(PendingIntent mbr) {
        try {
            this.mBinder.setMediaButtonReceiver(mbr);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setMediaButtonReceiver.", e);
        }
    }

    public void setFlags(int flags) {
        try {
            this.mBinder.setFlags(flags);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Failure in setFlags.", e);
        }
    }

    public void setPlaybackToLocal(AudioAttributes attributes) {
        if (attributes != null) {
            try {
                this.mBinder.setPlaybackToLocal(attributes);
                return;
            } catch (RemoteException e) {
                Log.wtf(TAG, "Failure in setPlaybackToLocal.", e);
                return;
            }
        }
        throw new IllegalArgumentException("Attributes cannot be null for local playback.");
    }

    public void setPlaybackToRemote(VolumeProvider volumeProvider) {
        if (volumeProvider != null) {
            synchronized (this.mLock) {
                this.mVolumeProvider = volumeProvider;
            }
            volumeProvider.setCallback(new android.media.VolumeProvider.Callback() {
                public void onVolumeChanged(VolumeProvider volumeProvider) {
                    MediaSession.this.notifyRemoteVolumeChanged(volumeProvider);
                }
            });
            try {
                this.mBinder.setPlaybackToRemote(volumeProvider.getVolumeControl(), volumeProvider.getMaxVolume());
                this.mBinder.setCurrentVolume(volumeProvider.getCurrentVolume());
                return;
            } catch (RemoteException e) {
                Log.wtf(TAG, "Failure in setPlaybackToRemote.", e);
                return;
            }
        }
        throw new IllegalArgumentException("volumeProvider may not be null!");
    }

    public void setActive(boolean active) {
        if (this.mActive != active) {
            try {
                this.mBinder.setActive(active);
                this.mActive = active;
            } catch (RemoteException e) {
                Log.wtf(TAG, "Failure in setActive.", e);
            }
        }
    }

    public boolean isActive() {
        return this.mActive;
    }

    public void sendSessionEvent(String event, Bundle extras) {
        if (TextUtils.isEmpty(event)) {
            throw new IllegalArgumentException("event cannot be null or empty");
        }
        try {
            this.mBinder.sendEvent(event, extras);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Error sending event", e);
        }
    }

    public void release() {
        try {
            this.mBinder.destroy();
        } catch (RemoteException e) {
            Log.wtf(TAG, "Error releasing session: ", e);
        }
    }

    public Token getSessionToken() {
        return this.mSessionToken;
    }

    public MediaController getController() {
        return this.mController;
    }

    public void setPlaybackState(PlaybackState state) {
        this.mPlaybackState = state;
        try {
            this.mBinder.setPlaybackState(state);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Dead object in setPlaybackState.", e);
        }
    }

    public void setMetadata(MediaMetadata metadata) {
        if (metadata != null) {
            metadata = new Builder(metadata, this.mMaxBitmapSize).build();
        }
        try {
            this.mBinder.setMetadata(metadata);
        } catch (RemoteException e) {
            Log.wtf(TAG, "Dead object in setPlaybackState.", e);
        }
    }

    public void setQueue(List<QueueItem> queue) {
        try {
            this.mBinder.setQueue(queue == null ? null : new ParceledListSlice(queue));
        } catch (RemoteException e) {
            Log.wtf("Dead object in setQueue.", e);
        }
    }

    public void setQueueTitle(CharSequence title) {
        try {
            this.mBinder.setQueueTitle(title);
        } catch (RemoteException e) {
            Log.wtf("Dead object in setQueueTitle.", e);
        }
    }

    public void setRatingType(int type) {
        try {
            this.mBinder.setRatingType(type);
        } catch (RemoteException e) {
            Log.e(TAG, "Error in setRatingType.", e);
        }
    }

    public void setExtras(Bundle extras) {
        try {
            this.mBinder.setExtras(extras);
        } catch (RemoteException e) {
            Log.wtf("Dead object in setExtras.", e);
        }
    }

    public final RemoteUserInfo getCurrentControllerInfo() {
        if (this.mCallback != null && this.mCallback.mCurrentControllerInfo != null) {
            return this.mCallback.mCurrentControllerInfo;
        }
        throw new IllegalStateException("This should be called inside of MediaSession.Callback methods");
    }

    /* JADX WARNING: Missing block: B:9:?, code skipped:
            r3.mBinder.setCurrentVolume(r4.getCurrentVolume());
     */
    /* JADX WARNING: Missing block: B:10:0x0015, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:11:0x0016, code skipped:
            android.util.Log.e(TAG, "Error in notifyVolumeChanged", r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyRemoteVolumeChanged(VolumeProvider provider) {
        synchronized (this.mLock) {
            if (provider != null) {
                if (provider != this.mVolumeProvider) {
                }
            }
            Log.w(TAG, "Received update from stale volume provider");
        }
    }

    public String getCallingPackage() {
        if (this.mCallback == null || this.mCallback.mCurrentControllerInfo == null) {
            return null;
        }
        return this.mCallback.mCurrentControllerInfo.getPackageName();
    }

    private void dispatchPrepare(RemoteUserInfo caller) {
        postToCallback(caller, 3, null, null);
    }

    private void dispatchPrepareFromMediaId(RemoteUserInfo caller, String mediaId, Bundle extras) {
        postToCallback(caller, 4, mediaId, extras);
    }

    private void dispatchPrepareFromSearch(RemoteUserInfo caller, String query, Bundle extras) {
        postToCallback(caller, 5, query, extras);
    }

    private void dispatchPrepareFromUri(RemoteUserInfo caller, Uri uri, Bundle extras) {
        postToCallback(caller, 6, uri, extras);
    }

    private void dispatchPlay(RemoteUserInfo caller) {
        postToCallback(caller, 7, null, null);
    }

    private void dispatchPlayFromMediaId(RemoteUserInfo caller, String mediaId, Bundle extras) {
        postToCallback(caller, 8, mediaId, extras);
    }

    private void dispatchPlayFromSearch(RemoteUserInfo caller, String query, Bundle extras) {
        postToCallback(caller, 9, query, extras);
    }

    private void dispatchPlayFromUri(RemoteUserInfo caller, Uri uri, Bundle extras) {
        postToCallback(caller, 10, uri, extras);
    }

    private void dispatchSkipToItem(RemoteUserInfo caller, long id) {
        postToCallback(caller, 11, Long.valueOf(id), null);
    }

    private void dispatchPause(RemoteUserInfo caller) {
        postToCallback(caller, 12, null, null);
    }

    private void dispatchStop(RemoteUserInfo caller) {
        postToCallback(caller, 13, null, null);
    }

    private void dispatchNext(RemoteUserInfo caller) {
        postToCallback(caller, 14, null, null);
    }

    private void dispatchPrevious(RemoteUserInfo caller) {
        postToCallback(caller, 15, null, null);
    }

    private void dispatchFastForward(RemoteUserInfo caller) {
        postToCallback(caller, 16, null, null);
    }

    private void dispatchRewind(RemoteUserInfo caller) {
        postToCallback(caller, 17, null, null);
    }

    private void dispatchSeekTo(RemoteUserInfo caller, long pos) {
        postToCallback(caller, 18, Long.valueOf(pos), null);
    }

    private void dispatchRate(RemoteUserInfo caller, Rating rating) {
        postToCallback(caller, 19, rating, null);
    }

    private void dispatchCustomAction(RemoteUserInfo caller, String action, Bundle args) {
        postToCallback(caller, 20, action, args);
    }

    private void dispatchMediaButton(RemoteUserInfo caller, Intent mediaButtonIntent) {
        postToCallback(caller, 2, mediaButtonIntent, null);
    }

    private void dispatchMediaButtonDelayed(RemoteUserInfo info, Intent mediaButtonIntent, long delay) {
        postToCallbackDelayed(info, 23, mediaButtonIntent, null, delay);
    }

    private void dispatchAdjustVolume(RemoteUserInfo caller, int direction) {
        postToCallback(caller, 21, Integer.valueOf(direction), null);
    }

    private void dispatchSetVolumeTo(RemoteUserInfo caller, int volume) {
        postToCallback(caller, 22, Integer.valueOf(volume), null);
    }

    private void dispatchCommand(RemoteUserInfo caller, String command, Bundle args, ResultReceiver resultCb) {
        postToCallback(caller, 1, new Command(command, args, resultCb), null);
    }

    private void postToCallback(RemoteUserInfo caller, int what, Object obj, Bundle data) {
        postToCallbackDelayed(caller, what, obj, data, 0);
    }

    private void postToCallbackDelayed(RemoteUserInfo caller, int what, Object obj, Bundle data, long delay) {
        synchronized (this.mLock) {
            if (this.mCallback != null) {
                this.mCallback.post(caller, what, obj, data, delay);
            }
        }
    }

    public static boolean isActiveState(int state) {
        switch (state) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
                return true;
            default:
                return false;
        }
    }
}
