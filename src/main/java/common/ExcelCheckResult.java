package common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ExcelCheckResult
 * @Description: 功能描述
 * @Author: byl
 * @Date: 2020/9/7
 * @Version 1.0
*/
@Data
public class ExcelCheckResult<T> {
    private List<T> successDtos;

    private List<ExcelCheckErrDto<T>> errDtos;

    public ExcelCheckResult(List<T> successDtos, List<ExcelCheckErrDto<T>> errDtos){
        this.successDtos =successDtos;
        this.errDtos = errDtos;
    }

    public ExcelCheckResult(List<ExcelCheckErrDto<T>> errDtos){
        this.successDtos =new ArrayList<>();
        this.errDtos = errDtos;
    }
}
