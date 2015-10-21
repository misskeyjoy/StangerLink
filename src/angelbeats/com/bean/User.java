package angelbeats.com.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

@SuppressWarnings("serial")
public class User extends BmobChatUser  {

	//性别-true-男
	private Boolean sex;
	//地理坐标
	private BmobGeoPoint location;
	//设置互动小游戏的种类
	private String friendAddPolicy;
	//个性签名
	private String character;
	//手机号码
	private String phone;
	//你画我猜画图
	private String game2_picture;
	//你画我猜答案
	private String game2_key;
	//你画我猜提示
	private String game2_hint;

	public User(){}
	public Boolean getSex() {
		return sex;
	}
	public void setSex(Boolean sex) {
		this.sex = sex;
	}
	public BmobGeoPoint getLocation() {
		return location;
	}
	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}
	public String getFriendAddPolicy() {
		return friendAddPolicy;
	}
	public void setFriendAddPolicy(String friendAddPolicy) {
		this.friendAddPolicy = friendAddPolicy;
	}
	public String getCharacter() {
		return character;
	}
	public void setCharacter(String character) {
		this.character = character;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getGame2_picture() {
		return game2_picture;
	}
	public void setGame2_picture(String game2_picture) {
		this.game2_picture = game2_picture;
	}
	public String getGame2_key() {
		return game2_key;
	}
	public void setGame2_key(String game2_key) {
		this.game2_key = game2_key;
	}
	public String getGame2_hint() {
		return game2_hint;
	}
	public void setGame2_hint(String game2_hint) {
		this.game2_hint = game2_hint;
	}


}
