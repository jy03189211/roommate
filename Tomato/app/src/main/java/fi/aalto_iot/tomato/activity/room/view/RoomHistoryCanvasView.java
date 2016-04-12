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
    private Paint paint = new Paint();

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
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
    }

    public void setData(List data) {
        path = null;
        this.data = data;

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Paint text = new Paint();
        text.setColor(Color.BLACK);
        text.setStyle(Paint.Style.STROKE);
        text.setStrokeWidth(2);
        canvas.drawText("0", 10, canvas.getHeight() - 10, text);

        canvas.drawLine(0, 0, 0, canvas.getHeight() - 30, paint);
        canvas.drawLine(0, canvas.getHeight() - 30, canvas.getWidth(), canvas.getHeight() - 30, paint);

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
            canvas.drawPath(path, paint);
        }
    }
}
