package com.apetheriotis.client.services

import com.apetheriotis.client.dao.AbstractDao
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import com.apetheriotis.client.dto.LatestNumberStatsRs


/**
 * Provides methods to retrieve statistics about the times a number has been submitted by the players
 */
class StatsService extends AbstractDao {
  val DATE_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmm").withZoneUTC()

  /**
   * Queries for the times a number has been submitted in the last draw
   * @return a LatestNumberStatsRs which contains all numbers with the times they have been submitted
   */
  def getLatestDrawStats(): LatestNumberStatsRs = {
    var mapData = Map[String, Long]()
    val latestTime = DrawIdHelper.latestDrawId
    val latestKeys = jedis.keys(DATE_FORMATTER.print(latestTime) + "_*")
    latestKeys.toArray.foreach(key => {
      val times = jedis.get(key.toString).toLong
      mapData += (key.toString.split("_")(1) -> times)
    })
    LatestNumberStatsRs(new DateTime().getMillis, DATE_FORMATTER.print(latestTime), mapData)
  }

  /**
   * Queries for the times a number has been submitted in the running draw
   * @return a LatestNumberStatsRs which contains all numbers with the times they have been submitted
   */
  def getRealTimeStats(): LatestNumberStatsRs = {
    var mapData = Map[String, Long]()
    val latestTime = DrawIdHelper.latestDrawId.plusMinutes(5)
    val latestKeys = jedis.keys(DATE_FORMATTER.print(latestTime) + "_*")
    latestKeys.toArray.foreach(key => {
      val times = jedis.get(key.toString).toLong
      mapData += (key.toString.split("_")(1) -> times)
    })
    LatestNumberStatsRs(new DateTime().getMillis, DATE_FORMATTER.print(latestTime), mapData)
  }





}
