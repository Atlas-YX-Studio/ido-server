/**
 * 文件名：FileOperate.java
 * 版本信息：
 * 日期：2013-6-21
 * Copyright zc Corporation 2013
 * 版权所有
 */
package com.bixin.ido.server.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author zhangcheng
 * create   2021/9/27
 */
@Slf4j
public class FileOperateUtil {


    /**
     * 新建目录
     */
    public static void newFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.mkdir();
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 新建文件
     */
    public static void newFile(String filePathAndName, String fileContent) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            String strContent = fileContent;
            myFile.println(strContent);
            resultFile.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 删除文件
     */
    public static void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myDelFile = new File(filePath);
            myDelFile.delete();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 删除文件夹
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (myFilePath.delete()) { // 删除空文件夹
                log.info("删除文件夹" + folderPath + "操作 成功执行");
            } else {
                log.info("删除文件夹" + folderPath + "操作 执行失败");
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                // delAllFile(path+"/"+ tempList[i]);//先删除文件夹里面的文件
                delFolder(path + File.separatorChar + tempList[i]);// 再删除空文件夹
            }
        }
        log.info("删除文件操作 成功执行");
    }

    /**
     * 获得文件夹下最新的一个文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public static String getNewestFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        if (!file.isDirectory()) {
            return null;
        }
        String[] tempList = file.list();
        File f = null;
        File temp = null;
        Long newestTime = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            Long modify = temp.lastModified();
            if (newestTime == null) {
                newestTime = modify;
                f = temp;
            } else if (modify > newestTime) {
                newestTime = modify;
                f = temp;
            }
        }
        if (f == null)
            return null;
        return f.getPath();
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            log.info("删除文件夹操作 成功执行");
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    // 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
            log.info("复制文件夹操作 成功执行");
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
