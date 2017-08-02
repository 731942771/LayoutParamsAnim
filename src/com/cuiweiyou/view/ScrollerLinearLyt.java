package com.cuiweiyou.view;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 自定义布局，处理touch事件实现侧滑<br/>
 * 务必重写onLayout()方法
 * @author cuiweiyou.com
 */
public class ScrollerLinearLyt extends LinearLayout {

	/** 手指滑动需要达到的速度100 */
	public int SNAP_VELOCITY = 100;
	/** 速度追踪器。用于计算手指滑动的速度。跟踪触摸屏事件(flinging事件和其他gestures手势事件)的速率 */
	private VelocityTracker mVelocityTracker;
	/** 本顶部view的宽度，根据屏幕宽度适配 */
	int contentWidth;
	/** 触发移动事件的最短距离，如果小于这个距离就不触发移动控件 **/
	int touchSlop;
	/** 本布局是否已经滑开了 **/
	boolean isOpen;
	/** 手指按下的 X 位置 **/
	float xDown = 0;
	/** 手指按下的Y 位置 **/
	float yDown = 0;
	/** 手指滑动过程中的 x 位置 **/
	float xMove = 0;
	/** 手指滑动过程中的 y 位置 **/
	float yMove = 0;
	/** 手指拿起时相对于屏幕的 x 位置 **/
	float xUp = 0;
	/** 手指拿起时相对于屏幕的y 位置 **/
	float yUp = 0;
	/** 侧滑开、侧滑关后手指touch事件的第一次滑动。第一次滑动决定是上下滑还是左右滑 **/
	boolean isFirstMove = true;
	/** 手指开始滑动后是否上下滑动状态。默认false **/
	boolean isUpDown = false;
	/** 本布局的属性实例，相对于父容器的，父容器是RelativeLayout */
	private RelativeLayout.LayoutParams contentParams;
	/** 手指按下时，手指和本布局左边线的距离 */
	private int paramsLeftMargin;
	/** 手指按下时，手指和本布局右边线的距离 */
	private int paramsRightMargin;

	/** 2参的构造方法用于在xml里定义 */
	public ScrollerLinearLyt(Context context, AttributeSet attrs) {
		super(context, attrs);
		/** 屏幕属性对象 **/
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		contentWidth = dm.widthPixels;
		//          视图的各种特性的常量记录对象（UI中所使用到的标准常量）得到触发移动事件的最短距离
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		Log.e("sst", "touchSlop:" + touchSlop);
	}

	/**
	 * 手指触摸事件，按下、滑动、拿起、...<br/>
	 * ACTION_DOWN按下后第一次的ACTION_MOVE判断主方向。主方向是左右滑时执行侧滑，否则就下发<br/>
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (event.getAction()) {

//--- 手指按下，down --------------------------------------------------------------
			case MotionEvent.ACTION_DOWN:
				xDown = event.getRawX();
				yDown = event.getRawY();
				// 无论是否已经侧滑，这个得到的都是手指和本布局左边线的距离
				paramsLeftMargin = (int) (xDown - contentParams.leftMargin);
				paramsRightMargin = (int) (contentParams.leftMargin + xDown);
				
				Log.e("sst", "xDown:" + xDown + ", yDown:" + yDown);
			break;
//--- 手指滑动，move --------------------------------------------------------------
			case MotionEvent.ACTION_MOVE:
				xMove = event.getRawX();
				yMove = event.getRawY();
				int distanceX = (int) (xMove - xDown);	// 负数：向左滑，正数：向右滑
				int distanceY = (int) (yMove - yDown);	// 负数：向上滑，正数：向下滑
				
				if(isFirstMove){
					// ！！！说明：如果手指上下滑动距离的n倍，大于左右滑动距离，那么，下发给子View处理
					// 否则就当做侧滑事件处理。处理了要return true ！！！！！！！
					if(Math.abs(distanceY)*1.5 > Math.abs(distanceX)){
						isUpDown = true;		// ok，手指做的是上下滑动
						super.dispatchTouchEvent(event);
					}
					
					Log.e("sst", "xMove:" + xMove + ", yMove:" + yMove);
					
					isFirstMove = false;
				} else {
					// 继续滑，初始是上下滑动的话，下发。处理了手指按在上下滑继而又左右滑
					if (isUpDown)
						return super.dispatchTouchEvent(event);
					
					// 如果向左滑的
					if (distanceX <= 0) {
						// 如果已经滑开
						if (isOpen) {
							// 手指滑动过程中的位置减去和边线的距离就是边线的目标位置。滑动中保持手指和边线距离不变
							contentParams.leftMargin = (int) (xMove - paramsLeftMargin);
							contentParams.rightMargin = (int) (xMove - paramsRightMargin);
							
							// 如果向左滑的太多
							if (contentParams.leftMargin < 0) {
								contentParams.leftMargin = 0;
								contentParams.rightMargin = 0;
							}
						} else {
							contentParams.leftMargin = 0;
							contentParams.rightMargin = 0;
						}
						setLayoutParams(contentParams);
					}
					
					// 如果向右滑动
					else if (distanceX > 0) {
						// 如果尚未滑开
						if (!isOpen) {
							contentParams.leftMargin = distanceX;	// 负数，绝对值即向右移动的距离
							contentParams.rightMargin = -distanceX;
							// 如果移动太多
							if (contentParams.leftMargin > contentWidth - contentWidth/4) {
								contentParams.leftMargin = contentWidth - contentWidth/4;
								contentParams.rightMargin = 2 * contentWidth - contentWidth/4;
							}
						} else {
							contentParams.leftMargin = contentWidth - contentWidth/4;
							contentParams.rightMargin = 2 * contentWidth - contentWidth/4;
						}
						
						setLayoutParams(contentParams);
					}
					
					return true;	// 本布局处理侧滑事件，不再下发
				}
			break;
//--- 手指拿起，up --------------------------------------------------------------
			case MotionEvent.ACTION_UP:
				xUp = event.getRawX();
				yUp = event.getRawY();
				int upDistanceX = (int) (xUp - xDown);	// 手指拿起后相对于按下时的距离。正数：向右，负数：向左
				int upDistanceY = (int) (yUp - yDown);	// 手指拿起后相对于按下时的距离。正数：向下，负数：向上

				// 如果按下（可能会稍稍指头有点晃动）迅速拿起，相当于点击屏幕。此时没有滑动
				if(Math.abs(upDistanceX) < touchSlop && Math.abs(upDistanceY) < touchSlop){
					isUpDown = false;
					return super.dispatchTouchEvent(event);
				}
				
				// 手指拿起，侧滑要么关要么开，无所谓。下一次的第一次滑动状态设为开
				isFirstMove = true;
				
				// 上下滑动时没有侧滑动作，不必复位处理。直接下发
				if (isUpDown){
					isUpDown = false;
					super.dispatchTouchEvent(event);
				} else {
					
					if (Math.abs(upDistanceX) >= touchSlop) {
							
						// 如果手指是向右滑动的，布局还没滑开
						if (upDistanceX > 0 && !isOpen) {
							mVelocityTracker.computeCurrentVelocity(1000);					// 计算1秒内速率
							int velocity = Math.abs((int) mVelocityTracker.getXVelocity());	// x轴速度
							
							// 如果滑动距离有效，或速度够快
							if ( upDistanceX > contentWidth/5 || velocity > SNAP_VELOCITY) {
								
								// 向右移动，速度30像素
								//new ScrollTask().execute(30); // TODO scrollTo
								
								// 向右移动
								contentParams.leftMargin = contentWidth - contentWidth/4;
								contentParams.rightMargin = 2 * contentWidth - contentWidth/4;
								setLayoutParams(contentParams);
								
								isOpen = true;
							}
							// 如果
							else {
	//							// 向左移动，速度30像素
	//							new ScrollTask().execute(-30);
	
								contentParams.leftMargin = 0;
								contentParams.rightMargin = 0;
								setLayoutParams(contentParams);
								
							}
						}
						else if (upDistanceX < 0 && isOpen) {
	
							mVelocityTracker.computeCurrentVelocity(1000);
							int velocity = Math.abs((int) mVelocityTracker.getXVelocity());
							
							if (Math.abs(upDistanceX) > contentWidth/5 || velocity > SNAP_VELOCITY) {
								// 向左移动，速度30像素
								//new ScrollTask().execute(-30);
	
								contentParams.leftMargin = 0;
								contentParams.rightMargin = 0;
								setLayoutParams(contentParams);
								
								isOpen = false;
							}
							else {
	//							// 向右移动，速度30像素
	//							new ScrollTask().execute(30);
	
								contentParams.leftMargin = contentWidth - contentWidth/4;
								contentParams.rightMargin = 2 * contentWidth - contentWidth/4;
								setLayoutParams(contentParams);
								
							}
						}
					}
					else if (Math.abs(upDistanceX) < touchSlop) {
						// 当手指按下向左滑又向右滑回来，不足以触发事件时
						if(isOpen){
							contentParams.leftMargin = contentWidth - contentWidth/4;
							contentParams.rightMargin = 2 * contentWidth - contentWidth/4;
							setLayoutParams(contentParams);
						} else {
							contentParams.leftMargin = 0;
							contentParams.rightMargin = 0;
							setLayoutParams(contentParams);
						}
					}
	
					// 重置速度追踪器
					mVelocityTracker.recycle();
					mVelocityTracker = null;
					
					return true;
				}
				
			break;
		} //  switch 结束
		
		return super.dispatchTouchEvent(event);
	} //  dispatchTouchEvent() 结束

	/**
	 * 随手指滑动时，系统持续调用此方法重绘此视图/界面/布局<br/>
	 * 应用运行第一次加载此布局时即调用一次。初始化属性实例，非常重要！！！
	 * @param changed 此布局滑动/移动 即true 
	 * @param l 此布局左边相对于父容器 左 边的位置，位于父左边的左边为负数
	 * @param r 此布局右边相对于父容器 左 边的位置
	 * @param t 此布局顶边相对于父容器 顶 边的位置 
	 * @param b 此布局底边相对于父容器 顶 边的位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		// 如果位置移动了
		if (changed) {
			// 获取布局对象
			contentParams = (RelativeLayout.LayoutParams) this.getLayoutParams();
			contentParams.width = contentWidth;
			setLayoutParams(contentParams);
		}
	}

	
	/**
	 * 延时操作类
	 * @author Administrator
	 */
	class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... speed) {
			int leftMargin = contentParams.leftMargin;
			// 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环
			while (true) {
				leftMargin = leftMargin + speed[0];
				if (leftMargin < -contentWidth) {
					leftMargin = -contentWidth;
					publishProgress(leftMargin);
					break;
				}
				if (leftMargin > 0) {
					leftMargin = 0;
					publishProgress(leftMargin);
					break;
				}
				publishProgress(leftMargin);
			}
			if (speed[0] > 0) {
				isOpen = false;
			} else {
				isOpen = true;
			}
			
			return leftMargin;
		}

		@Override
		protected void onPostExecute(Integer leftMargin) {
			contentParams.leftMargin = leftMargin;
			setLayoutParams(contentParams);
		}
	}

}