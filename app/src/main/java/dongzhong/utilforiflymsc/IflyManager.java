package dongzhong.utilforiflymsc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import dongzhong.utilforiflymsc.configs.Configs;
import dongzhong.utilforiflymsc.exceptions.InitException;
import dongzhong.utilforiflymsc.util.Parser;
import dongzhong.utilforiflymsc.util.ResultLogUtil;

/**
 * Created by dongzhong on 2018/1/17.
 */

public class IflyManager {
    private static IflyManager instance;

    private Context context;
    private SpeechRecognizer speechRecognizer;

    private FileInputThread fileInputThread;
    private IflyRecognizeListener listener;
    private ResultLogUtil resultLogUtil;

    private IflyManager(Context context) throws InitException {
        String appid = Configs.getAppId();
        if (appid == null) {
            throw new InitException("appid is null");
        }
        this.context = context;
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=" + appid + "," + SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        speechRecognizer = SpeechRecognizer.createRecognizer(context, new InitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        initSpeechRecognizer();
    }

    private void initSpeechRecognizer() {
        if (speechRecognizer != null) {
            // 清空参数
            speechRecognizer.setParameter(SpeechConstant.PARAMS, null);

            speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            speechRecognizer.setParameter(SpeechConstant.VAD_ENABLE, "1");
            speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "8000");
            speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "500");
            speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");
            speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
            speechRecognizer.setParameter(SpeechConstant.MIXED_THRESHOLD, "60");
            speechRecognizer.setParameter(SpeechConstant.NET_TIMEOUT, "2000");
        }
    }

    public static IflyManager getInstance(Context context) throws InitException {
        if (instance == null) {
            synchronized (IflyManager.class) {
                if (instance == null) {
                    instance = new IflyManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 通过音频文件识别
     *
     * @param fileName
     */
    public void startRecognize(@NonNull String fileName) throws FileNotFoundException {
        resultLogUtil = new ResultLogUtil();
        if (speechRecognizer != null) {
            speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
            fileInputThread = new FileInputThread(fileName);
            speechRecognizer.startListening(recognizerListener);
            fileInputThread.start();
        }
        else {
            if (listener != null) {
                listener.onError(-1, "speechRecognizer为空");
            }
        }
    }

    private class FileInputThread extends Thread {
        private String fileName;
        private File file;
        private FileInputStream fileInputStream;

        private boolean isRunning = false;

        private FileInputThread(@NonNull String fileName) throws FileNotFoundException {
            super();
            this.fileName = fileName;
            file = new File(fileName);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            fileInputStream = new FileInputStream(file);
        }

        @Override
        public void run() {
            if (fileInputStream == null) {
                return;
            }
            byte[] buffer = new byte[1024];
            try {
                Log.d("Test", "数据长度: " + fileInputStream.available());
                while (fileInputStream != null && isRunning && fileInputStream.read(buffer) >= 0) {
                    if (speechRecognizer != null) {
                        speechRecognizer.writeAudio(buffer, 0, buffer.length);
                    }
                    sleep(40);
                }
                fileInputStream.close();
            }
            catch (Exception e) {
                Log.d("Test", "数据写入异常: " + e.getMessage());
            }
            Log.d("Test", "写入结束");
        }

        @Override
        public synchronized void start() {
            Log.d("Test", "开始写入数据");
            isRunning = true;
            super.start();
        }

        public synchronized void stopInput() {
            Log.d("Test", "结束写入数据");
            isRunning = false;
        }
    }

    private StringBuilder resultStringBuilder = new StringBuilder();
    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {
            Log.d("Test", "说话结束");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            if (resultStringBuilder == null) {
                resultStringBuilder = new StringBuilder();
            }
            String iatReuslt = Parser.iatJsonResult2String(recognizerResult.getResultString());
            resultStringBuilder.append(iatReuslt);
            if (listener != null) {
                listener.onResult(iatReuslt);
                if (isLast) {
                    listener.onResultFinal(resultStringBuilder.toString());
                    Log.d("Test", "最终结果: " + resultStringBuilder.toString());
                    resultLogUtil.writeToResultFile(resultStringBuilder.toString());
                    resultLogUtil.writeToResultFile(",");
                    if (resultStringBuilder.length() > 0) {
                        resultStringBuilder.delete(0, resultStringBuilder.length());
                    }
                    speechRecognizer.startListening(recognizerListener);
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            if (speechError == null) {
                return;
            }
            if (listener != null) {
                listener.onError(speechError.getErrorCode(), speechError.getErrorDescription());
            }
            if (resultStringBuilder != null && resultStringBuilder.length() > 0) {
                resultStringBuilder.delete(0, resultStringBuilder.length());
            }
            resultLogUtil.writeToResultFile(speechError.getErrorDescription());
            resultLogUtil.writeToResultFile(",");
            speechRecognizer.startListening(recognizerListener);
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void setRecognizeListener(IflyRecognizeListener listener) {
        this.listener = listener;
    }

    public interface IflyRecognizeListener {
        /**
         * 获取实时识别结果
         *
         * @param result
         */
        void onResult(String result);

        /**
         * 获取最终结果
         *
         * @param finalResult
         */
        void onResultFinal(String finalResult);

        /**
         * 识别异常
         *
         * @param errorCode
         * @param errorDes
         */
        void onError(int errorCode, String errorDes);
    }
}
