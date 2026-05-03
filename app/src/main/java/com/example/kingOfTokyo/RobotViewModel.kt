package com.example.robot_sp26

import android.util.Log
import androidx.lifecycle.ViewModel

// you have to do the imports first before you do your constants
// since this is private, this TAG doesn't got confused with the other TAG in MainActivity.kt
private const val TAG = "RobotViewModel"

data class AttackOutcome(
    val tokyoOccupantDamagedTurn: Int = 0,
    val attackerWasOutsideTokyo: Boolean = false
)

// RobotViewModel is going to be a subclass/child class of ViewModel
class RobotViewModel : ViewModel() {
    // this is where we do a little more than what the ViewModel parent class does
    init {
        Log.d(TAG, "ViewModel instance created")
    }
    private var turnCount = 0
    private var tokyoOccupantTurn = 0
    private var robotHealth = mutableListOf(10, 10, 10)
    private var robotEnergy = mutableListOf(0, 0, 0)
    private var robotVictoryPoints = mutableListOf(0, 0, 0)

    //private var lastPurchase = mutableListOf<String?>(null, null, null)
    private var purchases = mutableListOf(
        mutableListOf<String?>(),
        mutableListOf<String?>(),
        mutableListOf<String?>()
    )

    private val allRewards = listOf(
        Rewards("Reward A", 1),
        Rewards("Reward B", 2),
        Rewards("Reward C", 3),
        Rewards("Reward D", 3),
        Rewards("Reward E", 4),
        Rewards("Reward F", 4),
        Rewards("Reward G", 7),
    )
    var selectedRewards = allRewards.shuffled().take(3).sortedWith(
        compareBy({ it.name })
    )

    fun shuffleRewards() {
        selectedRewards = allRewards.shuffled().take(3).sortedWith(
            compareBy({ it.name })
        )
    }

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
    }

    fun addEnergyFromRoll(lightningCount: Int) {
        if (turnCount in 1..3 && lightningCount > 0) {
            robotEnergy[turnCount - 1] += lightningCount
        }
    }

    fun applyHealingFromRoll(heartCount: Int) {
        if (heartCount <= 0 || turnCount !in 1..3) {
            return
        }
        if (tokyoOccupantTurn == turnCount) {
            return
        }

        val index = turnCount - 1
        robotHealth[index] = (robotHealth[index] + heartCount).coerceAtMost(10)
    }

    fun getEnergy() : Int {
        return robotEnergy[turnCount - 1]
    }

    fun getAllEnergy(): List<Int> = robotEnergy.toList()

    fun getAllHealth(): List<Int> = robotHealth.toList()

    fun getAllVictoryPoints(): List<Int> = robotVictoryPoints.toList()

    fun addVictoryPoints(amount: Int) {
        if (turnCount in 1..3) {
            robotVictoryPoints[turnCount - 1] += amount
        }
    }

    fun spendEnergy(amt: Int) {
        robotEnergy[turnCount - 1] -= amt
    }

    fun addPurchase(purchase: String) {
        purchases[turnCount - 1].add(purchase)
    }

    fun getPurchases(): MutableList<String?> {
        return purchases[turnCount - 1]
    }

    fun getTokyoOccupantTurn(): Int = tokyoOccupantTurn

    fun isTokyoOccupied(): Boolean = tokyoOccupantTurn in 1..3

    fun enterTokyoIfEmpty(turn: Int): Boolean {
        if (!isTokyoOccupied() && turn in 1..3) {
            tokyoOccupantTurn = turn
            return true
        }
        return false
    }

    fun applyAttackFromRoll(attackCount: Int): AttackOutcome {
        if (attackCount <= 0 || turnCount !in 1..3) {
            return AttackOutcome()
        }

        if (tokyoOccupantTurn == turnCount) {
            for (targetTurn in 1..3) {
                if (targetTurn != turnCount) {
                    damageRobot(targetTurn, attackCount)
                }
            }
            return AttackOutcome()
        }

        if (tokyoOccupantTurn in 1..3) {
            damageRobot(tokyoOccupantTurn, attackCount)
            return AttackOutcome(
                tokyoOccupantDamagedTurn = tokyoOccupantTurn,
                attackerWasOutsideTokyo = true
            )
        }

        return AttackOutcome()
    }

    fun vacateTokyo(turn: Int): Boolean {
        if (tokyoOccupantTurn == turn) {
            tokyoOccupantTurn = 0
            return true
        }
        return false
    }

    fun forceEnterTokyo(turn: Int) {
        if (turn in 1..3) {
            tokyoOccupantTurn = turn
        }
    }

    private fun damageRobot(targetTurn: Int, damage: Int) {
        val index = targetTurn - 1
        robotHealth[index] = (robotHealth[index] - damage).coerceAtLeast(0)
    }
}