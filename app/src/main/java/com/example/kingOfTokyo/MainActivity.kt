package com.example.robot_sp26

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    // wire up the message_box from the activity_main.xml file
    private lateinit var messageBox : TextView

    private lateinit var purchaseButton : Button
    private lateinit var rollButton : Button
    private lateinit var robotImages : MutableList<ImageView>
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

        purchaseButton = findViewById(R.id.purchase_button)
        rollButton = findViewById(R.id.roll_button)
        robotImages = mutableListOf(redRobotImg, whiteRobotImg, yellowRobotImg)
        if (robotViewModel.currentTurn != 0) {
            updateRobot()
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
            val currentRobot = robotViewModel.currentTurn
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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            // dice result returned — no extra data needed for now
        }

    private val robotPurchaseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // TODO do something with the data
                val robotPurchaseMade = result.data?.getStringExtra(EXTRA_ROBOT_PURCHASE_MADE) ?: "0"

                // HW 3a
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
                //Toast.makeText(this, "Purchase Made: ${robotPurchaseMade}", Toast.LENGTH_SHORT).show()
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
}