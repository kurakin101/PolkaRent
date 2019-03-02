package com.polka.rentplace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.polka.rentplace.Face;

import java.util.List;

import androidx.annotation.Nullable;

public class FaceView extends View {

    private List<FirebaseVisionFace> mFaces;

    private Paint mPaint; // кисть
    private float mScale; // во сколько раз нужно увеличить те faces, который отдал ML Kit

    public FaceView(Context context) {
        super(context);
        initialize();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void showFaces(List<FirebaseVisionFace> faces, float scale) {
        mFaces = faces;
        mScale = scale;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                // getBoundingBox() возвращает прямоугольник, в котором лежит лицо
                final Rect boundingBox = face.getBoundingBox();
                canvas.drawRect(boundingBox.left * mScale, boundingBox.top * mScale
                        , boundingBox.right * mScale, boundingBox.bottom * mScale
                        , mPaint);
                Face.faceResult += 1;
            }
        }
    }

    private void initialize() {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.WHITE);
    }

}