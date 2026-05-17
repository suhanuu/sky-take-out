package com.sky.test;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;


public class PoiTest {

    //操作excel文件
    @Test
    public void testImport() throws Exception {

        read();
    }
    public void write() throws Exception
    {
        //创建一个excel文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        //在excel文件中创建一个sheet
        XSSFSheet sheet = workbook.createSheet("员工信息");
        //创建一行
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("编号");
        row.createCell(1).setCellValue("姓名");
        //创建第二行
        row = sheet.createRow(1);
        row.createCell(0).setCellValue(1);
        row.createCell(1).setCellValue("张三");
        //创建第三行
        row = sheet.createRow(2);
        row.createCell(0).setCellValue(2);
        row.createCell(1).setCellValue("李四");
        //创建第四行
        row = sheet.createRow(3);
        row.createCell(0).setCellValue(4);
        row.createCell(2).setCellValue("王六");
        //创建第五行
        row = sheet.createRow(4);
        row.createCell(0).setCellValue(3);
        row.createCell(1).setCellValue("王五");


        FileOutputStream fos = new FileOutputStream("C:\\Users\\王淑涵\\Desktop\\info.xlsx");
        //写入excel文件
        workbook.write(fos);
        //关闭流
        fos.close();
        workbook.close();
    }
    //读取excel文件
    public void read() throws Exception
    {
        //创建一个excel文件
        XSSFWorkbook workbook = new XSSFWorkbook("C:\\Users\\王淑涵\\Desktop\\info.xlsx");
        XSSFSheet sheet = workbook.getSheet("员工信息");
        Integer rowNum = sheet.getLastRowNum();
        System.out.println(rowNum);
        //循环读取每一行数据
        for (int i = 0; i <= rowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            if(row != null)
            {
                //循环读取每一列数据
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    System.out.print(row.getCell(j)+" ");
                }
            }

        }
        workbook.close();
    }


}
