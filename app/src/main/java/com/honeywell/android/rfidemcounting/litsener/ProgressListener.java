package com.honeywell.android.rfidemcounting.litsener;

/**
 * Created by HUYUEHUI on 2018/4/1.
 */

public interface ProgressListener {
    void onProgress(long currentBytes, long contentLength, boolean done);

}
