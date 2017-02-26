package Plans.Allocation

import Startup.With
import Strategies.UnitMatchers.{UnitMatchAnything, UnitMatcher}
import Strategies.UnitPreferences.{UnitPreferAnything, UnitPreference}
import Utilities.Property

import scala.collection.mutable

class LockUnitsExactly extends LockUnits {
  
  description.set(Some("Reserve a fixed number of units"))
  
  val quantity        = new Property[Integer](1)
  val unitPreference  = new Property[UnitPreference](UnitPreferAnything)
  val unitMatcher     = new Property[UnitMatcher](UnitMatchAnything)
  
  override def getRequiredUnits(candidates:Iterable[Iterable[bwapi.Unit]]):Option[Iterable[bwapi.Unit]] = {
  
    val currentUnits = With.recruiter.getUnits(this)
    val desiredUnits =
      (if (currentUnits.size <= quantity.get) {
        currentUnits
      }
      else {
        currentUnits.toList.sortBy(unitPreference.get.preference).take(quantity.get)
      })
      .to[mutable.Set]
    
    //The candidates are offered in pools.
    //Originally, we wanted to force plans to hire from the unemployed pool first
    //But that meant that when we had a strong preference, the pooling was overriding it
    //Flattening it retains the "welfare" effect when there's no sort, but still allows sorting to work
    candidates
      .flatten
      .toList
      .sortBy(unitPreference.get.preference)
      .foreach(unit =>
        if (desiredUnits.size < quantity.get && unitMatcher.get.accept(unit)) {
          desiredUnits.add(unit)
        })
    if (desiredUnits.size >= quantity.get) {
      Some(desiredUnits)
    }
    else {
      None
    }
  }
}
