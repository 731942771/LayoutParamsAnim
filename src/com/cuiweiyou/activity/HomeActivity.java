package com.cuiweiyou.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cuiweiyou.layoutparamsanima.R;

/**
 * 布局属性动画测试   <br/>
 * 本aty的布局最外层时RelativeLayout  <br/>
 * bottom是个LinearLayout，和动画的实现没多大关系<br/>
 * top层是自定义的LinearLayout布局，重写onTouchEvent事件用于实现动画   <br/>
 * top层默认遮挡bottom
 * 
 * @author cuiweiyou.com
 */
public class HomeActivity extends Activity {
	
	List<String> btmList = new ArrayList<String>();
	List<String> topList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initBottomList();
		initTopList();
		
		// bottom层的按钮
		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(HomeActivity.this, "底层控件响应：威格灵博客", 0).show();
			}
		});
		
		// top层的按钮
		findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(HomeActivity.this, "top布局控件响应：cuiweiyou.com", 0).show();
			}
		});
		
		ArrayAdapter btmAdapter = new ArrayAdapter<String>(
				HomeActivity.this,
				R.layout.item_lv,
				R.id.tv_lv, btmList);
		
		ArrayAdapter topAdapter = new ArrayAdapter<String>(
				HomeActivity.this,
				R.layout.item_lv,
				R.id.tv_lv, topList);
		
		((ListView)findViewById(R.id.bottomlv)).setAdapter(btmAdapter);
		
		ListView toplv = (ListView) findViewById(R.id.toplv);
		toplv.setAdapter(topAdapter);
		toplv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.e("sst", "pos: " + position);
				Toast.makeText(HomeActivity.this, "点了：" + position, Toast.LENGTH_SHORT).show();
			}
		});
		
		
	}

	private void initBottomList() {
		btmList.add("全部");
		btmList.add("时政");
		btmList.add("frghjkl;");
		btmList.add("45678ijhbgfr567u");
		btmList.add("军事");
		btmList.add("45678ijhbgfr567u");
		btmList.add("军事");
		btmList.add("财经");
		btmList.add("娱乐");
		btmList.add("cv08765");
		btmList.add("财经");
		btmList.add("45678ijhbgfr567u");
		btmList.add("娱乐");
		btmList.add("XXXX");
	}
	
	private void initTopList() {
		topList.add("全部ttt");
		topList.add("时政ttt");
		topList.add("345678987654et");
		topList.add("军事tt");
		topList.add("财经tt");
		topList.add("txcvbnjkl;t");
		topList.add("娱乐t");
		topList.add("军事ttttt");
		topList.add("tttttttt8765t");
		topList.add("财经tt");
		topList.add("ft67yuhbvfedt");
		topList.add("娱乐t");
		topList.add("XXXXttt");
	}
}
