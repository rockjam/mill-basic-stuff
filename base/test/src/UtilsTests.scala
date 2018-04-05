import utest._

object UtilsTests extends TestSuite {

  def tests: Tests = Tests {
    "Utils.up" - {
      assert(Utils.up("hello") == "HELLO")
    }
  }

}
