package Debugging

class Configuration {
  var gameSpeed                         = 0
  
  var enableStdOut                      = false
  var enableChat                        = true
  var enableLatencyCompensation         = false
  
  var enableCamera                      = false
  var cameraDynamicSpeed                = false
  var cameraDynamicSpeedMin             = 30
  var cameraDynamicSpeedMax             = 0
  
  var enableVisualization               = true
  var enableVisualizationBattles        = false
  var enableVisualizationEconomy        = false
  var enableVisualizationGrids          = true
  var enableVisualizationPerformance    = true
  var enableVisualizationPlans          = false
  var enableVisualizationResources      = false
  var enableVisualizationScheduler      = false
  var enableVisualizationGeography      = false
  var enableVisualizationUnitsForeign   = false
  var enableVisualizationUnitsOurs      = false
}