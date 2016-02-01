package com.rstudio.view.waveview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by Ryan on 1/31/16.
 */
public class Utils {

    public static void drawPolygon(Canvas canvas, Paint paint, WavePoint[] points) {
        if (points.length < 3) return;
        Path p = new Path();
        p.reset();
        p.moveTo(points[0].x, points[0].y);
        for (int i = 1; i < points.length; i++) {
            p.lineTo(points[i].x, points[i].y);
        }
        p.lineTo(points[0].x, points[0].y);
        canvas.drawPath(p, paint);
    }

    /**
     * Transform axis to fit real screen size
     * @param midPivotX
     * @param midPivotY
     * @param ratio
     * @param ps
     * @return
     */
    public static WavePoint[] getRightAxis(int midPivotX, int midPivotY, float ratio, WavePoint[] ps) {
        WavePoint[] ret = new WavePoint[4];
        if (ps.length < 1) return null;
        for (int i = 0; i < ps.length; i++) {
            ret[i] = new WavePoint(midPivotX + ps[i].x*ratio
                    , midPivotY - ps[i].y*ratio);
        }
        return ret;
    }

    /**
     * We need this function because of floating-point bias :D
     * @param a
     * @param b
     * @return 1 for a greater than b, -1 for lesser and 0 for equal
     */
    public static int compare(float a, float b) {
        float ret = a-b;
        if (ret > 0.0001) return 1;
        else if (ret < -0.0001) return -1;
        else return 0;
    }

}
