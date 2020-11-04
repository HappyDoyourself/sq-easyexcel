package common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author admin
 */
@Data
public class ExcelImportResult {

    @ApiModelProperty("成功数量")
    private Integer successNum;
    @ApiModelProperty("失败数量")
    private Integer errorNum;
    @ApiModelProperty("失败文件名称")
    private String  errorFileName;
}
