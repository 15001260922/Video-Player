package adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.video_player.R;

import java.util.List;

import java_bean.MP4_Bean;
import utils.Make_Millisecond_To_Minute;

/**
 * Created by previous_off_yuan on 2016/12/14.
 * 作用：MP4的适配器，用ListView展示MP4视频数据
 */
public class Location_Mp4_Adapter extends BaseAdapter {
    private final Context context;
    private  List<MP4_Bean> mp4List;
    public Make_Millisecond_To_Minute make_millisecond_to_minute;

    public Location_Mp4_Adapter(Context context, List<MP4_Bean> mp4List){
        this.context = context;
        this.mp4List = mp4List;
        make_millisecond_to_minute = new Make_Millisecond_To_Minute();//毫秒转换成分钟的工具类
    }
    public int getCount() {
        return mp4List.size();
    }
    public Object getItem(int i) {
        return mp4List.get(i);
    }
    public long getItemId(int i) {
        return i;
    }
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.mp4_listview_item,null);
            holder.imgMp4Icon = (ImageView) view.findViewById(R.id.img_mp4_icon);
            holder.txtMp4Name = (TextView) view.findViewById(R.id.txt_mp4_name);
            holder.txtMp4Size = (TextView) view.findViewById(R.id.txt_mp4_size);
            holder.txtMp4Time = (TextView) view.findViewById(R.id.txt_mp4_time);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        //根据positon得到列表中对应位置的数据
        MP4_Bean mp4 = mp4List.get(position);
        holder.txtMp4Name.setText(mp4.getMp4Name());
        holder.txtMp4Size.setText(Formatter.formatFileSize(context,mp4.getMp4Size()));//把视频的单位字节转换成M兆
        holder.txtMp4Time.setText(make_millisecond_to_minute.makeMillisecond_To_Minute((int) mp4.getMp4Time()));//把视频的时间毫秒，转换成分钟（时分秒形式），用到外部工具类
        return view;
    }

//ViewHolder内部类
        class ViewHolder{
        public ImageView imgMp4Icon;
        public TextView txtMp4Name,txtMp4Size,txtMp4Time;
    }
}
