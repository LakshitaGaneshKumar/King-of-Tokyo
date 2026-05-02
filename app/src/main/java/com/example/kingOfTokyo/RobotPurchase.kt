package com.example.robot_sp26

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_ROBOT_ENERGY = "com.example.robot_sp2.ROBOT_ENERGY" // reverse DNS for name collisions
/*private*/ const val EXTRA_ROBOT_PURCHASE_MADE = "EXTRA_ROBOT_PURCHASE_MADE" // this is another example of the key:value pair
const val EXTRA_CURRENT_ROBOT = "EXTRA_CURRENT_ROBOT"
class RobotPurchase : AppCompatActivity() {
    private lateinit var reward_button_a : Button
    private lateinit var reward_button_b : Button
    private lateinit var reward_button_c : Button
    private lateinit var robot_energy_available : TextView
    private lateinit var robotImage : ImageView
    private lateinit var reward_a_cost : TextView
    private lateinit var reward_b_cost : TextView
    private lateinit var reward_c_cost : TextView
    private var robot_energy = -1
//    private val allRewards = listOf(
//        Rewards("Reward A", 1),
//        Rewards("Reward B", 2),
//        Rewards("Reward C", 3),
//        Rewards("Reward D", 3),
//        Rewards("Reward E", 4),
//        Rewards("Reward F", 4),
//        Rewards("Reward G", 7),
//    )
//    val selectedRewards = allRewards.shuffled().take(3).sortedWith(
//        compareBy({ it.name })
//    )
    private lateinit var rewards: List<Rewards>

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_purchase)

        reward_button_a = findViewById(R.id.buy_reward_a)
        reward_button_b = findViewById(R.id.buy_reward_b)
        reward_button_c = findViewById(R.id.buy_reward_c)
        robot_energy_available = findViewById(R.id.robot_energy_to_spend)

        robotImage = findViewById(R.id.current_robot)

        reward_a_cost = findViewById(R.id.reward_a_cost)
        reward_b_cost = findViewById(R.id.reward_b_cost)
        reward_c_cost = findViewById(R.id.reward_c_cost)

      //  robot_energy = 2 // temporary hardcoded for testing
        robot_energy = intent.getIntExtra(EXTRA_ROBOT_ENERGY, 0)
        val current_robot = intent.getIntExtra(EXTRA_CURRENT_ROBOT, 1)
        if (current_robot == 1) {
            robotImage.setImageResource(R.drawable.robot_red_large)
        } else if (current_robot == 2) {
            robotImage.setImageResource(R.drawable.robot_white_large)
        } else if (current_robot == 3) {
            robotImage.setImageResource(R.drawable.robot_yellow_large)
        }

        robot_energy_available.setText(robot_energy.toString())

        rewards = listOf(
            Rewards(
                intent.getStringExtra("REWARD_1_NAME") ?: "Reward A",
                intent.getIntExtra("REWARD_1_COST", 1)
            ),
            Rewards(
                intent.getStringExtra("REWARD_2_NAME") ?: "Reward B",
                intent.getIntExtra("REWARD_2_COST", 2)
            ),
            Rewards(
                intent.getStringExtra("REWARD_3_NAME") ?: "Reward C",
                intent.getIntExtra("REWARD_3_COST", 3)
            )
        )

        reward_button_a.text = rewards[0].name
        reward_button_b.text = rewards[1].name
        reward_button_c.text = rewards[2].name

        reward_a_cost.text = rewards[0].cost.toString()
        reward_b_cost.text = rewards[1].cost.toString()
        reward_c_cost.text = rewards[2].cost.toString()

        reward_button_a.setOnClickListener { makePurchase(1) }
        reward_button_b.setOnClickListener { makePurchase(2) }
        reward_button_c.setOnClickListener { makePurchase(3) }
    }// end onCreate

    // in order to get back a result, we must prep the Activity by using registerForActivityResult
    // the android OS, which also kept track of listening for intents, has an activity API called ActivityResult API
    /*
    CES 3200 - Study for Quiz 2 - what are the three different phases we used to get from one activity to
    the other (main activity to robot purchase) - DON'T say we went into the manifest and said "lets not start
    main activity, lets start at robot purchase". we had to register robot purchase as an activity.
    phase 1. make the intent for StartActivity(intent)

intent had two arguments (context and the activity)

phase 2: putExtra so that when we started the activity, the data went along for the ride. what we put on as an extra was the energy (which is just fake right now). just basically passing data over. COMPANION OBJECT - works like a static object in java. this means it belongs to the entire class. inside this object, we put in a function that basically acted like we are going to call our event constructor. companion object should be public, and so should the function inside of it

phase 3: we registerForActivityResult so that we can pass back info

we should be explaining this in detail for the quiz. look at the code from each of the phases.*/

    private fun makePurchase(index : Int) {
        val reward = rewards[index - 1]

        if (robot_energy >= reward.cost) {
            robot_energy -= reward.cost
            robot_energy_available.text = robot_energy.toString()
            Toast.makeText(this, "${reward.name} Purchased!", Toast.LENGTH_SHORT).show()
            setWhichPurchaseMade(index)
        } else {
            Toast.makeText(this, R.string.insufficient, Toast.LENGTH_SHORT).show()
        }

//        val rewards = listOf(R.string.reward_a, R.string.reward_b, R.string.reward_c)
//        if (robot_energy >= costOfPurchase) {
//            val s1 = getString(rewards[costOfPurchase - 1])
//            val s2 = getString(R.string.purchased)
//            val s3 = s1 + " " + s2
//            robot_energy -= costOfPurchase
//            robot_energy_available.setText(robot_energy.toString())
//            Toast.makeText(this, s3, Toast.LENGTH_SHORT).show()
//            setWhichPurchaseMade(costOfPurchase)
//        } else {
//            Toast.makeText(this, R.string.insufficient, Toast.LENGTH_SHORT).show()
//        }
    }

    private fun setWhichPurchaseMade(robotPurchaseMade: Int) {
        var resultIntent = Intent()
        resultIntent.putExtra(EXTRA_ROBOT_PURCHASE_MADE, robotPurchaseMade.toString())
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    // in order to call it, the class itself must make the call
    // bc this is like the Kotlin version of a static object
    companion object {
        fun newIntent(context : Context, robotEnergy : Int, currentRobot: Int) : Intent {
            return Intent(context, RobotPurchase::class.java).apply {
                putExtra(EXTRA_ROBOT_ENERGY, robotEnergy)
                putExtra(EXTRA_CURRENT_ROBOT, currentRobot)
            }
        }
    }
}