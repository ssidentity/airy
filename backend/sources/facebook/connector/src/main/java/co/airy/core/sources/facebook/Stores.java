package co.airy.core.sources.facebook;

import co.airy.avro.communication.Channel;
import co.airy.avro.communication.ChannelConnectionState;
import co.airy.avro.communication.DeliveryState;
import co.airy.avro.communication.Message;
import co.airy.avro.communication.Metadata;
import co.airy.avro.communication.SenderType;
import co.airy.core.sources.facebook.dto.SendMessageRequest;
import co.airy.core.sources.facebook.dto.Conversation;
import co.airy.kafka.schema.application.ApplicationCommunicationChannels;
import co.airy.kafka.schema.application.ApplicationCommunicationMessages;
import co.airy.kafka.schema.application.ApplicationCommunicationMetadata;
import co.airy.kafka.streams.KafkaStreamsWrapper;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static co.airy.model.metadata.MetadataRepository.getSubject;
import static co.airy.model.metadata.MetadataRepository.isConversationMetadata;

@Service
public class Stores implements ApplicationListener<ApplicationStartedEvent>, DisposableBean {
    private static final String appId = "sources.facebook.ConnectorStores";

    private final KafkaStreamsWrapper streams;
    private final String channelsStore = "channels-store";
    private final String applicationCommunicationChannels = new ApplicationCommunicationChannels().name();
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final Connector connector;

    public Stores(KafkaStreamsWrapper streams, KafkaProducer<String, SpecificRecordBase> producer, Connector connector) {
        this.streams = streams;
        this.producer = producer;
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Channel> channelStream = builder.<String, Channel>stream(applicationCommunicationChannels);

        channelStream.toTable(Materialized.as(channelsStore));

        // Channels table
        KTable<String, Channel> channelsTable = channelStream
                .filter((sourceChannelId, channel) -> "facebook".equalsIgnoreCase(channel.getSource())
                        && channel.getConnectionState().equals(ChannelConnectionState.CONNECTED)).toTable();

        // Facebook messaging stream by conversation-id
        final KStream<String, Message> messageStream = builder.<String, Message>stream(new ApplicationCommunicationMessages().name())
                .filter((messageId, message) -> "facebook".equalsIgnoreCase(message.getSource()))
                .selectKey((messageId, message) -> message.getConversationId());

        // Metadata table
        final KTable<String, Map<String, String>> metadataTable = builder.<String, Metadata>table(new ApplicationCommunicationMetadata().name())
                .filter((metadataId, metadata) -> isConversationMetadata(metadata))
                .groupBy((metadataId, metadata) -> KeyValue.pair(getSubject(metadata).getIdentifier(), metadata))
                .aggregate(HashMap::new, (conversationId, metadata, aggregate) -> {
                    aggregate.put(metadata.getKey(), metadata.getValue());
                    return aggregate;
                }, (conversationId, metadata, aggregate) -> {
                    aggregate.remove(metadata.getKey());
                    return aggregate;
                });

        // Conversation table
        final KTable<String, Conversation> conversationTable = messageStream
                .groupByKey()
                .aggregate(Conversation::new,
                        (conversationId, message, aggregate) -> {
                            if (SenderType.SOURCE_CONTACT.equals(message.getSenderType())) {
                                aggregate.setSourceConversationId(message.getSenderId());
                            }

                            aggregate.setChannelId(message.getChannelId());

                            return aggregate;
                        })
                .join(channelsTable, Conversation::getChannelId, (aggregate, channel) -> {
                    aggregate.setChannel(channel);
                    return aggregate;
                });

        // Send outbound messages
        messageStream.filter((messageId, message) -> DeliveryState.PENDING.equals(message.getDeliveryState()))
                .join(conversationTable, (message, conversation) -> new SendMessageRequest(conversation, message))
                .mapValues(connector::sendMessage)
                .to(new ApplicationCommunicationMessages().name());

        // Fetch missing metadata
        conversationTable
                // To avoid any redundant fetch contact operations the suppression interval should
                // be higher than the timeout of the Facebook API
                .suppress(Suppressed.untilTimeLimit(Duration.ofMillis(streams.getSuppressIntervalInMs()), Suppressed.BufferConfig.unbounded()))
                .toStream()
                .leftJoin(metadataTable, (conversation, metadataMap) -> conversation
                        .toBuilder()
                        .metadata(new HashMap<>(Optional.ofNullable(metadataMap).orElse(Map.of())))
                        .build())
                .filter((k, v) -> connector.needsMetadataFetched(v))
                .flatMap(connector::fetchMetadata)
                .to(new ApplicationCommunicationMetadata().name());

        streams.start(builder.build(), appId);
    }

    public ReadOnlyKeyValueStore<String, Channel> getChannelsStore() {
        return streams.acquireLocalStore(channelsStore);
    }

    public void storeChannel(Channel channel) throws ExecutionException, InterruptedException {
        producer.send(new ProducerRecord<>(applicationCommunicationChannels, channel.getId(), channel)).get();
    }

    @Override
    public void destroy() {
        if (streams != null) {
            streams.close();
        }
    }

    // visible for testing
    KafkaStreams.State getStreamState() {
        return streams.state();
    }
}
