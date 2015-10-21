package angelbeats.com.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import angelbeats.com.activity.NearPeopleActivity;
import angelbeats.com.activity.R;
import angelbeats.com.activity.ShakeActivity;
import angelbeats.com.activity.UserInfo;
import angelbeats.com.adapter.UserFriendAdapter;
import angelbeats.com.applcation.StrangerLinkApplication;
import angelbeats.com.bean.User;
import angelbeats.com.util.CollectionUtils;

/**
 * 联系人
 * 
 * @author AngelBeats 联系人显示的是昵称
 */
public class Contacts_fragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener {
	private UserFriendAdapter userAdapter;// 好友
	private InputMethodManager inputMethodManager;
	private BmobUserManager userManager;
	private View viewlayout;
	private Button btnAdd;
	private TextView titleText;
	ListView list_friends;
	List<User> friends = new ArrayList<User>();

	private AlertDialog.Builder builder;// 提示的dialog

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewlayout = inflater.inflate(R.layout.fragment_contract, null); // 先解析布局，得到一个view

		return viewlayout;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
		queryMyfriends();

	}

	private void init() {
		userManager = BmobUserManager.getInstance(getActivity());
		list_friends = (ListView) viewlayout.findViewById(R.id.list_friends);
		titleText = (TextView) viewlayout
				.findViewById(R.id.header_htv_subtitle);
		btnAdd = (Button) viewlayout.findViewById(R.id.headr_add);
		titleText.setText("联系人");
		btnAdd.setVisibility(View.VISIBLE);
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ShakeActivity.class);
				startActivity(intent);
			}
		});
		list_friends.setOnItemClickListener(this);
		list_friends.setOnItemLongClickListener(this);
		list_friends.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(
								getActivity().getCurrentFocus()
										.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
	}
	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void queryMyfriends() {
		// 在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		StrangerLinkApplication.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(getActivity())
						.getContactList()));

		Map<String, BmobChatUser> users = StrangerLinkApplication.getInstance()
				.getContactList();
		// 组装新的User
		filledData(CollectionUtils.map2list(users));
		if (userAdapter == null) {
			userAdapter = new UserFriendAdapter(getActivity(), friends);
			list_friends.setAdapter(userAdapter);
		} else {
			userAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			if (!user.getNick().isEmpty()) {
				sortModel.setNick(user.getNick());
			} else {
				sortModel.setNick(user.getUsername());
			}
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			friends.add(sortModel);
		}
	}

	/**
	 * 长按删除
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Log.i("--Contacts_fragment.onItemLongClick", position + "");
		User user = (User) userAdapter.getItem(position);
		showDeleteDialog(user);
		return false;
	}

	public void showDeleteDialog(final User user) {
		builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示");
		builder.setMessage("确定删除联系人吗？");
		builder.setCancelable(false);
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
		builder.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						deleteContact(user);
					}
				});

		builder.show();
	}

	/**
	 * 删除联系人 deleteContact
	 * 
	 * @return void
	 * @throws
	 */
	private void deleteContact(final User user) {
		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("正在删除...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();

		userManager.deleteContact(user.getObjectId(), new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("删除成功");
				// 删除内存
				StrangerLinkApplication.getInstance().getContactList()
						.remove(user.getUsername());
				// 更新界面
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						progress.dismiss();
						userAdapter.remove(user);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("删除失败：" + arg1);
				progress.dismiss();
			}
		});
	}

	/**
	 * 查看资料
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		User user = (User) userAdapter.getItem(position);
		// 先进入好友的详细资料页面
		Intent intent = new Intent(getActivity(), UserInfo.class);
		intent.putExtra("from", "Contacts_fragment");
		intent.putExtra("username", user.getUsername());
		startActivity(intent);
	}

	Toast mToast;

	public void ShowToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

}
