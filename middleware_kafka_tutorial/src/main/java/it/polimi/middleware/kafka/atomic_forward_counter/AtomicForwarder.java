package it.polimi.middleware.kafka.atomic_forward_counter;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AtomicForwarder {
    private static final String defaultConsumerGroupId = "groupA";
    private static final String defaultInputTopic = "topicA";
    private static final String defaultOutputTopic = "counterTopic";
    private static  final boolean autoCommit = true;
    private static final int autoCommitIntervalMs = 15000;
    private static final String offsetResetStrategy = "latest";

    private static final String serverAddr = "localhost:9092";
    private static final String producerTransactionalId = "forwarderTransactionalId";

    public static void main(String[] args) {

        // Basic Consumer
        final Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, defaultConsumerGroupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // The consumer does not commit automatically, but within the producer transaction
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(defaultInputTopic));

        // Counter State Consumer
        final Properties counterConsumerProps = new Properties();
        counterConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        counterConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, defaultConsumerGroupId);
        counterConsumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(autoCommit));
        counterConsumerProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, String.valueOf(autoCommitIntervalMs));
        counterConsumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetStrategy);
        counterConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        counterConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> getStateConsumer = new KafkaConsumer<String, String>(counterConsumerProps);
        getStateConsumer.subscribe(Collections.singletonList(defaultOutputTopic));

        HashMap<String, Integer> state = new HashMap<>();
        for (final ConsumerRecord<String, String> record : getStateConsumer.poll(Duration.of(5, ChronoUnit.MINUTES))) {
            System.out.println("Collecting initial state...");
            System.out.println("Partition: " + record.partition() +
                    "\tOffset: " + record.offset() +
                    "\tKey: " + record.key() +
                    "\tValue: " + record.value()
            );
            state.putIfAbsent(record.key(), Integer.parseInt(record.value()));
        }
        System.out.println("Finished");
        getStateConsumer.close();

        // Producer
        final Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, producerTransactionalId);
        producerProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, String.valueOf(true));

        final KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);
        producer.initTransactions();

        while (true) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.of(5, ChronoUnit.MINUTES));
            producer.beginTransaction();
            for (final ConsumerRecord<String, String> record : records) {
                state.putIfAbsent(record.key(), 0);
                state.put(record.key(), state.get(record.key()) + 1);
                System.out.println("Key: " + record.key() + "Counter: " + state.get(record.key()));

                state.forEach((key, value) -> {
                    producer.send(new ProducerRecord<String, String>(defaultOutputTopic, key, value.toString()));
                });

                // The producer manually commits the outputs for the consumer within the transaction
                /*
                final Map<TopicPartition, OffsetAndMetadata> map = new HashMap<>();
                for (final TopicPartition partition : records.partitions()) {
                    final List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                    final long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                    map.put(partition, new OffsetAndMetadata(lastOffset + 1));
                }

                producer.sendOffsetsToTransaction(map, defaultConsumerGroupId);
                */

                producer.commitTransaction();
            }
        }
    }
}