package com.xstocks.statistics.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Slf4j
public class ExcelUtils {


    /**
     * @param is   导入文件输入流
     * @param clazz Excel实体映射类
     * @return
     */
    public static <T extends BaseRowModel> List<T> readExcel(InputStream is, final Class<? extends BaseRowModel> clazz){

        List<T> rowsList = null;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(is);
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener();
            ExcelReader excelReader = EasyExcelFactory.getReader(bis, listener);
            excelReader.read(new Sheet(1, 1, clazz));
            rowsList = listener.getRows();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return rowsList;
    }

    /**
     *
     * @param os 文件输出流
     * @param clazz Excel实体映射类
     * @param data 导出数据
     * @return
     */
    public static Boolean writeExcel(OutputStream os, Class clazz, List<? extends BaseRowModel>data){

        BufferedOutputStream bos= null;
        try {
            bos = new BufferedOutputStream(os);
            ExcelWriter writer = new ExcelWriter(bos, ExcelTypeEnum.XLSX);
            //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
            Sheet sheet1 = new Sheet(1, 0,clazz);
            final ExcelWriter write = writer.write(data, sheet1);
            writer.finish();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * ResponseEntity下载文件
     *
     * @param fileName
     * @param byteOutPutStream
     */
    public static ResponseEntity<byte[]> downloadExcel(String fileName, ByteArrayOutputStream byteOutPutStream) {

        //下载文件
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName=java.net.URLEncoder.encode(fileName, "UTF-8"));//处理文件名中文乱码问题

            ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(byteOutPutStream.toByteArray(), headers, HttpStatus.CREATED);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 导出excel
     * @param fileName
     * @param clazz
     * @param dataList
     * @return
     * @param <T>
     */
    public static <T extends BaseRowModel> ResponseEntity downloadExcel(String fileName, Class clazz, List<T> dataList) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            boolean ret = ExcelUtils.writeExcel(bos, clazz, dataList);
            if (!ret) {
                log.error(fileName + "生成文件失败");
                throw new RuntimeException(fileName + "生成文件失败");
            }
            log.info(fileName + "开始下载导出的Excel文件");
            fileName = fileName + ".xlsx";
            return ExcelUtils.downloadExcel(fileName, bos);
        } catch (Exception e) {
            log.error(fileName + "文件生成异常", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> List<T> readExcel(MultipartFile excel, BaseRowModel rowModel) throws IOException {
        return readExcel(excel, rowModel, 1, 1);
    }

    /**
     * 读取某个 sheet 的 Excel
     * @param excel       文件
     * @param rowModel    实体类映射，继承 BaseRowModel 类
     * @param sheetNo     sheet 的序号 从1开始
     * @param headLineNum 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static <T extends BaseRowModel> List<T> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo, int headLineNum) throws IOException {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);
        if (reader == null) {
            return null;
        }
        reader.read(new Sheet(sheetNo, headLineNum, rowModel.getClass()));
        return excelListener.getRows();
    }

    /**
     * 读取指定sheetName的Excel(多个 sheet)
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @return Excel 数据 list
     * @throws IOException
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel,String sheetName) throws IOException {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = getReader(excel, excelListener);
        if (reader == null) {
            return null;
        }
        for (Sheet sheet : reader.getSheets()) {
            if (rowModel != null) {
                sheet.setClazz(rowModel.getClass());
            }
            //读取指定名称的sheet
            if(sheet.getSheetName().contains(sheetName)){
                reader.read(sheet);
                break;
            }
        }
        return excelListener.getRows();
    }

    /**
     * 返回 ExcelReader
     * @param excel 需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     * @throws IOException
     */
    private static ExcelReader getReader(MultipartFile excel,ExcelListener excelListener) throws IOException {
        String filename = excel.getOriginalFilename();
        if(filename != null && (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx"))){
            InputStream is = new BufferedInputStream(excel.getInputStream());
            return new ExcelReader(is, null, excelListener, false);
        }else{
            return null;
        }
    }


    /*public static void main(String[] args) {
        //1.导入Excel
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("c:\\test\\test1.xls");
           *//* List<UserExcelModel> userExcelModelList = ExcelUtils.readExcel(fis, UserExcelModel.class);
            System.out.println("导入是否成功：-------------->"+"数据行数是："+userExcelModelList.size());*//*
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //2.导出Excel
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("D:\\export.xlsx");
            //FileOutputStream fos, Class clazz, List<? extends BaseRowModel> data
            List<UserExcelModel> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                UserExcelModel excelEntity = new UserExcelModel();
                excelEntity.setUserName("我是名字" + i);
                list.add(excelEntity);
            }
            Boolean flag = ExcelUtils.writeExcel(fos, UserExcelModel.class, list);
            System.out.println("导出是否成功：" + flag);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }*/
}
