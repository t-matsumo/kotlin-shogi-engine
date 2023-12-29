package com.gmail.tatsukimatsumo.shogiai

enum class Turn(val lesserIntValue: Int) {
    FirstPlayer(1 shl 4), SecondPlayer(1 shl 5)
}