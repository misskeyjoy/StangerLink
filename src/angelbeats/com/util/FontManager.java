package angelbeats.com.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FontManager {
     /**
      * 使用回调的方式来改变字体
      * @param root
      * @param act
      */
	public static void changeFonts(ViewGroup root,Activity act){
		Typeface tf= Typeface.createFromAsset(act.getAssets(),"fonts/Roboto-Regular.ttf");
	    for(int i=0;i<root.getChildCount();i++){
	    	View v=root.getChildAt(i);
	    	   if (v instanceof TextView) {  
	               ((TextView) v).setTypeface(tf);  
	            } else if (v instanceof Button) {  
	               ((Button) v).setTypeface(tf);  
	            } else if (v instanceof EditText) {  
	               ((EditText) v).setTypeface(tf);  
	            } else if (v instanceof ViewGroup) {  
	               changeFonts((ViewGroup) v, act);  
	            }  
	    }
	}
	
}
