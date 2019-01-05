package org.shock.incrementalupdate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,permissions[0])!=PackageManager.PERMISSION_GRANTED){
                showDialogTipUserRequestPermission();
            }
        }
    }

    private void showDialogTipUserRequestPermission(){
        new AlertDialog.Builder(this)
                .setTitle("申请获取存储权限")
                .setMessage("增量修改需要存储功能")
                .setPositiveButton("立即开启",(dialog,which)->{
                    ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
                }).setNegativeButton("取消",(dialog,which)->{
                    finish();
        }).setCancelable(false).show();
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    if(!shouldShowRequestPermissionRationale(permissions[0])){//用户点击不在提醒
                        showDialogTipUserGoToAppSettting();
                    }else{
                        finish();
                    }
                }else{
                    Toast.makeText(this,"权限获取成功",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private AlertDialog dialog;
    private void showDialogTipUserGoToAppSettting(){
        dialog = new AlertDialog.Builder(this)
                .setTitle("存储不可用")
                .setMessage("请在-应用设置-权限-中,允许存储权限")
                .setPositiveButton("立即开启",(dialog,which)->{
                    goToAppSetting();
                }).setNegativeButton("取消",(dialog,which)->{
                    finish();
                }).setCancelable(false).show();
    }

    private void goToAppSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(this,permissions[0])!=PackageManager.PERMISSION_GRANTED){
                    showDialogTipUserGoToAppSettting();
                }else{
                    if(dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(this,"权限获取成功",Toast.LENGTH_SHORT);
                }
            }
        }
    }

    public void click(View view){
        switch (view.getId()){
            case R.id.diff:
                diff(getsdpath()+"test1.txt",getsdpath()+"test2.txt",getsdpath()+"test.patch");
                Toast.makeText(this,"差分包已生成",Toast.LENGTH_SHORT).show();
                break;
            case R.id.patch:
                patch(getsdpath()+"test1.txt",getsdpath()+"test3.txt",getsdpath()+"test.patch");
                Toast.makeText(this,"文件已合并",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String getsdpath(){
        return Environment.getExternalStorageDirectory().getPath()+ File.separator;
    }

    //生成差分包
    public native int diff(String oldpath,String newpath,String patch);
    //旧apk和差分包合并
    public native int patch(String oldpath,String newpath,String patch);
}
