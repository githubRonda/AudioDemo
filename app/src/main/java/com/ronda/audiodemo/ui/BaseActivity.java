package com.ronda.audiodemo.ui;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.ronda.audiodemo.MediaPlaybackService;
import com.ronda.audiodemo.MusicService;
import com.ronda.audiodemo.R;
import com.ronda.audiodemo.utils.LogHelper;
import com.ronda.audiodemo.utils.NetworkHelper;
import com.ronda.audiodemo.utils.ResourceHelper;

/**
 * Created by Ronda on 2017/12/29.
 * <p>
 * Base activity for activities that need to show a playback control fragment when media is playing.
 * <p>
 * Building a Media Browser Client 的步骤:
 * onCreate() constructs a MediaBrowserCompat. Pass in the name of your MediaBrowserService and the MediaBrowserCompat.ConnectionCallback that you've defined. (传入你MediaBrowserService和已定义的MediaBrowserCompat.ConnectionCallback 的名字)
 * onStart() connects to the MediaBrowserService.  If the connection is successful, the onConnect() callback creates the media controller, links it to the media session, links your UI controls to the MediaController, and registers the controller to receive callbacks from the media session.
 * onStop() disconnects your MediaBrowser and unregisters the MediaController.Callback when your activity stops.
 * 解释: 首先, 在onCreate() 中创建一个 MediaBrowserCompat 客户端对象, 需要传入服务端 MediaBrowserService 的组件名称 和 回调对象 MediaBrowserCompat.ConnectionCallback(用于回调连接过程中的结果)
 * -- 然后, 在onStart()方法中调用 connect() 方法连接 MediaBrowserService服务端, 若连接成功,则会回调上面传入的回调对象中的 onConnected()方法, 这时需要创建并保存 MediaControllerCompat 对象, 通过传入 Sessiontoken 和 服务端的MediaSession关联, 同时给这个Controller注册一个回调, 便于接收 Sessiontoken 中的状态
 * -- 最后, 在onStop()中, 断开连接, 同时注销会话回调对象.
 */

public class BaseActivity extends ActionBarCastActivity implements MediaBrowserProvider {
    private static final String TAG = LogHelper.makeLogTag(BaseActivity.class);

    private MediaBrowserCompat mMediaBrowser;
    private PlaybackControlsFragment mControlsFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogHelper.d(TAG, "Activity onCreate");

        if (Build.VERSION.SDK_INT >= 21) {
            // Since our app icon has the same color as colorPrimary, our entry(入口) in the Recent Apps
            // list gets weird(奇怪的). We need to change either the icon or the color
            // of the TaskDescription.
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(
                    getTitle().toString(),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_white),
                    ResourceHelper.getThemeColor(this, R.attr.colorPrimary, android.R.color.darker_gray)
            );

            setTaskDescription(taskDesc);
        }

        // Connect a media browser just to get the media session token. There are other ways
        // this can be done, for example by sharing the session token
        mMediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, MediaPlaybackService.class), mConnectionCallback, null);// optional Bundle
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.d(TAG, "Activity onStart");

        mControlsFragment = (PlaybackControlsFragment) getFragmentManager().findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }

        hidePlaybackControls();

        mMediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        LogHelper.d(TAG, "Activity onStop");


        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController != null) {
            mediaController.unregisterCallback(mMediaControllerCallback);
        }
        mMediaBrowser.disconnect();
    }


    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return mMediaBrowser;
    }

    protected void onMediaControllerConnected() {
        // empty implementation, can be overridden by clients.
    }

    /**
     * 显示PlaybackControlsFragment, 带有自定义动画
     */
    protected void showPlaybackControls() {
        LogHelper.d(TAG, "showPlaybackControls");

        if (NetworkHelper.isOnline(this)) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom,
                            R.animator.slide_in_from_bottom, R.animator.slide_out_to_bottom)
                    .show(mControlsFragment)
                    .commit();
        }
    }

    /**
     * 隐藏PlaybackControlsFragment
     */
    protected void hidePlaybackControls() {
        LogHelper.d(TAG, "hidePlaybackControls");

        getFragmentManager().beginTransaction().hide(mControlsFragment).commit();
    }


    /**
     * Check if the MediaSession is active and in a "playback-able" state
     * (not NONE and not STOPPED).
     *
     * @return true if the MediaSession's state requires playback controls to be visible.
     */
    protected boolean shouldShowControls() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController == null || mediaController.getMetadata() == null || mediaController.getPlaybackState() == null) {
            return false;
        }

        switch (mediaController.getPlaybackState().getState()) {
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                return false;
            default:
                return true;
        }
    }


    /**
     * 当客户端连接成功后, 需要和MediaSession建立连接
     */
    private void connectToSession() throws RemoteException {
        // Get the token for the MediaSession
        MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

        // Create a MediaControllerCompat
        MediaControllerCompat mediaController = new MediaControllerCompat(this, token);
        // Save the controller
        MediaControllerCompat.setMediaController(this, mediaController);
        // Register a Callback to stay in sync
        mediaController.registerCallback(mMediaControllerCallback);

        if (shouldShowControls()) {
            showPlaybackControls();
        } else {
            LogHelper.d(TAG, "connectionCallback.onConnected: hiding controls because metadata is null");
            hidePlaybackControls();
        }

        if (mControlsFragment != null) {
            mControlsFragment.onConnected();
        }

        onMediaControllerConnected();
    }

    // 连接时的回调
    private MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            LogHelper.d(TAG, "onConnected");

            try {
                connectToSession();
            } catch (RemoteException e) {
                e.printStackTrace();
                LogHelper.e(TAG, e, "could not connect media controller");
                hidePlaybackControls();
            }
        }

        @Override
        public void onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        @Override
        public void onConnectionFailed() {
            // The Service has refused our connection
            Log.d("Liu", "onConnectionFailed");
        }
    };


    // Callback that ensures that we are showing the controls
    // 会话过程中的一些回调
    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (shouldShowControls()) {
                showPlaybackControls();
            } else {
                LogHelper.d(TAG, "mediaControllerCallback.onPlaybackStateChanged: hiding controls because state is ", state.getState());
                hidePlaybackControls();
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (shouldShowControls()) {
                showPlaybackControls();
            } else {
                LogHelper.d(TAG, "mediaControllerCallback.onMetadataChanged: hiding controls because metadata is null");
                hidePlaybackControls();
            }
        }
    };
}
