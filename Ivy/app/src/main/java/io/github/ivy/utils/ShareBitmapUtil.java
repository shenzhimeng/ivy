//package io.github.ivy.utils;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Environment;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.formaxcopymaster.activitys.R;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FilenameFilter;
//import java.util.ArrayList;
//
//import base.formax.utils.FileUtils;
//import base.formax.utils.ImageDownloadUtils;
//import base.formax.utils.LogUtil;
//import formax.login.UserInfoUtils;
//import formax.utils.AppUtils;
//
///**
// * Created by zzb on 17/7/19.
// * 处理View相关的分享截图的工具
// */
//
//public class ShareBitmapUtil {
//
//    //截图保存名
//    public static String mCaptruePicName = "capture.png";
//    public static String QQ_CAPTRUE = "capture_qq.png";
//    //二维码默认图
//    public final static String TWO_CODE_DEFAULT = "twocode.png";
//    //二维码用户图
//    public final static String TWO_CODE_USER = "twocode_user.png";
//
//    //二维码下载地址
//    public final static String TWO_CODE_URL= "https://forbag.formaxmarket.com/common/invite_qrcode";
//    public final static Bitmap.Config  DEFAULT_CONFIG = Bitmap.Config.RGB_565;
//
//    /**
//     * 分享完的回调接口，这里暂时用来删sd卡截图的
//     */
//    public interface IShareBitmapListener{
//        void shareFinished();
//    }
//
//    /**
//     * 看类名就知道干啥的
//     */
//    public static class BitmapOOMException extends Exception{
//        OutOfMemoryError oom;
//        public String msg = "应用运行空间不足，截图生成失败!";
//        public BitmapOOMException(OutOfMemoryError e){
//            oom = e;
//        }
//    }
//
//
//
//    public static IShareBitmapListener mCurrentListener = null;
//    public static IShareBitmapListener mListener = new IShareBitmapListener() {
//        @Override
//        public void shareFinished() {
//            delLocalTimePic("capture.png", 0);
//        }
//    };
//
//    /**
//     * 清理达到一定时间的本地截图，本来分享完就清理，但是qq分享到"我的电脑"需要本地截图，所以按时间清理
//     * @param filterEndName 需要过滤的文件名，尾部字串
//     * @param duration 达到n天
//     */
//    public static void delLocalTimePic(final String filterEndName, final long duration){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    File file = new File(getRootFilePath());
//                    File[] fs = file.listFiles(new FilenameFilter() {
//                        @Override
//                        public boolean accept(File dir, String name) {
//                            return name.endsWith(filterEndName);
//                        }
//                    });
//                    for(File f : fs){
//                        if(f.exists() ){
//                            if(duration == 0){
//                                boolean isOk = f.delete();
//                                LogUtil.d("zzb_share", "删除："+isOk);
//                            }else{
//                                String time = f.getName().split(filterEndName)[0];
//                                if(!TextUtils.isEmpty(time)){
//                                    int day = (int)(System.currentTimeMillis() - Long.parseLong(time))/(24*60*60*1000);
//                                    if(day > duration){
//                                        boolean isOk = f.delete();
//                                        LogUtil.d("zzb_share", "超过"+duration+"天，删除："+f.getName());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//    /**
//     * 主要是data/data/cache目录或data/data/包名/cache
//     */
//    public static String getRootFilePath() {
//        if (hasSDCard()) {
//            //todo: 要是没有该目录呢，待验证
//            return AppUtils.getApplication().getExternalCacheDir() + "/";// filePath:/sdcard/
//        } else {
//            return AppUtils.getApplication().getCacheDir()+"/"; // filePath: /data/data/
//        }
//    }
//    public static boolean hasSDCard() {
//        String status = Environment.getExternalStorageState();
//        return status.equals(Environment.MEDIA_MOUNTED);
//    }
//
//    public static String getCapturePath(){
//        return getRootFilePath()+mCaptruePicName;
//    }
//
//    /**
//     * 请求二维码图片
//     */
//    public static synchronized void requestTwoCodePic(final String type){
//
//        if(TextUtils.isEmpty(type)){
//            return;
//        }
//
//        final StringBuilder fileName = new StringBuilder();
//        String url = TWO_CODE_URL;
//        if(TWO_CODE_DEFAULT.equals(type)){
//            fileName.append(TWO_CODE_DEFAULT);
//            if(new File(getRootFilePath()+fileName).exists()){
//                LogUtil.d("zzb_share",fileName+"存在----");
//                return;
//            }
//        }else if(TWO_CODE_USER.equals(type)){
//            String uid = "";
//            if(UserInfoUtils.isInLoginedStatus()){
//                uid = UserInfoUtils.getUid()+"";
//                url += "/"+uid;
//                fileName.append(TWO_CODE_USER);
//            }else{
//                return;
//            }
//        }
//
//
//        ImageDownloadUtils.getSmallDrawable(url, new ImageDownloadUtils.OnDrawableReadyListener() {
//            @Override
//            public void onGetDrawable(Drawable drawable) {
//                Bitmap bitmap = FileUtils.drawableToBitmap(drawable);
//                if(bitmap == null){
//                    LogUtil.d("zzb_share","二维码下载失败");
//                    return;
//                }
//                if(TWO_CODE_DEFAULT.equals(type)){
//                    LogUtil.d("zzb_share","默认二维码下载成功!");
//                }else if(TWO_CODE_USER.equals(type)){
//                    LogUtil.d("zzb_share","用户二维码下载成功!");
//                }
//                saveCacheDirBitmap(bitmap, type, Bitmap.CompressFormat.PNG, 100);
//            }
//            @Override
//            public void onGetFail() {
//                LogUtil.d("zzb_share","下载失败");
//                if(TWO_CODE_USER.equals(type)){
//                    //可能是旧的，删了
//                    new File(getRootFilePath()+TWO_CODE_USER).delete();
//                }
//            }
//        });
//    }
//
//    /**
//     * 截图分享
//     * @param context
//     */
//    public static void share(Context context){
//        mCaptruePicName = System.currentTimeMillis()+"capture.png";
//        final String tempPath = getRootFilePath() + mCaptruePicName;
//        ShareSdkUtils.ShareInfoEntity info = ShareSdkUtils.buildShareInfo("", "", tempPath , "");
//
//        mCurrentListener = mListener;
//        ShareSdkUtils.share(context, info, ShareType.STOCK_DETAIL, "", null);
//    }
//
//    /**
//     * 清除保存在硬盘上的临时截图
//     */
//    public static void notifyDeleteFile(){
//        if(mCurrentListener != null){
//            mCurrentListener.shareFinished();
//            mCurrentListener = null;
//        }
//    }
//    /**
//     * 抓取单个View的截图
//     * @return
//     */
//    public static Bitmap getViewCaptureBmp(View view) throws BitmapOOMException{
//        return getViewCaptureBmp(view, DEFAULT_CONFIG);
//    }
//
//    public static Bitmap getViewCaptureBmp(View view, Bitmap.Config config) throws BitmapOOMException{
//        if(view == null || view.getHeight() == 0 || view.getWidth() == 0){
//            return null;
//        }
//        Bitmap bitmap;
//        try {
//            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), config);
//            Canvas cv = new Canvas(bitmap);
//            //获取背景色
//            Drawable bgDrawable  = view.getBackground();
//            if(bgDrawable == null){
//                view.setBackgroundColor(Color.parseColor("#ffffff"));
//            }
//            view.draw(cv);
//            view.setBackgroundDrawable(bgDrawable);
//        }catch (OutOfMemoryError e){
//            throw new BitmapOOMException(e);
//        }
//        return bitmap;
//    }
//
//
//    /**
//     * 抓取ViewGroup的长图
//     * @param viewGroup
//     * @param maxH 截图截止底部高度，为0时，View多大截取多大
//     * @return
//     */
//    public static Bitmap getViewGroupCaptureBmp(ViewGroup viewGroup, int maxH) throws BitmapOOMException{
//        return getViewGroupCaptureBmp(viewGroup, maxH, DEFAULT_CONFIG);
//    }
//
//    public static Bitmap getViewGroupCaptureBmp(ViewGroup viewGroup, int maxH, Bitmap.Config config)throws BitmapOOMException{
//        if(viewGroup == null){
//            return null;
//        }
//        ArrayList<Bitmap> bms = new ArrayList<>();
//        int h = 0;
//        int w = viewGroup.getWidth();
//        View v;
//        for(int i=0; i<viewGroup.getChildCount(); i++){
//            //获取子View的截图
//            v = viewGroup.getChildAt(i);
//            if(v == null){
//                continue;
//            }
//            h += v.getHeight();
//            bms.add(getViewCaptureBmp(v));
//        }
//        Bitmap bgBmp = spliceBitmap(bms, w, maxH==0? h:maxH, config);
//        bms.clear();
//        return bgBmp;
//    }
//
//
//
//    /**
//     * 多图拼接，这里将会拼底部的logo图
//     * @param context
//     * @param bms
//     * @return
//     * @throws BitmapOOMException
//     */
//    public static Bitmap spliceShareLogo(Context context, ArrayList<Bitmap> bms)throws BitmapOOMException{
//        if(bms == null){
//            bms = new ArrayList<>();
//        }
//
//        int hh = 0, ww = 0;
//        for(Bitmap b : bms){
//            if(b != null){
//                hh += b.getHeight();
//                ww = ww>b.getWidth()?ww:b.getWidth();
//            }
//        }
//        Bitmap bmp = ShareBitmapUtil.spliceBitmap(bms, ww, hh);
//
//        //生成logo图
//        Bitmap logoBmp = ShareBitmapUtil.drawBottomLogo(context);
//
//        if(logoBmp != null && bmp != null){
//            Canvas canvas = new Canvas(bmp);
//            Paint paint = new Paint();
//            canvas.drawBitmap(logoBmp, 0, hh-logoBmp.getHeight(), paint);
//        }
//
//        if(AppUtils.DEBUG && bmp != null){
//            LogUtil.d("zzb_share", "size0 ="+ FileUtils.getFormatSize(bmp.getRowBytes() * bmp.getHeight()));
//        }
//        return bmp;
//    }
//
//    /**
//     * 拼接图片
//     * @param bms 要拼接的图片
//     * @return 一张拼接好的Bitmap
//     */
//
//    public static Bitmap  spliceBitmap(ArrayList<Bitmap> bms, int w, int h)throws BitmapOOMException{
//        return spliceBitmap(bms, w, h, DEFAULT_CONFIG );
//    }
//
//    public static Bitmap  spliceBitmap(ArrayList<Bitmap> bms, int w, int h, Bitmap.Config config)throws BitmapOOMException{
//        if(w == 0 || h==0 || bms == null){
//            return null;
//        }
//        Bitmap bgBmp =null;
//        try {
//            //拼图,bitmap太多,需要注意下oom
//            bgBmp = Bitmap.createBitmap(w, h, config);
//            bgBmp.eraseColor(Color.parseColor("#FFFFFF"));
//            Canvas cvs = new Canvas(bgBmp);
//            Paint paint = new Paint();
//            h = 0;
//            for(Bitmap b : bms){
//                if(b == null || b.isRecycled()){
//                    continue;
//                }
//                //将子view截图按顺序拼在一起
//                cvs.drawBitmap(b, 0,  h, paint);
//                h += b.getHeight();
//            }
//        }catch (OutOfMemoryError error){
//            throw new BitmapOOMException(error);
//        }
//        return bgBmp;
//    }
//
//
//    /**
//     * 根据xml布局动态绘制出logo的bmp
//     */
//    public static Bitmap drawBottomLogo(Context context) throws BitmapOOMException{
//        DisplayMetrics metric = context.getResources().getDisplayMetrics();
//        int width = metric.widthPixels;     // 屏幕宽度（像素）
//        //int height = metric.heightPixels;   // 屏幕高度（像素）
//
//        try {
//            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.share_logo, null);
//            if (rl == null) {
//                return null;
//            }
//
//            //根布局不设置params在4.4以下会null崩溃
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            rl.setLayoutParams(params);
//
//            ImageView imageView = (ImageView) rl.findViewById(R.id.twocode_img);
//
//            //TODO:后续再整理下
//            String path = getRootFilePath() + TWO_CODE_DEFAULT;
//            if (UserInfoUtils.isInLoginedStatus()) {
//                String tPath = getRootFilePath() + TWO_CODE_USER;
//                if (new File(tPath).exists()) {
//                    path = tPath;
//                }
//            }
//            Bitmap bmp = BitmapFactory.decodeFile(path);
//            if (bmp != null && !bmp.isRecycled() && imageView != null) {
//                imageView.setImageBitmap(bmp);
//            }
//
//            //这里要根据布局的长宽具体设定，所以不要乱动布局哦,要动一起动
//            rl.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
//                    , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            rl.layout(0, 0, rl.getMeasuredWidth(), rl.getMeasuredHeight());
//            return ShareBitmapUtil.getViewCaptureBmp(rl);
//        }catch (NullPointerException e){
//            return null;
//        }
//    }
//
//    /**
//     * 单图拼接底部logo
//     * @param context
//     * @param bitmap 原图
//     * @param left
//     * @param top
//     * @param right
//     * @param bottom
//     * @return
//     * @throws BitmapOOMException
//     */
//    public static Bitmap spliceShareLogo(Context context, Bitmap bitmap, int left, int top, int right, int bottom) throws BitmapOOMException{
//        if(bitmap == null || bitmap.isRecycled()){
//            return null;
//        }
//
//        Bitmap newBmp = null;
//        Bitmap logoBmp = null;
//        try {
//            newBmp = Bitmap.createBitmap(right, bottom, Bitmap.Config.RGB_565);
//            newBmp.eraseColor(Color.parseColor("#FFFFFF"));
//
//            Canvas canvas = new Canvas(newBmp);
//            Paint paint = new Paint();
//            canvas.drawBitmap(bitmap, left, top, paint);
//            //生成logo图
//            logoBmp = ShareBitmapUtil.drawBottomLogo(context);
//            if(logoBmp != null) {
//                canvas.drawBitmap(logoBmp, 0, bottom - logoBmp.getHeight(), paint);
//            }
//
//        }catch (NullPointerException e){
//            return null;
//        } catch (OutOfMemoryError error){
//            throw new BitmapOOMException(error);
//        }finally {
//            if(logoBmp != null){
//                logoBmp.recycle();
//                logoBmp = null;
//            }
//        }
//        return newBmp;
//    }
//
//    /**
//     * 保存sd中
//     */
//    public static void saveCacheDirBitmap(Bitmap bmp, String fileName, Bitmap.CompressFormat picType, int size){
//        if(bmp == null || bmp.isRecycled()){
//            return;
//        }
//
//        try {
//            String path = getRootFilePath()+ fileName;
//            FileOutputStream out = new FileOutputStream(path);
//            try {
//                bmp.compress(picType, size, out);
//                out.flush();
//                LogUtil.d("zzb_share","图片保存成功："+fileName);
//            } finally {
//                out.close();
//            }
//        }catch (Exception e){
//            LogUtil.d("zzb_share", "图片保存失败："+e.getMessage());
//        }
//    }
//
//    /**
//     * 取bitmap图
//     * @param path
//     * @param preW
//     * @param preH
//     * @param config
//     * @return
//     */
//    public static Bitmap getCacheDirBitmap(String path, int preW, int preH, Bitmap.Config config){
//        Bitmap bmp=null;
//        try {
//            if(preW == -1) {
//                DisplayMetrics metric = AppUtils.getApplication().getResources().getDisplayMetrics();
//                int width = metric.widthPixels;     // 屏幕宽度（像素）
//                int height = metric.heightPixels;   // 屏幕高度（像素）
//                int newH = 720 * height / width;
//                LogUtil.d("zzb_share", "h =" + newH);
//                preW = 720;
//                preH = newH;
//            }
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            // 获取这个图片的宽和高，注意此处的bitmap为null
//            bmp = BitmapFactory.decodeFile(path, options);
//
//            int size = FileUtils.calculateInSampleSize(options, preW, preH);
//            options.inJustDecodeBounds = false; // 设为 false
//            options.inSampleSize = size;
//            options.inPreferredConfig = config;
//            bmp = BitmapFactory.decodeFile(path, options);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bmp;
//    }
//
//}
