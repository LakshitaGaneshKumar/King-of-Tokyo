package com.example.robot_sp26

import android.util.Log
import androidx.lifecycle.ViewModel

// you have to do the imports first before you do your consts
// since this is private, this TAG doesn't got confused with the other TAG in MainActivity.kt
private const val TAG = "RobotViewModel"

// RobotViewModel is going to be a subclass/child class of ViewModel
class RobotViewModel : ViewModel() {
    // this is where we do a little more than what the ViewModel parent class does
    init {
        Log.d(TAG, "ViewModel instance created")
    }
    private var turnCount = 0

    val currentTurn : Int
        get() = turnCount

    fun advanceTurn() {
        turnCount++
        if (turnCount > 3) {
            turnCount = 1
        }
    }
}