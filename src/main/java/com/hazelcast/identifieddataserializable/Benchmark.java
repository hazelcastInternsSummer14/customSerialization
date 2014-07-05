package com.hazelcast.identifieddataserializable;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.util.Date;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;
import java.util.Random;
import com.hazelcast.mainbenchmark.MainBenchmark;
/**
 * Created by Mustafa Orkun Acar <mustafaorkunacar@gmail.com> on 23.06.2014.
 */

public class Benchmark
{
    public IMap<Integer, Customer> customerMap;
    public Config config;
    public HazelcastInstance hazelcastInstance;
    public Customer customer;
    public SerializationService serializationService;
    public long totalSize;
    public Random random;
    public long start, end;
    public int newRandom;

    public Benchmark()
    {
        config = new Config();
        config.setGroupConfig(new GroupConfig("identifieddataserializable"));
        config.getSerializationConfig().addDataSerializableFactory(1, new CustomerFactory());
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        customerMap = hazelcastInstance.getMap("customers");
        serializationService = new SerializationServiceBuilder().setConfig(config.getSerializationConfig()).build();
        random = new Random();
    }

    public double getReadPerformance()
    {
        start = System.currentTimeMillis();

        for(int i = 0; i < MainBenchmark.TEST_CASE_COUNT; i++)
        {
            newRandom = random.nextInt(MainBenchmark.MAX_RANDOM);
            customerMap.get(newRandom);
        }

        end = System.currentTimeMillis();
        return (end - start);
    }

    public double getWritePerformance()
    {
        start = System.currentTimeMillis();

        for(int i = 0; i < MainBenchmark.TEST_CASE_COUNT; i++)
        {
            newRandom = random.nextInt(MainBenchmark.MAX_RANDOM);
            customer = new Customer("MyNameIs" + newRandom, new Date(newRandom), ((newRandom % 2) == 0) ? Customer.Sex.MALE : Customer.Sex.FEMALE, "MyEmailIs" + newRandom, new long[newRandom]);
            customerMap.set(newRandom, customer);
        }

        end = System.currentTimeMillis();
        return (end - start);
    }

    public double getAverageSize()
    {
        totalSize = 0;

        for(int i = 0; i < MainBenchmark.TEST_CASE_COUNT; i++)
        {
            newRandom = random.nextInt(MainBenchmark.MAX_RANDOM);
            customer = new Customer("MyNameIs" + newRandom, new Date(newRandom), ((newRandom % 2) == 0) ? Customer.Sex.MALE : Customer.Sex.FEMALE, "MyEmailIs" + newRandom, new long[newRandom]);
            totalSize += (serializationService.toData(customer).bufferSize());
        }

        return (totalSize / MainBenchmark.TEST_CASE_COUNT);
    }
}