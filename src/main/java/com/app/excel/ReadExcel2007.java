package com.app.excel;


import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Properties;

public class ReadExcel2007 {
    public static void main(String[] args) {
        System.out.println(XSSFCell.class);
        HttpClient4 httpClient4 = new HttpClient4();
//        String excelName = "D:\\work\\test001.xlsx";
//        // 地址的列数
//        int addressCellIndex = 5 - 1;
//        // 要修改单元格列数
//        int updateIndex = 15 - 1;
//        int sheetIndex = 0;
        Properties properties = loadProps();
        // 地址的列数
        int addressCellIndex = Integer.parseInt(properties.getProperty("addressCellIndex")) - 1;
        // 要修改单元格列数
        int updateIndex = Integer.parseInt(properties.getProperty("updateIndex")) - 1;
        int sheetIndex = Integer.parseInt(properties.getProperty("sheetIndex")) - 1;
        String excelName = properties.getProperty("excelPath");
        try {
            FileInputStream file = new FileInputStream(excelName);
            // 延迟解析比率
            ZipSecureFile.setMinInflateRatio(-1.0d);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            file.close();
            XSSFRow row = null;

            for (int i = 0; sheet.getRow(i) != null; i++) {
                row = sheet.getRow(i);
                XSSFCell cell = row.getCell(addressCellIndex);
                String cellContent = cell.getStringCellValue();
                if (i == 0) {
                    row.createCell(updateIndex).setCellValue("文件是否下线");
                }
                if (cellContent.startsWith("https") || cellContent.startsWith("http")) {
                    System.out.println("正在请求的地址："+cellContent);
                    try {
                        String s = httpClient4.doGet(cellContent);
                        if (s != null) {
                            if (s.contains("File was deleted") || s.contains("File not found")) {
                                row.createCell(updateIndex).setCellValue("Removed");
                                System.out.println("文件下线," + cellContent);
                            }else {
                                row.createCell(updateIndex).setCellValue("online");
                                System.out.println("文件在线," + cellContent);
                            }
                        } else {
                            row.createCell(updateIndex).setCellValue("Removed");
                            System.out.println("文件下线," + cellContent);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            //将数据写入文件
            FileOutputStream out = new FileOutputStream(excelName);
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
            System.out.println("将结果写入excl文件中。。。。");
            System.out.println("程序运行结果");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient4.closeHttpClient();
        }
    }
    public static Properties loadProps() {
        Properties props = new Properties();
        InputStream in = null;
        try {
            //读取配置文件
            in =new BufferedInputStream(new FileInputStream("./readExcel.properties"));
            props.load(in);
        } catch (Exception e) {
            System.out.println("读取jdbc.properties时出现异常：" + e);
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("jdbc.properties文件流关闭出现异常");
            }
        }
        return props;
    }
}