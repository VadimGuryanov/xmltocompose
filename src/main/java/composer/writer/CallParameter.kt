package composer.writer

data class CallParameter(
    val value: ParameterValue,
    val name: String? = null
)

fun createCallParameter(name: String, value: ParameterValue?): CallParameter? {
    return value?.let {
        CallParameter(name = name, value = value)
    }
}

fun createCallParameter(value: ParameterValue?): CallParameter? {
    return value?.let { CallParameter(it) }
}
