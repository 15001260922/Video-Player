package paper_view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.video_player.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fragment.Location_MusicPlayer_Fragment;
import fragment.Location_VideoPlayer_Fragment;
import user_defined_view.LocationMedia_TitleBarIndicator;

/**
 * Created by previous_off_yuan on 2016/12/11.
 *作用：本地媒体类页面，继承父类
 */

public class Location_MediaPaper extends BasePaper {
    public ViewPager viewPager;
    public LocationMedia_TitleBarIndicator indicator;
    public List<String> titleList = Arrays.asList("本地音乐","本地视频");
    public List<Fragment> fragmentList = new ArrayList<Fragment>();;
    public Location_MusicPlayer_Fragment location_musicPlayer;
    public Location_VideoPlayer_Fragment location_videoPlayer;

    public Location_MediaPaper(Context context, FragmentManager fragmentManager) {
        super(context,fragmentManager);
        location_musicPlayer = new Location_MusicPlayer_Fragment();
        location_videoPlayer = new Location_VideoPlayer_Fragment();
        fragmentList.add(location_musicPlayer);
        fragmentList.add(location_videoPlayer);
    }

    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.location_media_paper,null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPaper);
        indicator = (LocationMedia_TitleBarIndicator) view.findViewById(R.id.indicator);
        return view;
    }

    public void initDate() {
        super.initDate();
        indicator.setViewPager(viewPager,0);
        indicator.setTitleItemData(titleList);
        //设置viewpager适配器
       viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
           public Fragment getItem(int position) {
               return fragmentList.get(position);
           }
           public int getCount() {
               return fragmentList.size();
           }
       });
    }


}
