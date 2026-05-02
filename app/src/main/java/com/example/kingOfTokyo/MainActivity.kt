package com.example.robot_sp26

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var redRobotImg : ImageView
    private lateinit var whiteRobotImg : ImageView
    private lateinit var yellowRobotImg : ImageView
    private lateinit var messageBox : TextView
    private lateinit var diceResultText : TextView
    private lateinit var applyRollButton : Button
    private lateinit var purchaseButton : Button
    private lateinit var rollButton : Button
    private lateinit var redRobotCard : View
    private lateinit var whiteRobotCard : View
    private lateinit var yellowRobotCard : View
    private lateinit var redRobotEnergy : TextView
    private lateinit var whiteRobotEnergy : TextView
    private lateinit var yellowRobotEnergy : TextView
    private lateinit var robotImages : MutableList<ImageView>
    private var pendingLightningCount = 0
    private val robotViewModel : RobotViewModel by viewModels()
    private val robots = listOf(
        Robot(R.string.red_message_text, false,
            R.drawable.robot_red_large, R.drawable.robot_red_small),

        Robot(R.string.white_message_text, false,
            R.drawable.robot_white_large, R.drawable.robot_white_small),

        Robot(R.string.yellow_message_text, false,
            R.drawable.robot_yellow_large, R.drawable.robot_yellow_small)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d(TAG, "Entered onCreate(savedInstanceState: Bundle?)")
        Log.d(TAG, "Got a viewModel : $robotViewModel")

        redRobotImg = findViewById(R.id.red_robot)
        whiteRobotImg = findViewById(R.id.white_robot)
        yellowRobotImg = findViewById(R.id.yellow_robot)
        messageBox = findViewById(R.id.message_box)
        diceResultText = findViewById(R.id.dice_result_text)
        applyRollButton = findViewById(R.id.apply_roll_button)
        purchaseButton = findViewById(R.id.purchase_button)
        rollButton = findViewById(R.id.roll_button)
        redRobotCard = findViewById(R.id.red_robot_card)
        whiteRobotCard = findViewById(R.id.white_robot_card)
        yellowRobotCard = findViewById(R.id.yellow_robot_card)
        redRobotEnergy = findViewById(R.id.red_robot_energy)
        whiteRobotEnergy = findViewById(R.id.white_robot_energy)
        yellowRobotEnergy = findViewById(R.id.yellow_robot_energy)
        robotImages = mutableListOf(redRobotImg, whiteRobotImg, yellowRobotImg)

        diceResultText.visibility = View.GONE
        applyRollButton.visibility = View.GONE

        applyRollButton.setOnClickListener {
            robotViewModel.addEnergyFromRoll(pendingLightningCount)
            pendingLightningCount = 0
            applyRollButton.visibility = View.GONE
            updateEnergyDisplays()
        }

        if (robotViewModel.currentTurn != 0) {
            updateRobot()
        } else {
            updateRobotCardBackgrounds()
        }

        redRobotImg.setOnClickListener {
            toggleImage()
        }

        whiteRobotImg.setOnClickListener {
            toggleImage()
        }

        yellowRobotImg.setOnClickListener {
            toggleImage()
        }

        rollButton.setOnClickListener {
            val currentRobot = if (robotViewModel.currentTurn in 1..3) {
                robotViewModel.currentTurn
            } else {
                1
            }
            val intent = RollDice.newIntent(this, currentRobot)
            rollDiceLauncher.launch(intent)
        }

        purchaseButton.setOnClickListener {
            val currentEnergy = robotViewModel.getEnergy()
            val currentRobot = robotViewModel.currentTurn
            robotViewModel.shuffleRewards()
            val rewards = robotViewModel.selectedRewards
            val intent = RobotPurchase.newIntent(this, currentEnergy, currentRobot).apply {
                putExtra("REWARD_1_NAME", rewards[0].name)
                putExtra("REWARD_1_COST", rewards[0].cost)
                putExtra("REWARD_2_NAME", rewards[1].name)
                putExtra("REWARD_2_COST", rewards[1].cost)
                putExtra("REWARD_3_NAME", rewards[2].name)
                putExtra("REWARD_3_COST", rewards[2].cost)
            }
            robotPurchaseLauncher.launch(intent)
        }

    }// end onCreate
    
    private val rollDiceLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val rawResult = result.data?.getStringExtra(EXTRA_DICE_RESULT) ?: ""
                if (rawResult.isNotEmpty()) {
                    val faces = rawResult.split(",")
                    pendingLightningCount = faces.count { it == "⚡" || it == "⚡️" }
                    diceResultText.text = "Last roll: ${faces.joinToString("  ")}"
                    diceResultText.visibility = View.VISIBLE
                    applyRollButton.visibility = View.VISIBLE
                }
            }
        }

    private val robotPurchaseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val robotPurchaseMade = result.data?.getStringExtra(EXTRA_ROBOT_PURCHASE_MADE) ?: "0"
                if (robotPurchaseMade != "0") {
                    robotViewModel.spendEnergy(robotPurchaseMade.toInt())
                    if (robotPurchaseMade == "1") {
                        robotViewModel.addPurchase(robotViewModel.selectedRewards[0].name)
                    } else if (robotPurchaseMade == "2") {
                        robotViewModel.addPurchase(robotViewModel.selectedRewards[1].name)
                    } else if (robotPurchaseMade == "3") {
                        robotViewModel.addPurchase(robotViewModel.selectedRewards[2].name)
                    }
                }
            }

        }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun toggleImage() {
        robotViewModel.advanceTurn()
        updateRobot()
        val purchases = robotViewModel.getPurchases()
        if (purchases.size != 0) {
            Toast.makeText(this, "Purchases: $purchases", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRobot() {
        updateMessageBox()
        setRobotTurn()
        setRobotImages()
        updateRobotCardBackgrounds()
    }
    private fun updateMessageBox() {
        messageBox.setText(robots[robotViewModel.currentTurn - 1].robotMessageResource)
    }
    private fun setRobotTurn() {
        for (robot in robots) {
            robot.myTurn = false
        }
        robots[robotViewModel.currentTurn - 1].myTurn = true
    }

    private fun setRobotImages() {
        for (indy in 0 .. 2) {
            if (robots[indy].myTurn) {
                robotImages[indy].setImageResource(robots[indy].robotImageLarge)
            } else {
                robotImages[indy].setImageResource(robots[indy].robotImageSmall)
            }
        }
    }

    private fun updateEnergyDisplays() {
        val energies = robotViewModel.getAllEnergy()
        redRobotEnergy.text = "⚡️ ${energies[0]}"
        whiteRobotEnergy.text = "⚡️ ${energies[1]}"
        yellowRobotEnergy.text = "⚡️ ${energies[2]}"
    }

    private fun updateRobotCardBackgrounds() {
        redRobotCard.setBackgroundResource(
            if (robots[0].myTurn) R.drawable.die_kept_background else R.drawable.die_unkept_background
        )
        whiteRobotCard.setBackgroundResource(
            if (robots[1].myTurn) R.drawable.die_kept_background else R.drawable.die_unkept_background
        )
        yellowRobotCard.setBackgroundResource(
            if (robots[2].myTurn) R.drawable.die_kept_background else R.drawable.die_unkept_background
        )
    }
}