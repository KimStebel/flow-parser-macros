package flow_parser_macros_impl

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class FlowParser extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro FlowParser.impl
}

object FlowParser {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {

    def classAndCompanion(group: FlowGroup): Seq[c.Tree] = {
      import c.universe._
      
      val className = s"Group${group.id}"
      
      val columnParams = group.columns.map { 
        case (colName, fieldType) => q"${TermName(colName)}: ${TypeName(fieldType.typeName)}"
      }
      
      val subGroupParams: IndexedSeq[Tree] = group.subGroups.toIndexedSeq.map(sg => {
        val tn = TermName(s"g${sg.id}")
        val tpN = TypeName(s"Group${sg.id}")
        q"$tn: Seq[$tpN]"
      })
      
      val params = columnParams ++ subGroupParams

      val groupIdStr = Literal(Constant(group.id.toString))
      val subGroupConstParams = IndexedSeq.fill(group.subGroups.size)(q"Seq.empty")
      val fields = group.columns.map { case (name, _) => pq"""${TermName(name)}""" }
      val rFields = group.columns.map { case (name,_) => q"""${TermName(name)}""" }
      
      println(fields)
      val constParams = rFields ++ subGroupConstParams
      
      Seq(
        q"""case class ${TypeName(className)}(..$params)""",
        q"""object ${TermName(className)} {
              val parser = {
                import fastparse.all._
                import scala.collection.mutable.ArrayBuffer
                import flow_parser_macros_impl.Parsing
  
                val lp = Parsing.lineParser($groupIdStr, 3)
                lp.map {
                  case ArrayBuffer(..$fields) => ${TermName(className)}(..$constParams)
                  case x => {println("couldn't match " + x + ". This is probably a bug in the code generator"); ???}
                }
              }
            }
         """
      ) 
    }
    
    def annotateObject(o: c.Tree): c.Tree = {
      import c.universe._
      o match {
        case q"$mods object $name extends $parent with ..$parents { $self => ..$stats }" => {
          val flowType = name.toString
          val groups = FlowDescription(flowType).toSeq.flatMap(_.allGroups)
          val classDefs = groups.flatMap(g => classAndCompanion(g))

          q"""$mods object $name extends $parent with ..$parents {
                $self =>

                def flowType: String = $flowType

                ..$classDefs

                ..$stats
              }"""

        }
      }
    }
    
    val tree:c.Tree = {
      annottees.map(_.tree).toList match {
        case a :: Nil => annotateObject(a)
        case _ => c.abort(c.enclosingPosition, "Annotation @FlowParser can only be used with objects")
      }
    }
    c.Expr[Any](tree)
  }
  
  
}

object Parsing {
  import fastparse.all._
    
  def lineParser(groupId: String, columns: Int):Parser[Seq[String]] = {
    val whitespace: Parser[Unit] = (" " | "\t").rep
    whitespace ~/ P(s"$groupId|" ~/ (CharPred('|' != _).rep(1).! ~/ "|").rep(exactly = columns)) ~/ whitespace
  }
  
  def multiLineParser[A](p: Parser[A], min: Int = 0):Parser[Seq[A]] = {
    val lineBreak: Parser[Unit] = ("\r\n" | "\n\r" | "\n" | "\r")
    p.rep(min = min, sep = lineBreak)
  }
  
  
}