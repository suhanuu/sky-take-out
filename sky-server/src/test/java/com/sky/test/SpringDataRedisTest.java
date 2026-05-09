//package com.sky.test;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.connection.DataType;
//import org.springframework.data.redis.core.*;
//
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//@SpringBootTest
//@Slf4j
//public class SpringDataRedisTest {
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Test
//    public void testRedis() {
////        // 存储数据
////        redisTemplate.opsForValue().set("name", "张三");
////        // 获取数据
////        String name = (String) redisTemplate.opsForValue().get("name");
////        System.out.println(name);
//        System.out.println(redisTemplate);
//    }
//
//    @Test
//    public void testString() {
//        log.info("String类型测试");
//        redisTemplate.opsForValue().set("name", "张三");
//        String value = (String) redisTemplate.opsForValue().get("name");
//        System.out.println(value);
//        redisTemplate.opsForValue().set("code", "2345", 3, TimeUnit.MINUTES);
//        redisTemplate.opsForValue().setIfAbsent("lock1","1");// 如果key不存在，则设置值
//        redisTemplate.opsForValue().setIfAbsent("lock1","2");
//    }
//    @Test
//    public void testHash() {
//        log.info("Hash类型测试");
//        HashOperations hashOperations = redisTemplate.opsForHash();
//        hashOperations.put("hash", "name", "张三");
//        hashOperations.put("hash", "age", "18");
//        hashOperations.put("hash", "sex", "男");
//        String name = (String) hashOperations.get("hash", "name");
//        System.out.println(name);
//        Set keys = hashOperations.keys("hash"); // 获取所有的key
//        System.out.println(keys);
//        List values = hashOperations.values("hash");// 获取所有的value
//        System.out.println(values);
//
//        hashOperations.delete("hash", "sex");// 删除指定的key
//
//    }
//
//    @Test
//    public void testList() {
//        log.info("List类型测试");
//        ListOperations listOperations = redisTemplate.opsForList();
//        listOperations.leftPushAll("list", "a","b","c");
//        listOperations.leftPush("list", "张三");
//        listOperations.leftPush("list", "lisi");
//        listOperations.leftPush("list", "wangwu");
//        List list = listOperations.range("list", 0, -1);
//        System.out.println(list);
//        String value = (String) listOperations.rightPop("list");
//        System.out.println(value);
//        Long size = listOperations.size("list");
//        System.out.println(size);
//    }
//    @Test
//    public void testSet() {
//        log.info("Set类型测试");
//        SetOperations setOperations = redisTemplate.opsForSet();
//        setOperations.add("set1", "a","b","c");
//        setOperations.add("set2", "a","b","c","d","e");
//        Set members = setOperations.members("set1");
//        System.out.println(members);
//        Long size = setOperations.size("set1");
//        System.out.println(size);
//        Set intersection = setOperations.intersect("set1", "set2");
//        System.out.println(intersection);
//        Set union = setOperations.union("set1", "set2");
//        System.out.println(union);
//        setOperations.remove("set1", "a");
//
//    }
//    @Test
//    public void testZSet() {
//        log.info("ZSet类型测试");
//        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
//        zSetOperations.add("zset", "a", 1);
//        zSetOperations.add("zset", "b", 2);
//        zSetOperations.add("zset", "c", 3);
//        Set members = zSetOperations.range("zset", 0, -1);
//        System.out.println(members);
//        Long size = zSetOperations.size("zset");
//        System.out.println(size);
//        zSetOperations.incrementScore("zset", "a", 3);
//        Set members1 = zSetOperations.range("zset", 0, -1);
//        System.out.println(members1);
//        zSetOperations.remove("zset", "b");
//
//    }
//    @Test
//    public void testHyperLogLog() {
//        log.info("同用类型测试");
//        Set keys = redisTemplate.keys("*");
//        System.out.println(keys);
//
//        Boolean name = redisTemplate.hasKey("name");
//        System.out.println(name);
//        for (Object key : keys) {
//            DataType type = redisTemplate.type(key);
//            System.out.println(type);
//        }
//        redisTemplate.delete("set2");
//    }
//
//}
