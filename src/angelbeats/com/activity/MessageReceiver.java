package angelbeats.com.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import angelbeats.com.activity.MainActivity;
import angelbeats.com.activity.R;
import angelbeats.com.applcation.StrangerLinkApplication;
import angelbeats.com.util.CollectionUtils;
import angelbeats.com.util.Utils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * ������Ϣ������
 * @ClassName: MessageReceiver
 * @author AngelBeats
 */
public class MessageReceiver extends BroadcastReceiver {
	private String Tag = "--MessageReceiver";
	// �¼�����
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();
	
	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;//
	BmobUserManager userManager;
	BmobChatUser currentUser;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String json = intent.getStringExtra("msg");
		Log.i(Tag, json);
		
		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		boolean isNetConnected = Utils.isNetworkAvailable(context);
		if(isNetConnected){
			parseMessage(context, json);
		}else{
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	/**
	 * 
	 * @param context
	 * @param json
	 */
	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			final BmobInvitation bmobInvitation = BmobInvitation.createReceiverInvitation(json);
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if(tag.equals(BmobConfig.TAG_OFFLINE)){//����֪ͨ
				if(currentUser!=null){
					if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
						for (EventListener handler : ehList)
							handler.onOffline();
					}else{
						//�������
						StrangerLinkApplication.getInstance().logout();
					}
				}
			}else{
				String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
			   //������Ϣ���շ���ObjectId--Ŀ���ǽ�����˻���½ͬһ�豸ʱ���޷����յ��ǵ�ǰ��½�û�����Ϣ��
				final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_MSGTIME);
				if(fromId!=null){//����Ϣ���ͷ���Ϊ�������û�
					if(TextUtils.isEmpty(tag)){//��Я��tag��ǩ--�˿ɽ���İ���˵���Ϣ
						BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
							
							@Override
							public void onSuccess(BmobMsg msg) {
								// TODO Auto-generated method stub
								if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
									for (int i = 0; i < ehList.size(); i++) {
										((EventListener) ehList.get(i)).onMessage(msg);
									}
								} else {
									boolean isAllow = StrangerLinkApplication.getInstance().getSpUtil().isAllowPushNotify();
									if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){//��ǰ��½�û����ڲ���Ҳ���ڽ��շ�id
										mNewNum++;
										showMsgNotify(context,msg);
									}
								}
							}
							
							@Override
							public void onFailure(int code, String arg1) {
								// TODO Auto-generated method stub
								BmobLog.i("��ȡ���յ���Ϣʧ�ܣ�"+arg1);
							}
						});
						
					}else{//��tag��ǩ
						if(tag.equals(BmobConfig.TAG_ADD_CONTACT)){
							try {
								//ͬ����Ӻ���
								BmobUserManager.getInstance(context).agreeAddContact(bmobInvitation, new UpdateListener() {
									
									@Override
									public void onSuccess() {
										// TODO Auto-generated method stub
										Log.i("--MyMessage.TAG_ADD_CONTACT", "onSuccess"+bmobInvitation.getFromname());
										
										//���浽application�з���Ƚ�
										StrangerLinkApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));	
									}
									
									@Override
									public void onFailure(int arg0, final String arg1) {
										
									}
								});
								Log.i("--MyMessage.TAG_ADD_CONTACT", "try++"+bmobInvitation.getFromname());
								//���������������أ������º�̨��δ���ֶ�
								BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
								if(currentUser!=null){//�е�½�û�
									if(toId.equals(currentUser.getObjectId())){
										if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ
											for (EventListener handler : ehList)
												handler.onAddUser(message);
										}else{
											showOtherNotify(context, message.getFromname(), toId,  message.getFromname()+"�������Ϊ��ϵ��", MainActivity.class);
										}
									}
								}
								
							} catch (final Exception e) {

							}
							
							
						}else if(tag.equals(BmobConfig.TAG_ADD_AGREE)){
							final String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
							//�յ��Է���ͬ������֮�󣬾͵���ӶԷ�Ϊ����--��Ĭ�����ͬ�ⷽΪ���ѣ������浽���غ������ݿ�
							BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {
								
								@Override
								public void onError(int arg0, final String arg1) {
									// TODO Auto-generated method stub
									Log.i("--MyMessage.TAG_ADD_AGREE", "onError++"+username);
								}
								
								@Override
								public void onSuccess(List<BmobChatUser> arg0) {
									// TODO Auto-generated method stub
									Log.i("--MyMessage.TAG_ADD_AGREE", "onSuccess++"+username);
									//���浽�ڴ���
									StrangerLinkApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
								}
							});
							//��ʾ֪ͨ
							showOtherNotify(context, username, toId,  "���"+username+"��Ϊ��ϵ��", MainActivity.class);
							//����һ����ʱ��֤�Ự--�����ڻỰ�����γɳ�ʼ�Ự
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
							
						}else if(tag.equals(BmobConfig.TAG_READED)){//�Ѷ���ִ
							String conversionId = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_CONVERSIONID);
							if(currentUser!=null){
								//����ĳ����Ϣ��״̬
								BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
								if(toId.equals(currentUser.getObjectId())){
									if (ehList.size() > 0) {// �м�����ʱ�򣬴�����ȥ--�����޸Ľ���
										for (EventListener handler : ehList)
											handler.onReaded(conversionId, msgTime);
									}
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ʾ��������Ϣ��֪ͨ
	 * @param context
	 * @param msg
	 */
	public void showMsgNotify(Context context,BmobMsg msg) {
		// ����֪ͨ��
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if(msg.getMsgType()==BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")){
			trueMsg = "[����]";
		}else if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
			trueMsg = "[ͼƬ]";
		}else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
			trueMsg = "[����]";
		}else{
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername()+ " (" + mNewNum + "������Ϣ)";
		
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		boolean isAllowVoice = StrangerLinkApplication.getInstance().getSpUtil().isAllowVoice();
		boolean isAllowVibrate = StrangerLinkApplication.getInstance().getSpUtil().isAllowVibrate();
		
		BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice,isAllowVibrate,icon, tickerText.toString(), contentTitle, tickerText.toString(),intent);
	}
	
	
	/**
	 * ��ʾ����Tag��֪ͨ
	 * @param context
	 * @param username
	 * @param toId
	 * @param ticker
	 * @param cls
	 */
	public void showOtherNotify(Context context,String username,String toId,String ticker,Class<?> cls){
		boolean isAllow = StrangerLinkApplication.getInstance().getSpUtil().isAllowPushNotify();
		boolean isAllowVoice = StrangerLinkApplication.getInstance().getSpUtil().isAllowVoice();
		boolean isAllowVibrate = StrangerLinkApplication.getInstance().getSpUtil().isAllowVibrate();
		if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){
			//ͬʱ����֪ͨ
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,isAllowVibrate,R.drawable.ic_launcher, ticker,username, ticker.toString(),MainActivity.class);
		}
	}
	
}
