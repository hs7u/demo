package com.batch.demo.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.batch.demo.service.PubSubService;
import com.batch.demo.vo.vo.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Service
public class PubSubImpl implements PubSubService {

    private final List<Message> createdMessages = new ArrayList<>();
  
    private final Many<Message> sink = Sinks.many().multicast().directBestEffort();

    @Override
    public Mono<List<Message>> getMessages() {
        return Mono.just(this.createdMessages);
    }

    @Override
    public Mono<Message> createMessage(String empName, String msg) {
        var message = new Message(empName, msg, OffsetDateTime.now());
        this.createdMessages.add(message);
        if (this.sink.currentSubscriberCount() > 0) {
            this.sink.tryEmitNext(message);
        }

        return Mono.just(message);
    }

    @Override
    public Flux<Message> getMessageFlux() {
        return Flux.concat(Flux.fromIterable(this.createdMessages), sink.asFlux());
    }
    
}
