//package com.hit.wi.t9.view;
//
//import com.hit.wi.t9.T9SoftKeyboard8;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.GridView;
//
//public class QPGridView extends GridView{
//
//	private T9SoftKeyboard8 activity;
//	private View[] mQPCandidateView;
//	private float startY;
//	private int currentCandi;
//
//	public QPGridView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//	}
//
//	public QPGridView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//	}
//
//	public QPGridView(Context context, int currentCandi) {
//		super(context);
//		activity = (T9SoftKeyboard8) context;
//		mQPCandidateView = activity.getQPCandiView();
//		this.currentCandi = currentCandi;
//	}
//
//	@SuppressLint("NewApi")
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		boolean b = super.dispatchTouchEvent(ev);
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			startY = ev.getY();
//			return true;
//		case MotionEvent.ACTION_UP:
//			for(int i = 0; i < 3; i++) {
//				if(i != currentCandi) {
//					((GridView) mQPCandidateView[i]).smoothScrollBy((int) (startY - ev.getY()), 1000);
//				}
//			}
//			return true;
//		}
//		return b;
//		
//	}
//
//}