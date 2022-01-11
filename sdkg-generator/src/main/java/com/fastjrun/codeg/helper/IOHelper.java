/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.helper;

import java.io.File;

public class IOHelper {

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dirName 将要删除的文件目录路径
     *
     * @return boolean Returns "true" if all deletions were successful. If a
     * deletion fails, the method stops attempting to delete and returns
     * "false".
     */
    public static boolean deleteDir(String dirName) {
        File dir = new File(dirName);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(dirName + File.separatorChar
                        + children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空或者文件，可以删除
        return dir.delete();
    }
}
