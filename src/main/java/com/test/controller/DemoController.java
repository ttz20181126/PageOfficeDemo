package com.test.controller;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zhuozhengsoft.pageoffice.poserver.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.zhuozhengsoft.pageoffice.*;

/**
 * @author Administrator
 *
 */
@RestController
public class DemoController {
	
	@Value("${posyspath:d:\\lic\\}")
	private String poSysPath;
	
	//@Value("${popassword}")
	//private String poPassWord;

	@Value("${txtPath}")
	private String txtPath;

	@Value("${docPath}")
	private String docPath;

	@RequestMapping("/hello")
	public String test() {
		System.out.println("hello run");
		return "Hello";
	}
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public ModelAndView showIndex(){
		ModelAndView mv = new ModelAndView("Index");
		return mv;
	}

	/**
	 * TODO:d盘新建lic文件夹,在lic文件夹下把序列号.txt放进去。首次访问，根据序列号。txt生成 license.lic
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/word", method=RequestMethod.GET)
	public ModelAndView showWord(HttpServletRequest request, Map<String,Object> map) throws IOException {
		
		PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
		poCtrl.setServerPage("/poserver.zz");//设置服务页面
		poCtrl.addCustomToolButton("保存","Save",1);//添加自定义保存按钮
		//poCtrl.addCustomToolButton("盖章","AddSeal",2);//添加自定义盖章按钮
		poCtrl.addCustomToolButton("提交","submit",2);//提交到文件
		poCtrl.setSaveFilePage("/save");//设置处理文件保存的请求方法

		//打开word
		System.out.println("posyspath:" + poSysPath);
		File folder = new File(poSysPath);
		if(!folder.exists() && !folder.isDirectory()){
			folder.mkdir();
		}
		String filePath = poSysPath + "test.doc";
		File file = new File(filePath);
		if(!file.exists()){
			System.out.println("写出test.doc");
			urlToFile(docPath,"D:\\lic\\test.doc");
		}
		String txtLocalPath = poSysPath + "序列号.txt";
		File txtFile = new File(txtLocalPath);
		if(!txtFile.exists()){
			System.out.println("写出序列号.txt");
			urlToFile(txtPath,"D:\\lic\\序列号.txt");
		}

		poCtrl.webOpen(poSysPath + "test.doc",OpenModeType.docAdmin,"张三");
		map.put("pageoffice",poCtrl.getHtmlCode("PageOfficeCtrl1"));
		
		ModelAndView mv = new ModelAndView("Word");
		return mv;
	}
	
	@RequestMapping("/save")
	public void saveFile(HttpServletRequest request, HttpServletResponse response){
		FileSaver fs = new FileSaver(request, response);
		fs.saveToFile("d:\\lic\\" + fs.getFileName());
		System.out.println("~~~~~~~~~~~~~~~正在保存~~~~~~~~~~~~~~~~~");
		fs.close();
	}

	@RequestMapping("/submit")
	public void submit(HttpServletRequest request, HttpServletResponse response){
		System.out.println("~~~~~~~~~~~~~~~正在提交到文件服务器~~~~~~~~~~~~~~~~~");
	}


	/**
	 * 添加PageOffice的服务器端授权程序Servlet（必须）
	 * 可以移动到App.class中，springboot启动类。
	 * @return
	 */
	@Bean
    public ServletRegistrationBean<Server> servletRegistrationBean() {
		com.zhuozhengsoft.pageoffice.poserver.Server poserver = new com.zhuozhengsoft.pageoffice.poserver.Server();
		poserver.setSysPath(poSysPath);//设置PageOffice注册成功后,license.lic文件存放的目录
		ServletRegistrationBean<Server> srb = new ServletRegistrationBean<>(poserver);
		srb.addUrlMappings("/poserver.zz");
		srb.addUrlMappings("/posetup.exe");
		srb.addUrlMappings("/pageoffice.js");
		srb.addUrlMappings("/jquery.min.js");
		srb.addUrlMappings("/pobstyle.css");
		srb.addUrlMappings("/sealsetup.exe");
        return srb;// 
    }
	
	/**
	 * 添加印章管理程序Servlet（可选）
	 * @return
	 */
	//@Bean
    //public ServletRegistrationBean servletRegistrationBean2() {
	//	com.zhuozhengsoft.pageoffice.poserver.AdminSeal adminSeal = new com.zhuozhengsoft.pageoffice.poserver.AdminSeal();
	//	adminSeal.setAdminPassword(poPassWord);//设置印章管理员admin的登录密码
	//	adminSeal.setSysPath(poSysPath);//设置印章数据库文件poseal.db存放的目录
	//	ServletRegistrationBean srb = new ServletRegistrationBean(adminSeal);
	//	srb.addUrlMappings("/adminseal.zz");
	//	srb.addUrlMappings("/sealimage.zz");
	//	srb.addUrlMappings("/loginseal.zz");
    //    return srb;//
    //}

	public static String urlToFile(String urlPath,String fileName) throws IOException {

		//String fileName = "D:\\lic\\test.doc";

		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (FileNotFoundException e) {
				//log.info("在临时目录创建文件失败");
				//throw new ServiceException(new SelfResultCode("创建文件失败"));
				System.out.println("创建文件失败");
			}
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			URL url = new URL(urlPath);
			// 打开连接
			URLConnection con = url.openConnection();
			// 输入流
			is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			os = new FileOutputStream(file);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
		} catch (IOException e) {
			//log.info(e.getMessage());
			//throw new ServiceException(new SelfResultCode("读取文件流输出到文件失败"));
			System.out.println("读取文件流输出到文件失败");
		}finally {
			// 完毕，关闭所有链接
			if(os != null){
				os.close();
			}
			if(is != null){
				is.close();
			}
		}
		return fileName;
	}
}
