package com.noprom.app.ui;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.noprom.app.AppContext;
import com.noprom.app.R;
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
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);

                String account = mAccount.getText().toString();
                String pwd = mPwd.getText().toString();

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
                login(account,pwd);
            }
        });

        // 是否显示登录信息
        AppContext ac = (AppContext) getApplication();
        User user = ac.getLo

    }



    // TODO 登录验证
    private void login(final String account,final String pwd){

        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                AppContext ac = (AppContext) getApplication();
                // TODO

            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
