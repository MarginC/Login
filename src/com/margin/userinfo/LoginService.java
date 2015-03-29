package com.margin.userinfo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LoginService {
	Context context;
	SharedPreferences sp;

	public LoginService(Context context) {
		this.context = context;
		this.sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
	}

	public Map<String, String> getLocalUserInfoMap() {
		String username = sp.getString("username", "");
		String password = sp.getString("password", "");
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", username);
		map.put("password", password);
		return map;
	}

	public boolean storeLocalUserInfoMap(Map<String, String> userInfoMap) {
		if(userInfoMap.get("username") == null || userInfoMap.get("password") == null)
			return false;
		Editor editor = sp.edit();
		editor.putString("username", userInfoMap.get("username"));
		editor.putString("password", userInfoMap.get("password"));
		return editor.commit();
	}

	public boolean verifyUserInfo(Map<String, String> userInfoMap) {
		if(userInfoMap == null)
			return false;
		String username = sp.getString("username", "");
		String password = sp.getString("password", "");
		return username.equals(userInfoMap.get("username"))
				&& password.equals(userInfoMap.get("password"));
	}

	public boolean delLocalUserInfo() {
		Editor editor = sp.edit();
		editor.clear();
		return editor.commit();
	}

}
