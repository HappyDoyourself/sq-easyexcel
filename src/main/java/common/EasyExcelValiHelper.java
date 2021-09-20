 
package common;

import com.alibaba.excel.annotation.ExcelProperty;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * @ClassName EasyExcelValiHelper
 * @Description: 功能描述
 * @Author: byl
 * @Date: 2020/9/7
 * @Version 1.0
*/
public class EasyExcelValiHelper {

    private EasyExcelValiHelper(){}
 
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
 
    public static <T> String validateEntity(T obj) throws NoSuchFieldException {
        StringBuilder result = new StringBuilder();
        Set< ConstraintViolation <T> > set = validator.validate(obj, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation <T> cv : set) {
            	Field declaredField = obj.getClass().getDeclaredField(cv.getPropertyPath().toString());
            	ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                result.append(annotation.value()[0]+cv.getMessage()).append(";");
            }
        }
        return result.toString();
    }
 


}