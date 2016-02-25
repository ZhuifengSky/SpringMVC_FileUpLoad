package com.main.user.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.main.common.bean.Page;
import com.main.common.bean.PageBean;
import com.main.common.bean.Result;
import com.main.common.util.DateUtil;
import com.main.common.util.RandomStrUtil;
import com.main.user.bean.StudentBean;
import com.main.user.model.Student;
import com.main.user.service.IStudentService;

/**
 * 用户控制类
 * @author zhangwu
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Resource
	private IStudentService studentServiceImpl;
	@Resource
	private HttpServletRequest request;
	
	
	@RequestMapping(value = "/uploadimg.do", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<Result> handleFormUpload(
			MultipartFile file, Model model) {
		System.out.println("方法开始执行了！");
		System.out.println(file.isEmpty());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		try {
			if (!file.isEmpty()) {
				String suffix = null;
				int index = file.getOriginalFilename().lastIndexOf(".");
				if (index != -1
						&& index != file.getOriginalFilename().length() - 1) {
					suffix = file.getOriginalFilename().substring(index + 1);
				}
				if (suffix == null || "png|jpg|bmp".indexOf(suffix) == -1) {
					Result result = new Result();
					result.setMessage("请上传图片文件");
					result.setResultCode("1001"); // 1001为 文件格式不正确
					HttpEntity<Result> httpEntity = new HttpEntity<Result>(
							result, headers);
					return httpEntity;
				}
				Result result = new Result();
				InputStream is = file.getInputStream();
				BufferedImage src = ImageIO.read(is);
				if (src.getWidth() != 222 || src.getHeight() != 192) {
					result.setMessage("图片大小不符合规范");
					result.setResultCode("1002"); // 1002为图片大小不符合规范
					HttpEntity<Result> httpEntity = new HttpEntity<Result>(
							result, headers);
					return httpEntity;
				} else {
					String remoteFileName = SaveFileFromInputStream(file.getInputStream(),"D:"+File.separatorChar+File.separatorChar+"image",suffix);

					result.setMessage(remoteFileName);
					result.setResultCode("1000"); // 1000上传成功
					HttpEntity<Result> httpEntity = new HttpEntity<Result>(
							result, headers);
					return httpEntity;
				}
			}
		} catch (Exception e) {
			Result result = new Result();
			result.setMessage("上传失败"+e.getMessage());
			result.setResultCode("1003"); // 1000上传失败
			HttpEntity<Result> httpEntity = new HttpEntity<Result>(
					result, headers);
			return httpEntity;
		}
		return null;

	}
	
	
	@RequestMapping(value = "/UploadImg.do")
	@ResponseBody
	public String UploadImg(Model model) {
		System.out.println("方法开始执行了！");
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();
		// 即硬盘缓存 1M
		diskFactory.setSizeThreshold(4 * 1024);
		ServletFileUpload upload = new ServletFileUpload(diskFactory);
		// 设置允许上传的最大文件大小 4M
		upload.setSizeMax(4 * 1024 * 1024);
		upload.setHeaderEncoding("utf-8");
		List<FileItem> fileItems = null;
		if(!ServletFileUpload.isMultipartContent(request)){
			return  getError("无上传文件。");
		}
		try {
			fileItems = upload.parseRequest(request);
		} catch (FileUploadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Iterator<FileItem> iter = fileItems.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			String fileName = item.getName();
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			// 如果不是表单普通内容
			if (!item.isFormField()) {
				if (fileExt == null || "png|jpg|bmp".indexOf(fileExt) == -1) {
					return  getError("请上传图片文件。");
				}
				if(item.getSize() > 10000){
					return  getError("上传文件大小超过限制。");
				}
				InputStream is;
				String remoteFileName = null;
				try {
					is = item.getInputStream();
					remoteFileName = SaveFileFromInputStream(is,"D:"+File.separatorChar+File.separatorChar+"image",fileExt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return remoteFileName;
			}else{
				return  getError("文件上传出错。");
			}
		}
		return  getError("无上传文件。");
	}
	
	
	
	
	
	
	
    public String SaveFileFromInputStream(InputStream stream,String path,String suffix) throws IOException   
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
	
    private String getError(String message) {
    	JSONObject obj = new JSONObject();
    	obj.put("error", 1);
    	obj.put("message", message);
    	return obj.toJSONString();
    }
    
    /**
     * 图片上传
     * @param ctx
     * @throws IOException
     */
    @RequestMapping(value = "/UploadImgEditor.do")
	@ResponseBody
    public void upload_img(HttpServletRequest ctx) throws IOException {
        File imgFile = (File) ctx.getAttribute("imgFile");
        /*if(imgFile.length() > MAX_IMG_SIZE ){
            ctx.output_json(
                new String[]{"error","message"}, 
                new Object[]{1,ResourceUtils.getString("error", "file_too_large", MAX_IMG_SIZE/1024)}
            );
            return ;
        }
        String uri = new SimpleDateFormat("yyyyMMdd").format(new Date()) 
            + "/IMG_"
            + RandomStringUtils.randomAlphanumeric(4) 
            + '_'
            + String.valueOf(ctx.user().getId()) 
            + '.'
            + FilenameUtils.getExtension(imgFile.getName()).toLowerCase();
     
        Multimedia.saveImage(imgFile, img_path + uri, 0, 0);
        ctx.output_json(new String[]{"error","url"}, new Object[]{0, LinkTool.upload("space/"+uri)});*/
    }

    
    
    
    
    
	@RequestMapping("/addUser.do")
	public String addUser(Student student){
		studentServiceImpl.saveStudent(student);		
		return "redirect:/user/listUser.do";
		
	}
	
	@RequestMapping("/listUser.do")
	public String listUser(StudentBean queryBean,PageBean pageBean,ModelMap map){
		Page<Student> page = studentServiceImpl.findStudents(queryBean,pageBean);
		map.put("page", page);
		map.put("pageBean", page.getPageBean());
		map.put("queryBean", queryBean);
		return "jsp/listStudent";
	}
	
	
	@RequestMapping("/queryUser.do")
	public String queryUser(String sid,ModelMap map){
		Student student = studentServiceImpl.queryStident(sid);
		map.put("s", student);
		return "jsp/addStudent";
	}
	
	
	@RequestMapping("/deleteUser.do")
	public String deleteUser(String sid){
		studentServiceImpl.deleteStudent(sid);
		return "redirect:/user/listUser.do";
	}
	
	@RequestMapping("/updateUser.do")
	public String updateUser(Student student){
		studentServiceImpl.updateStudent(student);
		return "redirect:/user/listUser.do";
	}
}
