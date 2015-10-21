package angelbeats.com.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import angelbeats.com.applcation.StrangerLinkApplication;
import angelbeats.com.bean.User;
import angelbeats.com.game2.Game2_key;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
/**
 * 查找附近的人
 * @author AngelBeats
 *
 */
public class NearPeopleActivity extends ActivityBase implements
		OnMarkerClickListener {
	private MapView mapView;
	private BaiduMap baiduMap;
	private BmobGeoPoint location; // 自己位置
	private BmobGeoPoint nearUserGeoPoint;// 附近人的位置
	private ProgressDialog progress;
	private BmobChatUser bombBmobChatUser;
	private String objectId;
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;// 模式，高精度
	private String tempcoor = "bd09ll";

	private User user;
	private double QUERY_KILOMETERS = 1;// 查询1公里范围内的人

	private AlertDialog.Builder builder;// 提示的dialog

	Marker marker = null;
	
	private String name;//记录点击的图标的用户名
	
	private TextView titleText;
	private Button btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化地图
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_nearpeople);

		progress = new ProgressDialog(NearPeopleActivity.this);
		progress.setMessage("正在查询附近的人...");
		progress.setCanceledOnTouchOutside(true);
		progress.show();

		init();

		userManager.queryKiloMetersListByPage(false, 0, "location",
				location.getLongitude(), location.getLatitude(), false,
				QUERY_KILOMETERS, null, false, new FindListener<User>() {
					@Override
					public void onSuccess(List<User> arg0) {
						// TODO Auto-generated method stub
						if (arg0 != null && arg0.size() > 0) {
							for (int i = 0; i < arg0.size(); i++) {
								user = arg0.get(i);
								nearUserGeoPoint = user.getLocation();
								nearInMap(nearUserGeoPoint, user.getUsername());
								Log.i("--nearuser", user.getUsername() + "");

							}
							Log.i("--queryKiloMetersListByPage", arg0.size()
									+ "");
							progress.dismiss();
						} else {
							ShowToast("暂无附近的人!");
							progress.dismiss();
						}
					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("暂无附近的人!");
						progress.dismiss();
					}

				});

		location();

	}

	private void nearInMap(BmobGeoPoint nearGeoPoint, String username) {

		LatLng point = new LatLng(nearGeoPoint.getLatitude(),
				nearGeoPoint.getLongitude()); // 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.near_others); // 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap); // 在地图上添加Marker，并显示

		marker = (Marker) (baiduMap.addOverlay(option));
		Bundle bundle = new Bundle();
		bundle.putSerializable("username", username);
		marker.setExtraInfo(bundle);

		// baiduMap.addOverlay(option);
	}

	// 初始化组件
	private void init() {
		userManager = BmobUserManager.getInstance(NearPeopleActivity.this);
		mapView = (MapView) findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		baiduMap.setOnMarkerClickListener(this);
		location = ((StrangerLinkApplication) getApplication()).geoPoint;
		mLocationClient = ((StrangerLinkApplication) getApplication()).mLocationClient;
		titleText = (TextView)findViewById(R.id.header_htv_subtitle);
		btnBack = (Button)findViewById(R.id.headr_back);
		titleText.setText("附近的人");
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentBack = new Intent(NearPeopleActivity.this,MainActivity.class);
				intentBack.putExtra("from", "nearpeople");
				startActivity(intentBack);
			}
			
		});
		InitLocation();
		mLocationClient.start();
		InitDialog();
		Log.i("--init", location.getLatitude() + "  " + location.getLongitude());
	}

	// 初始化定位
	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);
		int span = 999;// 小于1000，定位一次
		option.setScanSpan(span);
		mLocationClient.setLocOption(option);
	}

	private void InitDialog() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("你需要玩个简单的游戏才能看到对方信息，要进入游戏吗？");
		builder.setCancelable(false);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
public void onClick(DialogInterface arg0, int arg1) {
				
				userManager.queryUser(name, new FindListener<User>() {
					@Override
					public void onError(int arg0, String arg1) {
						ShowToast("获取信息失败！");
					}

					@Override
					public void onSuccess(List<User> user) {
						bombBmobChatUser = user.get(0);
						objectId = user.get(0).getObjectId();
						ShowLog(objectId);
						if (user.get(0).getFriendAddPolicy().equals("a")) {
							Intent intent_game = new Intent(NearPeopleActivity.this,Game1.class);
							intent_game.putExtra("username", name);
							startActivity(intent_game);
						}else {
							Intent intent_game = new Intent(NearPeopleActivity.this,Game2_key.class);
							
							intent_game.putExtra("username", name);
							intent_game.putExtra("hint", user.get(0).getGame2_hint());
							intent_game.putExtra("picture", user.get(0).getGame2_picture());
							intent_game.putExtra("key", user.get(0).getGame2_key());
							startActivity(intent_game);
						}
					}
				});
			}
		});
	}

	private void location() {
		baiduMap.setMyLocationEnabled(true);
		// 构造定位数据
		MyLocationData locData = new MyLocationData.Builder()
				.latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		// 设置定位数据
		baiduMap.setMyLocationData(locData);

		// float f = baiduMap.getMinZoomLevel();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(15);
		baiduMap.animateMapStatus(mapStatusUpdate);

		// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.near_own);
		MyLocationConfiguration config = new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.COMPASS, false,
				mCurrentMarker);
		baiduMap.setMyLocationConfigeration(config);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		name = marker.getExtraInfo().getString("username");
		Log.i("--onMarkerClick", name);
		
		builder.show();
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
}
