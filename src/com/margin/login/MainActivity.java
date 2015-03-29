package com.margin.login;

import java.util.HashMap;
import java.util.Map;

import com.margin.security.MD5;
import com.margin.userinfo.LoginService;
import com.margin.utils.FastClickUtil;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener, TextWatcher, OnFocusChangeListener {

	private EditText et_username;
	private EditText et_password;
	private CheckBox cb_remenber;
	private Button button;
	private boolean stored = false;

	private LoginService loginService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		cb_remenber = (CheckBox) findViewById(R.id.cb_remenber);
		button = (Button) findViewById(R.id.bt_login);

		loginService = new LoginService(this);

		Map<String, String> userInfoMap = loginService.getLocalUserInfoMap();
		if(userInfoMap != null) {
			et_username.setText(userInfoMap.get("username"));
			et_password.setText(userInfoMap.get("password"));
			stored = true;
		}

		et_username.addTextChangedListener(this);
		et_password.addTextChangedListener(this);
		et_password.setOnFocusChangeListener(this);
		button.setOnClickListener(this);
		((Button) findViewById(R.id.add_info)).setOnClickListener(this);
		((Button) findViewById(R.id.del_info)).setOnClickListener(this);
	}

	private void addUserInfo() {
		Map<String, String> userInfoMap = new HashMap<String, String>();
		userInfoMap.put("username", "1028519445");
		userInfoMap.put("password", MD5.getMD5("zyc@48755304"));
		if(loginService.storeLocalUserInfoMap(userInfoMap))
			showMessage(R.string.add_info, R.string.add_success);
		else
			showMessage(R.string.add_info, R.string.add_failed);
	}

	private void delUserInfo() {
		if(loginService.delLocalUserInfo())
			showMessage(R.string.del_info, R.string.del_success);
		else
			showMessage(R.string.del_info, R.string.del_failed);
		cleanEdit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if(FastClickUtil.isFastDoubleClick()) {
			Toast.makeText(this, R.string.click_too_fast, Toast.LENGTH_SHORT).show();
			return;
		}
		switch (v.getId()) {
		case R.id.bt_login:
			// 清空password焦点，再次获得焦点时，可以清空MD5内容
			et_password.clearFocus();
			new Handler().postDelayed(new Runnable(){
				public void run() {
					doLogin();
				}
			}, 5000);
			break;
		case R.id.add_info:
			addUserInfo();
			break;
		case R.id.del_info:
			delUserInfo();
			break;
		default:
			break;
		}
	}

	private void doLogin() {
		String username = et_username.getText().toString().trim();
		String password = et_password.getText().toString();
		if(!stored) {
			if(checkIsEmpty(username, password))
				return;
			password = MD5.getMD5(password.trim());
			if(password == null) {
				Toast.makeText(this,
						R.string.sys_error, Toast.LENGTH_LONG).show();
				return;
			}
			et_password.setText(password);
		}
		Map<String, String> userInfoMap = new HashMap<String, String>();
		userInfoMap.put("username", username);
		userInfoMap.put("password", password);
		if(loginService.verifyUserInfo(userInfoMap)) {
			if(cb_remenber.isChecked()) {
				loginService.storeLocalUserInfoMap(userInfoMap);
			} else {
				loginService.delLocalUserInfo();
				cleanEdit();
			}
			loginSuccess();
		} else {
			loginFailed();
		}
		stored = true;
	}

	private void cleanEdit() {
		et_username.setText("");
		et_password.setText("");
	}

	private boolean checkIsEmpty(String username, String password) {
		if(TextUtils.isEmpty(username)) {
			Toast.makeText(this,
					R.string.empty_username, Toast.LENGTH_LONG).show();
		} else if(TextUtils.isEmpty(password)) {
			Toast.makeText(this,
					R.string.empty_password, Toast.LENGTH_LONG).show();
		} else {
			return false;
		}
		return true;
	}

	private void loginSuccess() {
		showMessage(R.string.login_sucess, R.string.login_sucess_message);
	}

	private void loginFailed() {
		showMessage(R.string.login_failed, R.string.login_failed_message);
	}

	private void showMessage(int title, int message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok, null);
		builder.show();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_password:
			if (hasFocus && stored)
				et_password.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		stored = false;
	}
	@Override
	public void afterTextChanged(Editable s) {
	}

}
