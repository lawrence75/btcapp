package com.btc.application.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.btc.application.util.Constant;
import com.btc.application.util.FileUtils;
import com.btc.application.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ModAlipayCodeActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView picture;
    private PopupWindow pop;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Integer id;
    private String alipayAddress;
    String TAG = ModAlipayCodeActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_alipay_code);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        picture = findViewById(R.id.icon);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()   // or .detectAll() for all detectable problems //.detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        // 获取SharedPreference
        SharedPreferences preference = getWindow().getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
        // 获取存在SharedPreference中的用户名
        id = preference.getInt("id", 0);
        alipayAddress = preference.getString("alipayAddress", "");

        if (null != alipayAddress && !"".equals(alipayAddress) && !"null".equals(alipayAddress))
        {
            Bitmap bitmap = FileUtils.url2bitmap(Constant.APP_URL + Constant.FILE_PREFIX + alipayAddress);
            picture.setImageBitmap(bitmap);
        }

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop();
            }
        });

    }

    private void showPop() {
        View bottomView = View.inflate(getWindow().getContext(), R.layout.layout_bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, 200, 500);
        pop.setBackgroundDrawable(new ColorDrawable(Color.GREEN));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //相册
                        if (ContextCompat.checkSelfPermission(getWindow().getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            //相册中的照片都是存储在SD卡上的，需要申请运行时权限，WRITE_EXTERNAL_STORAGE是危险权限，表示同时授予程序对SD卡的读和写的能力
                            ActivityCompat.requestPermissions(ModAlipayCodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }else {
                            openAlbum();
                        }
                        break;
                    case R.id.tv_camera:
                        //拍照
                        File outputImage = new File(getWindow().getContext().getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg");
                        try{
                            if(outputImage.exists())
                                outputImage.delete();
                            outputImage.createNewFile();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        if(Build.VERSION.SDK_INT >=24){
                            imageUri = FileProvider.getUriForFile(getWindow().getContext(),
                                    "com.example.cameraalbumtest.fileprovider",outputImage);
                        }else{
                            imageUri = Uri.fromFile(outputImage);
                        }

                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(intent,TAKE_PHOTO);
                        break;
                    case R.id.tv_cancel:
                        //取消
                        closePopupWindow(pop);
                        break;
                }
                closePopupWindow(pop);
            }
        };
        mCamera.setOnClickListener(clickListener);
        mAlbum.setOnClickListener(clickListener);

        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow(PopupWindow pop) {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream is = getWindow().getContext().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        picture.setImageBitmap(bitmap);
                        //保存图片
                        uploadImage(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK) {
                    //因为sdk19以后返回的数据不同，所以要根据手机系统版本进行不同的操作
                    //判断手机系统版本
                    if(Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKiKai(data);
                    }else {
                        handleImageBeforeKiKai(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    //>=19的操作
    @TargetApi(19)
    private void handleImageOnKiKai(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    //<19的操作
    private void handleImageBeforeKiKai(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri 和selection获取真正的图片路径
        Cursor cursor = getWindow().getContext().getContentResolver().query(
                uri, null, selection, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String path) {
        if(path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            picture.setImageBitmap(bitmap);

            uploadImage(bitmap);
        }else {
            Toast.makeText(getWindow().getContext(), "Load Failed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 保存图片
     * @param bitmap
     * @param path 保存路径
     * @throws IOException
     */
    public File saveImage(Bitmap bitmap,String path) throws IOException{
        String imgePath = "";
        String imgeName = "img";
        File filePath = new File(path);
        if(!filePath.exists()){
            filePath.mkdir();
        }
        int i = 0;
        while(filePath.exists()){
            imgePath = path+"/"+imgeName+(i++)+".jpg";
            filePath = new File(imgePath);
        }
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(filePath));
// 压缩位图到指定的OutputStream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);
// 刷新此缓冲区的输出流
        bos.flush();
// 关闭此输出流并释放与此流有关的所有系统资源
        bos.close();
        return filePath;
    }

    private void uploadImage(Bitmap bitmap)
    {
        String retStr = null;
        try {
            retStr = HttpUtils.uploadFile(saveImage(bitmap,Constant.CACHE_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject json = null;
        String retCode = null;
        try {
            json = new JSONObject(retStr);
            retCode = json.getString("code");

            if (null != retCode && "000000".equals(retCode))
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("alipayAddress", json.getJSONObject("data").getString("path"));

                String method = "user/insertOrUpdate";
                String result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "PUT");
                Log.v(TAG , result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if ("000000".equals(code))
                    {
                        Log.v(TAG , code);
                        Toast.makeText(getApplicationContext(), "上传成功！", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "上传失败！", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}