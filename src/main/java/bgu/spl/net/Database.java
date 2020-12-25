package bgu.spl.net;
import bgu.spl.net.impl.rci.Course;
import bgu.spl.net.impl.rci.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
public static class SingletonHolder{
	private static Database instance= new Database();
}
private Vector<Course> courses= new Vector<>();
private ConcurrentHashMap<String,User> RegisterList= new ConcurrentHashMap<>();
private ConcurrentHashMap<String,User> LoginManagement= new ConcurrentHashMap<>();



	//to prevent user from creating new Database
	private Database() {
		// TODO: by name txt and not with args cant think other way so far because it is private...
		initialize("Courses.txt");
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	//TODO: i changed here getInstance that they gave us maybe i will chage later to original
//	public static Database getInstance() {
//		return singleton;
//	}
	public static Database getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
	 * loades the courses from the file path specified 
	 * into the Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		// TODO: implement
		try {
			String[] DataLine;
			 File myObj = new File(coursesFilePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				System.out.println(data);
				DataLine =  data.split("\\|");
				int[] KdamCheckAsInt={};
				if(!DataLine[2].equals("[]")){
				String[] KdamCheckAsString=(DataLine[2].substring(1,DataLine[2].length()-1)).split(",");
				 KdamCheckAsInt= Stream.of(KdamCheckAsString).mapToInt(Integer::parseInt).toArray();}
				 Course current= new Course(Integer.parseInt(DataLine[0]),DataLine[1],KdamCheckAsInt,Integer.parseInt(DataLine[3]));
				 courses.add(current);
				}
			myReader.close();
			}catch(FileNotFoundException e){
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return false;
	}


}
