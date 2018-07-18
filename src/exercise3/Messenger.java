package exercise3;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

public class Messenger {

	// default to user_id 1
	public static int session_user_id = 1;
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mainProgram();
	}
	
	public static void mainProgram(){
		// TODO Auto-generated method stub

		int choice1;
		do {
	        System.out.println("Hello " + getSessionUser().username + ", what would you like to do?");
	        System.out.println("1. Create a User");
	        System.out.println("2. Login as a Different User");
	        System.out.println("3. Send a message");
	        System.out.println("4. Retrieve all Messages with a specific user");
		    while (!sc.hasNextInt()) {
		       System.out.println("Error. Please enter a valid number");
		       sc.next(); 
		    }
		    choice1 = sc.nextInt();
		} while (!checkValidChoice(choice1,1,5));
		
    	switch (choice1){
			case 1:
				createUser();
				break;
			case 2:
				loginAsUser();
				break;
			case 3:
				sendMessageUserList();
				break;
			case 4:
				viewMessageUserList();
				break;
			default:
		        System.out.println("Invalid choice try again.");
				break;
		}		
	}
	
	public static void createUser(){

        System.out.format("Please type a username%n");
		String new_username;
		do {
		    while (!sc.hasNextLine()) {
		       System.out.println("Error. Please enter a valid string");
		       sc.next(); 
		    }
		    new_username = sc.nextLine();
		    if (new_username.isEmpty()) continue;
		} while (!checkValidUserName(new_username));
		
		try{
			// 1. Get a connection to database
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_messenger", "root", "");
			
			// 2. Create a statement
			Statement myStmt = myConn.createStatement();
			
			// 3. Execute SQL
        	int user_insert = myStmt.executeUpdate("INSERT INTO `user`(username) VALUE ('"+new_username+"')");
			
			// 4. Process the result
        	if (user_insert == 1){
    			System.out.println("username [" + new_username + "] added. Press 1 to return to main menu."); 
    			int return_int;
    			do {
    			    while (!sc.hasNextInt()) {
    			       System.out.println("Error. Please enter a valid number");
    			       sc.next(); 
    			    }
    			    return_int = sc.nextInt();
    			} while (!checkValidChoice(return_int,1,1));
    			mainProgram();
        	}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static User getSessionUser(){
		
		// initialize the elevator object
		return getUser(session_user_id);
	}
	
	public static User getUser(int p_user_id){
		
		// initialize the elevator object
		User user = new User();
		
		try{
			// 1. Get a connection to database
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_messenger", "root", "");
			
			// 2. Create a statement
			Statement myStmt = myConn.createStatement();
			
			// 3. Execute SQL
			ResultSet myRs = myStmt.executeQuery("SELECT * FROM user WHERE id = " + p_user_id);
			
			// 4. Process the result
			while (myRs.next()){
				user.id = myRs.getInt("id");
				user.username = myRs.getString("username");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return user;
	}
	
	public static boolean sendMessage(int p_to_user_id, String p_message){
		
		// step 1 try to look for an existing thread
		long existing_thread_id = 0;
		
		try{
			// 1. Get a connection to database
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_messenger", "root", "");
			
			// 2. Create a statement
			Statement myStmt = myConn.createStatement();
			
			// 3. check existing thread for both users
			ResultSet myRs = myStmt.executeQuery("SELECT thread_user.thread_id FROM thread_user INNER JOIN thread_user as thread_user2 WHERE thread_user.thread_id = thread_user2.thread_id AND thread_user.user_id = " + session_user_id + " AND thread_user2.user_id = " + p_to_user_id);
			
			// 4. Process the result
            while (myRs.next()){
            	existing_thread_id = myRs.getInt("thread_id");
				// System.out.println("thread_id: " + myRs.getInt("thread_id"));
            }
            // System.out.println("existing thread_id " + existing_thread_id);
            
            // no thread and thread_user created yet. we will create them
            if (existing_thread_id == 0){

            	// insert the new thread
            	myStmt.executeUpdate("INSERT INTO `thread`(created) VALUE ('"+Instant.now().getEpochSecond()+"')", Statement.RETURN_GENERATED_KEYS);

				myRs = myStmt.getGeneratedKeys();
				if (myRs != null && myRs.next()) {
					existing_thread_id = myRs.getLong(1);
				}
				
            	// insert the new thread_users
            	myStmt.executeUpdate("INSERT INTO `thread_user`(thread_id,user_id) VALUE ('"+existing_thread_id+"','" + session_user_id + "')", Statement.RETURN_GENERATED_KEYS);
            	myStmt.executeUpdate("INSERT INTO `thread_user`(thread_id,user_id) VALUE ('"+existing_thread_id+"','" + p_to_user_id + "')", Statement.RETURN_GENERATED_KEYS);            	
            }           

        	// insert the new message
        	myStmt.executeUpdate("INSERT INTO `thread_comment`(thread_id,from_user_id,message,created) VALUE ('"+existing_thread_id+"','"+session_user_id+"','"+p_message+"','"+Instant.now().getEpochSecond()+"')", Statement.RETURN_GENERATED_KEYS);
            
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	private static boolean checkValidChoice(int choice, int min, int max){
	    if (choice <min || choice > max) {     //Where MIN = 0 and MAX = 20
	        System.out.println("Error. Please choose integer between " + min + " and " + max + ".");
	        return false;
	    }
	    return true;
	}
	
	private static boolean checkValidString(String string, int max, boolean nowhitespace){

		if (string == "" || string.isEmpty()){
			return false;
		}
		if (nowhitespace && string.contains(" ")){
	        System.out.println("Error. Please choose a string with no space.");
			return false;
		}
		if (string.length() > max){
	        System.out.println("Error. Please choose a string with no more than " + max + " characters.");
			return false;
		}
	    return true;
	}
	
	private static boolean checkValidUserName(String string){
		
		boolean valid_string = checkValidString(string, 50, true);
		if (!valid_string) return false;
		
		int existing_user_count = 0;
		try{
			// 1. Get a connection to database
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_messenger", "root", "");
			
			// 2. Create a statement
			Statement myStmt = myConn.createStatement();
			
			// 3. Execute SQL
            ResultSet myRs = myStmt.executeQuery("SELECT COUNT(*) FROM user WHERE username = '" + string + "'");
			
			// 4. Process the result
            while (myRs.next()){
            	existing_user_count = myRs.getInt(1);
				// System.out.println(myRs.getString("id") + ": [" + myRs.getString("username") + "]");
            }
            // System.out.println("existing_user_count: " + existing_user_count);
            // no duplicate found, we will create the new user
            if (existing_user_count > 0){
				System.out.println("Duplicate user found. Please type a new one:");
            	return false;
            }
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		
	    return true;
	}
	
	public static void loginAsUser(){
		
		List<User> user_list = getAllOtherUsers();
		
		int login_as_user;
		int user_count = 0;
		do {
	        System.out.println("Please choose which user to login as");
	        user_count = 0;
			for (User user : user_list) {
				user_count++;
				user.choice_id = user_count;
				System.out.println(user_count + ". " + user.username);
			}
		    while (!sc.hasNextInt()) {
		       System.out.println("Error. Please enter a valid number");
		       sc.next(); 
		    }
		    login_as_user = sc.nextInt();
		} while (!checkValidChoice(login_as_user,1,user_count));

		for (User user : user_list) {
			if (user.choice_id == login_as_user){
				session_user_id = user.id;
			}
		}
		mainProgram();
	}
	
	public static void sendMessageUserList(){
		
		List<User> user_list = getAllOtherUsers();
		
		int login_as_user;
		int user_count = 0;
		do {
	        System.out.println("Please choose which user to send a message to");
	        user_count = 0;
			for (User user : user_list) {
				user_count++;
				user.choice_id = user_count;
				System.out.println(user_count + ". " + user.username);
			}
		    while (!sc.hasNextInt()) {
		       System.out.println("Error. Please enter a valid number");
		       sc.next(); 
		    }
		    login_as_user = sc.nextInt();
		} while (!checkValidChoice(login_as_user,1,user_count));

		for (User user : user_list) {
			if (user.choice_id == login_as_user){

		        System.out.format("Please type the message you would like to send to %s%n", user.username);
				String new_message;
				do {
				    while (!sc.hasNextLine()) {
				       System.out.println("Error. Please enter a valid message");
				       sc.next(); 
				    }
				    new_message = sc.nextLine();
				    if (new_message.isEmpty()) continue;
				} while (!checkValidString(new_message,500,false));
				
				sendMessage(user.id,new_message);
			}
		}
		System.out.println("message sent. Press 1 to return to main menu."); 
		int return_int;
		do {
		    while (!sc.hasNextInt()) {
		       System.out.println("Error. Please enter a valid number");
		       sc.next(); 
		    }
		    return_int = sc.nextInt();
		} while (!checkValidChoice(return_int,1,1));
		mainProgram();
	}
	
	public static void viewMessageUserList(){
		
		List<User> user_list = getAllOtherUsers();
		
		int login_as_user;
		int user_count = 0;
		do {
	        System.out.println("Please choose which user view all messages from");
	        user_count = 0;
			for (User user : user_list) {
				user_count++;
				user.choice_id = user_count;
				System.out.println(user_count + ". " + user.username);
			}
		    while (!sc.hasNextInt()) {
		       System.out.println("Error. Please enter a valid number");
		       sc.next(); 
		    }
		    login_as_user = sc.nextInt();
		} while (!checkValidChoice(login_as_user,1,user_count));

		for (User user : user_list) {
			if (user.choice_id == login_as_user){				
				int thread_id = retrieveAllMessagesFromUser(user.id);
				System.out.println("Enter 1 to return to main menu. Enter 2 to reply/send new message. Enter 3 to search for a message in this thread."); 
				int return_int;
				do {
				    while (!sc.hasNextInt()) {
				       System.out.println("Error. Please enter a valid number");
				       sc.next(); 
				    }
				    return_int = sc.nextInt();
				} while (!checkValidChoice(return_int,1,3));
				
				if (return_int == 1){
					mainProgram();
				}if (return_int == 2){
					if (user.choice_id == login_as_user){	
				        System.out.format("Please type the message you would like to send to %s%n", user.username);
						String new_message;
						do {
						    while (!sc.hasNextLine()) {
						       System.out.println("Error. Please enter a valid message");
						       sc.next(); 
						    }
						    new_message = sc.nextLine();
						    if (new_message.isEmpty()) continue;
						} while (!checkValidString(new_message,500,false));
						
						sendMessage(user.id,new_message);
					}
					System.out.println("message sent. Press 1 to return to main menu.");
					do {
					    while (!sc.hasNextInt()) {
					       System.out.println("Error. Please enter a valid number");
					       sc.next(); 
					    }
					    return_int = sc.nextInt();
					} while (!checkValidChoice(return_int,1,1));
					mainProgram();		
				}if(return_int == 3){
			        System.out.format("Please type the text you would like to search from your messages with %s.%n", user.username);
					String search_text;
					do {
					    while (!sc.hasNextLine()) {
					       System.out.println("Error. Please enter a valid message");
					       sc.next(); 
					    }
					    search_text = sc.nextLine();
					    if (search_text.isEmpty()) continue;
					} while (!checkValidString(search_text,500,false));
					searchForMessage(user.id,thread_id,search_text);
					System.out.println("Press 1 to return to main menu.");
					do {
					    while (!sc.hasNextInt()) {
					       System.out.println("Error. Please enter a valid number");
					       sc.next(); 
					    }
					    return_int = sc.nextInt();
					} while (!checkValidChoice(return_int,1,1));
					mainProgram();		
				}
			}
		}
	}

	public static List<User> getAllOtherUsers () {

		List<User> user_list = new ArrayList<>();
		
		try{			
			// 1. Get a connection to database
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_messenger", "root", "");
			
			// 2. Create a statement
			Statement myStmt = myConn.createStatement();
			
			// 3. Execute SQL
			ResultSet myRs = myStmt.executeQuery("SELECT * FROM user WHERE id <> " + session_user_id);

			// 4. Process the result
			while (myRs.next()){
				User user = new User();
				user.id = myRs.getInt("id");
				user.username = myRs.getString("username");				
				user_list.add(user);
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return user_list;
	}
	
	public static int retrieveAllMessagesFromUser(int p_to_user_id){
		
		// step 1 try to look for an existing thread
		int existing_thread_id = 0;
		User user1 = getUser(session_user_id);
		User user2 = getUser(p_to_user_id);
		
		try{
			// 1. Get a connection to database
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_messenger", "root", "");
			
			// 2. Create a statement
			Statement myStmt = myConn.createStatement();
			
			// 3. check existing thread for both users
			ResultSet myRs = myStmt.executeQuery("SELECT thread_user.thread_id FROM thread_user INNER JOIN thread_user as thread_user2 WHERE thread_user.thread_id = thread_user2.thread_id AND thread_user.user_id = " + session_user_id + " AND thread_user2.user_id = " + p_to_user_id);
			
			// 4. Process the result
            while (myRs.next()){
            	existing_thread_id = myRs.getInt("thread_id");
				// System.out.println(myRs.getString("id") + ": [" + myRs.getString("username") + "]");
            }
            // System.out.println("existing thread_id " + existing_thread_id);
            
            if (existing_thread_id == 0){
				System.out.println("Sorry. You have no messages with this user.");
            }else{
    			myRs = myStmt.executeQuery("SELECT * FROM thread_comment WHERE thread_id = " + existing_thread_id + " ORDER BY created ASC");
    			// 4. Process the result
    			int message_count = 0;
                while (myRs.next()){
                	message_count++;    				
                	Date sent_date = new Date(myRs.getInt("created") * 1000L);
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String formatted = format.format(sent_date);                    
                    String from_username = (myRs.getInt("from_user_id")==user1.id) ? user1.username : user2.username;

    				System.out.println("-------------------------");     
    				System.out.println("Sent From: " + from_username); 
    				System.out.println("Sent Date: " + formatted);      
    				System.out.println("Message: " + myRs.getString("message"));                 	
                }
                
                if (message_count  == 0){
    				System.out.println("Sorry. You have no messages with this user.");                	
                }
            }            
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return existing_thread_id;
	}
	
	public static void searchForMessage(int p_to_user_id, int p_thread_id, String p_search_string){
		
		User user1 = getUser(session_user_id);
		User user2 = getUser(p_to_user_id);
		
		try{
			// 1. Get a connection to database
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_messenger", "root", "");
			
			// 2. Create a statement
			Statement myStmt = myConn.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("SELECT * FROM thread_comment WHERE thread_id = " + p_thread_id + " AND message LIKE '%" + p_search_string + "%'");

			int message_count = 0;
            while (myRs.next()){
            	message_count++;    				
            	Date sent_date = new Date(myRs.getInt("created") * 1000L);
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                String formatted = format.format(sent_date);                    
                String from_username = (myRs.getInt("from_user_id")==user1.id) ? user1.username : user2.username;

				System.out.println("-------------------------");     
				System.out.println("Sent From: " + from_username); 
				System.out.println("Sent Date: " + formatted);      
				System.out.println("Message: " + myRs.getString("message"));                 	
            }
            
            if (message_count  == 0){
				System.out.println("Sorry. You have no messages that match your search term.");                	
            }          
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
