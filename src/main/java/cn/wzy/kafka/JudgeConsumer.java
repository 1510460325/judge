package cn.wzy.kafka;

import cn.wzy.vo.JudgeResult;
import cn.wzy.vo.JudgeTask;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Log4j
public class JudgeConsumer implements MessageListener<String, String> {

  @Autowired
  private KafkaTemplate<String,String> kafkaTemplate;

  private Executor executor = Executors.newFixedThreadPool(5);

  public void onMessage(ConsumerRecord<String, String> record) {
    log.info("==JudgeConsumer received:" + record.value());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        JudgeTask judgeTask = JSON.parseObject(record.value(), JudgeTask.class);
        JudgeResult result = Judge.judge(judgeTask);
        kafkaTemplate.sendDefault(JSON.toJSONString(result));
      }
    });
  }

}
