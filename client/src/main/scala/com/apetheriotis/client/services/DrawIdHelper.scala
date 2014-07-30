package com.apetheriotis.client.services

import org.joda.time.{DateTimeZone, DateTime}

object DrawIdHelper {

  /**
   * Get latest draw id. The id of the draw that is ready.
   * @return the latest draw id based on time
   */
  def latestDrawId: DateTime = {
    var nowTime = new DateTime().withSecondOfMinute(59).withZone(DateTimeZone.UTC)
    val minutesToAdd = nowTime.getMinuteOfHour % 10
    if (minutesToAdd > 4) nowTime = nowTime.plusMinutes(5 - minutesToAdd)
    else nowTime = nowTime.plusMinutes(5 - minutesToAdd).minusMinutes(5)
    println(
      nowTime
    )
    nowTime
  }


}
