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
 * Used to draw the graph by finger
 * And save the graph into Base64 string
 */
public class GraphView extends View{
    private Path path = new Path();
    private Paint paint  = new Paint();
    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init();
    }
    public GraphView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();

    }
    public GraphView(Context context, AttributeSet attrs) {
        super(context,attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    // initialize the paint.
    private void init(){
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        this.setDrawingCacheEnabled(true);
    }
    /*
    *   Get the graph from the cache and convert it to the bitmap and compress it into Base64 String
    * @return String the image in the cache and converted to base64 bitmap
    * */
    public String saveCanvasToBitmap(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.buildDrawingCache(true);
        Bitmap bitmap = getDrawingCache();
        if(bitmap == null){
            return null;
        }
        else{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            return Base64.encodeToString(byteArray, Base64.NO_WRAP);
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawPath(path, paint);
    }

    /*
    * Called when the screen is touched
    * Store the movement path into path
     */
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
