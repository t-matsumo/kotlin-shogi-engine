package shogiai

interface ShogiAI {
    suspend fun getProgramName(): ProgramName
    suspend fun getAuthorName(): AuthorName
    suspend fun onPrepareForGame()
    suspend fun onReceivePosition(position: Position)
    suspend fun onStartToPonder(timeLimit: TimeLimit)
    suspend fun onMove(): Move
}
