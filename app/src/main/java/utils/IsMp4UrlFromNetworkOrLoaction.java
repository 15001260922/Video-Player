package utils;

/**
 * Created by p on 2016/12/29.
 * 作用：工具类，判断视频的url是网络资源，还是本地资源
 */
public class IsMp4UrlFromNetworkOrLoaction {

    public static boolean isNewWorkOrLocation(String url){
        boolean isNetOrLocal = false;//默认是本地资源False

        if(url != null){
            if(url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("rtsp") || url.toLowerCase().startsWith("mms")){
                 isNetOrLocal = true;
            }
        }

        return isNetOrLocal;
    }
}






