package com.example.gftranning;

import java.util.ArrayList;
import java.util.Random;

public class ImageUtils {
    private static final int DEFAULT_FORCE_RATE = 20;
    private Random mRandom;
    private int mForceRate = DEFAULT_FORCE_RATE;

    private enum Command {
        ShowImage, HideImage, ShowErrorResult, ShowCorrect
    }

    public static interface CommandExecutor {
        void showImage();

        void hideImage();

        void showErrorResult();

        void showCorrectResult();
    }

    private ArrayList<Integer> mImageArray = new ArrayList<Integer>();
    private int mLevelLength = 0;

    public ImageUtils() {
        mRandom = new Random(System.currentTimeMillis());
    }

    public void handleCommand(Command cmd) {

    }

    boolean shouldForceRate() {
        return mRandom.nextInt(100) < mForceRate;
    }

    void setLevelLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length too small");
        }
        mLevelLength = length;
    }

    void startNewLevel() {
        mImageArray.clear();
    }

    private int generateNextImage() {
        return 0;
    }
}