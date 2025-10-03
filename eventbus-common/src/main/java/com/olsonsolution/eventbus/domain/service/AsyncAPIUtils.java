package com.olsonsolution.eventbus.domain.service;

import com.asyncapi.schemas.asyncapi.AsyncAPISchema;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.channel.message.Message;
import com.asyncapi.v3._0_0.model.component.Components;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AsyncAPIUtils {

    private static final String SCHEMA_REF_PREFIX = "#/components/schemas/";

    static Optional<AsyncAPISchema> findApiSchema(Components components, String schemaRef) {
        String schemaName = StringUtils.substringAfter(schemaRef, SCHEMA_REF_PREFIX);
        return Optional.ofNullable(components.getSchemas())
                .flatMap(schemas -> Optional.ofNullable(schemas.get(schemaName)))
                .filter(AsyncAPISchema.class::isInstance)
                .map(AsyncAPISchema.class::cast);
    }

    static Optional<Channel> findChannel(String topic, AsyncAPI apiDocs) {
        return Optional.ofNullable(apiDocs.getChannels())
                .flatMap(channels -> Optional.ofNullable(channels.get(topic)))
                .filter(Channel.class::isInstance)
                .map(Channel.class::cast);
    }

    static Optional<Message> findMessage(Channel channel, String messageName) {
        return Optional.ofNullable(channel.getMessages())
                .flatMap(messages -> Optional.ofNullable(messages.get(messageName)))
                .filter(Message.class::isInstance)
                .map(Message.class::cast);
    }


}
