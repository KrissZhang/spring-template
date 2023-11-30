package com.self.common.api.export.test;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 测试导出信息
 */
@HeadFontStyle(fontName = "微软雅黑", fontHeightInPoints= 12)
@ColumnWidth(15)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestExport {

    @ExcelProperty(value = "ID", index = 0)
    private Long id;

    @ColumnWidth(20)
    @ExcelProperty(value = "名称", index = 1)
    private String name;

    @ExcelProperty(value = "数值", index = 2)
    private Double value;

    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "时间", index = 3)
    private Date date;

}
