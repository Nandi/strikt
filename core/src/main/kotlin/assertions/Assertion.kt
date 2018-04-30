package assertions

import java.io.StringWriter

interface Assertion<T> {
  fun evaluate(predicate: (T) -> Result)
}

internal class FailFastAssertion<T>(private val subject: T) : Assertion<T> {
  override fun evaluate(predicate: (T) -> Result) {
    predicate(subject).let { result ->
      val message = StringWriter()
        .also { writer -> result.describeTo(writer) }
        .toString()
      println(message)
      if (result is Failure) {
        throw AssertionFailed(result)
      }
    }
  }
}

internal class CollectingAssertion<T>(private val subject: T) : Assertion<T> {
  private val _results = mutableListOf<Result>()

  override fun evaluate(predicate: (T) -> Result) {
    predicate(subject).let(_results::add)
  }

  val results: List<Result>
    get() = _results
}
