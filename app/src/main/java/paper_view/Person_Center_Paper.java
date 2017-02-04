package paper_view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.video_player.R;

/**
 * Created by previous_off_yuan on 2016/12/11.
 * 作用:本地音乐类页面，继承父类
 *
 */
public class Person_Center_Paper extends BasePaper {

    //实例化该类时，要先实例化父类的构造方法
    public Person_Center_Paper(Context context, FragmentManager fragmentManager) {
        super(context,fragmentManager);//调用父类的构造方法
    }

    //实现了父类的抽象方法，该方法返回值View对象,就是父类的属性
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.location_media_paper,null);
        return view;
    }

    //实现父类的普通方法
    public void initDate() {
        super.initDate();
    }
}
