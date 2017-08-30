package Micro.Agency

import Micro.Heuristics.Targeting.TargetingProfile

object TargetingProfiles {
  
  def default = new TargetingProfile(
    preferVpfEnemy    = 2.0,
    preferVpfOurs     = 2.0,
    preferDetectors   = 8.0,
    preferFocusFire   = 1.0,
    avoidPain         = 2.5,
    avoidDelay        = 0.05,
    avoidInterceptors = 4.0)
}
