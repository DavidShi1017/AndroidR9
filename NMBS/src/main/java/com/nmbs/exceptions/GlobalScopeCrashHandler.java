package com.nmbs.exceptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.Toast;

import com.nmbs.util.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;


/**
 *
 *
 * @author David
 */
public class GlobalScopeCrashHandler implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler defaultUEH;
    private Context context;

    public GlobalScopeCrashHandler(Context context) {

        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        ex.printStackTrace();

        Writer resultWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(resultWriter);
        StackTraceElement[] trace = ex.getStackTrace();
        StackTraceElement[] trace2 = new StackTraceElement[trace.length + 3];
        System.arraycopy(trace, 0, trace2, 0, trace.length);
        trace2[trace.length + 0] = new StackTraceElement("Android", "MODEL", android.os.Build.MODEL, -1);
        trace2[trace.length + 1] = new StackTraceElement("Android", "VERSION", android.os.Build.VERSION.RELEASE, -1);
        trace2[trace.length + 2] = new StackTraceElement("Android", "FINGERPRINT", android.os.Build.FINGERPRINT, -1);

        ex.setStackTrace(trace2);
        ex.printStackTrace(printWriter);

        String stacktraceString = resultWriter.toString();
        printWriter.close();



        // 这里把刚才异常堆栈信息写入SD卡的Log日志里面
        Utils.saveGlobalExceptionInfo(stacktraceString);

//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            String sdcardPath = Environment.getExternalStorageDirectory().getPath();
//            writeLog(stacktraceString, sdcardPath + "/mythou");
//        }

        /*if (!handleException(ex) && defaultUEH != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            defaultUEH.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }*/
        //defaultUEH.uncaughtException(thread, ex);
    }

    //final Activity activity = AppManager.getAppManager().currentActivity();

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
   /* private boolean handleException(Throwable ex) {

        if (ex == null) {
            return false;
        }

        final Activity activity = AppManager.getAppManager().currentActivity();

        if (activity == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                new AlertDialog.Builder(activity).setTitle(context.getString(R.string.review_option_title))
                        .setCancelable(false).setMessage(context.getString(R.string.exception_text))
                        .setPositiveButton(context.getString(R.string.positive_button_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppManager.getAppManager().exitApp(activity);
                            }
                        }).create().show();
                Looper.loop();
            }
        }.start();
        return true;
    }*/
}