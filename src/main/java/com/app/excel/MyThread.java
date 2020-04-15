package com.app.excel;

import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.concurrent.CountDownLatch;

public class MyThread implements Runnable {

    private HttpClient4 httpClient4;
    private int updateIndex;
    private XSSFRow XSSFRow;
    private String cellContent;
    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public MyThread(HttpClient4 httpClient4, int updateIndex, XSSFRow XSSFRow, String cellContent) {
        this.httpClient4 = httpClient4;
        this.updateIndex = updateIndex;
        this.XSSFRow = XSSFRow;
        this.cellContent = cellContent;
    }

    @Override
    public void run() {
        requestBaidu(httpClient4, updateIndex, XSSFRow, cellContent);
    }

    private void requestBaidu(HttpClient4 httpClient4, int updateIndex, XSSFRow row, String cellContent) {
        try {
            String s = httpClient4.doGet(cellContent);
            if (s != null) {
                if (s.contains("File was deleted") || s.contains("File not found")) {
                    row.createCell(updateIndex).setCellValue("Removed");
                    row.createCell(updateIndex + 1).setCellValue("Removed File was deleted File not found");
                    System.out.println("文件下线,Removed File was deleted File not found");
                }
                if (s.contains("share_nofound_des")) {
                    row.createCell(updateIndex).setCellValue("Removed");
                    row.createCell(updateIndex + 1).setCellValue("share_nofound_des");
                    System.out.println(row.getRowNum()+"文件下线,share_nofound_des" );

                } else if (s.contains("video") || s.contains("video-content")) {
                    row.createCell(updateIndex).setCellValue("online");
                    row.createCell(updateIndex + 1).setCellValue("online found video");
                    System.out.println(row.getRowNum()+"文件在线,online found video");

                } else if (s.contains("此链接分享内容可能因为涉及侵权")) {
                    row.createCell(updateIndex).setCellValue("Removed");
                    row.createCell(updateIndex + 1).setCellValue("此链接分享内容可能因为涉及侵权、色情、反动、低俗等信息，无法访问");
                    System.out.println(row.getRowNum()+"文件下线,此链接分享内容可能因为涉及侵权、色情、反动、低俗等信息，无法访问");

                } else if (s.contains("请输入提取码")) {
                    row.createCell(updateIndex).setCellValue("online");
                    row.createCell(updateIndex + 1).setCellValue("online found 请输入提取码");
                    System.out.println(row.getRowNum()+"文件在线,online found 请输入提取码");

                } else if (s.contains("你所访问的页面不存在了")) {
                    row.createCell(updateIndex).setCellValue("Removed");
                    row.createCell(updateIndex + 1).setCellValue("啊哦，你所访问的页面不存在了");
                    System.out.println(row.getRowNum()+"文件下线,啊哦，你所访问的页面不存在了" );

                } else if (s.contains("保存到网盘")) {
                    row.createCell(updateIndex).setCellValue("online");
                    row.createCell(updateIndex + 1).setCellValue("online found 保存到网盘");
                    System.out.println(row.getRowNum()+"文件在线,online found 保存到网盘");

                } else if (s.contains("失效时间：永久有效")) {
                    row.createCell(updateIndex).setCellValue("online");
                    row.createCell(updateIndex + 1).setCellValue("online found 保存到网盘");
                    System.out.println(row.getRowNum()+"文件在线,online found 保存到网盘");

                } else {
                    row.createCell(updateIndex).setCellValue("not found");
                    row.createCell(updateIndex + 1).setCellValue("目前无法判断");
                    System.out.println(row.getRowNum()+"目前无法判断");

                }
            } else {
                row.createCell(updateIndex).setCellValue("Removed");
                row.createCell(updateIndex + 1).setCellValue("Removed response is null");
                System.out.println("文件下线,Removed request time out");
            }
        } catch (Exception e) {
            row.createCell(updateIndex).setCellValue("Removed");
            row.createCell(updateIndex + 1).setCellValue("Removed request fail");
            System.out.println("文件下线,Removed request fail");
            System.out.println(e.getMessage());
        }
        countDownLatch.countDown();
    }
}
