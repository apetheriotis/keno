package com.apetheriotis.client.services

import com.apetheriotis.client.dao.AbstractDao
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import com.apetheriotis.client.dto.LatestDrawResultRs
import scala.collection.mutable.ListBuffer


/**
 * Provides methods to access the result of a draw
 */
class ResultsService extends AbstractDao {
  val DATE_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmm").withZoneUTC()

  /**
   * Queries for the latest results
   * @return a LatestDrawResultRs which contains the latest draw results and the time of the draw
   */
  def setupLatestResults(latestTime: DateTime = DrawIdHelper.latestDrawId): LatestDrawResultRs = {
    var mapData = Map[String, Long]()
    val latestKeys = jedis.keys(DATE_FORMATTER.print(latestTime) + "_*")
    latestKeys.toArray.foreach(key => {
      val times = jedis.get(key.toString).toLong
      mapData += (key.toString.split("_")(1) -> times)
    })

    // Add any numbers that may have not been submitted
    for (a <- 1 to 80) {
      if (!mapData.contains(a.toString)) {
        mapData += (a.toString -> 0L)
      }
    }

    // Take top 20 which correspond to the draw results
    val sorted = mapData.toList.sortWith(_._2 < _._2)
    val drawRs = new ListBuffer[Int]
    sorted.take(20).foreach(entry => {
      drawRs += entry._1.toInt
    })
    new LatestDrawResultRs(DATE_FORMATTER.print(latestTime), drawRs.toList)
  }

}
