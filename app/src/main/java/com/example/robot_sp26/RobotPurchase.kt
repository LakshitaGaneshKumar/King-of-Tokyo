package com.example.robot_sp26

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_ROBOT_ENERGY = "EXTRA_ROBOT_ENERGY"
class RobotPurchase : AppCompatActivity() {
    private lateinit var reward_button_a : Button
    private lateinit var reward_button_b : Button
    private lateinit var reward_button_c : Button
    private lateinit var robot_energy_available : TextView
    private var robot_energy = -1

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_purchase)

        reward_button_a = findViewById(R.id.buy_reward_a)
        reward_button_b = findViewById(R.id.buy_reward_b)
        reward_button_c = findViewById(R.id.buy_reward_c)
        robot_energy_available = findViewById(R.id.robot_energy_to_spend)

        robot_energy = 2 // temporary hardcoded for testing
        robot_energy = intent.getIntExtra(EXTRA_ROBOT_ENERGY, 4)

        robot_energy_available.setText(robot_energy.toString())

        reward_button_a.setOnClickListener { makePurchase(1) }
        reward_button_b.setOnClickListener { makePurchase(2) }
        reward_button_c.setOnClickListener { makePurchase(3) }
    }

    private fun makePurchase(costOfPurchase : Int) {
        val rewards = listOf(R.string.reward_a, R.string.reward_b, R.string.reward_c)
        if (robot_energy >= costOfPurchase) {
            val s1 = getString(rewards[costOfPurchase - 1])
            val s2 = getString(R.string.purchased)
            val s3 = s1 + " " + s2
            robot_energy -= costOfPurchase
            robot_energy_available.setText(robot_energy.toString())
            Toast.makeText(this, s3, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.insufficient, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newIntent(context : Context, robotEnergy : Int) : Intent {
            return Intent(context, RobotPurchase::class.java).apply {
                putExtra(EXTRA_ROBOT_ENERGY, robotEnergy)
            }
        }
    }
}