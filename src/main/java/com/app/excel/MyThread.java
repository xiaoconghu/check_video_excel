package com.app.excel;

import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MyThread implements Runnable {
    private HttpClient4 httpClient4;
    private int updateIndex;
    private XSSFRow XSSFRow;
    private String cellContent;
    private CountDownLatch countDownLatch;
    private String[] offlineKeywords;
    private String[] onlineKeywords;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public MyThread(HttpClient4 httpClient4, int updateIndex, XSSFRow XSSFRow, String cellContent,
                    String[] offlineKeywords,String[] onlineKeywords) {
        this.httpClient4 = httpClient4;
        this.updateIndex = updateIndex;
        this.XSSFRow = XSSFRow;
        this.cellContent = cellContent;
        this.offlineKeywords = offlineKeywords;
        this.onlineKeywords = onlineKeywords;
    }

    @Override
    public void run() {
        requestBaidu(httpClient4, updateIndex, XSSFRow, cellContent);
    }

    private void requestBaidu(HttpClient4 httpClient4, int updateIndex, XSSFRow row, String cellContent) {
        try {
            String s = httpClient4.doGet(cellContent);
            if (s != null) {
                boolean isUnknown = true;
                // 判断下线
                for (String keyword : this.offlineKeywords) {
                    if (s.contains(keyword)) {
                        row.createCell(updateIndex).setCellValue("Removed");
                        row.createCell(updateIndex + 1).setCellValue(keyword);
                        System.out.println(row.getRowNum()+": Removed," + keyword);
                        isUnknown = false;
                        break;
                    }
                }
                // 判断在线
                for (String keyword : this.onlineKeywords) {
                    if (s.contains(keyword)) {
                        row.createCell(updateIndex).setCellValue("Online");
                        row.createCell(updateIndex + 1).setCellValue(keyword);
                        System.out.println(row.getRowNum()+": online," + keyword);
                        isUnknown = false;
                        break;
                    }
                }
                if(isUnknown){
                    row.createCell(updateIndex).setCellValue("Unknown");
                    row.createCell(updateIndex + 1).setCellValue("根据目前的关键字无法判断");
                    System.out.println(row.getRowNum()+": Unknown," + "根据目前的关键字无法判断");
                }
            } else {
                row.createCell(updateIndex).setCellValue("time out");
                row.createCell(updateIndex + 1).setCellValue("Removed response is null");
                System.out.println(row.getRowNum()+": request time out");
            }
        } catch (Exception e) {
            row.createCell(updateIndex).setCellValue("fail");
            row.createCell(updateIndex + 1).setCellValue(e.getMessage());
            System.out.println(row.getRowNum()+": fail,Removed request fail");
            System.out.println(e.getMessage());
        } finally {
            countDownLatch.countDown();
        }

    }
}
