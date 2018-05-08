package com.way.test;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * 自定义Realm要继承AuthorizingRealm并实现两个抽象方法
 * @author Administrator
 *
 */
public class CustomRealm extends AuthorizingRealm {
	
	
	

	@Override
	//授权
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		//1.从主体传过来的授权信息 获得角色信息
		String userName = (String) principals.getPrimaryPrincipal();
		
		
		return null;
	}



	@Override
	//认证
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		// TODO Auto-generated method stub
		//1.从主体传过来的认证信息 获得用户名
		String userName = (String) authenticationToken.getPrincipal();
		
		
		//2.通过用户名到数据库获取凭证
		String password = getPasswordByUserName(userName);
		if(password==null) {
			return null;
		}
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, password, "customeRealm");
		
		
		
		return authenticationInfo;
	}
	
	
	private String getPasswordByUserName(String username) {
		String sql = "select password from test_user where user_name = ?";
		
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			if(resultSet.next()) {
				String password = resultSet.getString(1);
				if(password==null) {
					return null;
				}else {
					return password;
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	
	
	DruidDataSource dataSource = new DruidDataSource();
	
	{
		dataSource.setUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
	}
	
	
	
	@Test
	public void testAuthentication(){
		
	
		
		
		
		//自定义realm 

		CustomRealm CustomRealm =  new CustomRealm();
		
		
		//1.构建 SecurityManager环境
		DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
		
		
		//把realm设置到环境中
		defaultSecurityManager.setRealm(CustomRealm);
		
		//2.主体提交认证请求
		SecurityUtils.setSecurityManager(defaultSecurityManager);
		Subject subject = SecurityUtils.getSubject();
		
		UsernamePasswordToken token = new UsernamePasswordToken("Way", "123456");
		//登陆
		subject.login(token);
		
		System.out.println("是否认证"+subject.isAuthenticated());
		
		//认证完后验证角色
		//subject.checkRole("admin");
		
		//是否有用户删除权限 		//!!!注意 jdbcRealm要设置权限验证的开关
		//subject.checkPermission("user:delete");
		
		//可以验证多个角色
		//subject.checkRoles("admin","user");
		
		//登出
/*		
		subject.logout();
		
		System.out.println("是否认证"+subject.isAuthenticated());*/
	}



}
