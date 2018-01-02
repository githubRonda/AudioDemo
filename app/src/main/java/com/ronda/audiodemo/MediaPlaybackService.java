package com.ronda.audiodemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.ronda.audiodemo.model.MusicProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronda on 2017/12/27.
 *
 * 官方开发文档中的代码
 *
 * The recommended implementation of media browser services and media browsers are the classes MediaBrowserServiceCompat and MediaBrowserCompat
 * The recommended implementation of media sessions and media controllers are the classes MediaSessionCompat and MediaControllerCompat
 *
 * When a MediaBrowser running in another activity connects to a MediaBrowserService, it binds service (but not started).
 * 当 MediaBrowser 连接 MediaBrowserService 时,是通过绑定方式启动该Service的, 这个行为是 MediaBrowserServiceCompat 内部处理的.
 *
 * The lifecycle of the MediaBrowserService is controlled by the way it is created, the number of clients that have are to it, and the calls it receives from media session callbacks.
 * MediaBrowserService的生命周期是由它的创建方式，客户端的数量以及它所接收的媒体会话的回调函数控制。
 *
 * To summarize:(总结)
 * 1. The service is created when it is started in response to a media button or when an activity binds to it (after connecting via its MediaBrowser).
 * 2. The media session onPlay() callback should include code that calls startService(). This ensures that the service starts and continues to run,
 * -- even when all UI MediaBrowser activities that are bound to it unbind.(从绑定变成解绑状态)
 * 3. The onStop() callback should call stopSelf(). If the service was started, this stops it. In addition, the service is destroyed if there are no activities bound to it.
 * -- Otherwise, the service remains bound until all its activities unbind. (If a subsequent(后来的) startService() call is received before the service is destroyed, the pending(待定的) stop is cancelled.)
 * -- (如果在服务被销毁之前，有startService()的调用，那么待定的停止操作将被取消)
 */

public class MediaPlaybackService extends MediaBrowserServiceCompat {
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";


    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    /**
     * 1. Create and initialize the media session
     * -- 1) Set flags so that the media session can receive callbacks from media controllers and media buttons.
     * -- 2) Create and initialize an instance of PlaybackStateCompat and assign(分配) it to the session. (由于playback state在会话期间会改变, 所以为了复用,节省内容, 推荐使用PlaybackStateCompat.Builder)
     * -- 3) Create an instance of MediaSessionCompat.Callback and assign it to the session.
     * 2. Set the media session token
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Create a MediaSessionCompat
        mMediaSession = new MediaSessionCompat(this, "MusicService");

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSession.setCallback(new MySessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSession.getSessionToken());



//        MusicProvider mMusicProvider = new MusicProvider();
//
//        mMusicProvider.retrieveMediaAsync(null /* Callback */);
    }


    /**
     * A MediaBrowserService has two methods that handle client connections:
     * onGetRoot() controls access to the service,
     * and onLoadChildren() provides the ability for a client to build and display a menu of the MediaBrowserService's content hierarchy.
     */

    //The onGetRoot() method returns the root node of the content hierarchy. If the method returns null, the connection is refused.
    //所以 为了让所有用户连接你的服务和浏览媒体内容，onGetRoot()应该返回一个非null的BrowserRoot, 其中 rootId 表示内容层级 。如果只是让用户连接而不需要浏览内容，则也是返回非空BrowserRoot, 但是 rootID表示一个空的内容层级.
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
        }
    }

    private boolean allowBrowsing(String clientPackageName, int clientUid) {

        return true;
    }

    @Override
    public void onLoadChildren(@NonNull String parentMediaId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        //  Browsing not allowed
        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentMediaId)) {
            result.sendResult(null);
            return;
        }

        // Assume for example that the music catalog is already loaded/cached.

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID.equals(parentMediaId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems);

        // MediaItem objects delivered(交付,投递) by the MediaBrowserService should not contain icon bitmaps.
        // Use a Uri instead by calling setIconUri() when you build the MediaDescription for each item. (当为每个item构建 MediaDescription 时, 通过 setIconUri() 使用 Uri 来代替 icon bitmap)
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {

    }
}
