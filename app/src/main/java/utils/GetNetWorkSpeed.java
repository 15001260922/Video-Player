package utils;

import android.content.Context;
import android.net.TrafficStats;
import android.provider.Settings;

/**
 * Created by p on 2017/1/1.
 * 作用：得到网速，下载或缓冲时，显示的下载播放进度.每隔两秒调用一次
 */
public class GetNetWorkSpeed {

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public String showNetSpeed(Context context) {
        String netSpeed ="0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)
                ==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();

        long speed = ((nowTotalRxBytes - lastTotalRxBytes)
                * 1000 /(nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = String.valueOf(speed) + " kb/s";
        return netSpeed;
    }

}

