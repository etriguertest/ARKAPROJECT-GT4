package com.arka.notification.queue;

import com.arka.notification.dto.WebhookRequestDto;
import com.arka.notification.repository.N8nWebhookService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class MessageConsumer {
    private final N8nWebhookService sern8nWebhookServicevice;
//     The method listens for messages on the specified queue name
//     The framework handles polling, message receipt, and automatic deletion on success.
    @SqsListener("${queue-name-order-notification}")
    public void listen(@Payload WebhookRequestDto message) {
        System.out.println("Message");
        System.out.println("Received message: " + message.getEmailCustomer());
        SqsTransformer sqsTransformer = new SqsTransformer();
        sern8nWebhookServicevice.callN8nWebhook(message);
    }

//    @SqsListener("${queue-name-order-notification}")
//    public void listen(@Payload String message,
//                       @Header("Type") String infoAttribute) {
//        if(infoAttribute.equals("")){
//
//        }else if(infoAttribute.equals("")){
//
//        }else{
//
//        }
//    }
    // For consuming complex JSON objects, you can use your custom class
    /*
    @SqsListener("my-object-queue")
    public void listen(MyObject payload) {
        System.out.println("Received object: " + payload.getData());
    }
    */
}