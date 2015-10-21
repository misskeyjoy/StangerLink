package angelbeats.com.fragment;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import angelbeats.com.activity.Chat;
import angelbeats.com.activity.R;
import angelbeats.com.adapter.MessageRecentAdapter;

/**
 * �Ự
 * 
 * @author AngelBeats
 * 
 */
public class Consonvertion_fragment extends Fragment implements OnItemClickListener,OnItemLongClickListener{
	private View viewlayout;
	private TextView titleText;
	ListView listview;
	MessageRecentAdapter adapter;
	private AlertDialog.Builder builder;// ��ʾ��dialog
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewlayout = inflater.inflate(R.layout.fragment_recent, null); // �Ƚ������֣��õ�һ��view
		init();
		return viewlayout;
		
	}
	private void init(){
		listview = (ListView)viewlayout.findViewById(R.id.consonvertion_list);
		titleText = (TextView)viewlayout.findViewById(R.id.header_htv_subtitle);
		titleText.setText("�Ự");
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new MessageRecentAdapter(getActivity(), R.layout.chat_list_item_layout, BmobDB.create(getActivity()).queryRecents());
		listview.setAdapter(adapter);
	}
	
	
	/**
	 * ɾ���Ự
	 * @param recent
	 */
	private void deleteRecent(BmobRecent recent){
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}
	public void showDeleteDialog(final BmobRecent recent) {
		builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("��ʾ");
		builder.setMessage("ȷ��ɾ���Ự��");
		builder.setCancelable(false);
		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
		builder.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						deleteRecent(recent);
					}
				});

		builder.show();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BmobRecent recent = adapter.getItem(position);
		//����δ����Ϣ
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		//��װ�������
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), Chat.class);
		intent.putExtra("from", "Consonvertion_fragment");
		intent.putExtra("user",user );
		startActivity(intent);
		
	}
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(), R.layout.chat_list_item_layout, BmobDB.create(getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}

}
