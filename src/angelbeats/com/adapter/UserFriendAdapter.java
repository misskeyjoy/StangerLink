package angelbeats.com.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import angelbeats.com.activity.R;
import angelbeats.com.bean.User;
import angelbeats.com.util.ImageLoadOptions;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 好友列表
 * @author AngelBeats
 * @ClassName: UserFriendAdapter
 */
@SuppressLint("DefaultLocale")
public class UserFriendAdapter extends BaseAdapter  implements SectionIndexer{
	private Context ct;
	private List<User> data;

	public UserFriendAdapter(Context ct, List<User> datas) {
		this.ct = ct;
		this.data = datas;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<User> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(User user) {
		this.data.remove(user);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(
					R.layout.item_user_friend, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_friend_name);
			viewHolder.avatar = (ImageView) convertView
					.findViewById(R.id.img_friend_avatar);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		User friend = data.get(position);
		final String name = friend.getNick();
		final String avatar = friend.getAvatar();

		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, viewHolder.avatar,
					ImageLoadOptions.getOptions());
		} else {
			viewHolder.avatar.setImageDrawable(ct.getResources().getDrawable(
					R.drawable.head_pic));
		}
		viewHolder.name.setText(name);
		return convertView;
	}

	static class ViewHolder {
		// TextView alpha;// 首字母提示
		ImageView avatar;
		TextView name;

		public static ImageView getImageView(View convertView, int headeConsView) {
			// TODO Auto-generated method stub
			return null;
		}

		public static TextView getTextView(View convertView, int headeConsView) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}