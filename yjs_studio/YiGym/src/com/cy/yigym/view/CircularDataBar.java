package com.cy.yigym.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ejianshen on 15/8/30.
 */
public class CircularDataBar extends View {
    private Paint inerCircularpaint;
    private Context context;
    private static String number="0";
    private static String time="0";

    private static String distance="0";
    private static String calorie="0";
    /**内圆的圆心的x，y坐标*/
    private int inerCircularX,inerCircularY;
    public CircularDataBar(Context context) {
        super(context);
    }

    public CircularDataBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;

    }

    public CircularDataBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        inerCircularpaint=new Paint();
        inerCircularpaint.setAntiAlias(true); //消除锯齿
        int radius=70;
        /**
         * 次数
         */
        inerCircularX=getWidth()/8;
        inerCircularY=getHeight()/3+20;
        int[] inerColors0={130,219,252};
        int[] aroundColors0={238,238,238};
        int[] arcColors0={94,187,237};
        DrawDataCircular(canvas,number,"",inerCircularX, inerCircularY,
                radius, inerColors0, aroundColors0, arcColors0,0,90);
        /**
         * 时长
         */
        inerCircularX=getWidth()*3/8;

        inerCircularY=getHeight()/3+20;
        int[] inerColors1={244,134,190};
        int[] aroundColors1={238,238,238};
        int[] arcColors1={208,85,150};
        DrawDataCircular(canvas,time,"min",inerCircularX,inerCircularY,
                radius,inerColors1,aroundColors1,arcColors1,160,280);
        /**
         * 总里程
         */
        inerCircularX=getWidth()*5/8;

        inerCircularY=getHeight()/3+20;
        int[] inerColors2={254,206,101};
        int[] aroundColors2={238,238,238};
        int[] arcColors2={249,183,38};
        DrawDataCircular(canvas,distance,"km",inerCircularX,inerCircularY,
                radius,inerColors2,aroundColors2,arcColors2,-30,160);
        /**
         * 卡路里
         */
        inerCircularX=getWidth()*7/8;

        inerCircularY=getHeight()/3+20;
        int[] inerColors3={165,211,100};
        int[] aroundColors3={238,238,238};
        int[] arcColors3={124,190,25};
        DrawDataCircular(canvas,calorie,"cal",inerCircularX,inerCircularY,
                radius,inerColors3,aroundColors3,arcColors3,60,270);

        super.onDraw(canvas);
    }

    private void DrawDataCircular(Canvas canvas,String text,String Type,
                                  int inerCircularX,int inerCircularY,
                                  int radius,int[] inerColors,int[] aroundColors,int[] arcColors,int fromAngle,int toAngle){
        inerCircularpaint.setStyle(Paint.Style.FILL);
        inerCircularpaint.setARGB(255, inerColors[0], inerColors[1], inerColors[2]);
        inerCircularpaint.setStrokeWidth(2);
        canvas.drawCircle(inerCircularX, inerCircularY, radius, inerCircularpaint);
        inerCircularpaint.setARGB(255, aroundColors[0], aroundColors[1], aroundColors[2]);
        inerCircularpaint.setStyle(Paint.Style.STROKE);
        inerCircularpaint.setStrokeWidth(20);
        canvas.drawCircle(inerCircularX, inerCircularY, radius, inerCircularpaint);
        inerCircularpaint.setARGB(255, arcColors[0], arcColors[1], arcColors[2]);
        canvas.drawArc(new RectF((inerCircularX) - radius, (inerCircularY) - radius,
                (inerCircularX) + radius, (inerCircularY) + radius), fromAngle, toAngle, false, inerCircularpaint);

        inerCircularpaint.setStrokeWidth(2);
        inerCircularpaint.setColor(Color.WHITE);
        inerCircularpaint.setTextSize(30);
        inerCircularpaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, inerCircularX , inerCircularY, inerCircularpaint);
        inerCircularpaint.setStrokeWidth(2);
        inerCircularpaint.setColor(Color.WHITE);
        inerCircularpaint.setTextSize(22);
        inerCircularpaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(Type,inerCircularX, inerCircularY+20, inerCircularpaint);
    }
    public static void setNumber(String number) {
        CircularDataBar.number = number;
    }

    public static void setTime(String time) {
        CircularDataBar.time = time;
    }

    public static void setDistance(String distance) {
        CircularDataBar.distance = distance;
    }

    public static void setCalorie(String calorie) {
        CircularDataBar.calorie = calorie;
    }
}
