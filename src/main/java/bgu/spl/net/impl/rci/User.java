package bgu.spl.net.impl.rci;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
    private final TypeOfUser type;
    private final String userName;
    private final String password;
    private final AtomicBoolean IsLoggedIn = new AtomicBoolean(false);
    private final ConcurrentSkipListSet<Integer> CoursesRegisteredTo = new ConcurrentSkipListSet<>();
    private final ReadWriteLock UserListLock= new ReentrantReadWriteLock();


    public User(TypeOfUser type, String userName, String password) {
        this.type = type;
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public TypeOfUser getType() {
        return type;
    }

    public AtomicBoolean getIsLoggedIn() {
        return IsLoggedIn;
    }

    public boolean assertPassword(String otherPassword){
        return (password.equals(otherPassword));
    }

    public ConcurrentSkipListSet<Integer> getCoursesRegisteredTo(){
        return CoursesRegisteredTo;
    }

    public void RegisterToCourse(int CourseNum){
        try {
            UserListLock.writeLock().lock(); // case admin read the list the time student try to register course
            if (!CoursesRegisteredTo.add(CourseNum))
                throw new MyServerError("Student already registered course" + CourseNum);
        }finally {
            UserListLock.writeLock().unlock(); // freeing lock guaranteed (even in a case of exception)
        }
    }

    public void unregister(int CourseNum){
        try {
            UserListLock.writeLock().lock(); //// case admin read the list the time student try to unregister
            if (!CoursesRegisteredTo.remove(CourseNum)) throw new MyServerError("Student not registered to this course");
        }finally {
            UserListLock.writeLock().unlock(); // freeing lock guaranteed (even in a case of exception)
        }
    }

    public boolean IsRegisteredToCourse(int CourseNum){
        return (CoursesRegisteredTo.contains(CourseNum));
    }

    public void ReadCourses(){
        UserListLock.readLock().lock();
    }

    public void finishReadCourses(){
        UserListLock.readLock().unlock();
    }
}