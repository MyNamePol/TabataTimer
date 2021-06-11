package com.example.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ExerciseListActivity extends TrainingSettings
{

    private ArrayAdapter<String> arrayAdapter;

    private ListView listViewExercise;

    private String newExerciseName = "Null";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        initializeVariablesAndViews();
    }

    private void initializeVariablesAndViews()
    {
        arrayListAllPossibleExercises = new ArrayList<>();

        createOrOpenDatabase();
        takeDataFromDataBaseAndPutInArrayList();
        createArrayAdapterAndListViewThenConnectThem();

    }

    private void createArrayAdapterAndListViewThenConnectThem()
    {
        listViewExercise = findViewById(R.id.listViewExercises);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext() , R.layout.my_spinner_drodown_item , arrayListAllPossibleExercises);
        listViewExercise.setAdapter(arrayAdapter);
    }

    public void buttonClickAddNewExercise(View view)
    {
        insertNewExerciseInTheSystem();
    }

    private EditText editTextNewExercises;
    private AlertDialog.Builder alertForInsertingNewExercise;

    private void insertNewExerciseInTheSystem()
    {
        createAlertDialogAndEditText();
        setAlertDialogButtons(alertForInsertingNewExercise , editTextNewExercises);

        showAlertDialogBuilder(alertForInsertingNewExercise);
    }

    private void createAlertDialogAndEditText()
    {
         editTextNewExercises = prepareEditTextForInsertingData();
         alertForInsertingNewExercise = prepareAlertDialogForInsertingData(editTextNewExercises);
    }

    private EditText prepareEditTextForInsertingData()
    {
        EditText editTextData = new EditText(getApplicationContext());
        editTextData.setMaxLines(1);
        editTextData.setSingleLine(true);
        return editTextData;
    }

    private AlertDialog.Builder prepareAlertDialogForInsertingData(EditText editText)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(editText);
        return alert;
    }

    private void setAlertDialogButtons(AlertDialog.Builder alert , EditText editTextNewExercises)
    {
        setAlertDialogBuilderPositiveButton(alert , editTextNewExercises);
        setAlertDialogBuilderNegativeButton(alert);
    }

    private void setAlertDialogBuilderPositiveButton(AlertDialog.Builder alertDialog , EditText editTextData)
    {
        final EditText editText = editTextData;

        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                setNewExerciseName(editText.getText().toString());

                if(isSomethingInEditTextForEnteringExercises())
                    addNewExerciseToTheDatabaseAndListView();

            }
        });
    }

    private void setAlertDialogBuilderNegativeButton(AlertDialog.Builder alertDialog)
    {
        alertDialog.setNegativeButton("CANCLE" , null);
    }

    private void addNewExerciseToTheDatabaseAndListView()
    {
        makeAndExecuteSQLStatementForInsertingNewExerciseInDatabase();

        takeDataFromDataBaseAndPutInArrayList();

        setNewListViewUsingArrayAdapterAndArrayList();
    }

    private void makeAndExecuteSQLStatementForInsertingNewExerciseInDatabase()
    {
        String sqlStatement = getCommandForInsertingNewExercise();
        SQLiteStatement statement = compileStatement(sqlStatement);

        statement.bindString(1, newExerciseName);
        statement.execute();
    }

    private String getCommandForInsertingNewExercise()
    {
        return "INSERT INTO exercises (name) VALUES (?)";
    }

    private SQLiteStatement compileStatement(String sqlStatement)
    {
        return   sqLiteDatabaseExercises.compileStatement(sqlStatement);
    }

    private void setNewListViewUsingArrayAdapterAndArrayList()
    {
        listViewExercise = findViewById(R.id.listViewExercises);
        arrayAdapter = new ArrayAdapter<>(this , R.layout.my_spinner_drodown_item , arrayListAllPossibleExercises);
        listViewExercise.setAdapter(arrayAdapter);
    }

    private boolean isSomethingInEditTextForEnteringExercises()
    {
        return !newExerciseName.equals("");
    }


    private void showAlertDialogBuilder(AlertDialog.Builder alertDialog)
    {
        alertDialog.show();
    }


    public void setNewExerciseName(String newExerciseName) {
        this.newExerciseName = newExerciseName;
    }

}
