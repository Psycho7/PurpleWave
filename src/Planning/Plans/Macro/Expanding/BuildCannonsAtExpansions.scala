package Planning.Plans.Macro.Expanding

import Information.Geography.Types.Base
import Lifecycle.With

class BuildCannonsAtExpansions(initialCount: Int) extends BuildCannonsAtBases(initialCount) {
  
  override def eligibleBases: Iterable[Base] = {
    With.geography.ourBasesAndSettlements
      .filterNot(_ == With.geography.ourMain)
      .filterNot(_ == With.geography.ourNatural)
  }
}
