package server.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import components.Debug;
import components.model.User;

public class Model{
	private Connection connection;

	public Model(){
		// Load the Oracle JDBC driver
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@emu.cs.rmit.edu.au:1521:GENERAL";
			connection = DriverManager.getConnection(url, "csatici", "ZERYA12");
		}catch(ClassNotFoundException e){
			// TODO Auto-generated catch block
			System.err.println("Model: JDBC driver not found " + e.toString());
		}catch(SQLException e){
			// TODO Auto-generated catch block
			System.err.println("Model: Could not connect to the database " + e.toString());
		}
	}

	public boolean add(User user){
		/* Adds the user to the db */
		String query = "insert into Usertable values(?, ?,  ?, ?, ?, ?, ?)";
		try{
			/* Use prepared statement to execute the sql query */
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, user.getFirstName());
			ps.setString(2, user.getSurname());
			ps.setString(3, user.getAddress());
			ps.setString(4, user.getPhone());
			ps.setString(5, user.getUsername());
			ps.setString(6, user.getPassword());
			ps.setInt(7, 0);
			Debug.log("Model","adding with usr: "+user.getUsername()+" pwd: "+user.getPassword());
			ps.execute();
		}catch(SQLException e){
			// TODO Auto-generated catch block
			System.err.println("Model: bad SQL in 'add()'" + e.toString());
			return false;
		}
		return true;
	}

	public boolean exists(User user){
		/* Checks if the user exists */
		Debug.log("Model","Checking for login details");
		return exists(user.getUsername(), user.getPassword());
	}

	public boolean exists(String username, String password){
		/* Checks if the user exists */
		String query = "select username from Usertable where username=? and password=?";
		PreparedStatement ps;
		try{
			ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			Debug.log("Model","connecting with usr: "+username+" pwd: "+password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return true;
			// a user with those login details was found
		}catch(SQLException e){
			// TODO Auto-generated catch block
			System.err.println("Model: bad SQL in 'exists()'" + e.toString());
		}
		return false;
	}

	public void updateScore(User user, int score){
		/* Adds the new score to the user's existing score */
		updateScore(user.getUsername(), score);
	}

	public void updateScore(String username, int score){
		/* Adds the new score to the user's existing score */
	     String query = "update Usertable SET scores=? WHERE username = ?";
			PreparedStatement ps;
			try{
				ps = connection.prepareStatement(query);
				ps.setInt(1, score);
				ps.setString(2, username);
				ps.executeUpdate();
			}catch(SQLException e){
				// TODO Auto-generated catch block
				System.err.println("Model: bad SQL in 'update()'" + e.toString());
			}
	}

	public int getScore(User user){
		/* Returns the current score of the user */
		return getScore(user.getUsername());
	}

	public int getScore(String username){
		/* Returns the current score of the user */
		String query = "select scores from Usertable where username=?";
		PreparedStatement ps;
		try{
			ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
			// a user with those login details was found
		}catch(SQLException e){
			// TODO Auto-generated catch block
			System.err.println("Model: bad SQL in 'exists()'" + e.toString());
		}
		return 0;
	}
}
