package com.example.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TimeActivity extends AppCompatActivity
{

    private EditText editTextWorkingTime , editTextRestTime , editTextNumberOfSets ,editTextPauseAfterSetTime;
    private TextView textViewTrainingTimeData;

    private TrainingTime trainingTime;

    private ArrayList<String> arrayListTrainingPlanExercises;

    private int numberOfExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);


        initializeVariablesAndViews();
        setTextForEditTexts();
        editTextChangeTextListener();

    }

    private void initializeVariablesAndViews()
    {
        getValuesFromIntent();
        initializeEditTexts();
        initializeTextViews();
        initializeTrainingTime();
    }

    private void getValuesFromIntent()
    {
        numberOfExercises = getIntent().getIntExtra("numberOfExercises" , 0);

        arrayListTrainingPlanExercises = getIntent().getStringArrayListExtra("arrayListTrainingPlanExercises");
    }

    private void initializeEditTexts()
    {
        editTextRestTime =  findViewById(R.id.editTextRestTimeExercise);
        editTextWorkingTime =  findViewById(R.id.editTextWorkTime);
        editTextNumberOfSets =  findViewById(R.id.editTextNumberOfSets);
        editTextPauseAfterSetTime =  findViewById(R.id.editTextPauseAfterSet);
    }

    private void initializeTextViews()
    {
        textViewTrainingTimeData = findViewById(R.id.textViewTimeResults);
    }

    private void initializeTrainingTime()
    {
        trainingTime = new TrainingTime(numberOfExercises);
    }

    private void setTextForEditTexts()
    {
        setEditTextWorkTimeText();
        setEditTextRestTimeText();
        setEditTextPauseTimeText();
        setEditTextNumberOfSetsText();
    }

    private void setEditTextWorkTimeText()
    {
        String textWorkingTime = "<b>" + "Working" + "</b> " + "time for an exercise in seconds : ";
        setEditTextText(editTextWorkingTime , textWorkingTime);
    }

    private void setEditTextRestTimeText()
    {
        String textRestTime = "<b>" + "Rest" + "</b> " + "time after exercise in seconds : ";
        setEditTextText(editTextRestTime , textRestTime);
    }

    private void setEditTextPauseTimeText()
    {
        String textPauseAfterSet = "<b>" + " Pause time" + "</b> " + "after set in seconds : ";
        setEditTextText(editTextPauseAfterSetTime , textPauseAfterSet);
    }

    private void setEditTextNumberOfSetsText()
    {
        String textNumberOfSets ="<b>" + "Number of sets <br>" + "</b> " + "(times you want to repeat all exercises)";
        setEditTextText(editTextNumberOfSets , textNumberOfSets);
    }

    private void setEditTextText(EditText editText , String text)
    {
        editText.setHint(Html.fromHtml(text));
    }


    private void editTextChangeTextListener()
    {
        addTextChangedListenerToEditText(editTextWorkingTime);
        addTextChangedListenerToEditText(editTextRestTime);
        addTextChangedListenerToEditText(editTextPauseAfterSetTime);
        addTextChangedListenerToEditText(editTextNumberOfSets);
    }

    private void addTextChangedListenerToEditText(EditText editText)
    {
        editText.addTextChangedListener(new EditTextHelper());
    }

    private void calculateTrainingDuration()
    {
        setTrainingPlanValues();
        printTrainingValuesInTextView();
    }

    private void setTrainingPlanValues()
    {
        try
        {
            trainingTime.setWorkTimeInSeconds(Integer.valueOf(editTextWorkingTime.getText().toString()));
            trainingTime.setRestTimeInSeconds(Integer.valueOf(editTextRestTime.getText().toString()));
            trainingTime.setNumberOfSets(Integer.valueOf(editTextNumberOfSets.getText().toString()));
            trainingTime.setPauseTimeInSeconds(Integer.valueOf(editTextPauseAfterSetTime.getText().toString()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void printTrainingValuesInTextView()
    {
        textViewTrainingTimeData.setText("");
        textViewTrainingTimeData.append(("Time for one exercise : " + trainingTime.getTimeForExerciseSeconds() + "sec" + "\n"));
        textViewTrainingTimeData.append(("Time for one set : " + trainingTime.getTimeForSetSeconds() + "sec" +  "\n"));
        textViewTrainingTimeData.append(("Training duration : " +trainingTime.getTrainingDurationMin()
                + "min " + trainingTime.getTrainingDurationSec() + " sec"));
    }


    public void buttonClickStartTraining(View view)
    {
        if(isDataEnteredInEveryEditText())
        {
            Intent intent = makeAnIntent();
            putValuesInIntent(intent);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext() , "You did not enter data in all fields ! " , Toast.LENGTH_LONG).show();
    }

    private Intent makeAnIntent()
    {
        return new Intent(getApplicationContext() , TrainingActivity.class);
    }

    private void putValuesInIntent(Intent intent)
    {
        intent.putExtra("arrayListTrainingPlanExercises" , arrayListTrainingPlanExercises);
        intent.putExtra("trainingTime" , trainingTime);
    }

    private boolean isDataEnteredInEveryEditText()
    {
        return (checkIfEditTextTextIsNotEmpty(editTextWorkingTime)) && (checkIfEditTextTextIsNotEmpty(editTextRestTime)) &&
                (checkIfEditTextTextIsNotEmpty(editTextNumberOfSets)) && (checkIfEditTextTextIsNotEmpty(editTextPauseAfterSetTime));
    }

    private boolean checkIfEditTextTextIsNotEmpty(EditText editText)
    {
        return !(editText.getText().toString().equals(""));
    }

    public void buttonClickSetDefault(View view)
    {
        editTextWorkingTime.setText("40");
        editTextRestTime.setText("20");
        editTextPauseAfterSetTime.setText("120");
        editTextNumberOfSets.setText("5");
    }



    private class EditTextHelper implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable)
        {
            if(isDataEnteredInEveryEditText())
                calculateTrainingDuration();
        }
    }
}


