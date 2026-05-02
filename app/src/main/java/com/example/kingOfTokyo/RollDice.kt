package com.example.robot_sp26

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

    // The six possible die faces in King of Tokyo
    private val diceFaces = listOf("1", "2", "3", "⚡", "❤", "💥")

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

        val currentRobot = intent.getIntExtra(EXTRA_ROLL_DICE_ROBOT, 1)

        // Show the current robot's image and label — same pattern as RobotPurchase
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

        rollAllDice()

        rollAgainButton.setOnClickListener {
            rollAllDice()
        }

        doneButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun rollAllDice() {
        die1.text = diceFaces.random()
        die2.text = diceFaces.random()
        die3.text = diceFaces.random()
        die4.text = diceFaces.random()
        die5.text = diceFaces.random()
        die6.text = diceFaces.random()
    }

    companion object {
        fun newIntent(context: Context, currentRobot: Int): Intent {
            return Intent(context, RollDice::class.java).apply {
                putExtra(EXTRA_ROLL_DICE_ROBOT, currentRobot)
            }
        }
    }
}
