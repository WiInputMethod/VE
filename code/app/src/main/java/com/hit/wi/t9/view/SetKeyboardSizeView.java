package com.hit.wi.t9.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

public class SetKeyboardSizeView extends View {

    public enum SettingType {
        QuickSetMode, FullSetMode, MoveMode
    }

    private final int EDGE_Null=0;
    private final int EDGE_Top=1;
    private final int EDGE_Right=2;
    private final int EDGE_Bottom=3;
    private final int EDGE_Left=4;
    private final int EDGE_Fuck=5;
    private final int EDGE_Other=6;
    private final int EDGE_Finish=7;
    private final int EDGE_Reset=8;

    SettingType mSettingType;
    Rect mKeyboardRect;
    Rect mOldKBRect;
    RectF mFinishButtonRect;
    RectF mResetButtonRect;
    Paint mPaint;
    int mScreenHeight;
    int mScreenWidth;
    int mShadowRadius;
    int mBase;
    int mEdgeOn;
    Path mPath;
    float mDownX, mDownY;
    OnChangeListener mOnChangeListener;
    private String mMovingIcon;
    private int statusBarHight;

    public SetKeyboardSizeView(Context context, OnChangeListener ocl) {
        super(context);
        mPaint = new Paint();
        mPath = new Path();
        mOldKBRect = new Rect();
        mKeyboardRect = new Rect();
        mOnChangeListener = ocl;
        mFinishButtonRect = new RectF();
        mResetButtonRect = new RectF();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Align.CENTER);
    }

    public void SetPos(Rect keyboardRect) {
        mOldKBRect.set(keyboardRect);
        mKeyboardRect.set(keyboardRect);
    }

    public void SetScreenInfo(int width, int height, int statusbarhight) {
        mScreenHeight = height;
        mScreenWidth = width;
        mShadowRadius = Math.max(mScreenWidth * 3 / 320, 1);
        mBase = mShadowRadius * 3;
    }

    public void SetSettingType(SettingType type) {
        mSettingType = type;
    }

    private int GetMinKeyboardWidth() {
        return Math.max(mScreenWidth / 5,200);
    }

    private int GetMinKeyboardHeight() {
        return Math.max(mScreenHeight / 5, 300);
    }

    public void UpdatePos(float deltaX, float deltaY) {
        /**
         * 判断键盘横向会不会超出屏幕
         */
        if (mOldKBRect.left + deltaX < 0) {
            deltaX = -mOldKBRect.left;
        }
        if (mOldKBRect.right + deltaX > mScreenWidth) {
            deltaX = mScreenWidth - mOldKBRect.right;
        }
        /**
         * 判断键盘纵向边界会不会超出屏幕
         */
        if ((int) (mOldKBRect.top + deltaY) < statusBarHight) {
            deltaY = statusBarHight - mOldKBRect.top;
        }
        if ((int) (mOldKBRect.bottom + deltaY) > mScreenHeight) {
            deltaY = mScreenHeight - mOldKBRect.bottom;
        }
        mKeyboardRect.set(mOldKBRect);
        mKeyboardRect.offset((int) deltaX, (int) (deltaY));
        invalidate();
    }

    public void UpdateSize(float deltaX, float deltaY) {
        if (mOldKBRect.width() - deltaX < GetMinKeyboardWidth()) {
            deltaX = mOldKBRect.width() - GetMinKeyboardWidth();
        }
        deltaX = deltaX < -mOldKBRect.left ? -mOldKBRect.left : deltaX;
        if (mOldKBRect.height() - deltaY < GetMinKeyboardHeight()) {
            deltaY = mOldKBRect.height() - GetMinKeyboardHeight();
        }
        deltaY = deltaY < -mOldKBRect.top ? -mOldKBRect.top : deltaY;
        mKeyboardRect.left = (int) (mOldKBRect.left + deltaX);
        mKeyboardRect.top = (int) (mOldKBRect.top + deltaY);
        invalidate();
    }

    PathEffect effects = new DashPathEffect(new float[]{5, 5}, 1);

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(mShadowRadius / 2);

        mPaint.setStyle(Style.FILL);
        mPaint.setShadowLayer(mShadowRadius, mShadowRadius,
                mShadowRadius, Color.argb(23, 0x66, 0xcc, 255));
		/*
		 * 绘制键盘区域
		 */
        mPaint.setColor(Color.argb(85, 0x33, 0xaa, 255));
        canvas.drawRect(mKeyboardRect, mPaint);
        mPaint.clearShadowLayer();

        mPaint.setStyle(Style.STROKE);
        mPaint.setPathEffect(effects);
		/*
		 * 绘制键盘区域边框
		 */
        mPaint.setColor(Color.argb(225, 0x33, 0x33, 0x33));
        canvas.drawRect(mKeyboardRect, mPaint);
        mPaint.setPathEffect(null);
        mPaint.setStyle(Style.FILL);

        if (mSettingType == SettingType.MoveMode) {
            mPaint.setShadowLayer(1, 0, 1, Color.BLACK);
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(Math.min(mKeyboardRect.width(), mKeyboardRect.height()) / 5);
            canvas.drawText(mMovingIcon, mKeyboardRect.left, mKeyboardRect.top + (mPaint.getTextSize() - mPaint.descent()) / 2, mPaint);
        } else if (mSettingType == SettingType.QuickSetMode) {
            drawSlashArrow(mKeyboardRect.left - mBase, mKeyboardRect.top
                    - mBase, mBase, 100, canvas, mPaint, mPath);
        } else if (mSettingType == SettingType.FullSetMode) {

            mFinishButtonRect.left = mKeyboardRect.left
                    + mKeyboardRect.width() / 5;
            mFinishButtonRect.right = mKeyboardRect.right
                    - mKeyboardRect.width() / 5;
            mFinishButtonRect.top = mKeyboardRect.top
                    + mKeyboardRect.height() / 6;
            mFinishButtonRect.bottom = mKeyboardRect.centerY()
                    - mKeyboardRect.height() / 6;

            mResetButtonRect.left = mKeyboardRect.left
                    + mKeyboardRect.width() / 5;
            mResetButtonRect.right = mKeyboardRect.right
                    - mKeyboardRect.width() / 5;
            mResetButtonRect.top = mKeyboardRect.centerY()
                    + mKeyboardRect.height() / 6;
            mResetButtonRect.bottom = mKeyboardRect.bottom
                    - mKeyboardRect.height() / 6;

			/*
			 * 画按键内部填充色
			 */
            mPaint.setShadowLayer(mShadowRadius / 2, 0,
                    mShadowRadius, Color.argb(43, 0, 0, 0));
            if (mEdgeOn == 7) {
                mPaint.setColor(Color.argb(255, 0x99, 0xee, 255));
            } else {
                mPaint.setColor(Color.argb(255, 0x66, 0xcc, 255));
            }
            canvas.drawRoundRect(mFinishButtonRect, mShadowRadius, mShadowRadius, mPaint);
            if (mEdgeOn == 8) {
                mPaint.setColor(Color.argb(255, 0x99, 0xee, 255));
            } else {
                mPaint.setColor(Color.argb(255, 0x66, 0xcc, 255));
            }
            canvas.drawRoundRect(mResetButtonRect, mShadowRadius, mShadowRadius, mPaint);
            mPaint.setStyle(Style.FILL);
            mPaint.clearShadowLayer();
			
			/*
			 * 画按键边框
			 */
            mPaint.setColor(Color.argb(255, 0x33, 0xaa, 255));
            mPaint.setStyle(Style.STROKE);
            canvas.drawRoundRect(mFinishButtonRect, mShadowRadius, mShadowRadius, mPaint);
            canvas.drawRoundRect(mResetButtonRect, mShadowRadius, mShadowRadius, mPaint);
            mPaint.setStyle(Style.FILL);

			
			
			
			/*
			 * 绘制按键文字
			 */
            mPaint.setShadowLayer(1, 0, 1, Color.BLACK);
            mPaint.setColor(Color.argb(255, 0x33, 0xaa, 255));
            mPaint.setTextSize(mFinishButtonRect.height() / 2);
            String text = "完成";
            final float width = mPaint.measureText(text);
            if (width > mFinishButtonRect.width() * 0.9f) {
                mPaint.setTextSize(mFinishButtonRect.width() * mPaint.getTextSize() * 0.9f
                        / width);
            }
            canvas.drawText(text, mFinishButtonRect.centerX(), mFinishButtonRect.centerY() + (mPaint.getTextSize() - mPaint.descent()) / 2, mPaint);
            canvas.drawText("重置", mResetButtonRect.centerX(), mResetButtonRect.centerY() + (mPaint.getTextSize() - mPaint.descent()) / 2, mPaint);
            mPaint.clearShadowLayer();

            drawVerticalArrow(mKeyboardRect.centerX(), mKeyboardRect.top
                    - mBase * 2, mBase, 1, canvas, mPaint, mPath);
            drawVerticalArrow(mKeyboardRect.centerX(), mKeyboardRect.bottom
                    - mBase * 2, mBase, 3, canvas, mPaint, mPath);
            drawHorizontalArrow(mKeyboardRect.left - mBase * 2,
                    mKeyboardRect.centerY(), mBase, 4, canvas, mPaint, mPath);
            drawHorizontalArrow(mKeyboardRect.right - mBase * 2,
                    mKeyboardRect.centerY(), mBase, 2, canvas, mPaint, mPath);
//			if(!mJustKeyboard){
//				drawHorizontalArrow((mLeftHand ? mCandidateRect.right : mCandidateRect.left) - mBase * 2,
//						mCandidateRect.centerY(), mBase, mLeftHand ? 2 : 5, canvas, mPaint, mPath);
//			}


        }
//		mKeyboardRect.top=mScreenHeight-mKeyboardRect.top;
        super.onDraw(canvas);
    }

    private final void drawVerticalArrow(final float x, final float y,
                                         final float base, final int id, final Canvas canvas,
                                         final Paint paint, final Path path) {
        path.moveTo(x, y);
        path.rLineTo(base, base);
        path.rLineTo(-base / 2, 0);
        path.rLineTo(0, base * 2);
        path.rLineTo(base / 2, 0);
        path.rLineTo(-base, base);
        path.rLineTo(-base, -base);
        path.rLineTo(base / 2, 0);
        path.rLineTo(0, -base * 2);
        path.rLineTo(-base / 2, 0);
        path.close();
        paint.setShadowLayer(mShadowRadius << 1, 0, mShadowRadius << 1,
                Color.argb(255, 30, 33, 37));
        if (mEdgeOn == id) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.BLACK);
        }
        canvas.drawPath(path, paint);
        paint.clearShadowLayer();
        path.reset();

        paint.setColor(Color.WHITE);
        canvas.drawCircle(x, y + base * 2, base / 2, paint);
    }

    private final void drawHorizontalArrow(final float x, final float y,
                                           final float base, final int id, final Canvas canvas,
                                           final Paint paint, final Path path) {
        path.moveTo(x, y);
        path.rLineTo(base, -base);
        path.rLineTo(0, base / 2);
        path.rLineTo(base * 2, 0);
        path.rLineTo(0, -base / 2);
        path.rLineTo(base, base);
        path.rLineTo(-base, base);
        path.rLineTo(0, -base / 2);
        path.rLineTo(-base * 2, 0);
        path.rLineTo(0, base / 2);
        path.close();
        paint.setShadowLayer(mShadowRadius << 1, 0, mShadowRadius << 1,
                Color.argb(255, 30, 33, 37));
        if (mEdgeOn == id) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.BLACK);
        }
        canvas.drawPath(path, paint);
        paint.clearShadowLayer();
        path.reset();

        paint.setColor(Color.WHITE);
        canvas.drawCircle(x + base * 2, y, base / 2, paint);
    }

    private final void drawSlashArrow(final float x, final float y,
                                      final float base, final int id, final Canvas canvas,
                                      final Paint paint, final Path path) {
        path.moveTo(x, y);
        path.rLineTo(base * 2, 0);
        path.rLineTo(-base / 2, base / 2);
        path.rLineTo(base * 2, base * 2);
        path.rLineTo(base / 2, -base / 2);
        path.rLineTo(0, base * 2);
        path.rLineTo(-base * 2, 0);
        path.rLineTo(base / 2, -base / 2);
        path.rLineTo(-base * 2, -base * 2);
        path.rLineTo(-base / 2, base / 2);
        path.close();
        paint.setShadowLayer(mShadowRadius << 1, 0, mShadowRadius << 1,
                Color.argb(255, 30, 33, 37));
        if (mEdgeOn == id) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.BLACK);
        }
        canvas.drawPath(path, paint);
        paint.clearShadowLayer();
        path.reset();

        paint.setColor(Color.WHITE);
        canvas.drawCircle(x + base * 2, y + base * 2,
                base * (float) Math.sqrt(2) / 2, paint);
    }

    private final void drawBackSlashArrow(final float x, final float y,
                                          final float base, final int id, final Canvas canvas,
                                          final Paint paint, final Path path) {
        path.moveTo(x, y);
        path.rLineTo(-base * 2, 0);
        path.rLineTo(base / 2, base / 2);
        path.rLineTo(-base * 2, base * 2);
        path.rLineTo(-base / 2, -base / 2);
        path.rLineTo(0, base * 2);
        path.rLineTo(base * 2, 0);
        path.rLineTo(-base / 2, -base / 2);
        path.rLineTo(base * 2, -base * 2);
        path.rLineTo(base / 2, base / 2);
        path.close();
        paint.setShadowLayer(mShadowRadius << 1, 0, mShadowRadius << 1,
                Color.argb(255, 30, 33, 37));
        if (mEdgeOn == id) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.BLACK);
        }
        canvas.drawPath(path, paint);
        paint.clearShadowLayer();
        path.reset();

        paint.setColor(Color.WHITE);
        float mBackSlashCenterX = x - base * 2;
        float mBackSlashCenterY = y + base * 2;
        canvas.drawCircle(mBackSlashCenterX, mBackSlashCenterY, base
                * (float) Math.sqrt(2) / 2, paint);
    }

    public final void requestUpdateSize() {
        mOnChangeListener.onSizeChange(mKeyboardRect);
    }

    public final void requestUpdatePos() {
        mOnChangeListener.onPosChange(mKeyboardRect);
    }

    private float mDownX2, mDownY2;

    @Override
    public final boolean onTouchEvent(final MotionEvent event) {
        if (event.getPointerCount() >= 2) {
            mEdgeOn = 0;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    mDownX = event.getX(0);
                    mDownY = event.getY(0);
                    mDownX2 = event.getX(1);
                    mDownY2 = event.getY(1);
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float x = event.getX(0);
                    final float y = event.getY(0);
                    final float x2 = event.getX(1);
                    final float y2 = event.getY(1);
                    final float deltaX = Math.abs(x - x2) - Math.abs(mDownX - mDownX2);
                    final float deltaY = Math.abs(y - y2) - Math.abs(mDownY - mDownY2);
                    /**
                     * 键盘上下增加同样高度
                     */
                    mKeyboardRect.top = (int) (mOldKBRect.top - deltaY / 2);
                    mKeyboardRect.bottom = (int) (mOldKBRect.bottom + deltaY / 2);
                    /**
                     * 左右增加相同宽度
                     */
//				if(!mJustKeyboard){
//					if(mLeftHand){
//						mCandidateRect.right = (int) (mOldCanRect.right - deltaX / 2);
//						mKeyboardRect.left = (int) (mOldKBRect.left + deltaX / 2);
//					}else{
//						mCandidateRect.left = (int) (mOldCanRect.left - deltaX / 2);
//						mKeyboardRect.right = (int) (mOldKBRect.right + deltaX / 2);
//					}
//				}else{
                    mKeyboardRect.left = (int) (mOldKBRect.left - deltaX / 2);
                    mKeyboardRect.right = (int) (mOldKBRect.right + deltaX / 2);
//				}
                    /**
                     * 限制超出屏幕
                     */
                    if (mKeyboardRect.right > mScreenWidth) {
                        mKeyboardRect.right = mScreenWidth;
                    }
                    if (mKeyboardRect.left < 0) {
                        mKeyboardRect.left = 0;
                    }
                    /**
                     * 限制键盘不能过小
                     */
                    if (mKeyboardRect.width() < GetMinKeyboardWidth()) {
//					if(mLeftHand){
                        mKeyboardRect.left = mKeyboardRect.right - GetMinKeyboardWidth();
//					}else{
//						mKeyboardRect.right = mKeyboardRect.left + GetMinKeyboardWidth();
//					}
                    }
//				if(!mJustKeyboard){
                    /**
                     * 限制候选栏不能超出屏幕
                     */
//					if(mCandidateRect.right > mScreenWidth){
//						mCandidateRect.right = mScreenWidth;
//					}
//					if(mCandidateRect.left < 0){
//						mCandidateRect.left = 0;
//					}
                    /**
                     * 限制候选栏不能过小
                     */
//					if(mCandidateRect.width() < GetMinCanWidth()){
//						if(mLeftHand){
//							mCandidateRect.right = mCandidateRect.left + GetMinCanWidth();
//						}else{
//							mCandidateRect.left = mCandidateRect.right - GetMinCanWidth();
//						}
//					}
//				}
                    if (mKeyboardRect.top < 0) {
                        mKeyboardRect.top = 0;
                    }
                    if (mKeyboardRect.top > mKeyboardRect.bottom - GetMinKeyboardHeight()) {
                        mKeyboardRect.top = mKeyboardRect.bottom - GetMinKeyboardHeight();
                    }
                    if (mKeyboardRect.bottom > mScreenHeight) {
                        mKeyboardRect.bottom = mScreenHeight;
                    }
//				mCandidateRect.top = mKeyboardRect.top;
//				mCandidateRect.bottom = mKeyboardRect.bottom;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        } else {
            final float x = event.getX();
            final float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    setEdgeState(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    setKeyboardSize(x - mDownX, y - mDownY);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mEdgeOn == 7 && mFinishButtonRect.contains(x, y)) {
                        mOnChangeListener.onFinishSetting();
                    } else if (mEdgeOn == 8 && mResetButtonRect.contains(x, y)) {
                        mOnChangeListener.onResetSetting();
                    }
                    mEdgeOn = 0;
//				mOldCanRect.set(mCandidateRect);
                    mOldKBRect.set(mKeyboardRect);
                    mOnChangeListener.onSizeChange(mKeyboardRect);
                    break;
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    private void setKeyboardSize(float x, float y) {
        switch (mEdgeOn) {
            case EDGE_Top:
                if (mOldKBRect.top + y > mOldKBRect.bottom - GetMinKeyboardHeight()) {
                    y = mOldKBRect.height() - GetMinKeyboardHeight();
                }
                if (mOldKBRect.top + y < 0) {
                    y = -mOldKBRect.top;
                }
                mKeyboardRect.top = (int) (mOldKBRect.top + y);
                break;
            case EDGE_Right:
                if (mOldKBRect.right + x < mOldKBRect.left + GetMinKeyboardWidth()) {
                    x = GetMinKeyboardWidth() - mOldKBRect.width();
                }
                if (mOldKBRect.right + x > mScreenWidth) {
                    x = mScreenWidth - mOldKBRect.right;
                }
                mKeyboardRect.right = (int) (mOldKBRect.right + x);
                break;
            case EDGE_Bottom:
                if (mOldKBRect.bottom + y < mOldKBRect.top + GetMinKeyboardHeight()) {
                    y = GetMinKeyboardHeight() - mOldKBRect.height();
                }
                if (mOldKBRect.bottom + y > mScreenHeight) {
                    y = mScreenHeight - mOldKBRect.bottom;
                }
                mKeyboardRect.bottom = (int) (mOldKBRect.bottom + y);
                break;
            case EDGE_Left:
                if (mOldKBRect.left + x > mOldKBRect.right - GetMinKeyboardWidth()) {
                    x = mOldKBRect.width() - GetMinKeyboardWidth();
                }
                if (mOldKBRect.left + x < 0) {
                    x = -mOldKBRect.left;
                }
                mKeyboardRect.left = (int) (mOldKBRect.left + x);
                break;
            case EDGE_Fuck:
                if (mOldKBRect.left + x > mOldKBRect.right - GetMinKeyboardWidth()) {}
                if (mOldKBRect.left + x < 0) {
                    x = -mOldKBRect.left;
                }
                mKeyboardRect.left = (int) (mOldKBRect.left + x);
                break;
            case EDGE_Other:
                if (mOldKBRect.left + x < 0) {
                    x = -mOldKBRect.left;
                }
                if (mOldKBRect.right + x > mScreenWidth) {
                    x = mScreenWidth - mOldKBRect.right;
                }
                if (mOldKBRect.top + y < 0) {
                    y = -mOldKBRect.top;
                }
                if (mOldKBRect.bottom + y > mScreenHeight) {
                    y = mScreenHeight - mOldKBRect.bottom;
                }
                mKeyboardRect.left = (int) (mOldKBRect.left + x);
                mKeyboardRect.right = (int) (mOldKBRect.right + x);
                mKeyboardRect.top = (int) (mOldKBRect.top + y);
                mKeyboardRect.bottom = (int) (mOldKBRect.bottom + y);
                break;
            default:
                break;
        }
    }

    private void setEdgeState(float x, float y) {
        mEdgeOn = EDGE_Null;
        float radius = mBase * 2;
        if (mFinishButtonRect.contains(x, y)) {
            mEdgeOn = EDGE_Finish;
            return;
        }
        if (mResetButtonRect.contains(x, y)) {
            mEdgeOn = EDGE_Reset;
            return;
        }
        if (x > mKeyboardRect.left && x < mKeyboardRect.right) {
            if (Math.abs(mKeyboardRect.top - y) < radius) {
                mEdgeOn = EDGE_Top;
                return;
            } else if (Math.abs(mKeyboardRect.bottom - y) < radius) {
                mEdgeOn = EDGE_Bottom;
                return;
            }
        }
        if (y > mKeyboardRect.top && y < mKeyboardRect.bottom) {
            if (Math.abs(mKeyboardRect.left - x) < radius) {
                mEdgeOn = EDGE_Left;
                return;
            } else if (Math.abs(mKeyboardRect.right - x) < radius) {
                mEdgeOn = EDGE_Right;
                return;
            }
        }
        if (mEdgeOn == EDGE_Null) {
            if (mKeyboardRect.contains((int) x, (int) y)) {
                mEdgeOn = EDGE_Other;
                return;
            }
        }
    }

    public void SetTypeface(Typeface typeface) {
        mPaint.setTypeface(typeface);
    }

    public void SetMovingIcon(String icon) {
        mMovingIcon = icon;
    }

//	public void SetLeftHand(boolean leftHand){
//		mLeftHand = leftHand;
//	}

    public interface OnChangeListener {
        void onSizeChange(Rect keyboardRect);

        void onPosChange(Rect keyboardRect);

        void onFinishSetting();

        void onResetSetting();
    }

}
