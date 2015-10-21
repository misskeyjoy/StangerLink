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
 * �ҵĸ�����Ϣ
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
	private String name;//��¼�����ͼ����û���
	private String[] items = new String[] { "ѡ�񱾵�ͼƬ", "����" };

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
		titleText.setText("�ҵ���Ϣ");
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

						tv_set_gender.setText("��");
					} else {
						tv_set_gender.setText("Ů");
					}
					String type = user.getFriendAddPolicy();
					if (type.equals("a")) {
						tv_set_gametype.setText("����ƴͼ");
					} else if (type.equals("b")) {
						tv_set_gametype.setText("�㻭�Ҳ�");
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
	boolean isFromCamera = false;// �Ƿ�������
	int degree = 0;// ������ת
	String path;// ��ͼƬ·��
	boolean isSetAvater = false;

	/**
	 * ����ͷ��
	 */
	private void setHead() {
		new AlertDialog.Builder(MyInfo.this).setTitle("����ͷ��")
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
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	/**
	 * ѡ�񱾵�ͼƬ
	 */
	private void avatarFromLocal() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent,
				Constants.REQUESTCODE_UPLOADAVATAR_LOCATION);
	}

	/**
	 * ����
	 */
	@SuppressLint("SimpleDateFormat")
	private void avaterFromCamera() {
		File dir = new File(Constants.MyAvatarDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// ԭͼ
		File file = new File(dir,
				new SimpleDateFormat("yyMMddHHmmss").format(new Date())
						+ ".png");
		path = file.getAbsolutePath();// ��ȡ��Ƭ�ı���·��
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
		case Constants.REQUESTCODE_UPLOADAVATAR_CAMERA:// ����
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = true;
				File file = new File(path);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "���պ�ĽǶȣ�" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case Constants.REQUESTCODE_UPLOADAVATAR_LOCATION:// ����
			Uri uri = null;
			if (data == null) {// ȡ����
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,
						Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("��Ƭ��ȡʧ��");
			}
			break;
		case Constants.REQUESTCODE_UPLOADAVATAR_CROP:// �ü�ͷ�񷵻�
			if (data == null) {
				return;
			} else {
				saveCropAvator(data);
			}
			// ��ʼ���ļ�·��
			// filePath = "";
			// �ϴ�ͷ��
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	/**
	 * �ü�ͷ��
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
	 * ����ü���ͷ��
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
				path = Constants.MyAvatarDir + filename;// �ϴ�ͷ��ʱʹ��
				PhotoUtil.saveBitmap(Constants.MyAvatarDir, filename, bitmap,
						true);
			}
		}
	}

	/**
	 * �ϴ�ͷ��
	 */
	private void uploadAvatar() {
		Log.i("--ͷ���ַ��", path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {
			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(MyInfo.this);
				Log.i("--user.setAvatar(url)", url);
				// ����User����
				user.setAvatar(url);
				Log.i("--user.setAvatar(url)", user.getAvatar());
				updateAvatarInfo(url);
			}

			@Override
			public void onProgress(Integer arg0) {
			}

			@Override
			public void onFailure(int arg0, String msg) {
				ShowToast("ͷ���ϴ�ʧ�ܣ�" + msg);
			}
		});
	}

	/**
	 * �޸��û�����ͷ������
	 */
	private void updateAvatarInfo(String url) {
		final User u = new User();
		u.setAvatar(url);
		updateUserData(u, new UpdateListener() {

			@Override
			public void onSuccess() {
				ShowToast("�޸ĳɹ�");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	/**
	 * �����Ա�
	 */
	String[] sexs = new String[] { "��", "Ů" };

	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
				.setTitle("�����Ա�")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(sexs, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								BmobLog.i("�������" + sexs[which]);
								updateSexInfo(which);
								dialog.dismiss();
							}
						}).setNegativeButton("ȡ��", null).show();
	}

	/**
	 * ���ý�����Ϸ����
	 */
	String[] gameType = new String[] { "����ƴͼ", "�㻭�Ҳ�" };

	private void showGameTypeChooseDialog() {
		new AlertDialog.Builder(this)
				.setTitle("���ý�����Ϸ����")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(gameType, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								updateGametypeInfo(which);
								dialog.dismiss();
							}
						}).setNegativeButton("ȡ��", null).show();
	}

	/**
	 * �޸��û������Ա�����
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
				ShowToast("�޸ĳɹ�");
				tv_set_gender.setText(u.getSex() == true ? "��" : "Ů");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	/**
	 * �޸��û����н�����Ϸ����
	 */
	private void updateGametypeInfo(int which) {
		final User u = new User();
		if (which == 0) {
			u.setFriendAddPolicy("a");
			updateUserData(u, new UpdateListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					ShowToast("�޸ĳɹ�");
					tv_set_gametype.setText(u.getFriendAddPolicy().equals("a")  ? "����ƴͼ"
							: "�㻭�Ҳ�");
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ShowToast("onFailure:" + arg1);
				}
			});
		} else {
			// ��������ҳ��
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
