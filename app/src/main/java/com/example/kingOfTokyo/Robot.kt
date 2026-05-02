package com.example.robot_sp26

data class Robot(
    // val because this id shouldn't change
    val robotMessageResource : Int, // this is Int because the R class returns IDs of type Int
    var myTurn : Boolean, // var because this can change
    val robotImageLarge : Int,
    val robotImageSmall : Int
)
