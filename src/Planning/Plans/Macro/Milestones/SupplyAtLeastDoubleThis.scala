package Planning.Plans.Macro.Milestones

import Planning.Composition.Property
import Planning.Plan
import Lifecycle.With

class SupplyAtLeastDoubleThis(defaultQuantity:Int = 0) extends Plan {
  
  description.set("Require a minimum supply count")
  
  val quantity = new Property[Int](defaultQuantity)
  
  override def isComplete: Boolean = With.self.supplyUsed >= quantity.get * 2
}
