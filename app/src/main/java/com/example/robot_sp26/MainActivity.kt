package com.example.robot_sp26

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // if you don't write private, it's assumed to be public by default
    // protected = similar to inheritance, like a hybrid of private and public
    // lateinit = it has something to do with null. kotlin really doesn't want you to set things to null unless you intentionally did it.
    // so lateinit is a promise and a safety feature to guard against null. this just says you're gonna set it to null RIGHT NOW, but you'll change it before you use it
    private lateinit var redRobotImg : ImageView
    private lateinit var whiteRobotImg : ImageView
    private lateinit var yellowRobotImg : ImageView

    // wire up the message_box from the activity_main.xml file
    private lateinit var messageBox : TextView

    // START HW 1
//    private lateinit var clockwiseButton : ImageView
//    private lateinit var counterButton : ImageView
    // END HW 1

    // doesn't need to be a lateinit because we can just init it right here
    // this will help us keep track of which robot's turn it is
    // this needs to be a var because we will be changing this value
    // we don't have to explicitly say that it's an Int because it will just assume it is because we init it to 0
    // but we can explicitly set it to Int if we want (it's commented out right now)
    private var turnCount /*: Int*/ = 0

    private val robots = listOf(
        Robot(R.string.red_message_text, false,
            R.drawable.robot_red_large, R.drawable.robot_red_small),

        Robot(R.string.white_message_text, false,
            R.drawable.robot_white_large, R.drawable.robot_white_small),

        Robot(R.string.yellow_message_text, false,
            R.drawable.robot_yellow_large, R.drawable.robot_yellow_small)
    )

    // fun is a function in kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // we fulfill the lateinit promise now
        // get references to inflated views
        // setting the promise and fulfilling the promise are both a part of step one of the wiring up process
        redRobotImg = findViewById(R.id.red_robot)
        whiteRobotImg = findViewById(R.id.white_robot)
        yellowRobotImg = findViewById(R.id.yellow_robot)

        // START IN CLASS
        // fulfill the promise to messageBox
        messageBox = findViewById(R.id.message_box)

        // lambda is a nameless or anonymous function
        // there are two options for setOnClickListener, where one of them is a lambda
        // we use lambdas a lot in mobile development because it's just more efficient
        // use Toast - it's capitalized because it's the class that's making the call
        // if it's all CAPS like LENGTH_SHORT, it's a constant field
        // the toast is like a print debug statement to make sure that we wired up everything correctly


        redRobotImg.setOnClickListener {
            // this vs context
            // this is a key word
            // A.B(D) - A is the object, . is the method call, B is the method name, D is the parameter. then B will have access to A and D because A made the call and D is passed in
            // "this" is like an argument which gets passed implicitly. in this case, the program itself is being passed into the context
            // "context" is an activity, which right now it's the entire program
            // Toast.makeText(this, "Red Robot Clicked", Toast.LENGTH_SHORT).show() - we can comment this out now because we know that it's all wired up
            toggleImage()
        }

        whiteRobotImg.setOnClickListener {
           // Toast.makeText(this, "White Robot Clicked", Toast.LENGTH_SHORT).show()
            toggleImage()
        }

        yellowRobotImg.setOnClickListener {
         //   Toast.makeText(this, "Yellow Robot Clicked", Toast.LENGTH_SHORT).show()
            toggleImage()
        }

        // END OF WHAT WE DID IN CLASS - commented out for HW 1

        // START OF HW 1
//
//        // fulfilling the promise of wiring up
//        clockwiseButton = findViewById(R.id.clockwise_button)
//        counterButton = findViewById(R.id.counter_button)
//
//        // make the buttons function
//        clockwiseButton.setOnClickListener {
//            clockwiseButtonToggle()
//        }
//
//        counterButton.setOnClickListener {
//            counterButtonToggle()
//        }

        // END OF HW 1

    }// end onCreate

    // START FROM CLASS
    // we create a new function
    // when turnCount is 0, we just started the game and all the robots will be large
    // so the cycle needs to be 0, 1, 2, 3, 1, 2, 3, 1, 2, 3...
    private fun toggleImage() {
        turnCount++

        if (turnCount > 3) {
            turnCount = 1
        }

        // these setImageResource statements used to be within the setOnClickListener stuff
        // but we just
        if (turnCount == 1) {
            // the R class tracks IDs. so when we do R.drawable or R.id, it will return an ID of type Int
            redRobotImg.setImageResource(R.drawable.robot_red_large)
            whiteRobotImg.setImageResource(R.drawable.robot_white_small)
            yellowRobotImg.setImageResource(R.drawable.robot_yellow_small)
        } else if (turnCount == 2) {
            redRobotImg.setImageResource(R.drawable.robot_red_small)
            whiteRobotImg.setImageResource(R.drawable.robot_white_large)
            yellowRobotImg.setImageResource(R.drawable.robot_yellow_small)
        } else {
            redRobotImg.setImageResource(R.drawable.robot_red_small)
            whiteRobotImg.setImageResource(R.drawable.robot_white_small)
            yellowRobotImg.setImageResource(R.drawable.robot_yellow_large)
        }
        updateMessageBox()
        setRobotTurn()
        setRobotImages()
    }

    // END FROM CLASS

////    // START HW 1
//    private fun updateImage() {
//        if (turnCount == 1) {
//            // the R class tracks IDs. so when we do R.drawable or R.id, it will return an ID of type Int
//            redRobotImg.setImageResource(R.drawable.robot_red_large)
//            whiteRobotImg.setImageResource(R.drawable.robot_white_small)
//            yellowRobotImg.setImageResource(R.drawable.robot_yellow_small)
//        } else if (turnCount == 2) {
//            redRobotImg.setImageResource(R.drawable.robot_red_small)
//            whiteRobotImg.setImageResource(R.drawable.robot_white_large)
//            yellowRobotImg.setImageResource(R.drawable.robot_yellow_small)
//        } else {
//            redRobotImg.setImageResource(R.drawable.robot_red_small)
//            whiteRobotImg.setImageResource(R.drawable.robot_white_small)
//            yellowRobotImg.setImageResource(R.drawable.robot_yellow_large)
//        }
//    }
//    private fun clockwiseButtonToggle() {
//        if (turnCount == 0) {
//            turnCount = 1
//        } else {
//            turnCount--
//
//            if (turnCount < 1) {
//                turnCount = 3
//            }
//        }
//        updateImage()
//    }
//
//    private fun counterButtonToggle() {
//        turnCount++
//
//        if (turnCount > 3) {
//            turnCount = 1
//        }
//
//        updateImage()
//    }
//    // END HW 1

    // we need to finish this in class
    private fun updateMessageBox() {
        // when is kinda like an if else if
//        when (turnCount) {
//            1 -> messageBox.setText(R.string.red_message_text)
//            2 -> messageBox.setText(R.string.white_message_text)
//            else -> messageBox.setText(R.string.yellow_message_text)
//        }

        // do this instead so it's refactored
        messageBox.setText(robots[turnCount - 1].robotMessageResource)
    }

    // we can use this to update the images
    private fun setRobotTurn() {
        // this is an enhanced for loop in Java
        for (robot in robots) {
            robot.myTurn = false
        }
        robots[turnCount - 1].myTurn = true
    }

    private fun setRobotImages() {

    }
}