package com.example.tabatatimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class TrainingActivity extends AppCompatActivity {

    private int numberOfDoneSets = 0;
    private int numberDoneExercisesInCurrentSet = 0;
    private int tempWorkTime, tempRestTime, tempPauseTime;
    private int tempExerciseNumber;

    private boolean circleOver = false;
    private boolean isStopped = false;
    private boolean introductionTimeOver = false;
    private boolean volumeOn = true;
    private boolean womenIsTalking = true;

    private ArrayList<String> arrayListTrainingPlanExercises;

    private TextView textViewNumberOfSets, textViewExerciseTime, textViewCurrentExercise;
    private Button buttonMusicOnOff , buttonPause , buttonStart;
    private TextToSpeech mTTS;

    private TrainingTime trainingTime;

    private MediaPlayer mediaPlayer;

    private Handler han1;
    private Runnable run1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        initializeVariablesAndViews();
        startTraining();

        buttonStart.setClickable(false);
        buttonPause.setClickable(true);
    }

    private void initializeVariablesAndViews() {

        getFromIntentValues();
        initializeTextViews();
        initializeButtons();
        initializeTextToSpeech();
    }

    private void getFromIntentValues() {
        trainingTime = (TrainingTime) getIntent().getSerializableExtra("trainingTime");
        arrayListTrainingPlanExercises = getIntent().getStringArrayListExtra("arrayListTrainingPlanExercises");
    }

    private void initializeTextViews() {
        textViewExerciseTime = findViewById(R.id.textViewExerciseTime);
        textViewNumberOfSets =  findViewById(R.id.textViewNumberOfSets);
        textViewCurrentExercise = findViewById(R.id.textViewCurrentExercise);

    }

    private void initializeButtons() {
        buttonMusicOnOff = findViewById(R.id.buttonMusicOnOff);
        buttonPause = findViewById(R.id.buttonPause);
        buttonStart = findViewById(R.id.buttonStart);
    }

    private void initializeTextToSpeech() {
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (isTextSpeechSetUpProperly(status))
                    setMoreInfoForTextToSpeech();

                else
                    printMessageProcessFailed();

            }
        });
    }

    private boolean isTextSpeechSetUpProperly(int status) {
        return status == TextToSpeech.SUCCESS;
    }

    private void setMoreInfoForTextToSpeech() {
        int result = mTTS.setLanguage(Locale.ENGLISH);
        if (areThereProblemsWithTextToSpeech(result))
            printMessageProcessFailed2();
    }

    private boolean areThereProblemsWithTextToSpeech(int result) {
        return result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED;
    }

    private void printMessageProcessFailed2() {
        Log.e("TTS", "Language not supported");
    }

    private void printMessageProcessFailed() {
        Log.e("TTS", "Initialization failed");
    }


    private Handler handler;
    private Runnable runnable;

    private void startTraining() {
        setDefaultValuesForTraining();
        //if(isThisStartOfTheTraining())
          //  runTheIntroduction();
        createAndStartHandlerAndRunnable();
    }

    private void setDefaultValuesForTraining() {
        if (trainingIsNotStopped()) {
            setDefaultValuesForWorkingRestAndPauseTime();
            setDefaultValuesForExercisesAndSets();
        }
    }

    private int counter = 0;

    private boolean trainingIsNotStopped() {
        return !isStopped;
    }

    private void setDefaultValuesForWorkingRestAndPauseTime() {
        tempWorkTime = trainingTime.getWorkTimeInSeconds();
        tempRestTime = trainingTime.getRestTimeInSeconds();
        tempPauseTime = trainingTime.getRestTimeInSeconds();
    }

    private void setDefaultValuesForExercisesAndSets() {
        tempExerciseNumber = 0;
        numberOfDoneSets = 0;
        numberDoneExercisesInCurrentSet = 0;
    }

    private void createAndStartHandlerAndRunnable() {
        Log.i("INFO1234" , "createAndStartHandlerAndRunnable");
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                startTimeAndTraining(runnable);
            }
        };

        startHandlerMethod();
    }

    private void startHandlerMethod() {
        handler.post(runnable);
    }

    private void startTimeAndTraining(Runnable runnable) {
        setTrainingInformation();


        /*if(isIntroductionOver())
        {
            runTheIntroduction();
            waitForOneSecond();
        }*/

        if (isWorkRestOrPauseCircleOver())
            skipDelay();

        else
            waitForOneSecond();


            trainingTime();

    }

    private void setTrainingInformation() {
        textViewNumberOfSets.setText(numberOfDoneSets + " / " + trainingTime.getNumberOfSets() + " SET");
    }

    private boolean isWorkRestOrPauseCircleOver() {
        return circleOver;
    }

    private void skipDelay() {
        handler.postDelayed(runnable, 0);
    }

    private void waitForOneSecond() {
        handler.postDelayed(runnable, 1000);
    }

    private void trainingTime() {
        startEnterTrainingMessage();
        if (isTrainingOver())
            setTexViewValuesForDoneTraining();
        else
            SetTime();

    }

    private void startEnterTrainingMessage() {
        if (isThisStartOfTheTraining())
            sayToDoTheFirstExercise();
    }

    private boolean isThisStartOfTheTraining() {
        return ((tempWorkTime == trainingTime.getWorkTimeInSeconds() - 1) && (numberOfDoneSets == 0) && (numberDoneExercisesInCurrentSet == 0));
    }

    private void sayToDoTheFirstExercise() {
        speak("Start Doing " + arrayListTrainingPlanExercises.get(0));
    }

    private boolean isTrainingOver() {
        return numberOfDoneSets == trainingTime.getNumberOfSets();
    }

    private void setTexViewValuesForDoneTraining() {
        setTextViewExerciseTimeValues("TRAINING OVER ! ", Color.BLUE);
        setTextViewCurrentExerciseValue("DONE");
        runTrainingOverMessage();
        handler.removeCallbacks(runnable);
    }

    private void runTrainingOverMessage()
    {
        speak("TRAINING IS OVER ! ");
    }

    private void SetTime() {
        if (isSetOver())
            runSetTime();

        else if (isStillExerciseTime())
            runExerciseTime();

        else if (isStillRestTime())
            runRestTime();

        else
            runNextExerciseTime();
    }

    private boolean isSetOver() {
        return trainingTime.getNumberOfExercises() == numberDoneExercisesInCurrentSet;
    }

    private void runSetTime() {
        if (isStillPauseTime())
            runPauseTime();

        else
            runStepsAfterSetIsDone();
    }

    private boolean isStillPauseTime() {
        return (tempPauseTime >= 0);
    }

    private void runPauseTime() {
        runStartOfPause();
        setTextViewPauseTimeValues();

        decreasePauseTime();

        checkForPauseTime();
        checkForIntroducingNextExercise();
    }

    private void runStartOfPause() {
        if (isThisStartOfPause())
            speak("PAUSE TIME");
    }

    private boolean isThisStartOfPause() {
        return tempPauseTime == trainingTime.getPauseTimeInSeconds();
    }

    private void setTextViewPauseTimeValues() {
        setTextViewCurrentExerciseValue("PAUSE TIME");
        setTextViewExerciseTimeValues(tempPauseTime, Color.YELLOW);
        setTextViewCurrentExerciseValue("PAUSE");
    }

    private void decreasePauseTime() {
        --tempPauseTime;
    }

    private void checkForPauseTime() {
        if (tempPauseTime == -1)
            circleOver = true;
    }

    private void checkForIntroducingNextExercise() {
        if (isTimeForIntroducingExercise())
            speak("Get ready for " + arrayListTrainingPlanExercises.get(0));
    }

    private boolean isTimeForIntroducingExercise() {
        return ((tempPauseTime == 5) && ((numberOfDoneSets + 1) < trainingTime.getNumberOfSets()));
    }

    private void runStepsAfterSetIsDone() {
        circleOver = false;

        setDefaultValuesForWorkingRestAndPauseTime();
        setVariablesToDefaultStateAfterSetIsOver();
    }

    private void setVariablesToDefaultStateAfterSetIsOver() {
        numberDoneExercisesInCurrentSet = 0;
        tempExerciseNumber = 0;
        ++numberOfDoneSets;
    }

    private boolean isStillExerciseTime() {
        return (tempWorkTime >= 0);
    }

    private void runExerciseTime()
    {
        runWorkTimeMessage();
        setTextViewCurrentExerciseValue(arrayListTrainingPlanExercises.get(tempExerciseNumber));
        setTextViewValuesForWorkTime();
        decreaseWorkTime();
        checkForWorkTime();
    }

    private void runWorkTimeMessage()
    {
        if (isThisStartOfTheWorkTime())
            speak("START ! ");
    }

    private boolean isThisStartOfTheWorkTime()
    {
        return (tempWorkTime == trainingTime.getWorkTimeInSeconds());
    }

    private void setTextViewValuesForWorkTime()
    {
        setTextViewCurrentExerciseValue(arrayListTrainingPlanExercises.get(tempExerciseNumber));
        setTextViewExerciseTimeValues(tempWorkTime, Color.RED);
    }

    private void decreaseWorkTime()
    {
        tempWorkTime--;
    }

    private void checkForWorkTime()
    {
        isWorkTimeComingToTheEnd();
        isWorkTimeOver();
    }

    private void isWorkTimeComingToTheEnd()
    {
        if (tempWorkTime == 5)
            speak("Five more seconds ! ");
    }

    private void isWorkTimeOver()
    {
        if (tempWorkTime == 0)
            speak("STOP");
    }

    private boolean isStillRestTime() {
        return (tempRestTime >= 0);
    }

    private void runRestTime() {
        setTextViewValuesForRestTime();

        decreaseRestTime();
        checkForRestTime();
    }

    private void setTextViewValuesForRestTime()
    {
        setTextViewCurrentExerciseValue("REST TIME");
        setTextViewExerciseTimeValues(tempRestTime, Color.GREEN);
    }

    private void decreaseRestTime()
    {
        tempRestTime--;
    }

    private void checkForRestTime()
    {

        runIsRestTimeComingToTheEnd();
        runIsRestTimeOver();
    }

    private void runIsRestTimeComingToTheEnd()
    {
        if (isRestTimeComingToTheEnd())
            if (isThereAnyExerciseLeft())
                speak("Get ready for " + arrayListTrainingPlanExercises.get(tempExerciseNumber + 1));
    }

    private boolean isRestTimeComingToTheEnd()
    {
        return tempRestTime == 5;
    }

    private boolean isThereAnyExerciseLeft()
    {
        return tempExerciseNumber + 1 < arrayListTrainingPlanExercises.size();
    }

    private void runIsRestTimeOver()
    {
        if (tempRestTime == -1)
            circleOver = true;
    }


    private void runNextExerciseTime() {
        circleOver = false;

        setDefaultValuesForWorkingRestAndPauseTime();
        increaseExerciseVariablesValues();
    }

    private void increaseExerciseVariablesValues()
    {
        ++numberDoneExercisesInCurrentSet;
        ++tempExerciseNumber;
    }



    private void setTextViewExerciseTimeValues(int time, int color) {
        textViewExerciseTime.setText(String.valueOf(time));
        textViewExerciseTime.setBackgroundColor(color);
    }

    private void setTextViewExerciseTimeValues(String text, int color) {
        textViewExerciseTime.setText(text);
        textViewExerciseTime.setBackgroundColor(color);
    }

    private void setTextViewCurrentExerciseValue(String text) {
        textViewCurrentExercise.setText(text);
    }

    public void buttonClickPause(View view) {
        stopTraining();
        buttonStart.setClickable(true);
        buttonPause.setClickable(false);
    }

    private void stopTraining() {
        handler.removeCallbacks(runnable);
        isStopped = true;
    }

    public void buttonClickStart(View view) {
        startTraining();
        buttonStart.setClickable(false);
        buttonPause.setClickable(true);
    }

    public void buttonClickStartFromBeginning(View view)
    {
        stopTraining();
        AlertDialog.Builder alert = createAndSetDataForAlertDialogBuilder();
        setAlertDialogBuilderButtons(alert);
        showAlertDialogBuilder(alert);


    }

    private AlertDialog.Builder createAndSetDataForAlertDialogBuilder()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        setAlertDialogBuilderTitleAndMessage(alert);
        return  alert;
    }

    private void setAlertDialogBuilderTitleAndMessage(AlertDialog.Builder alert)
    {
        alert.setTitle("RESET THE TRAINING");
        alert.setMessage("Are you sure you want to reset the training ? ");
    }

    private void setAlertDialogBuilderButtons(AlertDialog.Builder alert)
    {
        setPositiveButtonAlertDialogBuilder(alert);
        setNegativeButtonAlertDialogBuilder(alert);
    }

    private void setPositiveButtonAlertDialogBuilder(AlertDialog.Builder alert)
    {
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                resetTheTraining();
                startTraining();
                buttonStart.setClickable(false);
                buttonPause.setClickable(true);
            }
        });
    }

    private void resetTheTraining()
    {
        handler.removeCallbacks(runnable);
        isStopped = false;
    }

    private void setNegativeButtonAlertDialogBuilder(AlertDialog.Builder alert)
    {
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startTraining();
            }
        });
    }

    private void showAlertDialogBuilder(AlertDialog.Builder alert)
    {
        alert.show();
    }

    private void speak(String text)
    {
        if(volumeOn)
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    public void buttonClickVolumeChange(View view)
    {
        volumeOn = !volumeOn;

        buttonMusicOnOff.setBackgroundResource(R.drawable.ic_volume_off_black_24dp);

        if(volumeOn)
            buttonMusicOnOff.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);

    }

    @Override
    protected void onDestroy()
    {
        shutDownTextToSpeech();
        super.onDestroy();
    }

    private void shutDownTextToSpeech()
    {
        if (mTTS != null)
        {
            mTTS.stop();
            mTTS.shutdown();
        }
    }

}
