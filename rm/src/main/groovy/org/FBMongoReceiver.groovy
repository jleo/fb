//package org
//
//import com.google.common.collect.ImmutableMap
//import kafka.consumer.Consumer
//import kafka.consumer.ConsumerConfig
//import kafka.consumer.KafkaStream
//import kafka.javaapi.consumer.ConsumerConnector
//import kafka.message.Message
//import kafka.message.MessageAndMetadata
//import org.vertx.java.core.json.JsonObject
//
//import java.nio.ByteBuffer
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import java.util.concurrent.atomic.AtomicInteger
//
//def logger = container.getLogger();
//def config = container.config;
//
//def bus = vertx.eventBus
//def received = 0
//
//def getMessage = { Message message ->
//    ByteBuffer buffer = message.payload();
//    byte[] bytes = new byte[buffer.remaining()];
//    buffer.get(bytes);
//    return new String(bytes);
//}
//
//logger.info "deploying mongo-persistor reader"
//container.deployModule('vertx.mongo-persistor-v1.2', config.mongo, 5){
//    logger.info "deployed"
//
//    def messageTopic = "fb"
//    def messageGroup = "fb"
//
//    def zk = "rm4:2181"
//
//    Properties props = new Properties();
//    props.put("zk.connect", zk);
//    props.put("zk.connectiontimeout.ms", "10000");
//    props.put("groupid", messageGroup);
//
//    ConsumerConfig consumerConfig = new ConsumerConfig(props);
//    ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
//    AtomicInteger count = new AtomicInteger(0)
//    AtomicInteger icCount = new AtomicInteger(0)
//
//	logger.info "ready"
//Map<String, List<KafkaStream<Message>>> topicMessageStreams =
//        consumerConnector.createMessageStreams(ImmutableMap.of(messageTopic, 1));
//    List<KafkaStream<Message>> streams = topicMessageStreams.get(messageTopic);
//
//    ExecutorService executor = Executors.newFixedThreadPool(5);
//
//    for (final KafkaStream<Message> stream : streams) {
//	logger.info "stream"
//        executor.submit([run: {
//logger.info "in thread"
//            for (MessageAndMetadata msgAndMetadata : stream) {
//                try {
//                    logger.info("incoming message ${icCount.incrementAndGet()}")
//                    def mStr = getMessage(msgAndMetadata.message())
//                    def crawlerMessage = new JsonObject(mStr)
//
//                    def mongoRequest = new JsonObject()
//                    mongoRequest.putString("action", "save")
//                    mongoRequest.putString("collection", "fbraw")
//                    mongoRequest.putObject("document", crawlerMessage)
//			if(icCount-count >= 100){
//	sleep 1000
//logger.info "sleeping..."
//}
//                    bus.send("mongo-bus", mongoRequest) { mRep ->
//                        logger.info "mongo response ${count.incrementAndGet()}"
//                        if (mRep.body.status == "ok") {
//                            def id = crawlerMessage.getNumber("id")
//                            logger.info("Your document has been saved.id $id")
//                        } else {
//                            logger.error "mongo failed"
//                        }
//                    }
//                } catch (e) {
//                    e.printStackTrace()
//                }
//            }
//        }] as Runnable)
//    }
//}
