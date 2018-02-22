package co.solinx.kafka.monitor.core.service;

import co.solinx.kafka.monitor.model.Message;
import co.solinx.kafka.monitor.model.Topic;
import co.solinx.kafka.monitor.model.Partition;
import co.solinx.kafka.monitor.model.Broker;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/15.
 */
public class MessageService {

    KafkaBaseInfoService kafkaService = KafkaBaseInfoService.getInstance();

    public List<Message> getMesage(String topicName, int partitionID, int offset, int count) {
        Topic topic = kafkaService.getTopic(topicName);
        Partition partition = topic.getPartition(partitionID);
        Broker broker = kafkaService.getBrokerById(partition.getLeader().getId());

        SimpleConsumer consumer = new SimpleConsumer(broker.getHost(), broker.getPort(), 10000, 10000, "");
        FetchRequestBuilder requestBuilder = new FetchRequestBuilder()
                .clientId("kafkaMonitor")
                .maxWait(5000)
                .minBytes(1);
        List<Message> messageList = new ArrayList<>(count);
        long currentOffset = offset;
        while (messageList.size() < count) {
            kafka.api.FetchRequest request = requestBuilder.addFetch(topicName, partitionID, currentOffset, 1024 * 1024).build();

            kafka.javaapi.FetchResponse response = consumer.fetch(request);
            ByteBufferMessageSet messageSet = response.messageSet(topicName, partitionID);
            if (messageSet.validBytes() <= 0) break;

            int oldSize = messageList.size();
            StreamSupport.stream(messageSet.spliterator(), false)
                    .limit(count - messageList.size())
                    .map(MessageAndOffset::message)
                    .map((msg) -> {
                        Message mmsg = new Message();
                        if (msg.hasKey()) {
                            mmsg.setKey(readString(msg.key()));
                        }
                        if (!msg.isNull()) {
                            mmsg.setMessage(readString(msg.payload()));
                        }
                        mmsg.setValid(msg.isValid());
                        mmsg.setCompressionCodec(msg.compressionCodec().name());
                        mmsg.setChecksum(msg.checksum());
                        return mmsg;
                    }).forEach(messageList::add);
            currentOffset += messageList.size() - oldSize;

        }
        consumer.close();
        return messageList;
    }

    private String readString(ByteBuffer buffer) {
        try {
            return new String(readBytes(buffer), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "<unsupported encoding>";
        }
    }

    private byte[] readBytes(ByteBuffer buffer) {
        return readBytes(buffer, 0, buffer.limit());
    }

    private byte[] readBytes(ByteBuffer buffer, int offset, int size) {
        byte[] dest = new byte[size];
        if (buffer.hasArray()) {
            System.arraycopy(buffer.array(), buffer.arrayOffset() + offset, dest, 0, size);
        } else {
            buffer.mark();
            buffer.get(dest);
            buffer.reset();
        }
        return dest;
    }

}
