package com.self.common.api.importo.test;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 测试导入信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestImport {

    @ExcelProperty("名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @ExcelProperty("值")
    @NotBlank(message = "值不能为空")
    private String value;

    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("时间")
    @NotNull(message = "时间不能为空")
    private Date time;

}
