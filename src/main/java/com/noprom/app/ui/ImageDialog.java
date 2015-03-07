package com.noprom.app.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.noprom.app.AppException;
import com.noprom.app.R;
import com.noprom.app.api.ApiClient;
import com.noprom.app.common.FileUtils;
import com.noprom.app.common.ImageUtils;
import com.noprom.app.common.StringUtils;
import com.noprom.app.common.UIHelper;

import java.io.File;
import java.io.IOException;

/**
 * 图片对话框
 *
 * @author noprom (http://github.com/noprom)
 * @version 1.0
 *          Created by noprom on 2015/2/22.
 */
public class ImageDialog extends BaseActivity {

    private ViewSwitcher mViewSwitcher;
    private Button btn_preview;
    private ImageView mImage;

    private Thread thread;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);

        this.initView();

        this.initData();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            thread.interrupt();
            handler = null;
            finish();
            return true;
        }
    };

    private void initView() {
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.imagedialog_view_switcher);
        mViewSwitcher.setOnTouchListener(touchListener);

        btn_preview = (Button) findViewById(R.id.imagedialog_preview_button);
        btn_preview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String imgURL = getIntent().getStringExtra("img_url");
                UIHelper.showImageZoomDialog(v.getContext(), imgURL);
                finish();
            }
        });

        mImage = (ImageView) findViewById(R.id.imagedialog_image);
        mImage.setOnTouchListener(touchListener);
    }

    private void initData() {
        final String imgURL = getIntent().getStringExtra("img_url");
        final String ErrMsg = getString(R.string.msg_load_image_fail);
        final String localImg = getIntent().getStringExtra("local_img");
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1 && msg.obj != null) {
                    mImage.setImageBitmap((Bitmap) msg.obj);
                    mViewSwitcher.showNext();
                } else {
                    UIHelper.ToastMessage(ImageDialog.this, ErrMsg);
                    finish();
                }
            }
        };
        thread = new Thread() {
            public void run() {
                Message msg = new Message();
                Bitmap bmp = null;
                if (!StringUtils.isEmpty(localImg)) {
                    bmp = BitmapFactory.decodeFile(localImg);
                    btn_preview.setVisibility(View.GONE);
                }
                String filename = FileUtils.getFileName(imgURL);
                try {
                    // 读取本地图片
                    if (imgURL.endsWith("portrait.gif")
                            || StringUtils.isEmpty(imgURL)) {
                        bmp = BitmapFactory.decodeResource(
                                mImage.getResources(), R.drawable.widget_dface);
                    }
                    if (bmp == null) {
                        // 是否有缓存图片
                        // Environment.getExternalStorageDirectory();返回/sdcard
                        String filepath = getFilesDir() + File.separator
                                + filename;
                        File file = new File(filepath);
                        if (file.exists()) {
                            bmp = ImageUtils.getBitmap(mImage.getContext(),
                                    filename);
                            if (bmp != null) {
                                // 缩放图片
                                bmp = ImageUtils.reDrawBitMap(ImageDialog.this,
                                        bmp);
                            }
                        }
                    }
                    if (bmp == null) {
                        bmp = ApiClient.getNetBitmap(imgURL);
                        if (bmp != null) {
                            try {
                                // 写图片缓存
                                ImageUtils.saveImage(mImage.getContext(),
                                        filename, bmp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // 缩放图片
                            bmp = ImageUtils
                                    .reDrawBitMap(ImageDialog.this, bmp);
                        }
                    }
                    msg.what = 1;
                    msg.obj = bmp;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                if (handler != null && !isInterrupted())
                    handler.sendMessage(msg);
            }
        };
        thread.start();
    }
}
