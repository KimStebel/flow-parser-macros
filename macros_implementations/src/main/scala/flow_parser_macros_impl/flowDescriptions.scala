package flow_parser_macros_impl

object FlowDescription {
  val d188: Group = Group(386, "Customer Payment Details", 15, Seq(Group(387, "MeterRegisterDetailPerMeter", 3, Seq.empty)))
  
  /*
  case class G386(f1 .. f15: String, g387s: Seq[G387])
  
  
    
    
   */
}

case class Group(id: Int, description:String, columns: Int, subGroups: Seq[Group]) {
  def allGroups:Seq[Group] = this :: subGroups.toList.flatMap(_.allGroups)
}


