package com.trainticketing.model;

public class Train {
    private String trainNumber;
    private int capacity;
    private int delayMinutes;

    public Train() {

    }

    public Train(String trainNumber, int capacity) {
        this.trainNumber = trainNumber;
        this.capacity = capacity;
        this.delayMinutes = 0;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public boolean hasDelay() {
        return delayMinutes > 0;
    }

    @Override
    public String toString() {
        if (hasDelay()) {
            return trainNumber + " | capacity: " + capacity + " | delay: " + delayMinutes + " minutes";
        }
        return trainNumber + " | capacity: " + capacity;
    }
}