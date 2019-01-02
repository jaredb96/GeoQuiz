package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import android.content.Intent;

import org.w3c.dom.Text;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private double score;
    private double correct = 0;
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    //challenge 3.1
    private ArrayList<Integer> mQuestionAsked = new ArrayList<Integer>(6);

    //challenge 6.1
    private TextView mAPILevel;

    //challenge 6.2
    private int MAX_CHEATS = 3;
    private TextView mCheatsLeft;




    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_africa, true),
            new Question(R.string.question_asia, false),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_oceans, true ),
            new Question(R.string.question_americas, true),
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreated(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mQuestionAsked = savedInstanceState.getIntegerArrayList("myArray");
        }

        //challenge 6.1 - show API level, determined at runtime
        mAPILevel = (TextView) findViewById(R.id.API_level);
        Integer apiLevelInteger = Build.VERSION.SDK_INT;
        String apiLevelString = apiLevelInteger.toString();
        mAPILevel.append("API Level " + apiLevelString);

        //get resource id from the index of the question in the array
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //mod to wrap around questions in a cycle
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });



        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                    checkAnswer(true);

            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                checkAnswer(false);

            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //mod to wrap around questions in a cycle
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (mCurrentIndex == 0){
                    mCurrentIndex = mQuestionBank.length - 1;

                }
                else{
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                }
                updateQuestion();
                }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MAX_CHEATS <= 0){
                        Toast.makeText(QuizActivity.this, "No more cheats available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        //cheat token implementation

                    }
                    // Start CheatActivity
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                    MAX_CHEATS--;
                }
            });
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            if (data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }




    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);

        //challenge 3.1: put ArrayList in bundle
        savedInstanceState.putIntegerArrayList("myArray", mQuestionAsked);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        //challenge 3.1
        mTrueButton.setVisibility(View.VISIBLE);
        mFalseButton.setVisibility(View.VISIBLE);

        for (Integer i = 0; i < mQuestionAsked.size(); i++){
            //check if question has been asked, if so disable buttons
            //we run this loop on every onClick to keep the proper buttons disabled
            if (mCurrentIndex == mQuestionAsked.get(i)){
                mTrueButton.setVisibility(View.INVISIBLE);
                mFalseButton.setVisibility(View.INVISIBLE);
            }

        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        //challenge 3.1
        mQuestionAsked.add(mCurrentIndex);
        mFalseButton.setVisibility(View.INVISIBLE);
        mTrueButton.setVisibility(View.INVISIBLE);

        int messageResId = 0;
        if (mIsCheater) {
            messageResId = R.string.judgement_toast;

        }
        else {

            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                correct = correct + 1;
            } else {
                messageResId = R.string.false_toast;
            }
        }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

            //challenge 3.1
            if (mCurrentIndex == 5) {
                score = (correct / 6) * 100;
                //truncate score to 3 decimal places
                score = BigDecimal.valueOf(score).setScale(1, RoundingMode.HALF_UP).doubleValue();
                String finalScore = "You scored " + score + "% on this quiz!";
                Toast.makeText(this, finalScore, Toast.LENGTH_SHORT).show();

            }


    }

}
