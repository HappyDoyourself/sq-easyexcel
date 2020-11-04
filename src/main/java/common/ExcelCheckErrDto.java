package common;

import lombok.Data;

/**
 * @ClassName ExcelCheckErrDto
 * @Description: 功能描述
 * @Author: byl
 * @Date: 2020/9/7
 * @Version 1.0
*/
@Data
public class ExcelCheckErrDto<T> {

    private T t;

    private String errMsg;

    public ExcelCheckErrDto(){}

    public ExcelCheckErrDto(T t, String errMsg){
        this.t = t;
        this.errMsg = errMsg;
    }
}
