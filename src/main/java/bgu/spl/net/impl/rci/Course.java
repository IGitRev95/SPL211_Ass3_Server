package bgu.spl.net.impl.rci;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * this class is representing specific course in the system
 */

public class Course {
    private final int courseNumber;
    private final String courseName;
    private final LinkedHashSet<Integer> KdamCourses;
    private final int CourseCapacity;
    private final ConcurrentHashMap<String,User> RegisteredStudents;
    private final ReadWriteLock LockForRegisteredStudents = new ReentrantReadWriteLock();

    public Course(int courseNumber, String courseName, LinkedHashSet<Integer> KdamCheack, int CourseCapacity) {
        this.courseNumber = courseNumber;
        this.courseName = courseName;
        this.KdamCourses = KdamCheack;
        this.CourseCapacity = CourseCapacity;
        RegisteredStudents = new ConcurrentHashMap<>(2* CourseCapacity);
    }

    public int getCourseNumber(){
        return courseNumber;
    }

    public String getCourseName(){
        return courseName;
    }

    public LinkedHashSet<Integer> getKdamCourses(){
        return KdamCourses;
    }

    public void register(User user){
        if (!CheckKdamOfUser(user)) throw new MyServerError("Not have Kdam courses");
        try {
            LockForRegisteredStudents.writeLock().lock(); // case that 2 students try to take the last place of the course at the same time or Admin read the list
            if ( RegisteredStudents.size() == CourseCapacity ) throw new MyServerError("The course is full");
            if ( RegisteredStudents.putIfAbsent( user.getUserName(), user ) != null ) throw new MyServerError("Already registered");
        }finally {
            LockForRegisteredStudents.writeLock().unlock();} // freeing lock guaranteed (even in a case of exception)
    }

    public void unregister(User user) {
        try {
            LockForRegisteredStudents.writeLock().lock(); // case that Admin read the list
            if ( RegisteredStudents.remove(user.getUserName()) == null )
                throw new MyServerError("The student not registered to this course");
        }finally {  LockForRegisteredStudents.writeLock().unlock(); } // freeing lock guaranteed (even in a case of exception)
    }

    private boolean CheckKdamOfUser(User user) {
        Collection<Integer> ListUser = user.getCoursesRegistered();
        return (ListUser.containsAll(getKdamCourses()));
    }

    public String CourseStat(){
        LockForRegisteredStudents.readLock().lock();    // case that other student register/unregister the time Admin reading course stat
        String output = "Course: (" + courseNumber + ")" + " " + courseName + "\n";
        output=output+"Seats Available: "+getSeatsAvailable()+"\n"+"Students Registered: "+getListofStudents();
        LockForRegisteredStudents.readLock().unlock();
        return output;
    }

    private String getListofStudents() {
        //Return List of Registered Students Ordered by name (for course stat operation)
        List<String> ListCopy= new ArrayList<>();
        for (Map.Entry<String, User> entry : RegisteredStudents.entrySet()){
            String key = entry.getKey();
            ListCopy.add(key);
        }
        ListCopy.sort(String::compareTo);
        return (Arrays.toString(ListCopy.toArray()).replaceAll(", ",","));
    }

    private String getSeatsAvailable(){
        //Return the amount free seats in this course (for course stat operation)
        int Available= CourseCapacity - RegisteredStudents.size();
        return Available+"/"+ CourseCapacity;
    }
}


