package com.example.robot_sp26

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

const val EXTRA_ROLL_DICE_ROBOT = "EXTRA_ROLL_DICE_ROBOT"

class RollDice : AppCompatActivity() {

    private lateinit var robotImage: ImageView
    private lateinit var robotTurnLabel: TextView
    private lateinit var die1: TextView
    private lateinit var die2: TextView
    private lateinit var die3: TextView
    private lateinit var die4: TextView
    private lateinit var die5: TextView
    private lateinit var die6: TextView
    private lateinit var rollAgainButton: Button
    private lateinit var doneButton: Button
    private lateinit var rollCountText: TextView
    private lateinit var rollInstructionText: TextView
    private lateinit var diceViews: List<TextView>

    private val diceFaces = listOf("1", "2", "3", "⚡", "❤", "💥")
    private val diceValues = MutableList(6) { "" }
    private val keptDice = MutableList(6) { false }
    private var rollCount = 0
    private val maxRolls = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll_dice)

        robotImage = findViewById(R.id.roll_dice_robot_image)
        robotTurnLabel = findViewById(R.id.roll_dice_robot_label)
        die1 = findViewById(R.id.die_1)
        die2 = findViewById(R.id.die_2)
        die3 = findViewById(R.id.die_3)
        die4 = findViewById(R.id.die_4)
        die5 = findViewById(R.id.die_5)
        die6 = findViewById(R.id.die_6)
        rollAgainButton = findViewById(R.id.roll_again_button)
        doneButton = findViewById(R.id.roll_done_button)
        rollCountText = findViewById(R.id.roll_count_text)
        rollInstructionText = findViewById(R.id.roll_instruction_text)

        diceViews = listOf(die1, die2, die3, die4, die5, die6)

        val currentRobot = intent.getIntExtra(EXTRA_ROLL_DICE_ROBOT, 1).let {
            if (it in 1..3) it else 1
        }

        when (currentRobot) {
            1 -> {
                robotImage.setImageResource(R.drawable.robot_red_large)
                robotTurnLabel.text = getString(R.string.red_message_text)
            }
            2 -> {
                robotImage.setImageResource(R.drawable.robot_white_large)
                robotTurnLabel.text = getString(R.string.white_message_text)
            }
            else -> {
                robotImage.setImageResource(R.drawable.robot_yellow_large)
                robotTurnLabel.text = getString(R.string.yellow_message_text)
            }
        }

        setupDiceToggleHandlers()
        rollAllDice()

        rollAgainButton.setOnClickListener {
            rollAllDice()
        }

        doneButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        Toast.makeText(
            this,
            "Yahtzee-style: tap dice to keep them, then reroll remaining dice up to 3 times.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun rollAllDice() {
        if (rollCount >= maxRolls) {
            return
        }

        for (index in diceViews.indices) {
            if (!keptDice[index]) {
                diceValues[index] = diceFaces.random()
                diceViews[index].text = diceValues[index]
            }
        }

        rollCount++
        updateRollUi()
    }

    private fun setupDiceToggleHandlers() {
        for (index in diceViews.indices) {
            diceViews[index].setOnClickListener {
                keptDice[index] = !keptDice[index]
                applyDieStateVisual(index)
            }
            applyDieStateVisual(index)
        }
    }

    private fun applyDieStateVisual(index: Int) {
        val dieView = diceViews[index]
        if (keptDice[index]) {
            dieView.setBackgroundResource(R.drawable.die_kept_background)
            dieView.alpha = 0.95f
        } else {
            dieView.setBackgroundResource(R.drawable.die_unkept_background)
            dieView.alpha = 1.0f
        }
    }

    private fun updateRollUi() {
        rollCountText.text = "Roll $rollCount of $maxRolls"

        if (rollCount >= maxRolls) {
            rollAgainButton.isEnabled = false
            rollAgainButton.text = "No Rolls Left"
            rollInstructionText.text = "Final roll reached. Tap Done to continue."
        } else {
            val rollsLeft = maxRolls - rollCount
            rollAgainButton.isEnabled = true
            rollAgainButton.text = "Roll Remaining Dice"
            rollInstructionText.text =
                "Tap dice to keep them static, then roll again. Rolls left: $rollsLeft"
        }
    }

    companion object {
        fun newIntent(context: Context, currentRobot: Int): Intent {
            return Intent(context, RollDice::class.java).apply {
                putExtra(EXTRA_ROLL_DICE_ROBOT, currentRobot)
            }
        }
    }
}
