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

    val result = {
      annottees.map(_.tree).toList match {
        case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends Flow with ..$parents { $self => ..$stats }" :: Nil => {
          val flowType = tpname.toString
          val groups = FlowDescription.d188.allGroups
          val classDefs = groups.map(group => {
            val className = TypeName("Group" + group.id)
            val termNames = (1 to group.columns).map(n => TermName(s"field$n"))
            val params = termNames.map(tn => (q"$tn: String"))
            val classDef = q"case class $className(..$params)"
            classDef
          })
          
          
          
          q"""$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends Flow with ..$parents {
            def flowType: String = {
              $flowType
            }
            ..$classDefs
          }"""
        }
        case _ => c.abort(c.enclosingPosition, "Annotation @FlowParser can be used only with case classes which extends Animal trait")
      }
    }
    c.Expr[Any](result)
  }
}
