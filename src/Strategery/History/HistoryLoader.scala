package Strategery.History

import java.io._

import Lifecycle.With

object HistoryLoader {
  
  private val loadFile = "bwapi-data/read/_history.json"
  private val saveFile = "bwapi-data/write/_history.json"
  private val seedFile = "bwapi-data/AI/trainingHistory.json"
  private val possibleFilenames = Array(loadFile, saveFile, seedFile)
  
  def load(): Iterable[HistoricalGame] = {
    val gamesSerialized = loadBestGames(possibleFilenames)
    val games = HistorySerializer.readGames(gamesSerialized)
    games
  }
  
  def save(games: Iterable[HistoricalGame]) {
    val gamesSerialized = HistorySerializer.writeGames(games)
    saveGames(saveFile, gamesSerialized)
  }
  
  
  private def loadBestGames(possibleFilenames: Iterable[String]): String = {
    possibleFilenames
      .view
      .map(loadGames)
      .find(_.isDefined)
      .map(_.get)
      .getOrElse("")
  }
  
  private def loadGames(filename: String): Option[String] = {
    
    var reader: BufferedReader = null
    var output: Option[String] = None
    
    try {
      var proceed = true
      val lines   = new StringBuilder
      val file    = new File(filename)
      val stream  = new FileInputStream(file)
          reader  = new BufferedReader(new InputStreamReader(stream))
      
      while (proceed) {
        val nextLine = reader.readLine()
        proceed = nextLine == null
        if (proceed) {
          lines.append(nextLine)
        }
      }
      output = Some(lines.toString)
    }
    catch { case exception: Exception =>
      With.logger.warn("Failed to load game history from " + filename)
      With.logger.onException(exception)
    }
    if (reader != null) {
      reader.close()
    }
    output
  }
  
  private def saveGames(filename: String, contents: Iterable[String]) {
    
    var bufferedWriter: BufferedWriter = null
    
    try {
      val file            = new File(filename)
      val fileWriter      = new FileWriter(file)
          bufferedWriter  = new BufferedWriter(fileWriter)
      
      contents.foreach(bufferedWriter.write)
    }
    catch { case exception: Exception =>
      With.logger.warn("Failed to save game history to " + filename)
      With.logger.onException(exception)
    }
    if (bufferedWriter != null) {
      bufferedWriter.close()
    }
  }
}
