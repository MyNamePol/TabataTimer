package com.example.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;

public class TrainingSettings extends AppCompatActivity
{

    private int numberOfExercises;

    private EditText editTextNumberOfExercises;
    private ArrayList<Spinner> arrayListSpinnerExercises;
    private LinearLayout linearLayoutSpinners;

    protected ArrayList<String> arrayListAllPossibleExercises;
    private ArrayAdapter<String> arrayAdapterSpinners;
    private ArrayList<String> arrayListTrainingPlanExercises;

    protected SQLiteDatabase sqLiteDatabaseExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_settings);

        createOrOpenDatabase();
        initializeVariablesAndViews();
    }

    protected void createOrOpenDatabase()
    {
        sqLiteDatabaseExercises = this.openOrCreateDatabase("Exercises" , MODE_PRIVATE , null);
        sqLiteDatabaseExercises.execSQL("CREATE TABLE IF NOT EXISTS exercises (ID INTEGER NOT NULL PRIMARY KEY , name VARCHAR)");
    }

    private void initializeVariablesAndViews()
    {
        if(isDBEmpty())
            fillDataBaseWithExercises();

        initializeViews();
        initializeArrayLists();

        takeDataFromDataBaseAndPutInArrayList();
        setEditTextTextChangedListener();
    }

    private boolean isDBEmpty()
    {
        Cursor cursor = getCursorForAllExercises();

        return cursor == null || cursor.getCount() <= 0;
    }

    private Cursor getCursorForAllExercises()
    {
        return sqLiteDatabaseExercises.rawQuery("SELECT * FROM exercises" , null);
    }

    private void fillDataBaseWithExercises()
    {
        sqLiteDatabaseExercises.execSQL("INSERT INTO exercises (name) VALUES ('Push up')");
        sqLiteDatabaseExercises.execSQL("INSERT INTO exercises (name) VALUES ('Pull up')");
    }


    private void initializeViews()
    {
        editTextNumberOfExercises = findViewById(R.id.editTextNumberOfExercises);
        linearLayoutSpinners = findViewById(R.id.linearLayoutSpinners);
    }

    private void initializeArrayLists()
    {
        arrayListSpinnerExercises = new ArrayList<>();
        arrayListAllPossibleExercises = new ArrayList<>();
    }


    protected void takeDataFromDataBaseAndPutInArrayList()
    {
        Cursor cursor = getCursorForAllExercises();

        arrayListAllPossibleExercises.clear();

        int nameIndex = cursor.getColumnIndex("name");

        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                arrayListAllPossibleExercises.add(cursor.getString(nameIndex));
            }
            while (cursor.moveToNext());
        }
    }

    private void setEditTextTextChangedListener()
    {
        editTextNumberOfExercises.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable)
            {
                startMakingTrainingPlan();
            }
        });
    }

    private void startMakingTrainingPlan()
    {
        prepareViewsForChoosingExercises();
        createSpinnerAndHisAdapterAndListener();
        addSpinnersToLinearLayout();
    }

    private void prepareViewsForChoosingExercises()
    {
        setNumberOfExercises();
        setArrayListTrainingPartSTARTPosition(numberOfExercises);
        clearSpinnersLists();
    }

    private void setNumberOfExercises()
    {
        String textNumberOfExercises = takeNumberExercisesText();

        try
        {
            convertNumberOfExercises(textNumberOfExercises);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String takeNumberExercisesText()
    {
        String textNumberOfExercises = editTextNumberOfExercises.getText().toString();
        textNumberOfExercises.trim();
        return textNumberOfExercises;
    }

    private void convertNumberOfExercises(String textNumberOfExercises)
    {
        numberOfExercises = Integer.valueOf(textNumberOfExercises);
    }

    private void setArrayListTrainingPartSTARTPosition(int trainingPlanExercisesSize)
    {
        arrayListTrainingPlanExercises = new ArrayList<>();
        setDefaultValuesForArrayListTrainingPlan(trainingPlanExercisesSize);
    }

    private void setDefaultValuesForArrayListTrainingPlan(int trainingPlanExercisesSize)
    {
        for(int i = 0; i < trainingPlanExercisesSize; ++i)
            arrayListTrainingPlanExercises.add("Push up");
    }

    private void clearSpinnersLists()
    {
        arrayListSpinnerExercises.clear();
        linearLayoutSpinners.removeAllViews();
    }




    private void createSpinnerAndHisAdapterAndListener()
    {
        for(int indexOfSpinner = 0; indexOfSpinner < numberOfExercises; ++indexOfSpinner)
        {
            createSpinnerAndAddItToLinearLayout();
            createAdapterAndSetItToSpinner(indexOfSpinner);
            setListenerForSpinnerForExerciseChoice(indexOfSpinner ,arrayListSpinnerExercises.get(indexOfSpinner));
        }
    }


    private void createSpinnerAndAddItToLinearLayout()
    {
        final Spinner tempSpinner = new Spinner(this);
        addSpinnerToArrayListSpinners(tempSpinner);
    }

    private void addSpinnerToArrayListSpinners(Spinner spinner)
    {
        arrayListSpinnerExercises.add((spinner));
    }



    private void createAdapterAndSetItToSpinner(int indexOfSpinner)
    {
        arrayAdapterSpinners = createAdapterForSpinners();
        setAdapterForSpinner(indexOfSpinner);
    }

    private ArrayAdapter createAdapterForSpinners()
    {
        arrayAdapterSpinners = new ArrayAdapter<>(getApplicationContext() , R.layout.my_spinner_drodown_item , arrayListAllPossibleExercises);
        arrayAdapterSpinners.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapterSpinners;
    }

    private void setAdapterForSpinner(int indexOfSpinner)
    {
        arrayListSpinnerExercises.get(indexOfSpinner).setAdapter(arrayAdapterSpinners);
    }



    private void setListenerForSpinnerForExerciseChoice(final int numberOfSpinnerInLinearLayout , final Spinner spinner)
    {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                setExerciseSpinnerChose(spinner , numberOfSpinnerInLinearLayout);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            { }
        });
    }

    private void setExerciseSpinnerChose(Spinner spinner , int numberOfSpinnerInLinearLayout)
    {
        int position = spinner.getSelectedItemPosition();
        setTrainingPlan(numberOfSpinnerInLinearLayout , arrayListAllPossibleExercises.get(position));
    }

    private void setTrainingPlan(int i , String exercise)
    {
        arrayListTrainingPlanExercises.set(i ,exercise);
    }




    private void addSpinnersToLinearLayout()
    {
        for(int i = 0; i < arrayListSpinnerExercises.size(); ++i)
            linearLayoutSpinners.addView(arrayListSpinnerExercises.get(i));
    }




    public void buttonClickSeeExercises(View view)
    {
        Intent intent = new Intent(getApplicationContext() ,  ExerciseListActivity.class);
        startActivity(intent);
    }

    public void buttonClickSetTime(View view)
    {
        if(isDataEnteredInEditTextNumberOfExercises())
        {
            Intent intent = new Intent(this , TimeActivity.class);
            intent.putExtra("numberOfExercises" , numberOfExercises);
            intent.putExtra("arrayListTrainingPlanExercises" , arrayListTrainingPlanExercises);

            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext() , "You did not enter data in number of exercises field ! " , Toast.LENGTH_LONG).show();
    }

    private boolean isDataEnteredInEditTextNumberOfExercises()
    {
        return !(editTextNumberOfExercises.getText().toString().equals(""));
    }

}
