package com.adrienpoupa.attestationcoronavirus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class FingerPaintActivity extends Activity {

    DrawingView dv;
    private Paint mPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        LinearLayout layout = new LinearLayout(this);

        // Define the LinearLayout's characteristics
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set generic layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button deleteButton = new Button(this);
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dv.setDrawingCacheEnabled(false);

                dv.onSizeChanged(dv.getWidth(), dv.getHeight(), dv.getWidth(), dv.getHeight());
                dv.invalidate();

                dv.setDrawingCacheEnabled(true);
            }
        });
        layout.addView(deleteButton, params);

        Button saveButton = new Button(this);
        saveButton.setText(R.string.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bitmap bitmap = dv.getDrawingCache();

                File signatureFile = new File(Environment.getExternalStorageDirectory() + "/Documents/Attestations/signature.png");

                try {
                    if (!signatureFile.exists()) {
                        signatureFile.createNewFile();
                    }

                    FileOutputStream ostream = new FileOutputStream(signatureFile);

                    bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);

                    ostream.close();

                    dv.invalidate();

                    Toast.makeText(getApplicationContext(), "Signature enregistrée", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
                } finally {
                    dv.setDrawingCacheEnabled(false);

                    finish();
                }
            }
        });
        layout.addView(saveButton, params);

        dv = new DrawingView(this);
        layout.addView(dv);

        setContentView(layout);
    }

    /**
     * https://stackoverflow.com/a/16650524
     */
    public class DrawingView extends View {

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            setDrawingCacheEnabled(true);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touchStart(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touchMove(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touchUp() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}