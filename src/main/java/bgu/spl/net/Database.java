package bgu.spl.net;
import bgu.spl.net.impl.rci.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


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
	private final ConcurrentHashMap<String, User> RegisterList = new ConcurrentHashMap<>();


	//to prevent user from creating new Database
	private Database() {
		if (!initialize("Courses.txt")) throw new Error(("something get wrong with the initialization"));
	}

	/**
	 * Retrieves the single instance of this class.
	 */

	public static Database getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * loades the courses from the file path specified
	 * into the Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		try { // try read the txt file
			File myObj = new File(coursesFilePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) { // while has one more line
				String data = myReader.nextLine(); // return this line and move forward
				String[] DataLine = data.split("\\|"); // return Array of String of this line split by '|'
				LinkedHashSet<Integer> KdamCheckAsInt = new LinkedHashSet<>();
				if (!DataLine[2].equals("[]")) { // if Kdam check is not empty
					// making List of Integer from the Array of Strings
					String[] KdamCheckAsString = (DataLine[2].substring(1, DataLine[2].length() - 1)).split(",");
					for (int i = 0; i < KdamCheckAsString.length; i++) {
						KdamCheckAsInt.add(Integer.parseInt(KdamCheckAsString[i]));
					}
				}
				// add this course to the list of courses
				int CourseNum = Integer.parseInt(DataLine[0]);
				Course current = new Course(CourseNum, DataLine[1], KdamCheckAsInt, Integer.parseInt(DataLine[3]));
				courses.put(CourseNum, current);
			}
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
		if (user == null) throw new MyServerError("No such user");
		else return user;
	}

	private Course getCourse(int CourseNum) {
		Course course = courses.getOrDefault(CourseNum, null);
		if (course == null) throw new MyServerError("No such corsue" + CourseNum + " in the System");
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
		if (!( user.assertPassword(password))) throw new MyServerError("Wrong password");
		if (!( user.getIsLoggedIn().compareAndSet(false, true )))
			throw new MyServerError("Already logged in");
		return user;
	}

	public void Logout(User user) {
		if (!(user.getIsLoggedIn().compareAndSet(true, false))) throw new MyServerError("allready Logout");
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
		return Arrays.toString( course.getKdamCourses().toArray() ).replaceAll(", ",",");
	}

	public String CourseStat(int CourseNum) {
		Course course = getCourse(CourseNum);
		return course.CourseStat();
	}

	public String StudentStat(String username) {
		User Student = getUser(username);
		if (Student.getType() != TypeOfUser.Student) throw new MyServerError("the user is not Student");
		try {
			  Student.ReadCourses(); // (lock) case that student try to register/unregister the time Admin iterate the list
			  return "Student: "+username + "\n" + "Courses: "+ListOfCoursesStudentRegisteredOrdered(Student);
		} finally { // freeing the lock guaranteed (even in case of exception)
			  Student.finishReadCourses();
		}
	}

	public String ListOfCoursesStudentRegisteredOrdered(User Student) {
		// no need for sync cause student perform the act and he is the only manipulator of the data
		Collection<Integer> CoursesOfStudent = Student.getCoursesRegisteredTo();
		int[] ArrayOrdered = new int[CoursesOfStudent.size()];
		int i = 0;
		for (Map.Entry<Integer, Course> entry : courses.entrySet()) {
			// for every course check if the student is registered to it, order preserved
			Integer key = entry.getKey();
			if (Student.IsRegisteredToCourse(key))
				ArrayOrdered[i++] = key;
		}
		return Arrays.toString(ArrayOrdered).replaceAll(", ",",");

	}

	public boolean IsRegisteredtoCoruse(User user, int CourseNum) {
		// no need for sync cause student perform the act and he is the only manipulator of the data
		return user.IsRegisteredToCourse(CourseNum);
	}
}

