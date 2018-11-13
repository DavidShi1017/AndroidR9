package com.cflint.util;


import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class FileManager {
	
	//private static final String TAG = FileManager.class.getSimpleName();
	private String filePath;
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public final static String FOLDER_PDF = "pdf";
	public final static String FOLDER_FILE = "file";
	public final static String POSTFIX_PDF = ".pdf";
	public final static String POSTFIX_ZIP = ".zip";
	public final static String FOLDER_HOMEBANNER = "HomeBanner";
	public final static String FILE_HOMEBANNER = "HomeBanner.json";

	public final static String FOLDER_STATIONBOARD = "StationBoard";
	public final static String FILE_STATIONBOARD = "StationBoard.json";

	private static FileManager instance;

	public static FileManager getInstance() {
		if (instance == null) {
			instance = new FileManager();

		}
		return instance;
	}

	private FileManager() {
		
	}

    public void createExternalStoragePrivateFile(Context context, InputStream inputStream, String folder, String fileName) {
    	
    	
        // Create a path where we will place our private file on external
        // storage.
        File file = new File(context.getExternalFilesDir(folder), fileName);

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
        	//OutputStream outputStream = null;
    		
    		//BufferedInputStream bin = null;
			FileOutputStream output = new FileOutputStream(file);

/*        	bin = new BufferedInputStream(inputStream, 1024);
			outputStream = new BufferedOutputStream(new FileOutputStream(file),
					1024);
			byte buffer[] = new byte[1];
			while (bin.read(buffer) != -1) {
				outputStream.write(buffer);
			}*/
			byte b[] = new byte[1024];
			int j = 0;
			while( (j = inputStream.read(b))!=-1){
				output.write(b,0,j);
			}

			output.flush();
			output.close();
			inputStream.close();
			//InputStream is = inputStream;
            //OutputStream os = new FileOutputStream(file);
           // byte[] data = new byte[inputStream.available()];
            //System.out.println("data=====" + data.length);
           // is.read(data);
           // os.write(data);
/*			bin.close();
			inputStream.close();
			outputStream.close();*/
        } catch (Exception e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing.......... " + file, e);
        }
    }

    public void createExternalStoragePrivateFileFromString(Context context, String folder, String fileName, String data) {
        // Create a path where we will place our private file on external
        // storage.
    	
        //Log.d(TAG, "Write data to sd card.....");
		// write the contents on mySettings to the file
		try {
			OutputStream outputStream = null;
			
			File file = new File(context.getExternalFilesDir(folder), fileName);
			//if(!file.exists()){
			   file.createNewFile();
			//}
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
			// open file for writing
			OutputStreamWriter out = new OutputStreamWriter(outputStream);
			out.write(data);
			// close the file
			out.close();
		} catch (IOException e) {
			// Don't save data to SDCard when no SDcard!!!!
			e.printStackTrace();
		}
    }
    public File getFileDirectory(Context context, String folder, String name){
    	  File file = new File(context.getExternalFilesDir(folder), name);
          String pathString = file.getPath().substring(0, file.getPath().lastIndexOf('/'));
          //Log.d(TAG, "file.getName()....." + file.getName());       
          File f = new File(pathString);
          return f;
    }
    public void deleteExternalStoragePrivateFile(Context context, String folder, String name) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
    	//Log.d(TAG, "name....." + name);  
    	File file = new File(context.getExternalFilesDir(folder), name);     
    	if (file != null) {
    		//Log.e("StationInfo", "file name....." + name);
    		file.delete();
		}           
    	
    }
    public void deleteAllExternalStoragePrivateFile(Context context, String folder) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
    	 
    	File file = context.getExternalFilesDir(folder);     
/*    	if (file != null) {
    		Log.d(TAG, "file name....." + name);  
    		file.delete();
		}   */
		//Log.d("deleteAll", "file.isDirectory()....." + file.isDirectory());
    	if (file.isDirectory()) {
			 File[] files = file.listFiles(); 
			 for (File file2 : files) {
				 if (file2 != null) {
					 file2.delete();
					 //Log.d("deleteAll", "file.isDirectory()....." + file2.getName());
				 }
			 }
    	}
    	  	
    }
	public boolean hasExternalStoragePrivateFile(Context context, String folder, String name) {
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		File file = new File(context.getExternalFilesDir(folder), name);

		if (file.length() > 0) {
			return true;
		} else {
			return false;
		}

	}
    
    public File getExternalStoragePrivateFile(Context context, String folder, String name) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(context.getExternalFilesDir(folder), name);
		//Log.d("file...", "name..." + name);
        return file;
    }
    
    public String getExternalStoragePrivateFilePath(Context context, String folder, String name){
    	File file = new File(context.getExternalFilesDir(folder), name);
    	String path = file.getPath();
    	//Log.d("file...", "file..." + file);
    	return path;
    }
    
    public String readExternalStoragePrivateFile(Context context, String folder, String fileName) {
        // read a path where we will place our private file from external
        // storage.
        File file = new File(context.getExternalFilesDir(folder), fileName);
       
        filePath = file.getPath();
		FileInputStream fileIS = null;
		String fileString = "";		
		// open the file for reading

		try {
			fileIS = new FileInputStream(filePath);
			fileString = DataUtil.ReadStringFromInputStream(fileIS);
		} catch (IOException e) {
			 //Log.w("ExternalStorage", "Error writing " + file, e);
			 return fileString;
		}
		return fileString;
    }
    
    
    public String readFileWithInputStream(InputStream inputStream) {
    	
    	
    	String fileString = "";		

        try {
        	fileString = DataUtil.ReadStringFromInputStream(inputStream);
        	return fileString;
        } catch (Exception e) {
            return fileString;
        }
    }
    
    public void createExternalStoragePrivateZipFile(Context context, ZipInputStream zipInputStream, String folder, String fileName) {
    	
    	
        // Create a path where we will place our private file on external
        // storage.
        File file = new File(context.getExternalFilesDir(folder), fileName);

        ZipEntry zipEntry;
		try {
			zipEntry = zipInputStream.getNextEntry();
			
	        byte[] buffer = new byte[1024 * 1024];  
	         
	        int count = 0;  
	          
	        while (zipEntry != null) {  
	              
	            if (zipEntry.isDirectory()) {  	               
	                 
	                if(!hasExternalStoragePrivateFile(context, folder, fileName)){  
	                    file.mkdir();  
	                }  
	            } else {  
	                  	                	                
	                if(!hasExternalStoragePrivateFile(context, folder, fileName)){  
	                    file.createNewFile();  
	                    FileOutputStream fileOutputStream = new FileOutputStream(file);  
	                    while ((count = zipInputStream.read(buffer)) > 0) {  
	                        fileOutputStream.write(buffer, 0, count);  
	                    }  
	                    fileOutputStream.close();  
	                }  
	            }  
	             
	            zipEntry = zipInputStream.getNextEntry();  
	        }  
	        zipInputStream.close();  
		} catch (IOException e) {
			Log.w("ExternalStorage", "Create zip file error", e);
			e.printStackTrace();
		}  
		
    }

	public String getPdfFileName(String dnr, String pdfId){
		String fileName = dnr + "-" + pdfId + ".pdf";
		return fileName;
	}

	public String getBarcodeName(String dnr, String pdfId){
		String fileName = dnr + "-" + pdfId + ".png";
		return fileName;
	}
}
