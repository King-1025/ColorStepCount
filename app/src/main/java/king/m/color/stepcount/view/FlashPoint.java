package king.m.color.stepcount.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import king.m.color.stepcount.R;


/**
 * Created by King on 2018/3/25.
 */

public class FlashPoint extends View {

    private Context context;
    private final static String TAG="FlashPoint";
    private Handler mhd;
    private Paint paint;
    private int width;
    private int height;
    private int mainColor=Color.BLACK;
    private int startAlpha=225;
    private int endAlpha=0;
    private float per=15f;
    private float offsetAlpha;
    private int currentAlpha;
    private long delayTime=200;
    private boolean isFlash=false;
    private boolean isInit=true;
    public FlashPoint(Context context){
        this(context,null);
    }
    public FlashPoint(Context context,AttributeSet attributeSet){
        this(context,attributeSet,0);
    }
    public FlashPoint(Context context, AttributeSet attributes, int defStyleAttr){
        super(context,attributes,defStyleAttr);
        this.context=context;
        mhd=new Handler(this.context.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0x00){
                    paint.setAlpha(getCurrentAlpha());
                    invalidate();
                }
            }
        };
        if(isInit) {
            paint = new Paint();
            paint.setColor(mainColor);
            paint.setAlpha(getCurrentAlpha());
            paint.setAntiAlias(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);//圆角弧度
            paint.setStyle(Paint.Style.FILL_AND_STROKE);//设置填充样式
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=getMeasuredWidth();
        height=getMeasuredHeight();
        paint.setStrokeWidth((width+height)/4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPoint(width/2,height/2,paint);
        if(isFlash)
        {
            mhd.sendEmptyMessageDelayed(0x00,delayTime);
        }
    }
    private int getCurrentAlpha(){
        if(isInit){
            if(startAlpha>=0&&endAlpha>=0&&startAlpha<=225&&endAlpha<=225){
                isInit=false;
                currentAlpha=startAlpha;
                offsetAlpha=(endAlpha-startAlpha)/per;
            }else {
                isFlash=false;
                currentAlpha=225;
                Log.w(TAG,"Alpha is invalid."+" startAlpha:"+startAlpha+" endAlpha:"+endAlpha);
            }
        }else {
            currentAlpha += offsetAlpha;
            if(currentAlpha<0||currentAlpha>225){
                currentAlpha=startAlpha;
            }
        }
        return currentAlpha;
    }
    public void start(long delayTime){
        if(!isFlash) {
            isFlash = true;
            if (delayTime == 0) delayTime = 500;
            mhd.sendEmptyMessageDelayed(0x00, delayTime);
        }
    }
    public void stop(){
        isFlash=false;
        mhd.removeMessages(0x00);
    }
    public void setMainColor(int color){
        this.mainColor=color;
        paint.setColor(this.mainColor);
    }
}
