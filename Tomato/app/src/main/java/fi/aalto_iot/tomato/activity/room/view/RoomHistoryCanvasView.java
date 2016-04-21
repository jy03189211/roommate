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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fi.aalto_iot.tomato.other.SensorData;

public class RoomHistoryCanvasView extends View {

    private List<SensorData> data;
    private Path path = null;
    private Paint dataPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint coordPaint = new Paint();
    private DateFormat df;

    private int max_data_value;
    private int min_data_value;
    private Calendar time_first;
    private Calendar time_last;

    // Screen's density scale,
    private final float scale = getResources().getDisplayMetrics().density;

    private static final int NUMBER_OF_HORIZONTAL_LINES = 5;
    private static final int NUMBER_OF_VERTICAL_LINES = 4;

    private final float LEFT_PADDING = 40 * scale + 0.5f;
    private final float RIGHT_PADDING = 40 * scale + 0.5f;
    private final float BOTTOM_PADDING = 50 * scale + 0.5f;
    private final float TOP_PADDING = 10 * scale + 0.5f;
    private final float V_LINE_LEN = 8 * scale + 0.5f;
    private final float TEXT_LEFT_PADDING = 8 * scale + 0.5f;

    // text size expressed in dp
    private static final float TEXT_SIZE = 12.0f;
    private final float TEXT_CENTER_PADDING  = (TEXT_SIZE * scale + 0.5f);

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
        dataPaint.setStrokeWidth(2 * scale);

        coordPaint.setColor(Color.GRAY);
        coordPaint.setStyle(Paint.Style.STROKE);
        coordPaint.setStrokeWidth(1 * scale);

        textPaint.setColor(Color.GRAY);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(scale);
        // Convert the dps to pixels
        textPaint.setTextSize((int) (TEXT_SIZE * scale + 0.5f));

        showDates(false);
    }

    public void setData(List data) {
        path = null;
        this.data = data;

        this.time_last = this.data.get(0).getTime();
        this.time_first = this.data.get(this.data.size() - 1).getTime();

        this.max_data_value = Collections.max(this.data).getData();
        this.min_data_value = Collections.min(this.data).getData();

        postInvalidate();
    }

    public void showDates(Boolean showDates) {
        if (showDates)
            df = new SimpleDateFormat("dd.MM");
        else
            df = new SimpleDateFormat("HH:mm");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data != null && canvas != null && canvas.getHeight() > 0 && canvas.getWidth() > 0) {
            int h = canvas.getHeight();
            int w = canvas.getWidth();

            float interval_v = (h - BOTTOM_PADDING - TOP_PADDING)  / (NUMBER_OF_HORIZONTAL_LINES -1);
            float interval_h = (w - LEFT_PADDING - RIGHT_PADDING)  / (NUMBER_OF_VERTICAL_LINES - 1);

            for (int i = 0; i < NUMBER_OF_HORIZONTAL_LINES; i++) {
                canvas.drawLine(LEFT_PADDING, h - BOTTOM_PADDING - i * interval_v,
                        w - RIGHT_PADDING, h - BOTTOM_PADDING - i * interval_v,coordPaint);

                canvas.drawText(Integer.toString(this.min_data_value + i * (this.max_data_value - this.min_data_value) / (NUMBER_OF_VERTICAL_LINES - 1)),
                        TEXT_LEFT_PADDING, h - BOTTOM_PADDING - i * interval_v +  4 * scale, textPaint);
            }

            for (int i = 0; i < NUMBER_OF_VERTICAL_LINES; i++) {
                canvas.drawLine(LEFT_PADDING + i * interval_h, h - BOTTOM_PADDING + V_LINE_LEN,
                        LEFT_PADDING + i * interval_h, h - BOTTOM_PADDING - V_LINE_LEN,coordPaint);

                long diff = this.time_last.getTimeInMillis() - this.time_first.getTimeInMillis();
                diff /= NUMBER_OF_VERTICAL_LINES - 1;
                diff *= i;
                diff = this.time_first.getTimeInMillis() + diff;
                Date date = new Date(diff);
                String time = df.format(date);

                canvas.drawText(time, LEFT_PADDING + i * interval_h - TEXT_CENTER_PADDING - scale * 2, h - BOTTOM_PADDING + 4 * V_LINE_LEN, textPaint);
            }
        }

        if (data != null && data.size() > 0 && canvas != null && canvas.getHeight() > 0 && canvas.getWidth() > 0) {
            if (path == null) {
                path = new Path();
                float width = canvas.getWidth() - RIGHT_PADDING - LEFT_PADDING;
                float height = canvas.getHeight() - TOP_PADDING - BOTTOM_PADDING;
                float size = width / data.size();
                float heightUnit = height / max_data_value;

                for (int i = 0; i < data.size() - 1; i++) {
                    int current = data.get(i).getData();
                    int next = data.get(i + 1).getData();

                    path.moveTo(size * i + LEFT_PADDING, canvas.getHeight() - BOTTOM_PADDING - current * heightUnit);
                    path.lineTo(size * (i + 1) + LEFT_PADDING, canvas.getHeight() - BOTTOM_PADDING - next * heightUnit);
                }
                path.close();
            }
            canvas.drawPath(path, dataPaint);
        }
    }
}
