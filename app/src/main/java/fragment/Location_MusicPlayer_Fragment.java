package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.video_player.R;

/**
 * Created by previous_off_yuan on 2016/12/13.
 * 作用：本地音乐列表页面
 */
public class Location_MusicPlayer_Fragment extends Fragment {


    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_media_mp3_paper_fragment,null);

        return view;
    }
}
