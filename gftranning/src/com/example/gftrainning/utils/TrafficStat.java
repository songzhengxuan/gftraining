
package com.example.gftrainning.utils;

import android.content.Context;

public class TrafficStat {
    public interface ITrafficStat {
        void insertBytesToTraffic(Context context, long upload, long download, int type, int subtype);
    }

    private static ITrafficStat sImpl;

    public static void setTrafficStatImpl(ITrafficStat impl) {
        sImpl = impl;
    }

    public static void insertBytesToTraffic(Context context, long upload, long download, int type, int subtype) {
        if (sImpl != null) {
            sImpl.insertBytesToTraffic(context, upload, download, type, subtype);
        }
    }
}
