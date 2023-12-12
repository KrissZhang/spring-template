package com.self.biz.excel.listener.test;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.google.common.collect.Lists;
import com.self.common.api.importo.test.TestImport;
import com.self.dao.entity.Test;
import com.self.dao.mapper.TestMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试导入监听器
 */
public class TestListener<T> extends AnalysisEventListener<T> {

    private LocalValidatorFactoryBean validator;

    private TestMapper testMapper;

    private List<TestImport> importList = Lists.newArrayList();

    public TestListener(LocalValidatorFactoryBean validator, TestMapper testMapper){
        this.validator = validator;
        this.testMapper = testMapper;
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        ReadSheetHolder currentReadHolder = (ReadSheetHolder)context.currentReadHolder();
        headMap = headMap.entrySet().stream().filter(r -> StringUtils.isNotBlank(r.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Integer headRowNumber = currentReadHolder.getHeadRowNumber();
        if(currentReadHolder.getApproximateTotalRowNumber() < headRowNumber){
            throw new ExcelAnalysisException("excel解析失败，请用正确模板导入！");
        }
        Integer rowIndex = currentReadHolder.getRowIndex();
        if (headRowNumber != rowIndex + 1) {
            return;
        }
        ExcelReadHeadProperty headProperty = currentReadHolder.getExcelReadHeadProperty();
        Map<Integer, Head> templateHead = headProperty.getHeadMap();
        if (headMap.size() != templateHead.size()) {
            throw new ExcelAnalysisException("excel解析失败，请用正确模板导入！");
        }

        for (Map.Entry<Integer, Head> entry : templateHead.entrySet()) {
            Integer key = entry.getKey();
            String columnName = entry.getValue().getHeadNameList().get(0);
            if(!columnName.equals(headMap.get(key))){
                throw new ExcelAnalysisException("excel解析失败，请用正确模板导入！");
            }
        }
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        TestImport testImport = (TestImport) t;

        List<ConstraintViolation<TestImport>> checkResultList = Lists.newArrayList(validator.validate(testImport));
        if(!CollectionUtils.isEmpty(checkResultList)){
            throw new ExcelAnalysisException("excel解析失败，" + checkResultList.get(0).getMessage() + "！");
        }

        if(StringUtils.length(testImport.getName()) > 10){
            throw new ExcelAnalysisException("excel解析失败，名称超过限制的长度！");
        }

        importList.add(testImport);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if(CollectionUtils.isEmpty(importList)){
            return;
        }

        try{
            List<Test> saveList = Lists.newArrayListWithCapacity(importList.size());

            for (TestImport testImport : importList) {
                Test test = new Test();
                test.setName(testImport.getName());
                test.setValue(testImport.getValue());
                test.setIsDeleted(NumberUtils.LONG_ZERO);
                test.setCreateBy(NumberUtils.LONG_ONE);
                test.setCreateTime(testImport.getTime());
                test.setUpdateBy(NumberUtils.LONG_ONE);
                test.setUpdateTime(testImport.getTime());

                saveList.add(test);
            }

            testMapper.insertBatch(saveList);
        }catch (Exception e){
            throw new ExcelAnalysisException(e.getMessage());
        }
    }

}
