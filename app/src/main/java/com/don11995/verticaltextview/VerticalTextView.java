package com.don11995.verticaltextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

public class VerticalTextView extends TextView {

    public static final int WRITE_UP_BOTTOM = 0;
    public static final int WRITE_BOTTOM_UP = 1;

    private Rect textBounds = new Rect();
    private int writeType = WRITE_UP_BOTTOM;
    private final int defaultFontPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getResources().getDisplayMetrics());

    public VerticalTextView(Context context) {
        super(context);
        init(null);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.VerticalTextView);
            int writeType = a.getInt(R.styleable.VerticalTextView_vtw_writeType,this.writeType);
            a.recycle();

            setWriteType(writeType);
        }
    }


    public int getWriteType() {
        return writeType;
    }

    public void setWriteType(int writeType) {
        if (writeType!=WRITE_BOTTOM_UP&&writeType!=WRITE_UP_BOTTOM) return;
        this.writeType = writeType;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        String text = getText().toString();
        TextPaint paint = getPaint();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        int paddingLeft;
        int paddingRight;
        int paddingTop;
        int paddingBottom;
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                paddingLeft = Math.max(getPaddingLeft(), getPaddingEnd());
                paddingRight = Math.max(getPaddingRight(), getPaddingStart());
            } else {
                paddingLeft = Math.max(getPaddingLeft(), getPaddingStart());
                paddingRight = Math.max(getPaddingRight(), getPaddingEnd());
            }
        } else {
            paddingLeft = getPaddingLeft();
            paddingRight = getPaddingRight();
        }
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:

                width = Math.min(textBounds.height() + paddingLeft + paddingRight, widthSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                width = textBounds.height() + paddingLeft + paddingRight;
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(textBounds.width() + paddingTop + paddingBottom,heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = textBounds.width() + paddingTop + paddingBottom;
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable background = getBackground();
        if (background!=null) {
            background.draw(canvas);
        }
        String text = getText().toString();

        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.setTextSize(getTextSize());
        paint.drawableState = getDrawableState();
        paint.setTypeface(getTypeface());
        paint.getTextBounds(text, 0, text.length(), textBounds);

        int layoutDirection = ViewCompat.getLayoutDirection(this);

        int paddingLeft;
        int paddingRight;
        int paddingTop;
        int paddingBottom;
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL) {
                paddingLeft = Math.max(getPaddingLeft(), getPaddingEnd());
                paddingRight = Math.max(getPaddingRight(), getPaddingStart());
            } else {
                paddingLeft = Math.max(getPaddingLeft(), getPaddingStart());
                paddingRight = Math.max(getPaddingRight(), getPaddingEnd());
            }
        } else {
            paddingLeft = getPaddingLeft();
            paddingRight = getPaddingRight();
        }
        int absGravity = Gravity.getAbsoluteGravity(getGravity(), layoutDirection);
        if (writeType==WRITE_UP_BOTTOM) {
            canvas.rotate(90);
            switch (absGravity&Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.RIGHT:
                    canvas.translate(0, - getWidth()+textBounds.height()-defaultFontPadding + paddingRight);
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    canvas.translate(0, (-getWidth()+textBounds.height())/2 - defaultFontPadding - paddingLeft + paddingRight);
                    break;
                default:
                    canvas.translate(0, -defaultFontPadding - paddingLeft);
                    break;
            }
            switch (absGravity&Gravity.VERTICAL_GRAVITY_MASK) {
                case Gravity.BOTTOM:
                    canvas.translate(getHeight()-textBounds.width() - paddingBottom, 0);
                    break;
                case Gravity.CENTER_VERTICAL:
                    canvas.translate((getHeight()-textBounds.width())/2 + paddingTop - paddingBottom, 0);
                    break;
                default:
                    canvas.translate(paddingTop, 0);
                    break;
            }
        } else {
            canvas.rotate(-90);
            switch (absGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.RIGHT:
                    canvas.translate(-textBounds.width(), getWidth() - defaultFontPadding - paddingRight);
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    canvas.translate(-textBounds.width(), (getWidth() + textBounds.height())/2 - defaultFontPadding - paddingRight + paddingLeft);
                    break;
                default:
                    canvas.translate(-textBounds.width(), textBounds.height() - defaultFontPadding + paddingLeft);
                    break;
            }
            switch (absGravity & Gravity.VERTICAL_GRAVITY_MASK) {
                case Gravity.BOTTOM:
                    canvas.translate(textBounds.width() - getHeight() + paddingBottom, 0);
                    break;
                case Gravity.CENTER_VERTICAL:
                    canvas.translate((textBounds.width() - getHeight())/2 + paddingBottom - paddingTop, 0);
                    break;
                default:
                    canvas.translate(-paddingTop, 0);

            }
        }
        canvas.drawText(text, 0, 0, paint);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setWriteType(ss.writeType);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.writeType = writeType;
        return ss;
    }

    private static class SavedState extends BaseSavedState {
        private int writeType;

        public SavedState(Parcel source) {
            super(source);
            writeType = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(writeType);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

