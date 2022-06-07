package user.service;

import java.sql.Connection;
import java.util.ArrayList;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import user.dao.UserDAO;
import user.model.License;
import user.model.User;

public class UserService {
	private UserDAO userDao = new UserDAO();
	private Connection conn = null;

	public User getUserByNo(User user, int no)  {
		try {
			conn = ConnectionProvider.getConnection();
			user = userDao.selectByNo(conn, user, no);
		} catch (Exception e) {
			System.out.println("error : userDao.getUser()");
			System.out.println(e.getMessage());
		} finally {
			JdbcUtil.close(conn);
		}
		return user;
	}
	public User getUserByReg_num(User user, long reg_num)  {
		try {
			conn = ConnectionProvider.getConnection();
			user = userDao.selectByReg_num(conn, user, reg_num);
		} catch (Exception e) {
			System.out.println("error : userDao.getUser()");
			System.out.println(e.getMessage());
		} finally {
			JdbcUtil.close(conn);
		}
		return user;
	}

	public User modifyUser(User user) {
		try {
			conn = ConnectionProvider.getConnection();
			user = userDao.modify(conn, user);
		} catch (Exception e) {
			System.out.println("error : userDao.modifyUser()");
			System.out.println(e.getMessage());
		} finally {
			JdbcUtil.close(conn);
		}
		return user;
	}
	
	public Boolean UserJoin(User user) {
		Boolean result = false;
		try {
			conn = ConnectionProvider.getConnection();
			userDao.insert(conn, user);
			if(userDao.checkUser(conn, user.getReg_num())){
				return result = true;
			}
			System.out.println("test2");
		} catch (Exception e) {
			System.out.println("error : userDao.modifyUser()");
			System.out.println(e.getMessage());
		} finally {
			JdbcUtil.close(conn);
		}
		return result;
	}

	public ArrayList<License> InsertAllLic(ArrayList<License> licList) {
		try {
			conn = ConnectionProvider.getConnection();
			licList = userDao.insertAllLic(conn, licList);
		} catch (Exception e) {
			System.out.println("error : userDao.InsertAllLic()");
			System.out.println(e.getMessage());
		} finally {
			JdbcUtil.close(conn);
		}
		return licList;
	}

	public Boolean Reg_NumCheck(long reg_num) {
		Boolean result = false;
		try {
			conn = ConnectionProvider.getConnection();
			result = userDao.reg_numCheck(conn, reg_num);
		} catch (Exception e) {
			System.out.println("error : userDao.InsertAllLic()");
			System.out.println(e.getMessage());
		} finally {
			JdbcUtil.close(conn);
		}
		return result;
	}
}
