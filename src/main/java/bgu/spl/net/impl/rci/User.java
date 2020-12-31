package bgu.spl.net.impl.rci;


import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private final TypeOfUser type;
    private final String userName;
    private final String password;
    private final AtomicBoolean IsLogedIn = new AtomicBoolean(false);
    private boolean IsRegistered = false; // TODO:: CHECK IF USED
    // private final LinkedHashSet<Integer> CoursesRegistered= new LinkedHashSet<>();
    private final ConcurrentSkipListSet<Integer> CoursesRegistered= new ConcurrentSkipListSet<>();
    ReadWriteLock UserListLock= new ReentrantReadWriteLock();


    public User(TypeOfUser type, String userName, String password) {
        this.type = type;
        this.userName = userName;
        this.password=password;
    }

    public String getUserName() {
        return userName;
    }

    public TypeOfUser getType() {
        return type;
    }

    public AtomicBoolean getIsLogedIn() {
        return IsLogedIn;
    }

    public boolean assertPassword(String otherpassword){
        return (password.equals(otherpassword));
    }

    public ConcurrentSkipListSet<Integer> getCoursesRegistered(){
        return CoursesRegistered;
    }

    public void RegisterToCourse(int CourseNum){ //TODO:: CHECK IF NEED SYNC
        try {
            UserListLock.writeLock().lock(); // case admin iterate the list the time student try to register
            if (!CoursesRegistered.add(CourseNum))
                throw new MyServerError("student already registered course" + CourseNum);
        }finally {
            UserListLock.writeLock().unlock();
        }
    }

    public void unregister(int CourseNum){
        try {
            UserListLock.writeLock().lock(); //// case admin iterate the list the time student try to unregister
            if (!CoursesRegistered.remove(CourseNum)) throw new MyServerError("student not registered to this course");
        }finally {
            UserListLock.writeLock().unlock();
        }
    }

    public boolean IsRegisteredToCourse(int CourseNum){
        return (CoursesRegistered.contains(CourseNum));
    }
    public void ReadCourses(){
        UserListLock.readLock().lock();
    }
    public void finishReadCourses(){
        UserListLock.readLock().unlock();
    }
}