// Copyright 2020 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.nats.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

import io.nats.client.ConsumerConfiguration.AckPolicy;
import io.nats.client.ConsumerConfiguration.DeliverPolicy;
import io.nats.client.ConsumerConfiguration.ReplayPolicy;

public class ConsumerConfigurationTests {
    @Test
    public void testBuilder() {
        LocalDateTime ldt = LocalDateTime.of(2012, Month.JANUARY, 12, 6, 30, 1);

        ConsumerConfiguration c = ConsumerConfiguration.builder()
            .ackPolicy(AckPolicy.Explicit)
            .ackWait(Duration.ofSeconds(99))
            .deliverPolicy(DeliverPolicy.ByStartSequence)
            .durable("foo")
            .filterSubject("fs")
            .maxDelivery(5555)
            .rateLimit(4242)
            .replayPolicy(ReplayPolicy.Original)
            .sampleFrequency("10s")
            .startSequence(2001)
            .startTime(ldt)
            .build();

        c.setDeliverySubject("delsubj");

        assertEquals(AckPolicy.Explicit, c.getAckPolicy());
        assertEquals(Duration.ofSeconds(99), c.getAckWait());
        assertEquals(DeliverPolicy.ByStartSequence, c.getDeliverPolicy());
        assertEquals("delsubj", c.getDeliverSubject());
        assertEquals("foo", c.getDurable());
        assertEquals("fs", c.getFilterSubject());
        assertEquals(5555, c.getMaxDeliver());
        assertEquals(4242, c.getRateLimit());
        assertEquals(ReplayPolicy.Original, c.getReplayPolicy());
        assertEquals(2001, c.getStartSequence());
        assertEquals(ldt, c.getStartTime());
    }

    @Test
    public void testJSONParsing() {
        String configJSON = "{\n    \"durable_name\": \"foo-durable\",\n    \"deliver_subject\": \"bar\",\n    \"deliver_policy\": \"all\",\n    \"ack_policy\": \"all\",\n    \"ack_wait\": 30000000000,\n    \"max_deliver\": 10,\n   \"opt_start_time\": \"2020-11-05T19:33:21.163377Z\", \"opt_start_seq\": \"1234\"\"replay_policy\": \"original\"\n  },\n  \"delivered\": {\n    \"consumer_seq\": 0,\n    \"stream_seq\": 0\n  },\n  \"ack_floor\": {\n    \"consumer_seq\": 0,\n    \"stream_seq\": 0\n  },\n  \"num_pending\": 0,\n  \"num_redelivered\": 0\n}";

        ConsumerConfiguration c = new ConsumerConfiguration(configJSON);
        assertEquals("foo-durable", c.getDurable());
        assertEquals("bar", c.getDeliverSubject());
        assertEquals(DeliverPolicy.All, c.getDeliverPolicy());
        assertEquals(AckPolicy.All, c.getAckPolicy());
        assertEquals(Duration.ofSeconds(30), c.getAckWait());
        assertEquals(10, c.getMaxDeliver());
        assertEquals(ReplayPolicy.Original, c.getReplayPolicy());
    }

}