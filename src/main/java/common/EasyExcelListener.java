package common;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @ClassName EasyExcelListener
 * @Description: easyExcel监听器
 * @Author: byl
 * @Date: 2020/9/7
 * @Version 1.0
*/
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
public class EasyExcelListener <T>  extends AnalysisEventListener<T> {

    /**
     * 成功结果集
     */
    private List<T> successList = new ArrayList<>();

    /**
     * 失败结果集
     */
    private List<ExcelCheckErrDto<T>> errList = new ArrayList<>();

    /**
     * 处理逻辑service
     */
    private ExcelCheckManager<T> excelCheckManager;

    private List<T> list = new ArrayList<>();
    public static final int BATCH_MAX = 1000;
    /**
     * excel对象的反射类
     */
    private Class<T> clazz;

    /**
     * 导入相关上下文
     */
    private Object context;

    public EasyExcelListener(ExcelCheckManager<T> excelCheckManager){
        this.excelCheckManager = excelCheckManager;
    }

    public EasyExcelListener(ExcelCheckManager<T> excelCheckManager,Class<T> clazz){
        this.excelCheckManager = excelCheckManager;
        this.clazz = clazz;
    }

    public EasyExcelListener(ExcelCheckManager<T> excelCheckManager, Class<T> clazz, Object context) {
        this.excelCheckManager = excelCheckManager;
        this.clazz = clazz;
        this.context = context;
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        String errMsg;
        try {
            /**
             * 根据excel数据实体中的javax.validation + 正则表达式来校验excel数据
             */
            errMsg = EasyExcelValiHelper.validateEntity(t);
        } catch (NoSuchFieldException e) {
            errMsg = "解析数据出错";
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(errMsg)){
            ExcelCheckErrDto excelCheckErrDto = new ExcelCheckErrDto(t, errMsg);
            errList.add(excelCheckErrDto);
        }else{
            list.add(t);
        }
        /**
         * 每1000条处理一次
         */
        if (list.size() > BATCH_MAX){
            /**
             * 校验
             */
            ExcelCheckResult result = excelCheckManager.checkImportExcel(list, context);
            successList.addAll(result.getSuccessDtos());
            errList.addAll(result.getErrDtos());
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        ExcelCheckResult result = excelCheckManager.checkImportExcel(list, context);
        successList.addAll(result.getSuccessDtos());
        errList.addAll(result.getErrDtos());
        list.clear();
    }


    /**
      * @description: 校验excel头部格式，必须完全匹配
      * @param headMap 传入excel的头部（第一行数据）数据的index,name
      * @param context
      * @throws
      * @return void
      * @author byl
      * @date
      */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);
        if (clazz != null){
            try {
                Map<Integer, String> indexNameMap = getIndexNameMap(clazz);
                Set<Integer> keySet = indexNameMap.keySet();
                for (Integer key : keySet) {
                    if (StringUtils.isEmpty(headMap.get(key))){
                        throw new ExcelAnalysisException("解析excel出错，请传入正确格式的excel");
                    }
                    if (!headMap.get(key).equals(indexNameMap.get(key))){
                        log.error("解析excel出错,请传入正确格式的excel,导入标题{}与检查标题{}不匹配",headMap.get(key),indexNameMap.get(key));
                        throw new ExcelAnalysisException("解析excel出错,请传入正确格式的excel,导入标题"+headMap.get(key)+"与检查标题"+indexNameMap.get(key)+"不匹配");
                    }
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    /**
      * @description: 获取注解里ExcelProperty的value，用作校验excel
      * @param clazz 
      * @throws
      * @return java.util.Map<java.lang.Integer,java.lang.String> 
      * @author byl
      * @date
      */
    public Map<Integer,String> getIndexNameMap(Class clazz) throws NoSuchFieldException {
        Map<Integer,String> result = new HashMap<>(8);
        Field field;
        Field[] fields=clazz.getDeclaredFields();
        for (int i = 0; i <fields.length ; i++) {
            field=clazz.getDeclaredField(fields[i].getName());
            field.setAccessible(true);
            ExcelProperty excelProperty=field.getAnnotation(ExcelProperty.class);
            if(excelProperty!=null){
                int index = excelProperty.index();
                String[] values = excelProperty.value();
                StringBuilder value = new StringBuilder();
                for (String v : values) {
                    value.append(v);
                }
                result.put(index,value.toString());
            }
        }
        return result;
    }

    /*@Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            log.error("第{}行，第{}列解析异常", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex());
        }
    }*/

}
