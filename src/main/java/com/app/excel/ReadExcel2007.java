package com.app.excel;


import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReadExcel2007 implements Runnable {

    public static void main(String[] args) {
        System.out.println("开启多线程请求任务。。。。");
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        HttpClient4 httpClient4 = new HttpClient4();
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

            CountDownLatch countDownLatch = new CountDownLatch(sheet.getLastRowNum());

            for (int i = 0; sheet.getRow(i) != null; i++) {
                row = sheet.getRow(i);
                XSSFCell cell = row.getCell(addressCellIndex);
                String cellContent = cell.getStringCellValue();
                if (i == 0) {
                    row.createCell(updateIndex).setCellValue("文件是否下线");
                    row.createCell(updateIndex + 1).setCellValue("代码扫描原因");
                }
                if (cellContent.startsWith("https") || cellContent.startsWith("http")) {
                    MyThread myThread = new MyThread(httpClient4, updateIndex, row, cellContent);
                    myThread.setCountDownLatch(countDownLatch);
                    executorService.submit(myThread);
                }
            }
            //将数据写入文件
            countDownLatch.await();
            FileOutputStream out = new FileOutputStream(excelName);
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();
            System.out.println("将结果写入excl文件中。。。。");
            httpClient4.closeHttpClient();
            System.out.println("关闭多线程。。。。");
            executorService.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("程序运行结果");
        }
    }


    public static Properties loadProps() {
        Properties props = new Properties();
        InputStream in = null;
        try {
            //读取配置文件
            in = new BufferedInputStream(new FileInputStream("./readExcel.properties"));
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

    @Override
    public void run() {

    }
}