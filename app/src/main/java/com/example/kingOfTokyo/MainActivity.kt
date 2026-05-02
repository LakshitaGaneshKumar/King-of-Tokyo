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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var redRobotImg : ImageView
    private lateinit var whiteRobotImg : ImageView
    private lateinit var yellowRobotImg : ImageView
    private lateinit var tokyoRobotImg : ImageView
    private lateinit var messageBox : TextView
    private lateinit var tokyoOccupantText : TextView
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
    private lateinit var redRobotHealth : TextView
    private lateinit var whiteRobotHealth : TextView
    private lateinit var yellowRobotHealth : TextView
    private lateinit var redRobotVP : TextView
    private lateinit var whiteRobotVP : TextView
    private lateinit var yellowRobotVP : TextView
    private lateinit var robotImages : MutableList<ImageView>
    private var pendingLightningCount = 0
    private var pendingVPCount = 0
    private var pendingAttackCount = 0
    private var hasRolledThisTurn = false
    private var awaitingTokyoChoice = false
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
        tokyoRobotImg = findViewById(R.id.tokyo_robot_image)
        messageBox = findViewById(R.id.message_box)
        tokyoOccupantText = findViewById(R.id.tokyo_occupant)
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
        redRobotHealth = findViewById(R.id.red_robot_health)
        whiteRobotHealth = findViewById(R.id.white_robot_health)
        yellowRobotHealth = findViewById(R.id.yellow_robot_health)
        redRobotVP = findViewById(R.id.red_robot_vp)
        whiteRobotVP = findViewById(R.id.white_robot_vp)
        yellowRobotVP = findViewById(R.id.yellow_robot_vp)
        robotImages = mutableListOf(redRobotImg, whiteRobotImg, yellowRobotImg)

        diceResultText.visibility = View.GONE
        applyRollButton.visibility = View.GONE
        updateRollButtonState()

        applyRollButton.setOnClickListener {
            val currentTurn = robotViewModel.currentTurn
            robotViewModel.addEnergyFromRoll(pendingLightningCount)
            robotViewModel.addVictoryPoints(pendingVPCount)
            val attackOutcome = robotViewModel.applyAttackFromRoll(pendingAttackCount)
            if (robotViewModel.enterTokyoIfEmpty(currentTurn)) {
                Toast.makeText(
                    this,
                    "${getRobotName(currentTurn)} entered Tokyo!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            pendingLightningCount = 0
            pendingVPCount = 0
            pendingAttackCount = 0
            applyRollButton.visibility = View.GONE
            hasRolledThisTurn = false
            updateEnergyDisplays()
            updateHealthDisplays()
            updateTokyoOccupantDisplay()

            if (attackOutcome.attackerWasOutsideTokyo && attackOutcome.tokyoOccupantDamagedTurn in 1..3) {
                showTokyoLeavePrompt(
                    damagedRobotTurn = attackOutcome.tokyoOccupantDamagedTurn,
                    attackerTurn = currentTurn
                )
            } else {
                finishTurnAfterApply()
            }
        }

        updateTokyoOccupantDisplay()
        updateHealthDisplays()

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
            hasRolledThisTurn = true
            updateRollButtonState()
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
                    pendingVPCount = calculateVPFromDice(faces)
                    pendingAttackCount = faces.count { it == "💥" }
                    diceResultText.text = "Last roll: ${faces.joinToString("  ")}"
                    diceResultText.visibility = View.VISIBLE
                    applyRollButton.visibility = View.VISIBLE
                    updateRollButtonState()
                } else {
                    hasRolledThisTurn = false
                    pendingLightningCount = 0
                    pendingVPCount = 0
                    pendingAttackCount = 0
                    updateRollButtonState()
                }
            } else {
                hasRolledThisTurn = false
                pendingLightningCount = 0
                pendingVPCount = 0
                pendingAttackCount = 0
                updateRollButtonState()
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
        if (robotViewModel.currentTurn in 1..3 && !hasRolledThisTurn) {
            Toast.makeText(this, "Roll dice before ending your turn", Toast.LENGTH_SHORT).show()
            return
        }
        if (applyRollButton.visibility == View.VISIBLE) {
            Toast.makeText(this, "Apply roll before ending your turn", Toast.LENGTH_SHORT).show()
            return
        }
        robotViewModel.advanceTurn()
        hasRolledThisTurn = false
        applyRollButton.visibility = View.GONE
        updateRollButtonState()
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

    private fun calculateVPFromDice(faces: List<String>): Int {
        var totalVP = 0
        for (number in listOf("1", "2", "3")) {
            val count = faces.count { it == number }
            if (count >= 3) {
                totalVP += number.toInt() + (count - 3)
            }
        }
        return totalVP
    }

    private fun updateEnergyDisplays() {
        val energies = robotViewModel.getAllEnergy()
        redRobotEnergy.text = "⚡️ ${energies[0]}"
        whiteRobotEnergy.text = "⚡️ ${energies[1]}"
        yellowRobotEnergy.text = "⚡️ ${energies[2]}"
        updateVPDisplays()
    }

    private fun updateVPDisplays() {
        val vps = robotViewModel.getAllVictoryPoints()
        redRobotVP.text = "🔷 ${vps[0]} VP"
        whiteRobotVP.text = "🔷 ${vps[1]} VP"
        yellowRobotVP.text = "🔷 ${vps[2]} VP"
    }

    private fun updateHealthDisplays() {
        val health = robotViewModel.getAllHealth()
        redRobotHealth.text = "❤️ ${health[0]}"
        whiteRobotHealth.text = "❤️ ${health[1]}"
        yellowRobotHealth.text = "❤️ ${health[2]}"
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

    private fun updateRollButtonState() {
        val isRobotTurnActive = robotViewModel.currentTurn in 1..3
        val isApplyVisible = applyRollButton.visibility == View.VISIBLE
        rollButton.isEnabled =
            isRobotTurnActive && !isApplyVisible && !hasRolledThisTurn && !awaitingTokyoChoice
    }

    private fun updateTokyoOccupantDisplay() {
        val occupantTurn = robotViewModel.getTokyoOccupantTurn()
        if (occupantTurn in 1..3) {
            tokyoOccupantText.text = "Occupied by ${getRobotName(occupantTurn)}"
            tokyoRobotImg.visibility = View.VISIBLE
            tokyoRobotImg.setImageResource(
                when (occupantTurn) {
                    1 -> R.drawable.robot_red_small
                    2 -> R.drawable.robot_white_small
                    3 -> R.drawable.robot_yellow_small
                    else -> R.drawable.robot_red_small
                }
            )
        } else {
            tokyoOccupantText.text = getString(R.string.tokyo_occupant)
            tokyoRobotImg.visibility = View.GONE
        }
    }

    private fun getRobotName(turn: Int): String {
        return when (turn) {
            1 -> getString(R.string.red_robot)
            2 -> getString(R.string.white_robot)
            3 -> getString(R.string.yellow_robot)
            else -> "Robot"
        }
    }

    private fun showTokyoLeavePrompt(damagedRobotTurn: Int, attackerTurn: Int) {
        awaitingTokyoChoice = true
        updateRollButtonState()

        val damagedRobotName = getRobotName(damagedRobotTurn)
        val attackerName = getRobotName(attackerTurn)

        AlertDialog.Builder(this)
            .setTitle("Tokyo Decision")
            .setMessage("$damagedRobotName took damage in Tokyo. Leave Tokyo?")
            .setPositiveButton("Leave") { _, _ ->
                robotViewModel.vacateTokyo(damagedRobotTurn)
                robotViewModel.forceEnterTokyo(attackerTurn)
                Toast.makeText(
                    this,
                    "$damagedRobotName left Tokyo. $attackerName entered Tokyo!",
                    Toast.LENGTH_SHORT
                ).show()
                awaitingTokyoChoice = false
                updateTokyoOccupantDisplay()
                finishTurnAfterApply()
            }
            .setNegativeButton("Stay") { _, _ ->
                Toast.makeText(this, "$damagedRobotName stayed in Tokyo", Toast.LENGTH_SHORT).show()
                awaitingTokyoChoice = false
                updateTokyoOccupantDisplay()
                finishTurnAfterApply()
            }
            .setCancelable(false)
            .show()
    }

    private fun finishTurnAfterApply() {
        robotViewModel.advanceTurn()
        updateRobot()
        updateRollButtonState()
    }
}