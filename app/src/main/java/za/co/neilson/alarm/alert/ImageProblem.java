package za.co.neilson.alarm.alert;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import za.co.neilson.alarm.AlarmActivity;
import za.co.neilson.alarm.R;            //????????????????

/**
 * Created by swucomputer on 2017. 5. 19..
 */

public class ImageProblem extends AppCompatActivity{



    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "Mul";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/retrained_graph_optimized.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/retrained_labels.txt";

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult;
    private CameraView cameraView;

    private MathProblem mathProblem;




    //hj
    private TextView timer;
    String mission="";

    ImageProblem(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnToggleCamera = (Button) findViewById(R.id.btnToggleCamera);
        btnDetectObject = (Button) findViewById(R.id.btnDetectObject);

        //hj
        timer = (TextView) findViewById(R.id.timerTxt);



        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                //hj 여기서 TensorFlowImageClassifier의 이미지분류 데이터를 넘겨주고 있다.
                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);


                //hj Classifier의 toString()호출로, 화면에 인식결과를 찍어낸다.
                textViewResult.setText(results.toString());

                //hj
                //성공메시지 띄우는 메소드
                verifyImage();

            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        //탐지 버튼이 눌리면
        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage(); //이미지 캡쳐


            }
        });

        initTensorFlowAndLoadModel();
    }


    //hj
    // 미션성공시 성공메시지 출력


    public boolean verifyImage(){

        boolean flag=false;

        //인증 성공하면 알람이 꺼지는 동작 넣기

        if(mission=="칫솔"){
            if(textViewResult.getText().toString().contains("brush")){
                Toast.makeText(this,"인증 성공!",Toast.LENGTH_SHORT).show();

                flag=true;

                finish();
                finishAffinity();



                /*
                try {
                    Intent intent = new Intent(this, AlarmAlertActivity.class);
                    intent.putExtra("flag", flag);
                    startActivity(intent);

                    this.finish();
                } catch(Exception e){

                }
                */

                //return flag;

            }
            else{
                Toast.makeText(this,"인증 실패..",Toast.LENGTH_SHORT).show();
                flag=false;
                //return flag;
            }
        }
        else if(mission=="컵"){
            if(textViewResult.getText().toString().contains("cup")){
                Toast.makeText(this,"인증 성공!",Toast.LENGTH_SHORT).show();
                flag=true;
                finish();
                finishAffinity();

                /*
                try {
                    Intent intent = new Intent(this, AlarmAlertActivity.class);
                    intent.putExtra("flag", flag);
                    startActivity(intent);

                    this.finish();
                } catch(Exception e){

                }
                */
                //return flag;


            }
            else{
                Toast.makeText(this,"인증 실패..",Toast.LENGTH_SHORT).show();
                flag=false;

                //return flag;
            }

        }
        else if(mission=="변기"){
            if(textViewResult.getText().toString().contains("seat")){
                Toast.makeText(this,"인증 성공!",Toast.LENGTH_SHORT).show();
                flag=true;
                finish();
                finishAffinity();


                /*
                try {
                    Intent intent = new Intent(this, AlarmAlertActivity.class);
                    intent.putExtra("flag", flag);
                    startActivity(intent);

                    this.finish();
                } catch(Exception e){

                }
                */
                //return flag;

            }
            else{
                Toast.makeText(this,"인증 실패..",Toast.LENGTH_SHORT).show();
                flag=false;
                //return flag;
            }
        }
        else if(mission=="수도꼭지"){
            if(textViewResult.getText().toString().contains("faucet")){
                Toast.makeText(this,"인증 성공!",Toast.LENGTH_SHORT).show();
                flag=true;
                finish();
                finishAffinity();
                /*
                try {
                    Intent intent = new Intent(this, AlarmAlertActivity.class);
                    intent.putExtra("flag", flag);
                    startActivity(intent);

                    this.finish();
                } catch(Exception e){

                }
                */
                //return flag;


            }
            else{
                Toast.makeText(this,"인증 실패..",Toast.LENGTH_SHORT).show();
                flag=false;
                //return flag;
            }
        }

        return flag;


    }

    //hj 이화면에서 제일 먼저 시작되는 메소드
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();

        //hj
        //랜덤하게 미션을 골라서 사용자에게 보내기
        Random mRand = new Random();
        int nResult = mRand.nextInt(4); //0,1,2,3 중에 난수생성


        if(nResult==0){
            mission="칫솔";
        } else if(nResult==1){
            mission="컵";
        } else if(nResult==2){
            mission="변기";
        } else if(nResult==3){
            mission="수도꼭지";
        }

        //hj 미션 다이얼로그 생성
        final AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(this);


        //hj 딜레이 3초 줘서 미션 변수 전달 완료
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                alertDialogBuilder2.setMessage("미션! \n" +mission+ " 을 찍어오세요. (제한시간 1분)");
                alertDialogBuilder2.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //확인 클릭시 인식 시작
                            }
                        });

                //Showing the alert dialog
                AlertDialog alertDialog2 = alertDialogBuilder2.create();
                alertDialog2.show();

            }
        }, 3000);

        //hj
        //화면 켜지자마자 1분 타이머
        final CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText("제한시간 : " + millisUntilFinished / 1000 + "초");

            }

            public void onFinish() {
                timer.setText("타임 오버");
                finish(); //이 액티비티 종료하고
                //부모 액티비티로 이동
                //이뒤에 나오는 부모 액티비티에서 계산 문제가 안나오는 문제점을 해결해야 함.
                //mathAfterImage();


                /*
                *
                * 실패시 사칙연산으로 넘어가는 동작 넣기
                *
                *
                *
                *
                *
                */




            }
        }.start();



    }


    public void mathAfterImage(){
        Intent intent = new Intent(this,AlarmAlertActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }



}
