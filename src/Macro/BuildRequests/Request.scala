package Macro.BuildRequests

import ProxyBwapi.Techs.Tech
import ProxyBwapi.UnitClass.UnitClass
import ProxyBwapi.Upgrades.Upgrade

object Request {
  
  def unit(unit: UnitClass, quantity: Int = 1): BuildRequest = {
    RequestAtLeast(quantity, unit)
  }
  
  def upgr(upgrade: Upgrade, level: Int = 1): BuildRequest = {
    RequestUpgrade(upgrade, level)
  }
  
  def tech(tech: Tech): BuildRequest = {
    RequestTech(tech)
  }
}
