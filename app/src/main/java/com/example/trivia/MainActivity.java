package com.example.trivia;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int currentindex = 0;
    private Button trueButton;
    private Button falseButton;
    private TextView questionText;
    private TextView noOfQuestion;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private TextView scoreText;
    private TextView highScore;
    private Button resetButton;
    private List<Question> questionList;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

//        Log.d("Second", "onCreate: "+prefs.getHighScore());

        highScore = findViewById(R.id.highscore_text);
        scoreText = findViewById(R.id.score_text);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        questionText = findViewById(R.id.questions);
        noOfQuestion = findViewById(R.id.question_text);
        resetButton = findViewById(R.id.reset_button);

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        highScore.setText(MessageFormat.format("HighScore : {0}", String.valueOf(prefs.getHighScore())));

        currentindex = prefs.getState();
        scoreCounter = prefs.getScores();

        scoreText.setText(MessageFormat.format("Score : {0}", String.valueOf(scoreCounter)));

                questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
//                Log.d("Inside", "processFinished: "+questionArrayList);
                questionText.setText(questionArrayList.get(currentindex).getAnswer());
                noOfQuestion.setText(currentindex + "/" + questionList.size());
            }
        });
//        Log.d("List", "onCreate: "+questionList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.next_button:
                currentindex = (currentindex+1) % questionList.size();
                updateQuestion();
                break;
            case R.id.previous_button:
                if(currentindex>0)
                    currentindex = (currentindex-1) % questionList.size();
                updateQuestion();
                break;
            case R.id.reset_button:
                reset_all();
                break;
        }
    }

    private void reset_all() {
        currentindex = 0;
        scoreCounter = 0;
        scoreText.setText(MessageFormat.format("Score : {0}", String.valueOf(scoreCounter)));
        updateQuestion();
    }

    private void checkAnswer(boolean b) {
        boolean answer = questionList.get(currentindex).isAnswerTrue();
        int Toastid;
        if(b == answer){
            fadeview();
            addPoints();
            Toastid = R.string.correct;
        }else{
            shakeAnimation();
            deductPoints();
            Toastid = R.string.wrong;
        }
        Toast.makeText(MainActivity.this,Toastid,Toast.LENGTH_SHORT).show();
    }

    private void deductPoints() {
        scoreCounter -= 100;
        if (scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreText.setText(MessageFormat.format("Score : {0}", String.valueOf(score.getScore())));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreText.setText(MessageFormat.format("Score : {0}", String.valueOf(score.getScore())));
        }
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreText.setText(MessageFormat.format("Score : {0}", String.valueOf(score.getScore())));
        if((scoreCounter - 100) == prefs.getHighScore()){
            prefs.saveHighScore(scoreCounter);
            highScore.setText(MessageFormat.format("HighScore : {0}", String.valueOf(prefs.getHighScore())));
        }
    }

    private void updateQuestion() {
        String question = questionList.get(currentindex).getAnswer();
        questionText.setText(question);
        noOfQuestion.setText(currentindex + "/" + questionList.size());
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardview = findViewById(R.id.card);
        cardview.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardview.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardview.setCardBackgroundColor(Color.WHITE);
                currentindex = (currentindex+1) % questionList.size();
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void fadeview(){
        final CardView cardView = findViewById(R.id.card);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                currentindex = (currentindex+1) % questionList.size();
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentindex);
        prefs.setScores(score.getScore());
        super.onPause();
    }
}
