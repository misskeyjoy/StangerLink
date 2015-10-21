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
 * 推送消息接收器
 * @ClassName: MessageReceiver
 * @author AngelBeats
 */
public class MessageReceiver extends BroadcastReceiver {
	private String Tag = "--MessageReceiver";
	// 事件监听
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
			if(tag.equals(BmobConfig.TAG_OFFLINE)){//下线通知
				if(currentUser!=null){
					if (ehList.size() > 0) {// 有监听的时候，传递下去
						for (EventListener handler : ehList)
							handler.onOffline();
					}else{
						//清空数据
						StrangerLinkApplication.getInstance().logout();
					}
				}
			}else{
				String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
			   //增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
				final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_MSGTIME);
				if(fromId!=null){//该消息发送方不为黑名单用户
					if(TextUtils.isEmpty(tag)){//不携带tag标签--此可接收陌生人的消息
						BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
							
							@Override
							public void onSuccess(BmobMsg msg) {
								// TODO Auto-generated method stub
								if (ehList.size() > 0) {// 有监听的时候，传递下去
									for (int i = 0; i < ehList.size(); i++) {
										((EventListener) ehList.get(i)).onMessage(msg);
									}
								} else {
									boolean isAllow = StrangerLinkApplication.getInstance().getSpUtil().isAllowPushNotify();
									if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){//当前登陆用户存在并且也等于接收方id
										mNewNum++;
										showMsgNotify(context,msg);
									}
								}
							}
							
							@Override
							public void onFailure(int code, String arg1) {
								// TODO Auto-generated method stub
								BmobLog.i("获取接收的消息失败："+arg1);
							}
						});
						
					}else{//带tag标签
						if(tag.equals(BmobConfig.TAG_ADD_CONTACT)){
							try {
								//同意添加好友
								BmobUserManager.getInstance(context).agreeAddContact(bmobInvitation, new UpdateListener() {
									
									@Override
									public void onSuccess() {
										// TODO Auto-generated method stub
										Log.i("--MyMessage.TAG_ADD_CONTACT", "onSuccess"+bmobInvitation.getFromname());
										
										//保存到application中方便比较
										StrangerLinkApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));	
									}
									
									@Override
									public void onFailure(int arg0, final String arg1) {
										
									}
								});
								Log.i("--MyMessage.TAG_ADD_CONTACT", "try++"+bmobInvitation.getFromname());
								//保存好友请求道本地，并更新后台的未读字段
								BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
								if(currentUser!=null){//有登陆用户
									if(toId.equals(currentUser.getObjectId())){
										if (ehList.size() > 0) {// 有监听的时候，传递下去
											for (EventListener handler : ehList)
												handler.onAddUser(message);
										}else{
											showOtherNotify(context, message.getFromname(), toId,  message.getFromname()+"把你添加为联系人", MainActivity.class);
										}
									}
								}
								
							} catch (final Exception e) {

							}
							
							
						}else if(tag.equals(BmobConfig.TAG_ADD_AGREE)){
							final String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
							//收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
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
									//保存到内存中
									StrangerLinkApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
								}
							});
							//显示通知
							showOtherNotify(context, username, toId,  "你和"+username+"互为联系人", MainActivity.class);
							//创建一个临时验证会话--用于在会话界面形成初始会话
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
							
						}else if(tag.equals(BmobConfig.TAG_READED)){//已读回执
							String conversionId = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_CONVERSIONID);
							if(currentUser!=null){
								//更改某条消息的状态
								BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
								if(toId.equals(currentUser.getObjectId())){
									if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
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
	 * 显示与聊天消息的通知
	 * @param context
	 * @param msg
	 */
	public void showMsgNotify(Context context,BmobMsg msg) {
		// 更新通知栏
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if(msg.getMsgType()==BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")){
			trueMsg = "[表情]";
		}else if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
			trueMsg = "[图片]";
		}else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
			trueMsg = "[语音]";
		}else{
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername()+ " (" + mNewNum + "条新消息)";
		
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		boolean isAllowVoice = StrangerLinkApplication.getInstance().getSpUtil().isAllowVoice();
		boolean isAllowVibrate = StrangerLinkApplication.getInstance().getSpUtil().isAllowVibrate();
		
		BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice,isAllowVibrate,icon, tickerText.toString(), contentTitle, tickerText.toString(),intent);
	}
	
	
	/**
	 * 显示其他Tag的通知
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
			//同时提醒通知
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,isAllowVibrate,R.drawable.ic_launcher, ticker,username, ticker.toString(),MainActivity.class);
		}
	}
	
}
