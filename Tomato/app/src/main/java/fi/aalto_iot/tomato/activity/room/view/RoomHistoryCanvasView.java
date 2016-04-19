package fi.aalto_iot.tomato.activity.room.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class RoomHistoryCanvasView extends View {

    private List<Integer> data;
    private Path path = null;
    private Paint dataPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint coordPaint = new Paint();
    private static final int NUMBER_OF_HORIZONTAL_LINES = 5;
    private static final int NUMBER_OF_VERTICAL_LINES = 5;
    private static final int LEFT_PADDING = 90;
    private static final int RIGHT_PADDING = 40;
    private static final int BOTTOM_PADDING = 80;
    private static final int TOP_PADDING = 80;
    private static final int V_LINE_LEN = 8;

    // text size expressed in dp
    // Screen's density scale,
    private final float scale = getResources().getDisplayMetrics().density;
    private static final float TEXT_SIZE = 12.0f;
    private final float TEXT_CENTER_PADDING  = (TEXT_SIZE * scale + 0.5f) / 2;


    public RoomHistoryCanvasView(Context context) {
        super(context);
        init();
    }

    public RoomHistoryCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoomHistoryCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoomHistoryCanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        dataPaint.setColor(Color.RED);
        dataPaint.setStyle(Paint.Style.STROKE);
        dataPaint.setStrokeWidth(2);

        coordPaint.setColor(Color.GRAY);
        coordPaint.setStyle(Paint.Style.STROKE);
        coordPaint.setStrokeWidth(1);

        textPaint.setColor(Color.GRAY);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(1);
        // Convert the dps to pixels
        textPaint.setTextSize((int) (TEXT_SIZE * scale + 0.5f));


    }

    public void setData(List data) {
        path = null;
        this.data = data;

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas != null && canvas.getHeight() > 0) {
            int h = canvas.getHeight();
            int w = canvas.getWidth();

            float interval_v = (h - BOTTOM_PADDING - TOP_PADDING)  / NUMBER_OF_HORIZONTAL_LINES;
            float interval_h = (w - LEFT_PADDING - RIGHT_PADDING)  / NUMBER_OF_VERTICAL_LINES;

            for (int i = 0; i < NUMBER_OF_HORIZONTAL_LINES; i++) {
                canvas.drawLine(LEFT_PADDING, h - BOTTOM_PADDING - i * interval_v, w - RIGHT_PADDING, h - BOTTOM_PADDING - i * interval_v,coordPaint);
                //canvas.drawLine(startx, starty,stopx,stopy, paint);

                canvas.drawText("1000", 8 * scale + 0.5f, h - BOTTOM_PADDING - i * interval_v + TEXT_CENTER_PADDING, textPaint);

            }

            for (int i = 0; i < NUMBER_OF_VERTICAL_LINES; i++) {
                canvas.drawLine(LEFT_PADDING * 2 + i * interval_h, h - BOTTOM_PADDING + V_LINE_LEN, LEFT_PADDING * 2 + i * interval_h,
                        h - BOTTOM_PADDING - V_LINE_LEN,coordPaint);
                //canvas.drawLine(startx, starty,stopx,stopy, paint);

                //canvas.drawText(text, x, y, textPaint);

                canvas.drawText("18:00", LEFT_PADDING * 2 + i * interval_h - 2 * TEXT_CENTER_PADDING, h - BOTTOM_PADDING + 6 * V_LINE_LEN, textPaint);

            }
        }




        //canvas.drawLine(0, 0, 0, canvas.getHeight() - 30, coordPaint);
        //canvas.drawLine(0, canvas.getHeight() - HORIZONTAL_PADDING, canvas.getWidth(), canvas.getHeight() - 30, coordPaint);


/*
        // text size expressed in dp
        final float TEXT_SIZE = 12.0f;
        // Screen's density scale,
        final float scale = getResources().getDisplayMetrics().density;

        Paint text = new Paint();
        // Convert the dps to pixels
        text.setTextSize((int) (TEXT_SIZE * scale + 0.5f));
        canvas.drawText("0", 10, canvas.getHeight() - 10, text);
  */


        if (data != null && data.size() > 0 && canvas != null && canvas.getHeight() > 0 && canvas.getWidth() > 0) {
            if (path == null) {
                path = new Path();
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                int size = width / data.size();
                int heightUnit = height / 255;

                for (int i = 0; i < data.size() - 1; i++) {
                    int current = data.get(i);
                    int next = data.get(i + 1);

                    path.moveTo(size * i, current * heightUnit);
                    path.lineTo(size * (i + 1), next * heightUnit);
                }
                path.close();
            }
            canvas.drawPath(path, dataPaint);
        }
    }
}
