package shogiai

@JvmInline
value class ProgramName(val value: String) {
    private companion object {
        val valueRegex = Regex("\\p{Alnum}{1,50}")
    }
    init {
        // ここの制約は適当に決めた。
        require(valueRegex.matches(value))
    }
}