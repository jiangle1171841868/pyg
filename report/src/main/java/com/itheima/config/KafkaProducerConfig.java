package com.itheima.config;

import com.itheima.partition.RoundRobinPartition;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: pyg
 * @author: Mr.Jiang
 * @create: 2019-09-04 19:08
 * @description: 注入kafkaTemplate kafka生产者
 * - springBoot里面没有配置文件
 * - 1.使用注解Configuration  创建配置类
 * - 2.在方法上使用注解Bean 装配不是自定义的类
 **/
@Configuration
@EnableKafka
public class KafkaProducerConfig {

    //todo 1.注入kafka配置属性
    @Value("${kafka.producer.servers}")
    private String servers;
    @Value("${kafka.producer.retries}")
    private int retries;
    @Value("${kafka.producer.batch.size}")
    private int batchSize;
    @Value("${kafka.producer.linger}")
    private int linger;
    @Value("${kafka.producer.buffer.memory}")
    private int memory;

    //todo 2.装配kafkaTemplate
    @Bean
    public KafkaTemplate kafkaTemplate(){

        //将kafka配置封装到map集合里面
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,servers) ;//browker地址
        map.put(ProducerConfig.RETRIES_CONFIG,retries);
        map.put(ProducerConfig.BATCH_SIZE_CONFIG,batchSize);
        map.put(ProducerConfig.LINGER_MS_CONFIG,linger);
        map.put(ProducerConfig.BUFFER_MEMORY_CONFIG,memory);

        //设置key，value的序列化  (网络传输就要设置序列化)
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);

        //避免数据倾斜
        //算法RoundRobin，轮询的算法  设置kafka的分区(自定义分区)
        map.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,RoundRobinPartition.class);

        /**
         * 创建kafka生产者工厂
         * 构造方法
         *    -  参数:kafka生产者配置信息 Map集合
         */
        ProducerFactory kafkaProducerFactory = new DefaultKafkaProducerFactory(map);
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<String, String>(kafkaProducerFactory);
        return kafkaTemplate;
    }

}
