package com.main.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class FileUpLoadUtil {

	public static String SaveFileFromInputStream(InputStream stream,String path,String suffix) throws IOException   
    {   
    	String fileName = DateUtil.format(new Date(), DateUtil.DATE)+RandomStrUtil.getCharAndNumr(5);
        String folderName = DateUtil.format(new Date(), DateUtil.DATE);
    	String savePath = path + File.separatorChar+File.separatorChar + folderName + File.separatorChar+File.separatorChar + fileName +"."+suffix;
    	String folderPath =path + File.separatorChar + folderName; 
        File file = new File(folderPath);
    	if(!file .exists() && !file .isDirectory())      
		{       
		    System.out.println("//不存在");  
		    file .mkdir();    
		} else   
		{  
		    System.out.println("//目录存在");  
		}  
    	FileOutputStream fs=new FileOutputStream(savePath);   
        byte[] buffer =new byte[1024*1024];   
        int bytesum = 0;   
        int byteread = 0;    
        while ((byteread=stream.read(buffer))!=-1)   
        {   
           bytesum+=byteread;   
           fs.write(buffer,0,byteread);   
           fs.flush();   
        }    
        fs.close();   
        stream.close();
		return savePath;         
   }         
}
