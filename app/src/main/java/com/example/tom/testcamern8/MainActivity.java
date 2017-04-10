package com.example.tom.testcamern8;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.DIRECTORY_PICTURES;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CONTACTS = 1;
    final String[] picture = {"拍照", "照片一", "照片二",};
    Uri imgUri;
    ImageView imv, imv2,imv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imv = (ImageView)findViewById(R.id.imageView);
        imv2 = (ImageView)findViewById(R.id.imageView2);
        imv3 = (ImageView)findViewById(R.id.imageView3);
    }

    public void onClick(View v) {
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
        dialog_list.setTitle("拍照功能");
        dialog_list.setItems(picture, new DialogInterface.OnClickListener() {
            @Override

            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "你選的是" + picture[which], Toast.LENGTH_SHORT).show();
                Log.e("選取", picture[which]);
                Log.e("選取數字", String.valueOf(which));
                if (which == 0) {
                    takePicture();
                }
                else if(which ==1){
                    pickPicture1();
                }
                else if(which ==2){
                    pickPicture2();
                }
            }
        });
        dialog_list.show();
    }

    private void pickPicture2() {
        int permission = ActivityCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            //若尚未取得權限，則向使用者要求允許聯絡人讀取與寫入的權限，REQUEST_CONTACTS常數未宣告則請按下Alt+Enter自動定義常數值。
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    REQUEST_CONTACTS);
        } else {
            //已有權限，可進行以下方法
            makeSave();
            File picDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
            Uri uri2 = Uri.parse(String.valueOf(picDir));
            //final Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            //開啟照片資料夾
            final Intent intent = new Intent(Intent.ACTION_PICK, uri2);
            //複選
            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(intent, 102);
        }
    }

    private void pickPicture1() {
        int permission = ActivityCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            //若尚未取得權限，則向使用者要求允許聯絡人讀取與寫入的權限，REQUEST_CONTACTS常數未宣告則請按下Alt+Enter自動定義常數值。
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    REQUEST_CONTACTS);
        } else {
            //已有權限，可進行以下方法
            //i++;
            makeSave();
            File picDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
            Uri uri2 = Uri.parse(String.valueOf(picDir));
            final Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            //開啟照片資料夾
            final Intent intent = new Intent(Intent.ACTION_PICK, uri2);
            //複選
            //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(intent, 101);
        }
    }
    private void makeSave() {
        if (saveToPictureFolder()) {
            Toast.makeText(MainActivity.this, "儲存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "儲存失敗", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveToPictureFolder() {
        //取得 Pictures 目錄
        File picDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        Log.d(">>>", "Pictures Folder path: " + picDir.getAbsolutePath());
        //假如有該目錄
        if (picDir.exists()) {
            //儲存圖片
            ImageView imv = (ImageView)findViewById(R.id.imageView2);
            File pic = new File(picDir, "pic"+System.currentTimeMillis()+".jpg");
            imv.setDrawingCacheEnabled(true);
            imv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
            Bitmap bmp = imv.getDrawingCache();
            return saveBitmap(bmp, pic);


        }
        return false;
    }

    private boolean saveBitmap(Bitmap bmp, File pic) {
        if (bmp == null || pic == null) return false;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pic);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            scanGallery(this, pic);
            Log.d(">>>", "bmp path: " + pic.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.e(">>>", "save bitmap failed!");
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void scanGallery(Context ctx, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    private void takePicture() {
        int permission = ActivityCompat.checkSelfPermission(this,
                CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //若尚未取得權限，則向使用者要求允許聯絡人讀取與寫入的權限，REQUEST_CONTACTS常數未宣告則請按下Alt+Enter自動定義常數值。
            ActivityCompat.requestPermissions(this,
                    new String[]{CAMERA},
                    REQUEST_CONTACTS);
        } else {
            //已有權限，可進行以下方法
            makePicture();
        }
    }

    private void makePicture() {
        String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
                DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
        imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件

        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
        startActivityForResult(it, 100);

    }
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {   //要求的意圖成功了
            if(requestCode==100){
                Intent it = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imgUri);//設為系統共享媒體檔
                sendBroadcast(it);
                showImg3();
            }
            else if(requestCode==101){
                imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                showImg();
            }
            else if(requestCode==102){
                imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                showImg2();
            }
            /**
            switch(requestCode) {
                case 100: //拍照
                    Intent it = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imgUri);//設為系統共享媒體檔
                    sendBroadcast(it);
                    break;
                case 101: //選取相片
                    imgUri = convertUri(data.getData());  //取得選取相片的 Uri 並做 Uri 格式轉換
                    break;
            }
            showImg();  //顯示 imgUri 所指明的相片
             */
        }
        else {
            Toast.makeText(this, "沒有拍到照片", Toast.LENGTH_LONG).show();
        }
    }

    Uri convertUri(Uri uri) {
        if(uri.toString().substring(0, 7).equals("content")) {  //如果是以 "content" 開頭
            String[] colName = { MediaStore.MediaColumns.DATA };    //宣告要查詢的欄位
            Cursor cursor = getContentResolver().query(uri, colName,  //以 imgUri 進行查詢
                    null, null, null);
            cursor.moveToFirst();      //移到查詢結果的第一筆記錄
            uri = Uri.parse("file://" + cursor.getString(0)); //將路徑轉為 Uri
            cursor.close();     //關閉查詢結果
        }
        return uri;   //傳回 Uri 物件
    }

    void showImg() {
        int iw, ih, vw, vh;
        boolean needRotate;  //用來儲存是否需要旋轉

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中
        iw = option.outWidth;   //由 option 中讀出圖檔寬度
        ih = option.outHeight;  //由 option 中讀出圖檔高度
        vw = imv.getWidth();    //取得 ImageView 的寬度
        vh = imv.getHeight();   //取得 ImageView 的高度

        int scaleFactor;
        if(iw<ih) {    //如果圖片的寬度小於高度
            needRotate = false;       				//不需要旋轉
            scaleFactor = Math.min(iw/vw, ih/vh);   // 計算縮小比率
        }
        else {
            needRotate = true;       				//需要旋轉
            scaleFactor = Math.min(iw/vh, ih/vw);   // 將 ImageView 的寬、高互換來計算縮小比率
        }

        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 4;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("base64",base64);
        if(needRotate) { //如果需要旋轉
            Matrix matrix = new Matrix();  //建立 Matrix 物件
            matrix.postRotate(90);         //設定旋轉角度
            //bmp = Bitmap.createBitmap(bmp , //用原來的 Bitmap 產生一個新的 Bitmap
                    //0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        imv = (ImageView)findViewById(R.id.imageView);
        imv.setImageBitmap(bmp);
        new AlertDialog.Builder(this)
                .setTitle("圖檔資訊")
                .setMessage("圖檔路徑:" + imgUri.getPath() +
                        "\n 原始尺寸:" + iw + "x" + ih +
                        "\n 載入尺寸:"+bmp.getWidth()+"x"+bmp.getHeight()+
                        "\n 顯示尺寸:" + vw + "x" + vh
                )
                .setNegativeButton("關閉", null)
                .show();
    }
    void showImg2() {
        int iw, ih, vw, vh;
        boolean needRotate;  //用來儲存是否需要旋轉

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中
        iw = option.outWidth;   //由 option 中讀出圖檔寬度
        ih = option.outHeight;  //由 option 中讀出圖檔高度
        vw = imv2.getWidth();    //取得 ImageView 的寬度
        vh = imv2.getHeight();   //取得 ImageView 的高度

        int scaleFactor;
        if(iw<ih) {    //如果圖片的寬度小於高度
            needRotate = false;       				//不需要旋轉
            scaleFactor = Math.min(iw/vw, ih/vh);   // 計算縮小比率
        }
        else {
            needRotate = true;       				//需要旋轉
            scaleFactor = Math.min(iw/vh, ih/vw);   // 將 ImageView 的寬、高互換來計算縮小比率
        }

        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 4;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("base64",base64);
        if(needRotate) { //如果需要旋轉
            Matrix matrix = new Matrix();  //建立 Matrix 物件
            matrix.postRotate(90);         //設定旋轉角度
            //bmp = Bitmap.createBitmap(bmp , //用原來的 Bitmap 產生一個新的 Bitmap
            //0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        imv2 = (ImageView)findViewById(R.id.imageView2);
        imv2.setImageBitmap(bmp);
        new AlertDialog.Builder(this)
                .setTitle("圖檔資訊")
                .setMessage("圖檔路徑:" + imgUri.getPath() +
                        "\n 原始尺寸:" + iw + "x" + ih +
                        "\n 載入尺寸:"+bmp.getWidth()+"x"+bmp.getHeight()+
                        "\n 顯示尺寸:" + vw + "x" + vh
                )
                .setNegativeButton("關閉", null)
                .show();
    }
    void showImg3() {
        int iw, ih, vw, vh;
        boolean needRotate;  //用來儲存是否需要旋轉

        BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
        option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
        BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中
        iw = option.outWidth;   //由 option 中讀出圖檔寬度
        ih = option.outHeight;  //由 option 中讀出圖檔高度
        vw = imv3.getWidth();    //取得 ImageView 的寬度
        vh = imv3.getHeight();   //取得 ImageView 的高度

        int scaleFactor;
        if(iw<ih) {    //如果圖片的寬度小於高度
            needRotate = false;       				//不需要旋轉
            scaleFactor = Math.min(iw/vw, ih/vh);   // 計算縮小比率
        }
        else {
            needRotate = true;       				//需要旋轉
            scaleFactor = Math.min(iw/vh, ih/vw);   // 將 ImageView 的寬、高互換來計算縮小比率
        }

        option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
        option.inSampleSize = 4;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
        Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath(), option); //載入圖檔
        //轉成base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
        byte bytes[] = stream.toByteArray();
        // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
        String base64 = Base64.encodeToString(bytes, Base64.DEFAULT); // 把byte變成base64
        Log.e("base64",base64);

        if(needRotate) { //如果需要旋轉
            Matrix matrix = new Matrix();  //建立 Matrix 物件
            matrix.postRotate(90);         //設定旋轉角度
            //bmp = Bitmap.createBitmap(bmp , //用原來的 Bitmap 產生一個新的 Bitmap
            //0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        imv3 = (ImageView)findViewById(R.id.imageView3);
        imv3.setImageBitmap(bmp);
        new AlertDialog.Builder(this)
                .setTitle("圖檔資訊")
                .setMessage("圖檔路徑:" + imgUri.getPath() +
                        "\n 原始尺寸:" + iw + "x" + ih +
                        "\n 載入尺寸:"+bmp.getWidth()+"x"+bmp.getHeight()+
                        "\n 顯示尺寸:" + vw + "x" + vh
                )
                .setNegativeButton("關閉", null)
                .show();

    }


}