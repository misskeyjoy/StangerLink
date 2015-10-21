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
 * ���Ҹ�������
 * @author AngelBeats
 *
 */
public class NearPeopleActivity extends ActivityBase implements
		OnMarkerClickListener {
	private MapView mapView;
	private BaiduMap baiduMap;
	private BmobGeoPoint location; // �Լ�λ��
	private BmobGeoPoint nearUserGeoPoint;// �����˵�λ��
	private ProgressDialog progress;
	private BmobChatUser bombBmobChatUser;
	private String objectId;
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;// ģʽ���߾���
	private String tempcoor = "bd09ll";

	private User user;
	private double QUERY_KILOMETERS = 1;// ��ѯ1���ﷶΧ�ڵ���

	private AlertDialog.Builder builder;// ��ʾ��dialog

	Marker marker = null;
	
	private String name;//��¼�����ͼ����û���
	
	private TextView titleText;
	private Button btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʼ����ͼ
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_nearpeople);

		progress = new ProgressDialog(NearPeopleActivity.this);
		progress.setMessage("���ڲ�ѯ��������...");
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
							ShowToast("���޸�������!");
							progress.dismiss();
						}
					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("���޸�������!");
						progress.dismiss();
					}

				});

		location();

	}

	private void nearInMap(BmobGeoPoint nearGeoPoint, String username) {

		LatLng point = new LatLng(nearGeoPoint.getLatitude(),
				nearGeoPoint.getLongitude()); // ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.near_others); // ����MarkerOption�������ڵ�ͼ�����Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap); // �ڵ�ͼ�����Marker������ʾ

		marker = (Marker) (baiduMap.addOverlay(option));
		Bundle bundle = new Bundle();
		bundle.putSerializable("username", username);
		marker.setExtraInfo(bundle);

		// baiduMap.addOverlay(option);
	}

	// ��ʼ�����
	private void init() {
		userManager = BmobUserManager.getInstance(NearPeopleActivity.this);
		mapView = (MapView) findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		baiduMap.setOnMarkerClickListener(this);
		location = ((StrangerLinkApplication) getApplication()).geoPoint;
		mLocationClient = ((StrangerLinkApplication) getApplication()).mLocationClient;
		titleText = (TextView)findViewById(R.id.header_htv_subtitle);
		btnBack = (Button)findViewById(R.id.headr_back);
		titleText.setText("��������");
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

	// ��ʼ����λ
	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// ���ö�λģʽ
		option.setCoorType(tempcoor);
		int span = 999;// С��1000����λһ��
		option.setScanSpan(span);
		mLocationClient.setLocOption(option);
	}

	private void InitDialog() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle("��ʾ");
		builder.setMessage("����Ҫ����򵥵���Ϸ���ܿ����Է���Ϣ��Ҫ������Ϸ��");
		builder.setCancelable(false);
		builder.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.setPositiveButton("ȷ��", new OnClickListener() {
			@Override
public void onClick(DialogInterface arg0, int arg1) {
				
				userManager.queryUser(name, new FindListener<User>() {
					@Override
					public void onError(int arg0, String arg1) {
						ShowToast("��ȡ��Ϣʧ�ܣ�");
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
		// ���춨λ����
		MyLocationData locData = new MyLocationData.Builder()
				.latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		// ���ö�λ����
		baiduMap.setMyLocationData(locData);

		// float f = baiduMap.getMinZoomLevel();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(15);
		baiduMap.animateMapStatus(mapStatusUpdate);

		// ���ö�λͼ������ã���λģʽ���Ƿ���������Ϣ���û��Զ��嶨λͼ�꣩
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
