package com.apetheriotis.spark

import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream
import com.twitter.finagle.redis.util.StringToChannelBuffer

/**
 * The manager of the Keno tickets stream.
 * Any functionality for the Keno System resides in here
 */
class KenoTicketsManager {

  /**
   * Processes keno streams. The procedure:
   * <ul>
   * <li> Every two seconds.
   * <li>Filter out the useless data.
   * <li> Get the last rdd time.
   * <li> Count each occurrence of a number
   * <li> Save to redis
   * <ul>
   */
  def processKenoTickets(stream: DStream[String]) {
    //Map like: (drawId,weight___numbers)
    val pairs = stream.map(draw => (draw.split("___")(0), draw.split("___")(2) + "___" + draw.split("___")(3)))
    val pairz = pairs.window(Seconds(2), Seconds(2))
    pairz.foreachRDD((rdd, time) => {
      RedisStorage.store.put(StringToChannelBuffer("last_rdd_time"), Some(time.milliseconds))
      val rddData = rdd.collect()
      rddData.foreach(kenoTicketData => {
        val numbers = kenoTicketData._2.split("___")(1).split(",")
        numbers.foreach(number => {
          RedisStorage.store.merge(StringToChannelBuffer(kenoTicketData._1 + "_" + number), 1)
        })
      })
    })
    pairz.print()
  }

}
