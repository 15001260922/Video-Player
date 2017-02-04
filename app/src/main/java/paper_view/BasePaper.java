package paper_view;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

/**
 * Created by previous_off_yuan on 2016/12/11.
 * //作用：基类，在基类里定义好，要做的事（有些要子类强制做的-如abstract方法，还有些不必须做的方法）
 *         同时该类，也是Activiey中Fragment,和View视图连接的载体，为Fragment提供动态创建视图的载体，保持对象单例
 */
public abstract class BasePaper {

    public View rootView;
    public boolean isInstance;
    public final Context mContext;
    public final FragmentManager fragmentManager;

    public BasePaper(Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.mContext = context;
        rootView = initView();
    }

    public abstract View initView();//基础父类必须实现此方法

    public void initDate(){

    }
}
