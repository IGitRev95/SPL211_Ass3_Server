package bgu.spl.net.impl.rci;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Course {
    private final int courseNumber;
    private final String courseName;
    private final LinkedHashSet<Integer> KdamCheck;
    private final int LimitEnterance;
    private final List<String> ListofStudents= new ArrayList<>();
    private final ConcurrentHashMap<String,User> RegisterOfStudent;
    private ReadWriteLock lockforList = new ReentrantReadWriteLock();

public Course(int courseNumber,String courseName,LinkedHashSet<Integer> KdamCheack,int LimitEnterance) {
    this.courseNumber = courseNumber;
    this.courseName = courseName;
    this.KdamCheck = KdamCheack;
    this.LimitEnterance=LimitEnterance;
    RegisterOfStudent=new ConcurrentHashMap<>(2*LimitEnterance);
}

public int getCourseNumber(){
    return courseNumber;
}

public String getCourseName(){
    return courseName;
}

public LinkedHashSet<Integer> getKdamCheck(){
    return KdamCheck;
    }

public void register(User user){
    if (!CheckKdamOfUser(user)) throw new MyServerError("not have Kdam courses");
 try {
     lockforList.writeLock().lock(); // case that 2 students try to take the last place of the course at the same time
     if (RegisterOfStudent.size() == LimitEnterance) throw new MyServerError("the course is full");
     if (RegisterOfStudent.putIfAbsent(user.getUserName(), user) != null) throw new MyServerError("allready register");
     ListofStudents.add(user.getUserName());
 }finally {lockforList.writeLock().unlock();}
}

public void unregister(User user) {
    try {
        lockforList.writeLock().lock(); // case that Admin iterate/read the list
        if (RegisterOfStudent.remove(user.getUserName()) == null ||
                (!ListofStudents.remove(user.getUserName())))
            throw new MyServerError("the student not registered to this course");
    }finally {  lockforList.writeLock().unlock();}
}
private boolean CheckKdamOfUser(User user) {
    Collection<Integer> ListUser = user.getCoursesRegistered();
    return (ListUser.containsAll(getKdamCheck()));
}
// maybe synch think not


public String CourseStat(){
    lockforList.readLock().lock();    // case that no other student register/unregister time print the course stat (Reliable information)
    String output = "Course: (" + courseNumber + ")" + " " + courseName + "\n";
     output=output+"Seats Available: "+getSeatsAvailable()+"\n"+"Students Registered: "+getListofStudents();
     lockforList.readLock().unlock();
     return output;
    }


    private String getListofStudents() {
        List<String> ListCopy= new ArrayList<>(ListofStudents);
        ListCopy.sort(String::compareTo);
        return (Arrays.toString(ListCopy.toArray()));
    }

    private String getSeatsAvailable(){
        int Available= LimitEnterance-RegisterOfStudent.size();
        return Available+"/"+LimitEnterance;
    }
}


