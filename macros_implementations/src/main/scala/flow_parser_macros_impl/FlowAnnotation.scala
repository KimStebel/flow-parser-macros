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
          val classDefs = groups.flatMap(group => {
            val className = TypeName(s"Group${group.id}")
            val classNameTerm = TermName(s"Group${group.id}")
            val termNames = (1 to group.columns).map(n => TermName(s"field$n"))
            val columnParams = termNames.map(tn => q"$tn: String")
            val subGroupParams: IndexedSeq[Tree] = group.subGroups.toIndexedSeq.map(sg => {
              val tn = TermName(s"g${sg.id}")
              val tpN = TypeName(s"Group${sg.id}")
              q"$tn: Seq[$tpN]"
            })
            val params = columnParams ++ subGroupParams
            
            val groupIdStr = Literal(Constant(group.id.toString))
            val subGroupConstParams = IndexedSeq.fill(group.subGroups.size)(q"Seq.empty")
            val fields = (1 to group.columns).map(n => pq"""${TermName("matchField" + n)}""")
            val rFields = (1 to group.columns).map(n => q"""${TermName("matchField" + n)}""")
            println(fields)
            val constParams = rFields ++ subGroupConstParams
            Seq(
              q"""case class $className(..$params) {
                    
                  }""", q"""
                  object $classNameTerm {
                    val parser = {
                      import fastparse.all._
                      import scala.collection.mutable.ArrayBuffer
                      import flow_parser_macros_impl.Parsing
                      
                      val lp = Parsing.lineParser($groupIdStr, 3)
                      lp.map {
                        case ArrayBuffer(..$fields) => $classNameTerm(..$constParams)
                        case x => {println("couldn't match " + x + ". This is probably a bug in the code generator"); ???}
                      }
                    }
                  }
               """
            )
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
    val whitespace: Parser[Unit] = (" " | "\t").rep
    whitespace ~/ P(s"$groupId|" ~/ (CharPred('|' != _).rep(1).! ~/ "|").rep(exactly = columns)) ~/ whitespace
  }
  
  def multiLineParser[A](p: Parser[A], min: Int = 0):Parser[Seq[A]] = {
    val lineBreak: Parser[Unit] = ("\r\n" | "\n\r" | "\n" | "\r")
    p.rep(min = min, sep = lineBreak)
  }
  
  
}