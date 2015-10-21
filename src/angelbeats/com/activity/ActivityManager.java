package angelbeats.com.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * ActivityManager
 * @author AngelBeats
 *
 */
public class ActivityManager {
	private static List<Activity> mListActivity = new ArrayList<>();

	public static void addActivity(Activity mActivity) {
		mListActivity.add(mActivity);
	}

	public static void remove(Activity mActivity) {
		mListActivity.remove(mActivity);
	}

	public static void removeAll() {
		mListActivity.clear();
		mListActivity = null;
	}
}
