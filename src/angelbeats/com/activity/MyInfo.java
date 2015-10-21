package angelbeats.com.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import angelbeats.com.bean.User;
import angelbeats.com.game2.Game2;
import angelbeats.com.util.Constants;
import angelbeats.com.util.ImageLoadOptions;
import angelbeats.com.util.PhotoUtil;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 我的个人信息
 * @author AngelBeats
 */
public class MyInfo extends ActivityBase {
	private TextView tv_set_name, tv_set_nick, tv_set_gender,tv_set_gametype;
	private RelativeLayout layout_head, layout_nick, layout_gender,
			layout_gametype;
	private ImageView iv_set_avator;
	private Button btnAdd;
	private Button btnStartConson;
	private String objectId;
	private TextView titleText;
	private Button btnBack;
	private User user;
	private String name;//记录点击的图标的用户名
	private String[] items = new String[] { "选择本地图片", "拍照" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);

		initView();
		init();

	}

	private void initView() {
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		tv_set_gametype = (TextView) findViewById(R.id.tv_set_gametype);
		titleText = (TextView) findViewById(R.id.header_htv_subtitle);
		layout_head = (RelativeLayout) findViewById(R.id.layout_head);
		layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
		layout_gametype = (RelativeLayout) findViewById(R.id.layout_gametype);
		btnBack = (Button) findViewById(R.id.headr_back);
		btnAdd = (Button) findViewById(R.id.btn_add_friend);
		btnStartConson = (Button) findViewById(R.id.btn_start_consonvertion);
		titleText.setText("我的信息");
		btnBack.setVisibility(View.VISIBLE);
		btnAdd.setVisibility(View.GONE);
		btnStartConson.setVisibility(View.GONE);
		layout_gametype.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentBack = new Intent(MyInfo.this, MainActivity.class);
				intentBack.putExtra("from", "MyInfo");
				startActivity(intentBack);
			}

		});
		layout_head.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setHead();
			}
		});
		layout_nick.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent_nick = new Intent(MyInfo.this, UpdateNick.class);
				intent_nick.putExtra("from", "MyInfo");
				startActivity(intent_nick);
			}
		});
		layout_gender
				.setOnClickListener(new android.view.View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showSexChooseDialog();
					}
				});
		layout_gametype
				.setOnClickListener(new android.view.View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showGameTypeChooseDialog();
					}
				});
	}

	private void init() {
		userManager = BmobUserManager.getInstance(this);
		String name = BmobUserManager.getInstance(MyInfo.this).getCurrentUser()
				.getUsername();
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					objectId = user.getObjectId();
					ShowLog(objectId);

					if (user.getSex() == true) {

						tv_set_gender.setText("男");
					} else {
						tv_set_gender.setText("女");
					}
					String type = user.getFriendAddPolicy();
					if (type.equals("a")) {
						tv_set_gametype.setText("数字拼图");
					} else if (type.equals("b")) {
						tv_set_gametype.setText("你画我猜");
					}
					tv_set_name.setText(user.getUsername().toString());
					if (user.getNick() == null) {
						tv_set_nick.setText(" ");
					} else {
						tv_set_nick.setText(user.getNick().toString());
					}

					String avatar = user.getAvatar();
					Log.i("--MyInfo.init", avatar);
					if (avatar != null && !avatar.equals("")) {
						ImageLoader.getInstance().displayImage(avatar,
								iv_set_avator, ImageLoadOptions.getOptions());
					} else {
						iv_set_avator.setImageResource(R.drawable.head_pic);
					}
					Log.i("--queryUser", user.getUsername());
				}
			}
		});
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 是否是拍照
	int degree = 0;// 拍照旋转
	String path;// 存图片路径
	boolean isSetAvater = false;

	/**
	 * 设置头像
	 */
	private void setHead() {
		new AlertDialog.Builder(MyInfo.this).setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							avatarFromLocal();
							break;
						case 1:
							avaterFromCamera();
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	/**
	 * 选择本地图片
	 */
	private void avatarFromLocal() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent,
				Constants.REQUESTCODE_UPLOADAVATAR_LOCATION);
	}

	/**
	 * 拍照
	 */
	@SuppressLint("SimpleDateFormat")
	private void avaterFromCamera() {
		File dir = new File(Constants.MyAvatarDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 原图
		File file = new File(dir,
				new SimpleDateFormat("yyMMddHHmmss").format(new Date())
						+ ".png");
		path = file.getAbsolutePath();// 获取相片的保存路径
		Uri imageUri = Uri.fromFile(file);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent,
				Constants.REQUESTCODE_UPLOADAVATAR_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(path);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case Constants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地
			Uri uri = null;
			if (data == null) {// 取消了
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,
						Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}
			break;
		case Constants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			if (data == null) {
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			// filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	/**
	 * 裁剪头像
	 * 
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 * @param isCrop
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	@SuppressLint("SimpleDateFormat")
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundBitmap(bitmap);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avator.setImageBitmap(bitmap);
				isSetAvater = true;
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()) + ".png";
				path = Constants.MyAvatarDir + filename;// 上传头像时使用
				PhotoUtil.saveBitmap(Constants.MyAvatarDir, filename, bitmap,
						true);
			}
		}
	}

	/**
	 * 上传头像
	 */
	private void uploadAvatar() {
		Log.i("--头像地址：", path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {
			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(MyInfo.this);
				Log.i("--user.setAvatar(url)", url);
				// 更新User对象
				user.setAvatar(url);
				Log.i("--user.setAvatar(url)", user.getAvatar());
				updateAvatarInfo(url);
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int arg0, String msg) {
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	/**
	 * 修改用户表中头像资料
	 */
	private void updateAvatarInfo(String url) {
		final User u = new User();
		u.setAvatar(url);
		updateUserData(u, new UpdateListener() {

			@Override
			public void onSuccess() {
				ShowToast("修改成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	/**
	 * 设置性别
	 */
	String[] sexs = new String[] { "男", "女" };

	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置性别")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(sexs, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								BmobLog.i("点击的是" + sexs[which]);
								updateSexInfo(which);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	/**
	 * 设置解密游戏类型
	 */
	String[] gameType = new String[] { "数字拼图", "你画我猜" };

	private void showGameTypeChooseDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置解密游戏类型")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(gameType, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								updateGametypeInfo(which);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	/**
	 * 修改用户表中性别资料
	 */
	private void updateSexInfo(int which) {
		final User u = new User();
		if (which == 0) {
			u.setSex(true);
		} else {
			u.setSex(false);
		}
		updateUserData(u, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("修改成功");
				tv_set_gender.setText(u.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	/**
	 * 修改用户表中解密游戏类型
	 */
	private void updateGametypeInfo(int which) {
		final User u = new User();
		if (which == 0) {
			u.setFriendAddPolicy("a");
			updateUserData(u, new UpdateListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					ShowToast("修改成功");
					tv_set_gametype.setText(u.getFriendAddPolicy().equals("a")  ? "数字拼图"
							: "你画我猜");
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ShowToast("onFailure:" + arg1);
				}
			});
		} else {
			// 跳到设置页面
			Intent intent_game = new Intent(MyInfo.this,Game2.class);
			
			startActivity(intent_game);
		}

	}

	private void updateUserData(User user, UpdateListener listener) {
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
