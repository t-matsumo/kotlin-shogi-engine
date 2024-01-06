package com.gmail.tatsukimatsumo.shogiai

interface ShogiAI {
    suspend fun getProgramName(): ProgramName
    suspend fun getAuthorName(): AuthorName
    suspend fun onPrepareForGame()
    suspend fun onReceivePosition(positionUSI: PositionUSI)
    suspend fun onStartToPonder(timeLimit: TimeLimit)
    suspend fun onMove(): Move
}
