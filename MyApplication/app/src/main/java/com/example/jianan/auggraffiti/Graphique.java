package com.example.jianan.auggraffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * Created by Jianan on 9/22/2016.
 */
public class Graphique extends View{
    private Path path = new Path();
    private Paint paint  = new Paint();
    private float startX = 0;
    private float startY = 0;
    private float endX = 0;
    private float endY = 0;
    private Canvas canvas ;
    public Graphique(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(attrs, defStyleAttr);
    }
    public Graphique(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(null, 0);

    }
    public Graphique(Context context, AttributeSet attrs) {
        super(context,attrs);
        // TODO Auto-generated constructor stub
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr){
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        this.setDrawingCacheEnabled(true);
    }
    public String saveCanvasToBitmap(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.buildDrawingCache(true);
        Bitmap bitmap = getDrawingCache();
        if(bitmap == null){
            Log.v("bitmap is null");
            return null;
        }
        else{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.v("the length of the compress string is" + String.valueOf(base64.length()));
            return base64;
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        this.canvas = canvas;
        canvas.drawColor(Color.TRANSPARENT);


        paint.setColor(Color.RED);
        canvas.drawCircle(50, 50, 40, paint);
        paint.setColor(Color.BLACK);

        canvas.drawLine(startX,startY, endX,endY, paint);

        canvas.drawPath(path, paint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        float motionX = motionEvent.getX();
        float motionY = motionEvent.getY();

        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(motionX,motionY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(motionX,motionY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();

        return true;
    }
}
