package com.payasia.api.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;


public class RedisConnectionUtil {

	//address of your redis server
    private  String keyExpire;
    //the jedis connection pool..
    private  JedisPool pool = null;
    
    public RedisConnectionUtil(String redisHost,String redisPort,String keyExpire,String databaseName) {
        //configure our pool connection
       // pool = new JedisPool(redisHost, Integer.valueOf(redisPort)); 
    	pool = new JedisPool(new GenericObjectPoolConfig(),redisHost, Integer.parseInt(redisPort), Protocol.DEFAULT_TIMEOUT, null, Integer.parseInt(databaseName));
        this.keyExpire=keyExpire;
 
    }
    
	public boolean add(String key, String value) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			// save to redis
			//jedis.sadd(key.getBytes(),value.getBytes());
			 Map<String, String> map = new HashMap<>();
			 map.put("otp", value);
			 jedis.hmset(key,map);
			if (Integer.valueOf(keyExpire) >= 0) {
				jedis.expire(key, Integer.valueOf(keyExpire));
			}
			
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		}finally {
			/// it's important to return the Jedis instance to the pool once you've finished using it
			if (jedis != null) {
				pool.returnResource(jedis);
				return true;
			}
		}
		return false;
	}
    
	
	public boolean isValidOtp(String key,String otpValue){
		 Jedis jedis = pool.getResource();
		  Map<String, String> retrieveMap = jedis.hgetAll(key);
          for(String keyMap : retrieveMap.keySet()) {
        	 String otp =  retrieveMap.get(keyMap);
        	 if(otpValue.equals(otp)){
        		 return true; 
        	 }
           }
		 return false;
	}
	
	
	public void deleteKey(String key){
		 Jedis jedis = pool.getResource();
      	 jedis.del(key);
	}
    

}
