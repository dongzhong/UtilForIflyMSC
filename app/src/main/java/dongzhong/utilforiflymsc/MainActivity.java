package dongzhong.utilforiflymsc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;

import dongzhong.utilforiflymsc.exceptions.InitException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private Button button;

    private IflyManager iflyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.textview_result);
        button = (Button) findViewById(R.id.button_start);
        button.setOnClickListener(this);
    }

    private void init() {
        try {
            iflyManager = IflyManager.getInstance(this);
        }
        catch (InitException e) {
            final String exceptionMsg = e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(exceptionMsg);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start:
                iflyManager.setRecognizeListener(new IflyManager.IflyRecognizeListener() {
                    @Override
                    public void onResult(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //textView.setText(result);
                            }
                        });
                    }

                    @Override
                    public void onResultFinal(final String finalResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(finalResult);
                            }
                        });
                    }

                    @Override
                    public void onError(final int errorCode, final String errorDes) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("errorCode: " + errorCode + "\n" + "errorDes: " + errorDes);
                            }
                        });
                    }
                });
                try {
                    iflyManager.startRecognize("/mnt/sdcard/ifly/test.pcm");
                }
                catch (FileNotFoundException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("文件不存在");
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
