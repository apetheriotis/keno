package com.apetheriotis.client.dao

import redis.clients.jedis.Jedis
import com.typesafe.config.ConfigFactory

trait AbstractDao {
  val envConf = ConfigFactory.load()
 val jedis = new Jedis(envConf.getString("jedisIp"))
  jedis.connect()
}
