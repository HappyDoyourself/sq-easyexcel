package common;

import java.util.List;

/**
 * @ClassName ExcelCheckManager
 * @Description: 校验接口
 * @Author: byl
 * @Date: 2020/9/7
 * @Version 1.0
*/
public interface ExcelCheckManager<T> {


    /**
     * 校验方法
     * @param objects
     * @param context
     * @return
     */
    ExcelCheckResult<T> checkImportExcel(List<T> objects, Object context);
}
