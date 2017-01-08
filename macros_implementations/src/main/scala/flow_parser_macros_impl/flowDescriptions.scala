package flow_parser_macros_impl

object FlowDescription {
  def apply(name: String): Option[FlowGroup] = name match {
    case "D188" => Some(d188)
    case _ => None
  }
  
  val d188: FlowGroup = FlowGroup(386, "Customer Payment Details", (1 to 15).map(n => ("field" + n) -> StringField), Seq(
                      FlowGroup(387, "MeterRegisterDetailPerMeter", (1 to 3).map(n => ("field" + n) -> StringField), Seq.empty)
                    ))
  
  //case class G386(f1 .. f15: String, g387s: Seq[G387])
  
}

sealed trait FieldType {
  def typeName: String
}
object StringField extends FieldType {
  val typeName = "String"
}

case class FlowGroup(id: Int, description:String, columns: Seq[(String, FieldType)], subGroups: Seq[FlowGroup]) {
  def allGroups:Seq[FlowGroup] = this :: subGroups.toList.flatMap(_.allGroups)
}


