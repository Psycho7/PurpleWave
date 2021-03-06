package Planning.Plans.GamePlans

import Planning.Plan
import Planning.Plans.Compound.Parallel
import Planning.Plans.Information.{Always, Never}

abstract class Mode extends Parallel {
  
  override def isComplete: Boolean = completionCriteria.isComplete || ! activationCriteria.isComplete
  
  val activationCriteria: Plan = new Always
  val completionCriteria: Plan = new Never
  
  override def onUpdate() {
    if ( ! isComplete) {
      super.onUpdate()
    }
  }
}
