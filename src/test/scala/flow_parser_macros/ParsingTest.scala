package flow_parser_macros


import org.scalatest._
import fastparse.core.Parsed
import flow_parser_macros_impl.Parsing._

class ParsingSpec extends FunSpec with Matchers {
  
  describe("A line parser") {
    it("should parse a line without columns") {
      val Parsed.Success(value, _) = lineParser("386", 0).parse("386|")
      value should equal(Seq.empty)
    }
    
    it("should parse a line with one column") {
      val Parsed.Success(value, _) = lineParser("386", 1).parse("386|1|")
      value should equal(Seq("1"))
    }
    
    it("should parse a line with two columns and reach the end of the line") {
      val testData = "386|1|2|"
      val Parsed.Success(value, index) = lineParser("386", 2).parse(testData)
      value should equal(Seq("1", "2"))
      index should equal(testData.size)
    }
    
  }
  
  describe("A multiline parser") {
    it("should parse multiple lines with different kinds of line breaks") {
      val testData = "386|1|2|\n386|1|2|\r386|1|2|\r\n386|1|2|"
      val Parsed.Success(value, index) = multiLine(lineParser("386", 2)).parse(testData)
      value should equal(Seq.fill(4)(Seq("1", "2")))
        
    }
  }
  
}
