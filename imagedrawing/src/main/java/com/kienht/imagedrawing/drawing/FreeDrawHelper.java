package com.kienht.imagedrawing.drawing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by Riccardo Moro on 10/23/2016.
 */

public class FreeDrawHelper {

    /**
     * Function used to check whenever a list of points is a line or a path to draw
     */
    static boolean isAPoint(@NonNull List<Point> points) {
        if (points.size() == 0)
            return false;

        if (points.size() == 1)
            return true;

        for (int i = 1; i < points.size(); i++) {
            if (points.get(i - 1).x != points.get(i).x || points.get(i - 1).y != points.get(i).y)
                return false;
        }

        return true;
    }

    /**
     * Create, initialize and setup a paint
     */
    static Paint createPaintAndInitialize(int paintColor, int paintAlpha,
                                          float paintWidth, boolean fill) {

        Paint paint = createPaint();

        initializePaint(paint, paintColor, paintAlpha, paintWidth, fill);

        return paint;
    }

    static Paint createPaint() {
        return new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    static void initializePaint(Paint paint, int paintColor, int paintAlpha, float paintWidth,
                                boolean fill) {

        if (fill) {
            setupFillPaint(paint);
        } else {
            setupStrokePaint(paint);
        }

        paint.setStrokeWidth(paintWidth);
        paint.setColor(paintColor);
        paint.setAlpha(paintAlpha);
    }

    static void setupFillPaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
    }

    static void setupStrokePaint(Paint paint) {
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new ComposePathEffect(
                new CornerPathEffect(100f),
                new CornerPathEffect(100f)));
        paint.setStyle(Paint.Style.STROKE);
    }

    static void copyFromPaint(Paint from, Paint to, boolean copyWidth) {

        to.setColor(from.getColor());
        to.setAlpha(from.getAlpha());

        if (copyWidth) {
            to.setStrokeWidth(from.getStrokeWidth());
        }
    }

    static void copyFromValues(Paint to, int color, int alpha, float strokeWidth,
                               boolean copyWidth) {

        to.setColor(color);
        to.setAlpha(alpha);

        if (copyWidth) {
            to.setStrokeWidth(strokeWidth);
        }
    }

    /**
     * Converts a given dp number to it's pixel corresponding number
     */
    public static float convertDpToPixels(float dp) {
        return (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Converts a given pixel number to it's dp corresponding number
     */
    public static float convertPixelsToDp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     *  Draw on canvas a shape from points with path and paint. Or generate path (without canvas)
     * @param points
     * @param shape
     * @param canvas Pass null if we only need to generate path only (Eg: for history path)
     * @param path
     * @param strokePaint Pass null if we only need to generate path only (Eg: for history path)
     */
    public static void generatePathOrDraw(List<Point> points, ShapeType shape, Canvas canvas, Path path, Paint strokePaint) {
        // Initialize the current path
        if (path == null)
            path = new Path();
        else
            path.rewind();

        if (shape == ShapeType.Free
                || shape == ShapeType.Line) {

            // If a single point, add a circle to the path
            if (points.size() == 1 || FreeDrawHelper.isAPoint(points)) {

                if (canvas == null) {
                    return;
                }

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(strokePaint.getColor());
                paint.setAlpha(strokePaint.getAlpha());

                canvas.drawCircle(points.get(0).x, points.get(0).y,
                        paint.getStrokeWidth() / 2,
                        paint);
            } else if (points.size() != 0) {// Else draw the complete series of points

                boolean first = true;

                for (Point point : points) {

                    if (first) {
                        path.moveTo(point.x, point.y);
                        first = false;
                    } else {
                        path.lineTo(point.x, point.y);
                    }
                }

                if (canvas == null) {
                    return;
                }
                canvas.drawPath(path, strokePaint);
            }
        } else if (shape == ShapeType.Rectangle) {

            // If a single point, add a circle to the path
            if (points.size() == 1 || FreeDrawHelper.isAPoint(points)) {

                if (canvas == null) {
                    return;
                }

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(strokePaint.getColor());
                paint.setAlpha(strokePaint.getAlpha());

                canvas.drawCircle(points.get(0).x, points.get(0).y,
                        paint.getStrokeWidth() / 2,
                        paint);

            } else if (points.size() == 2) {

                RectF rectF = new RectF(
                        Math.min(points.get(0).x, points.get(1).x),
                        Math.min(points.get(0).y, points.get(1).y),
                        Math.max(points.get(0).x, points.get(1).x),
                        Math.max(points.get(0).y, points.get(1).y));
                path.addRect(rectF, Path.Direction.CW);

                if (canvas == null) {
                    return;
                }

                Paint paint = FreeDrawHelper.createPaint();
                paint.setColor(strokePaint.getColor());
                paint.setAlpha(strokePaint.getAlpha());
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(strokePaint.getStrokeWidth());

                canvas.drawPath(path, paint);
            }
        } else {
            throw new IllegalArgumentException("Invalid ShapeType");
        }
    }
}
