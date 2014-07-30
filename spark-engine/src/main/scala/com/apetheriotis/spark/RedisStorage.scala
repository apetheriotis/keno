package com.apetheriotis.spark

import com.twitter.finagle.redis.Client
import com.twitter.storehaus.redis.{RedisStore, RedisLongStore}
import com.typesafe.config.ConfigFactory

/**
 * Connection settings for redis instance
 */
object RedisStorage {

  val envConf = ConfigFactory.load()
  val redisClient = Client(envConf.getString("redis") + ":6379")
  val store = RedisLongStore(redisClient)
  val stringStore = RedisStore(redisClient)

}