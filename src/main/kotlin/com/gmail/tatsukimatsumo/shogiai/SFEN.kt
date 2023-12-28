package com.gmail.tatsukimatsumo.shogiai

@JvmInline
value class SFEN(private val value: String) {
    private companion object {
        // 最小長："9/9/9/9/9/9/9/9/9 w - 1"
        // 最大長さは計算が面倒かつ、現時点で平手以外を対応する気がないので適当
        val valueRegex = Regex("[lnsgkgsnlrbpLNSGKGSNLBRP/1-9 wb\\-+]{23,500}")
    }
    init {
        require(valueRegex.matches(value))
    }

    fun currentTurnFor(): Turn = when (value.split(" ")[1]) {
        "b" -> Turn.FirstPlayer
        "w" -> Turn.SecondPlayer
        else -> error("the value is not valid (value: $value).")
    }
}