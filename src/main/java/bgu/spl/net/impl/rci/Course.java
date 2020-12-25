package bgu.spl.net.impl.rci;

import java.util.concurrent.Semaphore;

public class Course {
    public int courseNumber;
    public String courseName;
    public int[] KdamCheck;
    Semaphore LimitEnterance;
public Course(int courseNumber,String courseName,int[] KdamCheack,int LimitEnterance){
    this.courseNumber=courseNumber;
    this.courseName=courseName;
    this.KdamCheck=KdamCheack;
    this.LimitEnterance= new Semaphore(LimitEnterance);
}
}
