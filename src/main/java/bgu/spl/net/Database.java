package bgu.spl.net;
import bgu.spl.net.impl.BGUSERVER.EncoderDecoderBGU;
import bgu.spl.net.impl.rci.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
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
	public static class SingletonHolder {
		private static Database instance = new Database();
	}

	private final LinkedHashMap<Integer, Course> courses = new LinkedHashMap<>();
	//private Vector<Course> courses= new Vector<>();
	private final ConcurrentHashMap<String, User> RegisterList = new ConcurrentHashMap<>();
//private ConcurrentHashMap<String,User> LoginManagement= new ConcurrentHashMap<>();


	//to prevent user from creating new Database
	private Database() {
		// TODO: by name txt and not with args cant think other way so far because it is private...
		if (!initialize("Courses.txt")) throw new Error(("something get wrong with the initialization"));
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
		try { // try read the txt file
			File myObj = new File(coursesFilePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) { // while has one more line
				String data = myReader.nextLine(); // return this line and move forward
				String[] DataLine = data.split("\\|"); // return Array of String of this line split by |
				//int[] KdamCheckAsInt={};
				LinkedHashSet<Integer> KdamCheckAsInt = new LinkedHashSet<>();
				if (!DataLine[2].equals("[]")) { // if Kdam check is not empty
					// making Array of ints from the Array of Strings
					String[] KdamCheckAsString = (DataLine[2].substring(1, DataLine[2].length() - 1)).split(",");
					for (int i = 0; i < KdamCheckAsString.length; i++) {
						KdamCheckAsInt.add(Integer.parseInt(KdamCheckAsString[i]));
					}
					//KdamCheckAsInt= Stream.of(KdamCheckAsString).mapToInt(Integer::parseInt).toArray();
				}
				// add this course to the list of courses
				int CourseNum = Integer.parseInt(DataLine[0]);
				Course current = new Course(CourseNum, DataLine[1], KdamCheckAsInt, Integer.parseInt(DataLine[3]));
				courses.put(CourseNum, current);
			}
			//---------------checking-----------------------------------
			{
//				Register(TypeOfUser.Student, "Nave", "123123");
//				User nave = Login("Nave", "123123");
//				RegisterCourse(nave,101);
//				RegisterCourse(nave, 201);
//				Register(TypeOfUser.Student, "yoav", "123123");
//				User yoav = Login("yoav", "123123");
//				RegisterCourse(yoav, 102);
//				RegisterCourse(yoav ,101);
//				Register(TypeOfUser.Student, "yossi", "123123");
//				User yossi = Login("yossi", "123123");
//				RegisterCourse(yossi,101);
//				RegisterCourse(yossi, 201);
//				Register(TypeOfUser.Student, "tiltil", "123123");
//				User tiltil = Login("tiltil", "123123");
//				RegisterCourse(tiltil, 102);
//				RegisterCourse(tiltil ,101);
//				Register(TypeOfUser.Student, "ido", "123123");
//				User ido = Login("ido", "123123");
//				RegisterCourse(ido,101);
//				RegisterCourse(ido, 201);
//				Logout(nave);
//				Register(TypeOfUser.Student, "yossi", "123123");
//				User yossi = Login("yossi", "123123");
//				Register(TypeOfUser.Admin, "Yoav", "123123");
//				User Yoav = Login("Yoav", "123123");
//				RegisterCourse(nave, 101);
//				try {
//					RegisterCourse(nave, 101);
//					UnRegisterCourse(yossi, 202);
//				} catch (MyServerError e) {
//					System.out.println(e);
//				}
//
//				Thread t1 = new Thread(() -> RegisterCourse(yossi, 101));
//				Thread t3 = new Thread(() -> RegisterCourse(nave, 201));
//				Thread t2 = new Thread(() -> System.out.println(CourseStat(101)));
//				Thread t4 = new Thread(() -> System.out.println(StudentStat("Nave")));
//				t1.start();
//				t2.start();
//				t3.start();
//				t4.start();
//				System.out.println(CourseStat(101));
			}
			//
			myReader.close();
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return false;
	}

	private User getUser(String username) {
		User user = RegisterList.getOrDefault(username, null);
		if (user == null) throw new MyServerError("user by this name not registered");
		else return user;
	}

	private Course getCourse(int CourseNum) {
		Course course = courses.getOrDefault(CourseNum, null);
		if (course == null) throw new MyServerError("there is not corsue" + CourseNum + " in the System");
		return course;
	}

	public void Register(TypeOfUser type, String username, String password) {
		synchronized (RegisterList) { // case that 2 Students try to Register with the same name
			if (RegisterList.containsKey(username))
				throw new MyServerError("already user with name" + username + " in the system");
			else {
				User user = new User(type, username, password);
				RegisterList.put(username, user);
			}
		}
	}

	public User Login(String username, String password) {
		User user = getUser(username);
		if (!(user.assertPassword(password))) throw new MyServerError("wrong password");
		if (!(user.getIsLogedIn().compareAndSet(false, true))) throw new MyServerError("already Loged in");
		return user;
	}

	public void Logout(User user) {
		if (!(user.getIsLogedIn().compareAndSet(true, false))) throw new MyServerError("allready Logout");
	}

	public void RegisterCourse(User user, int CourseNum) {
		Course course = getCourse(CourseNum);
		course.register(user);
		user.RegisterToCourse(CourseNum);
	}

	public void UnRegisterCourse(User user, int CourseNum) {
		Course course = getCourse(CourseNum);
		course.unregister(user);
		user.unregister(CourseNum);
	}

	public String getKdamCheckList(int CourseNum) {
		Course course = getCourse(CourseNum);
		return (Arrays.toString(course.getKdamCheck().toArray()));
	}

	public String CourseStat(int CourseNum) {
		Course course = getCourse(CourseNum);
		return course.CourseStat();
	}

	public String StudentStat(String username) {
		User Student = getUser(username);
		if (Student.getType() != TypeOfUser.Student) throw new MyServerError("the user is not Student");
		//TODO: check if need Sync
		try {
			Student.ReadCourses(); // (lock)case that student try to register/unregister the time Admin iterate the list
			{
				return "Student "+username + "\n" + "Courses "+ListOfCoursesStudentRegisteredOrdered(Student);
			}
		} finally {
			Student.finishReadCourses();
		}
	}

	public String ListOfCoursesStudentRegisteredOrdered(User Student) {
		Collection<Integer> CoursesOfStudent = Student.getCoursesRegistered();
		int[] ArrayOrdered = new int[CoursesOfStudent.size()];
		int i = 0;
		for (Map.Entry<Integer, Course> entry : courses.entrySet()) {
			Integer key = entry.getKey();
			if (Student.IsRegisteredToCourse(key))
				ArrayOrdered[i++] = key;
		}
		//return Arrays.toString(ArrayOrdered);
		return toString(ArrayOrdered);
	}

	public boolean IsRegisteredtoCoruse(User user, int CourseNum) {
		return user.IsRegisteredToCourse(CourseNum);
	}

	private String toString(int[] Array) {
		String output="[";
		if(Array.length!=0) {
			for (int i = 0; i < Array.length; i++) {
				output = output + Array[i] + ",";
			}
			output = output.substring(0, output.length() - 1);
		}
      output=output+"]";
      return output;
	}
}

