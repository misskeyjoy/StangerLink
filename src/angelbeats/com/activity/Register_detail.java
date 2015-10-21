package angelbeats.com.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import angelbeats.com.bean.User;
import angelbeats.com.util.Constants;
import angelbeats.com.util.PhotoUtil;
import angelbeats.com.util.Utils;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import angelbeats.com.applcation.StrangerLinkApplication;
/**
 * ע�����-��дͷ���û���������
 * 
 * @author AngelBeats 2015.5.9
 */
public class Register_detail extends BaseActivity implements OnClickListener {
	private Button registerBtn;
	private ImageView avatarImgV;
	private EditText nameEdit;
	private EditText passwdEdit;
	private EditText conpasswdEdit;
	private Intent intent;
	private BmobGeoPoint geoPointlocation;
	final User user = new User();
	private boolean isExist;
	private String[] items = new String[] { "ѡ�񱾵�ͼƬ", "����" };
	
	private TextView titleText;
	private Button btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_detail);
		userManager = BmobUserManager.getInstance(this);
		Init();
	}

	public void Init() {
		geoPointlocation = ((StrangerLinkApplication) getApplication()).geoPoint;
		avatarImgV = (ImageView) findViewById(R.id.head_pic);
		nameEdit = (EditText) findViewById(R.id.name_edit);
		passwdEdit = (EditText) findViewById(R.id.passwd_edit);
		conpasswdEdit = (EditText) findViewById(R.id.con_passwd_edit);
		registerBtn = (Button) findViewById(R.id.btnregister);
		avatarImgV.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		
		titleText = (TextView)findViewById(R.id.header_htv_subtitle);
		btnBack = (Button)findViewById(R.id.headr_back);
		titleText.setText("��ϸ��Ϣ");
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentBack = new Intent(Register_detail.this,Register_phone.class);
				intentBack.putExtra("from", "Register_detail");
				startActivity(intentBack);
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_pic:
			setHead();
			break;
		case R.id.btnregister:
			login();
			break;
		}
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
		new AlertDialog.Builder(Register_detail.this).setTitle("����ͷ��")
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
				avatarImgV.setImageBitmap(bitmap);
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
				String url = bmobFile.getFileUrl(Register_detail.this);
				Log.i("--user.setAvatar(url)", url);
				// ����User����
				user.setAvatar(url);
				Log.i("--user.setAvatar(url)", user.getAvatar());
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
	 * ��½
	 */
	private void login() {
		String name = nameEdit.getText().toString();
		String passwd = passwdEdit.getText().toString();
		String conpasswd = conpasswdEdit.getText().toString();
		if (!isSetAvater) {
			ShowToast(R.string.toast_error_headpicture_null);
			return;
		}
		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}
		if (TextUtils.isEmpty(passwd)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (!passwd.equals(conpasswd)) {
			ShowToast(R.string.toast_error_comfirm_password_no_same);
			return;
		}
		String regEx = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(name);
		boolean b = matcher.matches();
		if (!b) {
			ShowToast(R.string.toast_error_username_illegal);
			return;
		}
		if (passwd.length() < 6) {
			ShowToast(R.string.toast_error_password_too_short);
			return;
		}

		// �������
		boolean isNetWorkConnected = Utils
				.isNetworkAvailable(Register_detail.this);
		if (!isNetWorkConnected) {
			ShowToast(R.string.network_error_not_connected);
			return;
		}

		// ������
		final ProgressDialog progress = new ProgressDialog(Register_detail.this);
		progress.setMessage("����ע��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		//isExist = false;
		BmobQuery<User> bmobQuery = new BmobQuery<User>("User");
		bmobQuery.addWhereEqualTo("username", name);
		bmobQuery.count(Register_detail.this, User.class, new CountListener() {
			@Override
			public void onSuccess(int arg0) {
				if (arg0 != 0) {
					isExist = true;
				} 
			}
			@Override
			public void onFailure(int arg0, String arg1) {
			}
		});

		if (isExist == true) {
			ShowToast(R.string.toast_error_username_exit);
			progress.dismiss();
			return;
		}
		// �û���Ϣ
		user.setUsername(name);
		user.setNick(name);
		user.setPassword(passwd);
		user.setSex(true);
		user.setLocation(geoPointlocation);
		Log.i("--geoPointlocation", geoPointlocation.getLatitude()+""+geoPointlocation.getLongitude());
		// user.setAvatar(arg0);
		user.setFriendAddPolicy("a");
		user.setCharacter("��һ������ʲô��û�����£�");
		intent = getIntent();
		String phstring = intent.getStringExtra("phone");
		user.setPhone(phstring);
		user.setDeviceType("android");
		user.setInstallId(BmobInstallation.getInstallationId(this));
		Log.i("--uploadAvatar", "user.getAvatar()");
		user.signUp(Register_detail.this, new SaveListener() {
			@Override
			public void onSuccess() {
				progress.dismiss();
				ShowToast("ע��ɹ�");
				// ���豸��username���а�
				userManager.bindInstallationForRegister(user.getUsername());
				// ������½
				Intent intent = new Intent(Register_detail.this, Login.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("ע��ʧ��:" + arg1);
				progress.dismiss();
			}
		});
	}
}
