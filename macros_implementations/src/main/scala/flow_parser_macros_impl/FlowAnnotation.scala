package flow_parser_macros_impl

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class FlowParser extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro FlowParser.impl
}

object FlowParser {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val tree = {
      annottees.map(_.tree).toList match {
        case q"$mods object $tpname extends $parent with ..$parents { $self => ..$stats }" :: Nil => {
          val flowType = tpname.toString
          val groups = FlowDescription(flowType).toSeq.flatMap(_.allGroups)
          val classDefs = groups.map(group => {
            val className = TypeName("Group" + group.id)
            val termNames = (1 to group.columns).map(n => TermName(s"field$n"))
            val columnParams = termNames.map(tn => (q"$tn: String"))
            val subGroupParams: IndexedSeq[Tree] = group.subGroups.toIndexedSeq.map(sg => {
              val tn = TermName(s"g${sg.id}")
              val tpN = TypeName(s"Group${sg.id}")
              q"$tn: Seq[$tpN]"
            })
            val params = columnParams ++ subGroupParams
            val classDef = q"case class $className(..$params)"
            classDef
          })
          
          q"""$mods object $tpname extends $parent with ..$parents {
                $self =>
              
                def flowType: String = $flowType
            
                ..$classDefs
            
                ..$stats
              }"""
        }
        case _ => c.abort(c.enclosingPosition, "Annotation @FlowParser can only be used with objects")
      }
    }
    c.Expr[Any](tree)
  }
}

object Parsing {
  import fastparse.all._
    
  def lineParser(groupId: String, columns: Int):Parser[Seq[String]] = {
    P(groupId ~/ ("|" ~/ (!"|" ~ AnyChar).rep(1).!).rep(exactly = columns) ~/ "|")
  }
  
  def multiLine[A](p: Parser[A]):Parser[Seq[A]] = {
    val lineBreak: Parser[Unit] = ("\r\n" | "\n\r" | "\n" | "\r")
    p.rep(min = 0, sep = lineBreak)
  }
  
}