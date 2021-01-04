package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.Database;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.rci.*;


public class MessagingProtocolUser implements MessagingProtocol<CSCommand> {
    private boolean shouldTerminate=false;
    private final Database database= Database.getInstance();
    private User CurrentLoggedInUser = null;  // referance to the logged User

    public CSCommand process(CSCommand msg) {
            String[] CmdArgs = msg.getArgs(); // arguments of the command
            short opcode = msg.getOpcode();
            CSCommand replyCommand= null;
            try{
            switch (opcode) {
                //Admin Register
                case 1:
                    if (CurrentLoggedInUser != null) throw new MyServerError("already logged in");
                    database.Register(TypeOfUser.Admin, CmdArgs[0], CmdArgs[1]);
                    replyCommand = new ACKimp(opcode); //ACK 1
                    break;
                //Student Register
                case 2:
                    if (CurrentLoggedInUser !=null) throw new MyServerError("already logged in");
                    database.Register(TypeOfUser.Student, CmdArgs[0], CmdArgs[1]);
                    replyCommand = new ACKimp(opcode); //ACK 2
                    break;
                //Login
                case 3:
                    CurrentLoggedInUser = database.Login(CmdArgs[0], CmdArgs[1]); //return this logged in user if Login was successfully
                    replyCommand = new ACKimp(opcode); //ACK 3
                    break;
                //Logout
                case 4:
                    if (CurrentLoggedInUser == null) throw new MyServerError("not logged in");
                    database.Logout(CurrentLoggedInUser);
                   //  CurrentLoggedInUser = null; // remove current logged in user //TODO: delete
                    shouldTerminate=true;
                    replyCommand = new ACKimp(opcode); //ACK 4
                    break;
                //COURSEREG
                case 5:
                    if (CurrentLoggedInUser == null || CurrentLoggedInUser.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not logged in or the user is not allowed"); // not allowed because Admin
                    database.RegisterCourse(CurrentLoggedInUser, Integer.parseInt(CmdArgs[0]));
                    replyCommand = new ACKimp(opcode); // ACK 5
                    break;
                //KDAMCHECK
                case 6:
                    if (CurrentLoggedInUser == null)
                        throw new MyServerError("not logged in");
                    else {
                        String KdamCheck = database.getKdamCheckList(Integer.parseInt(CmdArgs[0]));
                        replyCommand = new ACKimp(opcode); //ACK 6
                        replyCommand.SetArgument2(KdamCheck);
                    }
                    break;
                //COURSESTAT
                case 7:
                    if (CurrentLoggedInUser == null || CurrentLoggedInUser.getType() == TypeOfUser.Student)
                        throw new MyServerError("not login or the user is not allowed");  // not allowed because Student
                    else {
                        String Stat = database.CourseStat(Integer.parseInt(CmdArgs[0]));
                        replyCommand = new ACKimp(opcode); //ACK 7
                        replyCommand.SetArgument2(Stat);
                    }
                    break;
                //STUDENTSTAT
                case 8:
                    if (CurrentLoggedInUser == null || CurrentLoggedInUser.getType() == TypeOfUser.Student)
                        throw new MyServerError("not login or the user is not allowed");  // not allowed because Student
                    else {
                        String Stat = database.StudentStat(CmdArgs[0]);
                        replyCommand = new ACKimp(opcode); //ACK 8
                        replyCommand.SetArgument2(Stat);
                    }
                    break;
                //ISREGISTERED
                case 9:
                    if (CurrentLoggedInUser == null || CurrentLoggedInUser.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed"); //not allowed because Admin
                    else {
                        String Message = "NOT REGISTERED";
                        if (database.IsRegisteredtoCoruse(CurrentLoggedInUser, Integer.parseInt(CmdArgs[0])))
                            Message = "REGISTERED"; // send back some how
                        replyCommand = new ACKimp(opcode); //ACK 9
                        replyCommand.SetArgument2(Message);
                    }
                    break;
                //UNREGISTER
                case 10:
                    if (CurrentLoggedInUser == null || CurrentLoggedInUser.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed"); //not allowed because Admin
                    else {
                        database.UnRegisterCourse(CurrentLoggedInUser, Integer.parseInt(CmdArgs[0]));
                        replyCommand = new ACKimp(opcode); //ACK 10
                    }
                    break;
                //MYCOURSES
                case 11:
                    if (CurrentLoggedInUser == null || CurrentLoggedInUser.getType() == TypeOfUser.Admin)
                        throw new MyServerError("not login or the user is not allowed"); //not allowed because Admin
                    String Message = database.ListOfCoursesStudentRegisteredOrdered(CurrentLoggedInUser);
                    replyCommand = new ACKimp(opcode); //ACK 11
                    replyCommand.SetArgument2(Message);
                    break;
            }}catch(MyServerError e){
                replyCommand= new Errorimp(opcode); // if error is thrown up return ERROR according to relevant opcode
            }
            return replyCommand;}

            public boolean shouldTerminate() {
                return shouldTerminate;
            }
        }
