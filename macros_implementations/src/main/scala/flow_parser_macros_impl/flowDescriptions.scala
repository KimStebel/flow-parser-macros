package flow_parser_macros_impl

object FlowDescription {
  def apply(name: String): Option[FlowGroup] = name match {
    case "D188" => Some(d188)
    case _ => None
  }
  
  val d188: FlowGroup =
    FlowGroup(386, "CustomerPaymentDetails", (Seq("foo") ++ (2 to 15).map(n => ("field" + n))).map(_ -> StringField), Seq(
      FlowGroup(387, "MeterRegisterDetailPerMeter", Seq("meterRegisterId", "registerReading", "prepaymentUnitRate").map(_ -> StringField), Seq.empty)))
  
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


