package flow_parser_macros

object Main extends App {
  val d188 = D188()
  println(d188.flowType)
  println(d188.Group387("f1", "f2", "f3"))
  println(d188.Group386("f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10", "f11", "f12", "f13", "f14", "f15"))

}

