package com.example.tabatatimer;

import java.io.Serializable;

public class TrainingTime implements Serializable
{
    private int workTimeInSeconds , restTimeInSeconds , numberOfSets , pauseTimeInSeconds;
    private int numberOfExercises;

    private int timeForExerciseSeconds ,timeForSetSeconds , timeForTrainingSeconds;
    private int trainingDurationMin ,trainingDurationSec;

    public TrainingTime()
    {
        calculateDurationOfExerciseSetAndTraining();
    }

    public TrainingTime(int numberOfExercises)
    {
        this.numberOfExercises = numberOfExercises;
        calculateDurationOfExerciseSetAndTraining();
    }

    private void calculateDurationOfExerciseSetAndTraining()
    {
        calculateExerciseSetTraining();
        calculateTrainingDuration();
    }

    private void calculateExerciseSetTraining()
    {
        timeForExerciseSeconds = workTimeInSeconds + restTimeInSeconds;
        timeForSetSeconds = (timeForExerciseSeconds * numberOfExercises) + pauseTimeInSeconds;
        timeForTrainingSeconds = timeForSetSeconds * numberOfSets;
    }

    private void calculateTrainingDuration()
    {
        trainingDurationMin = convertTimeToMinutesAndSeconds(timeForTrainingSeconds)[0];
        trainingDurationSec = convertTimeToMinutesAndSeconds(timeForTrainingSeconds)[1];
    }



    private int[] convertTimeToMinutesAndSeconds(int seconds)
    {
        int time[] = new int[2];
        time[0] = seconds / 60;
        time[1] = seconds % 60;

        return time;
    }


    public int getWorkTimeInSeconds() {
        return workTimeInSeconds;
    }

    public int getRestTimeInSeconds() {
        return restTimeInSeconds;
    }

    public int getNumberOfSets() {
        return numberOfSets;
    }

    public int getPauseTimeInSeconds() {
        return pauseTimeInSeconds;
    }

    public void setWorkTimeInSeconds(int workTimeInSeconds) {
        this.workTimeInSeconds = workTimeInSeconds;
        calculateDurationOfExerciseSetAndTraining();
    }

    public void setRestTimeInSeconds(int restTimeInSeconds) {
        this.restTimeInSeconds = restTimeInSeconds;
        calculateDurationOfExerciseSetAndTraining();
    }

    public void setNumberOfSets(int numberOfSets) {
        this.numberOfSets = numberOfSets;
        calculateDurationOfExerciseSetAndTraining();
    }

    public void setPauseTimeInSeconds(int pauseTimeInSeconds) {
        this.pauseTimeInSeconds = pauseTimeInSeconds;
        calculateDurationOfExerciseSetAndTraining();
    }


    public int getTimeForExerciseSeconds() {
        return timeForExerciseSeconds;
    }

    public int getTimeForSetSeconds() {
        return timeForSetSeconds;
    }

    public int getTimeForTrainingSeconds() {
        return timeForTrainingSeconds;
    }

    public int getTrainingDurationMin() {
        return trainingDurationMin;
    }

    public int getTrainingDurationSec() {
        return trainingDurationSec;
    }

    public int getNumberOfExercises() {
        return numberOfExercises;
    }


}

