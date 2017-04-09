package com.suturesapp.workspace;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.suturesapp.workspace.utils.NativePlatform;

public class IOSLauncher extends IOSApplication.Delegate implements NativePlatform {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new com.suturesapp.workspace.SmallIntestineDemoGame(this), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void saveScreenshot(FileHandle fileHandle) {

    }

    @Override
    public void showMessage(String msg) {

    }

    @Override
    public void loadingFinished() {

    }
}