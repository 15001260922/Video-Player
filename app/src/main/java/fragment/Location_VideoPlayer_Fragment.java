package fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.video_player.Play_Mp4_Activity;
import com.video_player.R;
import com.video_player.Vitamio_Play_Mp4_Activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.Location_Mp4_Adapter;
import java_bean.MP4_Bean;

/**
 * Created by previous_off_yuan on 2016/12/13.
 * 作用：本地音乐列表页面
 */
public class Location_VideoPlayer_Fragment extends Fragment {

    public ListView listView;
    public TextView txtDisplay;
    public ProgressBar progressBar;
    public List<MP4_Bean> mp4List;
    public Location_Mp4_Adapter mp4_adapter;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_media_mp4_paper_fragment,null);
        listView = (ListView) view.findViewById(R.id.location_video_listView);
        txtDisplay = (TextView) view.findViewById(R.id.txt_no_video);
        progressBar = (ProgressBar) view.findViewById(R.id.loaction_video_progressBar);
        //调用获取mp4数据的方法
        getMp4Datas_FromContextResolver();
        //开始播放视频，设置listview的每一条的点击事件
        listView.setOnItemClickListener(new ListviewItemOnClick());
        return view;
    }

    //实现listView的每一条点击事件的接口
    class ListviewItemOnClick implements AdapterView.OnItemClickListener{
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            MP4_Bean mp4 = mp4List.get(position);
            //调用自己的视频播放器，显示意图启动模式,播放的控件是系统自带的，
            Intent intent = new Intent(getActivity(),Play_Mp4_Activity.class);
//            intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/12/12/mp4/161212190638286683_4800.mp4"),"video/*");//传递一个播放地址
            Bundle bundle = new Bundle();
            bundle.putSerializable("mp4list", (Serializable) mp4List);
            intent.putExtras(bundle);
            intent.putExtra("mp4Position",position);
            getActivity().startActivity(intent);
        }
    }


    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mp4List!=null && mp4List.size()>0){
                //有视频数据，设置适配器
                mp4_adapter = new Location_Mp4_Adapter(getActivity(),mp4List);
                listView.setAdapter(mp4_adapter);
                txtDisplay.setVisibility(View.GONE);
            }else {
                //无视频数据，让Textview，和ProgressBar隐藏
                txtDisplay.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    };

    //获得数据库中，视频数据
    public void getMp4Datas_FromContextResolver(){
        //在子线程中获得视频数据信息
        mp4List = new ArrayList<MP4_Bean>();
        new Thread(){
            public void run() {
                super.run();
                //获取数据库中视频的信息，如果是Android6.0版本手机，需要加动态权限，才能获取到，内容提供者提供的信息
                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] object = {MediaStore.Video.Media.DISPLAY_NAME,//视频文件在SD卡的名字
                                   MediaStore.Video.Media.DURATION,//视频总时长
                                   MediaStore.Video.Media.SIZE,//视频文件的大小
                                   MediaStore.Video.Media.DATA,//视频的绝对地址路径
                                   MediaStore.Video.Media.ARTIST//视频作者，艺术家
                };
                Cursor cursor = contentResolver.query(uri, object, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){
                        MP4_Bean mp4 = new MP4_Bean();
                        String name = cursor.getString(0);
                        long time = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String address = cursor.getString(3);
                        String ariist = cursor.getString(4);
                        mp4.setMp4Name(name);
                        mp4.setMp4Time(time);
                        mp4.setMp4Size(size);
                        mp4.setMp4Address(address);
                        mp4.setMp4Artist(ariist);
                        mp4List.add(mp4);
                    }
                    cursor.close();
                }else {
                    listView.setVisibility(View.GONE);
                    txtDisplay.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                    //发消息，通知hangMessage,处理内容
                    handler.sendEmptyMessage(0);
            }
        }.start();
    }

}
