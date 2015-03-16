package com.noprom.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.noprom.app.AppContext;
import com.noprom.app.AppException;
import com.noprom.app.R;
import com.noprom.app.api.ApiClient;
import com.noprom.app.bean.Result;
import com.noprom.app.bean.User;
import com.noprom.app.common.StringUtils;
import com.noprom.app.common.UIHelper;

/**
 * 用户登录
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/3/12.
 */
public class LoginActivity extends ActionBarActivity {

    private AutoCompleteTextView mAccount;
    private EditText mPwd;
    private Button mLoginBtn;
    private CheckBox chb_rememberMe;
    private InputMethodManager imm;
    private int curLoginType;

    public final static int LOGIN_OTHER = 0x00;
    public final static int LOGIN_MAIN = 0x01;
    public final static int LOGIN_SETTING = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        curLoginType = getIntent().getIntExtra("LOGINTYPE", LOGIN_OTHER);

        // 初始化控件
        mAccount = (AutoCompleteTextView) findViewById(R.id.login_account);
        mPwd = (EditText) findViewById(R.id.login_password);
        chb_rememberMe = (CheckBox) findViewById(R.id.login_checkbox_rememberMe);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);

                String account = mAccount.getText().toString();
                String pwd = mPwd.getText().toString();
                boolean isRememberMe = chb_rememberMe.isChecked();
                // 判断输入
                if(StringUtils.isEmpty(account)){
                    UIHelper.ToastMessage(v.getContext(),getString(R.string.msg_login_email_null));
                    return;
                }
                if(StringUtils.isEmpty(pwd)){
                    UIHelper.ToastMessage(v.getContext(),getString(R.string.msg_login_pwd_null));
                    return;
                }

                // 登陆操作
                login(account,pwd,isRememberMe);
            }
        });

        // 是否显示登录信息
        AppContext ac = (AppContext) getApplication();
        User user = ac.getLoginInfo();
        if(user == null || !user.isRememberMe()) return;
        if(!StringUtils.isEmpty(user.getAccount())){
            mAccount.setText(user.getAccount());
            mAccount.selectAll();
            chb_rememberMe.setChecked(user.isRememberMe());
        }
        if(!StringUtils.isEmpty(user.getPwd())){
            mPwd.setText(user.getPwd());
        }
    }



    // 登录验证
    private void login(final String account,final String pwd,final boolean isRememberMe){

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    User user = (User) msg.obj;
                    if(user != null){
                        // 清空原先的cookie
                        ApiClient.cleanCookie();
                        // 发送通知广播
                        UIHelper.sendBroadCast(LoginActivity.this,user.getNotice());
                        // 提示登录成功
                        UIHelper.ToastMessage(LoginActivity.this,R.string.msg_login_success);
                        if(curLoginType == LOGIN_MAIN){
                            // 跳转 -- 加载用户动态
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("LOGIN",true);
                            startActivity(intent);
                        }else if(curLoginType == LOGIN_SETTING){
                            // 跳转 -- 用户界面设置
                            Intent intent = new Intent(LoginActivity.this,SettingsActivity.class);
                            intent.putExtra("LOGIN",true);
                            startActivity(intent);
                        }
                        finish();
                    }
                }else if(msg.what == 0){
                    UIHelper.ToastMessage(LoginActivity.this,getString(R.string.msg_login_fail)+msg.obj);
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(LoginActivity.this);
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try{
                    AppContext ac = (AppContext) getApplication();
                    User user = ac.loginVerify(account,pwd);
                    user.setAccount(account);
                    user.setPwd(pwd);
                    user.setRememberMe(isRememberMe);
                    Result res = user.getValidate();
                    if(res.OK()){
                        ac.saveLoginInfo(user);
                        msg.what = 1;   // 成功
                        msg.obj = user;
                    }else{
                        ac.cleanLoginInfo();
                        msg.what = 0;   //失败
                        msg.obj = res.getErrorMessage();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            this.onDestroy();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
