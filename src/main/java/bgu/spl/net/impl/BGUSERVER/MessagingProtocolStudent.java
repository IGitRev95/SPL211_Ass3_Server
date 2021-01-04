package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.Database;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.rci.*;


public class  MessagingProtocolStudent implements MessagingProtocol<CSCommand> {
    private boolean shouldTerminate=false;
    private final Database database= Database.getInstance();
    private User CurrentUserLogin= null;
    public CSCommand process(CSCommand msg) {
            String[] CmdArgs = msg.getArgs();
            // what the command and what the field
            short opcode = msg.getOpcode();
            CSCommand cs= null;
            try{
            switch (opcode) {
                //Admin Register
                case 1:
                    if (CurrentUserLogin!=null) throw new MyServerError("already login");
                    database.Register(TypeOfUser.Admin, CmdArgs[0], CmdArgs[1]);
                    //send ACK 1
                    cs = new ACKimp(opcode);
                    break;
                //Student Register
                case 2:
                    if (CurrentUserLogin!=null) throw new MyServerError("already login");
                    database.Register(TypeOfUser.Student, CmdArgs[0], CmdArgs[1]);
                    cs = new ACKimp(opcode);
                    break;
                //Login
                case 3:
                    CurrentUserLogin = database.Login(CmdArgs[0], CmdArgs[1]);
                    cs = new ACKimp(opcode);
                    break;
                    //Logout
                case 4:
                    if (CurrentUserLogin == null) throw new MyServerError("not login");
                    database.Logout(CurrentUserLogin);
                    CurrentUserLogin = null;
                    shouldTerminate=false;
                    cs = new ACKimp(opcode);
                    break;
                //COURSEREG
                case 5:
                    if (CurrentUserLogin == null || CurrentUserLogin.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed to do so (because Admin)");
                    database.RegisterCourse(CurrentUserLogin, Integer.parseInt(CmdArgs[0]));
                    cs = new ACKimp(opcode);
                    break;
                //KDAMCHECK
                case 6:
                    //TODO:: maybe Admin can request Kdam check as well, need to check
                    if (CurrentUserLogin == null || CurrentUserLogin.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed to do so (because Admin)");
                    else {
                        String KdamCheck = database.getKdamCheckList(Integer.parseInt(CmdArgs[0]));
                        cs = new ACKimp(opcode);
                        cs.SetArgument2(KdamCheck);
                    }
                    break;
                //COURSESTAT
                case 7:
                    if (CurrentUserLogin == null || CurrentUserLogin.getType() == TypeOfUser.Student)
                        throw new MyServerError("not login or the user is not allowed to do so (because student)");
                    else {
                        String Stat = database.CourseStat(Integer.parseInt(CmdArgs[0])); //return
                        cs = new ACKimp(opcode);
                        cs.SetArgument2(Stat);
                    }
                    break;
                //STUDENTSTAT
                case 8:
                    if (CurrentUserLogin == null || CurrentUserLogin.getType() == TypeOfUser.Student)
                        throw new MyServerError("not login or the user is not allowed to do so (because student)");
                    else {
                        String Stat = database.StudentStat(CmdArgs[0]);
                        cs = new ACKimp(opcode);
                        cs.SetArgument2(Stat);
                    }
                    break;
                //  ISREGISTERED
                case 9:
                    if (CurrentUserLogin == null || CurrentUserLogin.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed to do so (because admin)");
                    else {
                        String Message = "NOT REGISTERED";
                        if (database.IsRegisteredtoCoruse(CurrentUserLogin, Integer.parseInt(CmdArgs[0])))
                            Message = "REGISTERED"; // send back some how
                        cs = new ACKimp(opcode);
                        cs.SetArgument2(Message);
                    }
                    break;
                    //UNREGISTER
                case 10:
                    if (CurrentUserLogin == null || CurrentUserLogin.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed to do so (because admin)");
                    else {
                        database.UnRegisterCourse(CurrentUserLogin, Integer.parseInt(CmdArgs[0]));
                        cs = new ACKimp(opcode);
                    }
                    break;
                //MYCOURSES
                case 11:
                    if (CurrentUserLogin == null || CurrentUserLogin.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed to do so (because admin)");
                    String Message = database.ListOfCoursesStudentRegisteredOrdered(CurrentUserLogin);
                    cs = new ACKimp(opcode);
                    cs.SetArgument2(Message);
                    break;
            }}catch(MyServerError e){
                cs= new Errorimp(opcode);
            }
            return cs;}

            public boolean shouldTerminate() {
                return shouldTerminate;
            }
        }
