package com.codewaves.youtubethumbnailview;


import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codewaves.youtubethumbnailview.downloader.VideoInfoDownloader;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Sergej Kravcenko on 4/16/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

class ThumbnailRequest extends CancellableTask {
   private final ThreadPoolExecutor executor;
   private final ThumbnailView view;
   private final String url;
   private final int minThumbnailSize;
   private final VideoInfoDownloader infoDownloader;
   private final ThumbnailLoadingListener listener;
   private final ImageLoader imageLoader;
   private final boolean ignoreCache;

   private ThumbnailTask task;

   ThumbnailRequest(@NonNull ThreadPoolExecutor executor,
                    @NonNull ThumbnailView view,
                    @NonNull String url,
                    int minThumbnailSize,
                    @NonNull VideoInfoDownloader infoDownloader,
                    @NonNull ImageLoader imageLoader,
                    @Nullable ThumbnailLoadingListener listener,
                    boolean ignoreCache) {
      this.executor = executor;
      this.view = view;
      this.url = url;
      this.minThumbnailSize = minThumbnailSize;
      this.infoDownloader = infoDownloader;
      this.listener = listener;
      this.imageLoader = imageLoader;
      this.ignoreCache = ignoreCache;
   }

   @Override
   public void run() {
      if (listener != null) {
         listener.onLoadingStarted(url, view);
      }

      final Handler handler = new Handler();
      task = new ThumbnailTask(url, minThumbnailSize, infoDownloader, imageLoader, new ThumbnailDownloadListener() {
         @Override
         public void onDownloadFinished(@NonNull VideoInfo info, @Nullable Bitmap bitmap) {
            if (!isCanceled()) {
               // Update views
               view.setThumbnailAndShow(info.getTitle(), info.getLength(), bitmap, null);

               finish();
               if (listener != null) {
                  listener.onLoadingComplete(url, view);
               }
            }
         }

         @Override
         public void onDownloadFailed(@NonNull Throwable error) {
            if (!isCanceled()) {
               finish();
               if (listener != null) {
                  listener.onLoadingFailed(url, view, error);
               }
            }
         }
      }, handler, ignoreCache);
      executor.execute(task);
   }

   @Override
   public void onCancel() {
      executor.remove(task);

      if (listener != null) {
         listener.onLoadingCanceled(url, view);
      }
   }
}
