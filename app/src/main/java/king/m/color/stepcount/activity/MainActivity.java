package king.m.color.stepcount.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import king.m.color.stepcount.R;
import king.m.color.stepcount.step.UpdateUiCallBack;
import king.m.color.stepcount.step.service.StepService;
import king.m.color.stepcount.step.utils.SharedPreferencesUtils;
import king.m.color.stepcount.view.FlashPoint;
import king.m.color.stepcount.view.StepArcView;

/**
 * King-1025
 * 主页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_data;//历史记录标签
    private StepArcView cc;
    private TextView tv_set;//设置锻炼计划
    private TextView tv_isSupport;
    private SharedPreferencesUtils sp;
    private FlashPoint flashPoint[]={null,null,null};
    private Intent stepService;
    private boolean isBind = false;
    private boolean isServiceRunning=false;
    private final static String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        initData();
        addListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isServiceRunning)
        {
            startFlashPoint();
            tv_isSupport.setText(getResources().getString(R.string.counting));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopFlashPoint();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
            isBind=false;
        }
    }

    private void assignViews() {
        tv_data = (TextView) findViewById(R.id.tv_data);
        cc = (StepArcView) findViewById(R.id.cc);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_isSupport = (TextView) findViewById(R.id.tv_isSupport);
        flashPoint[0]= (FlashPoint) findViewById(R.id.flash_point_0);
        flashPoint[1]= (FlashPoint) findViewById(R.id.flash_point_1);
        flashPoint[2]= (FlashPoint) findViewById(R.id.flash_point_2);

        int color=getResources().getColor(R.color.main_green);
        flashPoint[0].setMainColor(color);
        flashPoint[1].setMainColor(color);
        flashPoint[2].setMainColor(color);
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        stepService= new Intent(this, StepService.class);
        isServiceRunning=checkService(this, "king.m.color.stepcount.step.service.StepService");
        Log.i(TAG,"isServerRunning:"+isServiceRunning);
        startStepService();

    }

    private void addListener() {
        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);
        tv_isSupport.setOnClickListener(this);
    }

    private void startStepService() {
        if(!isBind) isBind = bindService(stepService, conn, Context.BIND_AUTO_CREATE);
        if(!isServiceRunning)startService(stepService);
        isServiceRunning=checkService(this, "king.m.color.stepcount.step.service.StepService");
        if(isServiceRunning) {
            startFlashPoint();
            tv_isSupport.setText(getResources().getString(R.string.counting));
        }
    }

    private void stopStepService(){
        if(isBind) {
            unbindService(conn);
            isBind=false;
            Log.i(TAG,"unbindService(conn);");
        }
        stopService(stepService);
        isServiceRunning=checkService(this, "king.m.color.stepcount.step.service.StepService");
        if(!isServiceRunning) {
            stopFlashPoint();
            tv_isSupport.setText(getResources().getString(R.string.start_count));
        }else{
            Toast.makeText(this,"服务停止异常！",Toast.LENGTH_LONG).show();
        }
    }

    private void startFlashPoint(){
        flashPoint[0].start(0);
        flashPoint[1].start(1500);
        flashPoint[2].start(2000);
    }
    private void stopFlashPoint(){
        for(int i=0;i<flashPoint.length;i++) {
            flashPoint[i].stop();
        }
    }
    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
            cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepService.getStepCount());

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
                    cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this,"失去计步服务连接.",Toast.LENGTH_LONG);
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, SetPlanActivity.class));
                break;
            case R.id.tv_data:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
//            case R.id.tv_isSupport:
//                if(!isServiceRunning){
//                    startStepService();
//                }else{
//                    stopStepService();
//                }
//                break;
        }
    }

    //检测服务是否正在运行
    public static boolean checkService(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }
        Log.e("OnlineService：",className);
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e("serviceName：",serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().contains(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
