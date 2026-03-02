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
    private var robotEnergy = mutableListOf(0, 0, 0)
    private var lastPurchase = mutableListOf<String?>(null, null, null)
    val currentTurn : Int
        get() = turnCount

    override fun onCleared() {
        super.onCleared()
    }

    fun advanceTurn() {
        turnCount++
        if (turnCount > 3) {
            turnCount = 1
        }
        robotEnergy[turnCount - 1]++
    }

    fun getEnergy() : Int {
        return robotEnergy[turnCount - 1]
    }

    fun spendEnergy(amt: Int) {
        robotEnergy[turnCount - 1] -= amt
    }

    fun setLastPurchase(purchase: String) {
        lastPurchase[turnCount - 1] = purchase
    }

    fun getLastPurchase(): String? {
        return lastPurchase[turnCount - 1]
    }
}