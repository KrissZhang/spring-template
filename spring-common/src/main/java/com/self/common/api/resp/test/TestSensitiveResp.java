package com.self.common.api.resp.test;

import com.self.common.annotation.Sensitive;
import com.self.common.enums.SensitiveTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@ApiModel(description = "测试脱敏信息响应参数")
@Data
public class TestSensitiveResp {

    @Schema(name = "id", description = "id")
    private Long id;

    @Schema(name = "中文名称", description = "中文名称")
    @Sensitive(type = SensitiveTypeEnum.CHINESE_NAME)
    private String chineseName;

    @Schema(name = "身份证号码", description = "身份证号码")
    @Sensitive(type = SensitiveTypeEnum.ID_CARD)
    private String idCard;

    @Schema(name = "手机号码", description = "手机号码")
    @Sensitive(type = SensitiveTypeEnum.MOBILE_PHONE)
    private String mobilePhone;

    @Schema(name = "文本", description = "文本")
    @Sensitive(type = SensitiveTypeEnum.CUSTOM, prefixNoMaskLen = 5, suffixNoMaskLen = 5, symbol = "#")
    private String text;

}
