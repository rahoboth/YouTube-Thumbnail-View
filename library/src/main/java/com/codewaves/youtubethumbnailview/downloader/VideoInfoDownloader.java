package com.codewaves.youtubethumbnailview.downloader;

import android.support.annotation.NonNull;

import com.codewaves.youtubethumbnailview.VideoInfo;

import java.io.IOException;

/**
 * Created by Sergej Kravcenko on 4/15/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public interface VideoInfoDownloader {
   @NonNull
   VideoInfo download(@NonNull String url, int minThumbnailWidth) throws IOException;
}
