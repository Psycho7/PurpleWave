package Planning.Plans.Macro.Expanding

import Information.Geography.Types.Base
import Lifecycle.With

class BuildCannonsAtExpansions(initialCount: Int) extends BuildCannonsAtBases(initialCount) {
  
  override def eligibleBases: Iterable[Base] = {
    (With.geography.ourBases ++ With.geography.bases.filter(_.planningToTake))
      .filterNot(With.geography.ourMain.contains)
      .filterNot(With.geography.ourNatural.contains)
      .toSet
  }
}