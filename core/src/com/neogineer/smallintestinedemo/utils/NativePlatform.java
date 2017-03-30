package com.neogineer.smallintestinedemo.utils;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by neogineer on 03/03/17.
 */
public interface NativePlatform {
    void saveScreenshot(FileHandle fileHandle);
    void showMessage(String msg);
    void loadingFinished();
}