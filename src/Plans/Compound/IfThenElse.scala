package Plans.Compound

import Plans.Plan
import Utilities.Property

class IfThenElse extends Plan {
  
  description.set("If/Then/Else")
  
  val predicate = new Property[Plan](new Plan)
  val whenTrue  = new Property[Plan](new Plan)
  val whenFalse = new Property[Plan](new Plan)
  
  override def getChildren: Iterable[Plan] = List(predicate.get, whenTrue.get, whenFalse.get)
  override def isComplete: Boolean = predicate.get.isComplete && whenTrue.get.isComplete
  
  override def onFrame() {
    predicate.get.onFrame()
    if (predicate.get.isComplete)
      whenTrue.get.onFrame()
    else
      whenFalse.get.onFrame()
  }
}
