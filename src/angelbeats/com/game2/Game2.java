package angelbeats.com.game2;


import java.io.File;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import angelbeats.com.activity.R;


public class Game2 extends Activity {
	LinearLayout LinearLayout;
	MyView myview;
	Paint p = new Paint();
	Button draw_btn;// 选择图形的按钮
	Button clear_btn;
	Button save_btn;
	String usernameString;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game2_layout);
		myview = new MyView(getApplicationContext());
		myview.setBackgroundColor(Color.TRANSPARENT);
		// 将view加入到布局中
		LinearLayout = (LinearLayout) findViewById(R.id.linearLayout01);
		LinearLayout.removeAllViews();
		LinearLayout.addView(myview);

		draw_btn = (Button) findViewById(R.id.draw_button);
		clear_btn = (Button) findViewById(R.id.clear_button);
		save_btn = (Button) findViewById(R.id.save_button);
		draw_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				myview.setDrawTool(6);// 绘画
			}
		});

		clear_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {// 清屏
				LinearLayout.removeAllViews();
				myview = new MyView(getApplicationContext());
				LinearLayout.addView(myview);
			}
		});

		save_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {// 保存		
				String path = UUID.randomUUID().toString();
				myview.savePicture(path, myview); 
				String filepath = Environment.getExternalStorageDirectory().getPath()+"/StrangerLink/nihuawocai"+ File.separator +path+".jpg";
				Intent intent_info = new Intent(Game2.this, Game2_next.class);
				intent_info.putExtra("filepath", filepath);
				startActivity(intent_info);
				}
		});
	}
}
