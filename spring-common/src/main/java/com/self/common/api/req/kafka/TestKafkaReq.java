package com.self.common.api.req.kafka;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "测试Kafka消息参数")
@Data
public class TestKafkaReq {

    @Schema(name = "测试消息", description = "测试消息")
    @NotBlank(message = "测试消息不能为空")
    private String msg;

}
