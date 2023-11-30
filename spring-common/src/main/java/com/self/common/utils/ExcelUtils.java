package com.self.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 导出Excel(07版.xlsx)到web
     * @param response  响应
     * @param excelName Excel名称
     * @param sheetName sheet页名称
     * @param tClass    Excel要转换的类型
     * @param data      要导出的数据
     * @throws Exception Exception
     */
    public static void exportToWeb(HttpServletResponse response, String excelName, String sheetName, Class tClass, List data) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        //这里URLEncoder.encode可以防止中文乱码
        excelName = URLEncoder.encode(excelName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + excelName + ExcelTypeEnum.XLSX.getValue());
        EasyExcel.write(response.getOutputStream(), tClass).sheet(sheetName).doWrite(data);
    }

}
