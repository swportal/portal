package com.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.controller.entity.Role;
import com.web.controller.entity.User;
import com.web.controller.service.RoleService;
import com.web.controller.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	private Log log = LogFactory.getLog(this.getClass());
	@Resource
	private UserService userService;
	@Resource 
	private RoleService roleService;
	
	@RequestMapping("/toAddUser")
	public String toAddUser(HttpServletRequest request){
		
		//准备岗位列表
		List<Role> roleList = roleService.findAll();
		request.setAttribute("roleList", roleList);
		//return "/userController/addUser";
		return "/userController/saveUser";
	}
	
	
	@RequestMapping("/addUser")
	public String addUser(User user,Long[] roleIds, HttpServletRequest request){		
		String md5Digest = DigestUtils.md5Hex(user.getPassword());
		user.setPassword(md5Digest);
		List<Role> roleList = roleService.getByIds(roleIds);
		//request.setAttribute("roleList", roleList);
		user.setRoles(new TreeSet<Role>(roleList));
		userService.save(user);
		
		return "redirect:/user/getAllUser";
	}
	@RequestMapping("/updateUser")
	public String updateUser(User user,Long[] roleIds,HttpServletRequest request){
		/*String originalPwd=user.getPassword();
		if(userService.findPwdById(user.getId()).equals(user.getPassword())){
		}*/
		
		if(user.getPassword().length()<20){
			String md5Digest = DigestUtils.md5Hex(user.getPassword());
			user.setPassword(md5Digest);
		}

		
		List<Role> roleList = roleService.getByIds(roleIds);
		user.setRoles(new TreeSet<Role>(roleList));
		userService.update(user);
		return "redirect:/user/getAllUser";
	}
	
	@RequestMapping("/getUser")
	public String getUser(Long id,Long[] roleIds,HttpServletRequest request){
		User user = userService.getById(id);
		request.setAttribute("user", user);
		//准备岗位列表
		List<Role> roleList = roleService.findAll();
		request.setAttribute("roleList", roleList);
		
		if(user.getRoles() != null){
			roleIds = new Long[user.getRoles().size()];
			int index = 0;
			for(Role role : user.getRoles()){
				roleIds[index++] = role.getId();
			}
		}
		request.setAttribute("roleIds", roleIds);
		//return "/userController/editUser";
		return "/userController/saveUser";
	}
	

	
	
	@RequestMapping("/getAllUser")
	public String getAllUser(Integer curpage,HttpServletRequest request){
		if(curpage==null)
			curpage=1;
		Integer pageSize=15;
		List<User> users = userService.findUserList(curpage,pageSize);
		request.setAttribute("users", users);
		request.setAttribute("curpage",curpage);
		request.setAttribute("total", (userService.findAll().size()%pageSize==0)?(userService.findAll().size()/pageSize):(userService.findAll().size()/pageSize+1));
		//log.fatal("this is fatal info");
		return "/userController/userManager";
	}
	/*public String getAllUser(HttpServletRequest request){
		List<User> users = userService.findAll();
		request.setAttribute("users", users);	
		//log.fatal("this is fatal info");
		return "/userController/userManager";
	}*/
	
	@RequestMapping("/delUser")
	public void delUser(Long id,HttpServletResponse response){
		String result = "{\"result\":\"success\"}";
		userService.delete(id);
		PrintWriter out = null;
	    response.setContentType("application/json");
	    try {
	    	out=response.getWriter();
	        out.write(result);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}	
	
	@RequestMapping("/initPassword")
	public void initPassword(Long id,HttpServletResponse response){
		String result = "{\"result\":\"success\"}";
		User user = userService.getById(id);
		String md5Digest = DigestUtils.md5Hex("1234");
		user.setPassword(md5Digest);
		userService.update(user);
		PrintWriter out = null;
	    response.setContentType("application/json");
	    try {
	    	out=response.getWriter();
	        out.write(result);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	@RequestMapping("/logUI")
	public String loginUI(HttpServletResponse response){
		return "/userController/loginUI";
	}
	
	@RequestMapping("/login")
	public String login(String loginName, String password, HttpSession session,HttpServletRequest request){
		//session.invalidate();
		User user = null;
		if(loginName!=null&&password!=null){
		    user = userService.findByLoginNameAndPassword(loginName, password);}
		
		if(user == null){
			request.setAttribute("loginError", "用户名或者密码错误");			
			return "/userController/loginUI";
		}
		else{
			session.setAttribute("usersession", user);
		}
		return "/homeController/index";
	}
	
	@RequestMapping("/logout")
	public String logout( HttpSession session,HttpServletResponse response){		
		session.removeAttribute("usersession");
		session.invalidate();
		JSONObject data = new JSONObject();
		try {
			data.put("result", "success");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		PrintWriter out = null;
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8"); 
	    try {
	    	out=response.getWriter();
	        out.write(data.toString());
	        out.flush();
			out.close();	
			return null;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }	   
		//return "/userController/logout";
		return "/homeController/index";
	}
	
	@RequestMapping("/calendar")
	public String calendar(HttpServletRequest request){
		//return "/calendar";
		return "/homeController/kpi";
		//return "/fileController/upload";
	}
	
	@RequestMapping("/ext")
	public String ext(HttpServletRequest request){
		return "/ext";
	}
}
