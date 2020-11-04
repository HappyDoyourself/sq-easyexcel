package common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import exception.ServiceException;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName EasyExcelUtils
 * @Description: easyExcel工具类
 * @Author: byl
 * @Date: 2020/9/7
 * @Version 1.0
*/
public class EasyExcelUtils {

    public static final String DOWNLOAD_DIR = "/u01/upload/api-mp-manage";

    public static final String UPLOAD_DIR = "/u01/upload/api-mp-manage";

    public static final String PREFIX = ".xlsx";
    private EasyExcelUtils(){}

    /**
     * 验证上传文件
     * @param uploadFile
     * @return
     * @throws Exception
     */
    public static boolean checkExcel(MultipartFile uploadFile) throws Exception{

        if (uploadFile == null || uploadFile.isEmpty()) {
            throw new ServiceException(400, "未选择需上传的文件");
        }
        String originName = uploadFile.getOriginalFilename();
        int index = originName.indexOf(".");
        String prefixName = originName.substring(index);

        if (!prefixName.equalsIgnoreCase(PREFIX)) {
            throw new ServiceException(400, "上传文件格式不支持");
        }

        return true;
    }

    public static String genDynamicExcelName(String prefix) {
        return prefix +  UUID.randomUUID().toString().replace("-","") + ".xlsx";
    }

    @SuppressWarnings("rawtypes")
	public static void webWriteExcel(HttpServletResponse response, List objects, Class clazz, String fileName) throws IOException {
        String sheetName = fileName;
        webWriteExcel(response,objects,clazz,fileName,sheetName);
    }

    
    @SuppressWarnings("rawtypes")
	public static void webWriteExcel(HttpServletResponse response, List objects, Class clazz, String fileName, String sheetName) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为白
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            EasyExcel.write(outputStream, clazz).registerWriteHandler(horizontalCellStyleStrategy).excelType(ExcelTypeEnum.XLSX).sheet(sheetName).doWrite(objects);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            outputStream.close();
        }
    }


    /**
     * 基于模板导出
     * @param response
     * @param exportList
     * @param clazz
     * @param fileName
     * @param resourceAsStream
     * @throws IOException
     */
    public static void templateWriteExcel(HttpServletResponse response, List exportList, Class clazz, String fileName,InputStream resourceAsStream) throws Exception {
        OutputStream outputStream = getOutputStream(fileName, response);
        EasyExcel.write(outputStream,clazz).excelType(ExcelTypeEnum.XLSX).withTemplate(resourceAsStream).sheet().needHead(false).doWrite(exportList);
    }

    public static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        if (!fileName.contains(ExcelTypeEnum.XLSX.getValue())) {
            fileName = fileName + ExcelTypeEnum.XLSX.getValue();
        }
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName );
        return response.getOutputStream();
    }


    /**
     * 导入失败信息输出(有模板)
     * @param errorFileName    失败文件名称
     * @param exportList       失败内容
     * @param clazz            模型
     * @param resourceAsStream 模板
     */
    public static void writeErrorExcel(String errorFileName, List exportList, Class clazz, InputStream resourceAsStream) {
        writeErrorExcel(DOWNLOAD_DIR, errorFileName, exportList, clazz, resourceAsStream);
    }

    /**
     * 导入失败信息输出(有模板)
     *
     * @param dir              失败文件保存地址路径
     * @param errorFileName    失败文件名称
     * @param exportList       失败内容
     * @param clazz            模型
     * @param resourceAsStream 模板
     */
    public static void writeErrorExcel(String dir, String errorFileName, List exportList, Class clazz, InputStream resourceAsStream) {
        String downloadFilePath = dir + File.separator + "importError" + File.separator + errorFileName;
        File file = new File(downloadFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        EasyExcel.write(downloadFilePath, clazz).excelType(ExcelTypeEnum.XLSX).withTemplate(resourceAsStream).sheet().needHead(false).doWrite(exportList);
    }

    /**
     * 错误信息输出(无模板)
     * @param errorFileName
     * @param exportList
     * @param clazz
     */
    public static void writeErrorExcel(String errorFileName, List exportList, Class clazz) {
        writeErrorExcel(DOWNLOAD_DIR, errorFileName, exportList, clazz);
    }


    /**
     * 错误信息输出(无模板)
     * @param dir
     * @param errorFileName
     * @param exportList
     * @param clazz
     */
    public static void writeErrorExcel(String dir, String errorFileName, List exportList, Class clazz) {
        String downloadFilePath = DOWNLOAD_DIR + File.separator + "importError" + File.separator + errorFileName;
        File file = new File(downloadFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        EasyExcel.write(downloadFilePath, clazz).sheet().doWrite(exportList);
    }
}
