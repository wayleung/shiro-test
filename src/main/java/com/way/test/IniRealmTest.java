package com.way.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

public class IniRealmTest {
	

	
	@Test
	public void testAuthentication(){
		
		IniRealm iniRealm = new IniRealm("classpath:user.ini");
		/**
		 * [users]
Way=123456,admin
[roles]
admin=user:delete,user:update
		 */
		
		//1.构建 SecurityManager环境
		DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
		
		//把realm设置到环境中
		defaultSecurityManager.setRealm(iniRealm);
		
		//2.主体提交认证请求
		SecurityUtils.setSecurityManager(defaultSecurityManager);
		Subject subject = SecurityUtils.getSubject();
		
		UsernamePasswordToken token = new UsernamePasswordToken("Way", "123456");
		//登陆
		subject.login(token);
		
		System.out.println("是否认证"+subject.isAuthenticated());
		
		//认证完后验证角色
		subject.checkRole("admin");
		
		//是否有用户删除权限
		subject.checkPermission("user:delete");
		
		//可以验证多个角色
		//subject.checkRoles("admin","user");
		
		//登出
/*		
		subject.logout();
		
		System.out.println("是否认证"+subject.isAuthenticated());*/
	}
}
