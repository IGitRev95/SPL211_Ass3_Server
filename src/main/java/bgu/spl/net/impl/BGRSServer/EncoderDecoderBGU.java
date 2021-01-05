package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.rci.ACKimp;
import bgu.spl.net.impl.rci.CSCommand;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class EncoderDecoderBGU implements MessageEncoderDecoder<CSCommand> {
    private short opcode= -1;
    Vector<Byte> vectorBuffer = new Vector<>();
    int ArgumentIndex =0;
    private final ByteBuffer lengthBuffer = ByteBuffer.allocate(2); // using for reading short
    String[] arguments= new String[2];

    /* Decoding process:
     * gather all the information to the fitting fields,
     * by using Buffers reading command opcode and decode by opcode,
     * then create the command and setting it up with the gathered data.
     */


    public CSCommand decodeNextByte(byte nextByte) {
        if (opcode == -1) { // not had been read
            lengthBuffer.put(nextByte); // stash Byte
            if (!lengthBuffer.hasRemaining()) { // when Buffer is in length 2 we read the opcode
                lengthBuffer.flip();
                opcode= lengthBuffer.getShort(); // Bytes to Short
                lengthBuffer.clear();
                //case of opcode 4 or opcode 11 decode directly
                if (opcode==4|opcode==11) return decodeByopcode(nextByte);
            }

        } else { // when the opcode had been read decode by opcode
            return decodeByopcode(nextByte);
        }
        return null;
    }

    private CSCommand decodeByopcode(byte nextByte){
        CSCommand output= null;
        switch (opcode){
            case 1:
            case 2:
            case 3:
                if (nextByte=='\0'){ // '/0' means end of argument
                arguments[ArgumentIndex++]= popString(vectorToarray(vectorBuffer)); //decode to String by UTF8
                vectorBuffer.clear(); // argument had read, clear for next arguments
                if(ArgumentIndex ==2) { // in case 1,2,3 we have two arguments, all arguments had been read
                    output = new CSCommand(opcode);
                    output.SetArgument1(arguments[0]);
                    output.SetArgument2(arguments[1]);
                    resetFields();
                }
                }else{ // if next Byte isn't \0 store the byte
                    vectorBuffer.add(nextByte);
                }
                break;
            case 4:
            case 11:
                output=new CSCommand(opcode);
                resetFields();
                break;
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
                lengthBuffer.put(nextByte); //Buffer is used to read Course Number
                if (!lengthBuffer.hasRemaining()){ // when Buffer length is 2 we read the course number
                    lengthBuffer.flip();
                    short CourseNumber = lengthBuffer.getShort();
                    lengthBuffer.clear();
                    output = new CSCommand(opcode);
                    output.SetArgument1(""+ CourseNumber);
                    resetFields();
                }
                break;
            case 8:
                if (nextByte=='\0'){// '/0' means end of argument
                    arguments[ArgumentIndex++]= popString(vectorToarray(vectorBuffer)); //decode to String by UTF8
                    vectorBuffer.clear();// argument had read, clear for next arguments
                    output = new CSCommand(opcode);
                    output.SetArgument1(arguments[0]);
                    resetFields();
                }else{
                    vectorBuffer.add(nextByte);  // if next Byte isn't \0 store the byte
                }
                break;
        }
        return output;
    }

    public byte[] encode(CSCommand message){
        byte[] output;
        String[] CmdArgs= message.getArgs();
        short opcode = message.getOpcode();
        if (message instanceof ACKimp){
            short num=12;
            output= shortToBytes(num);
            output= appendBytes(output,shortToBytes((opcode))); // append 12(ACK) with the relevant opcode
            switch (opcode){
                case 6:
                case 7:
                case 8:
                case 9:
                case 11:
                    output= appendBytes(output,encodeString(CmdArgs[1]));
            }
            byte[] zero= new byte[1]; //Zero Byte means end of ACK
            output= appendBytes(output,zero); //append zero byte to the end of the output bytes array
        }else {
            short num=13;
            output= shortToBytes(num);
            output= appendBytes(output,shortToBytes((opcode))); // append 13(ERROR) with the relevant opcode
        }
        return output;
    }

    private void resetFields(){
        ArgumentIndex =0;
        opcode=-1;
        arguments= new String[2];
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private String popString(byte[] bytes){
        //decode to String by UTF8
        return new String(bytes,0,bytes.length, StandardCharsets.UTF_8);
    }

    private byte[] encodeString(String arg){
        //encode String by UTF 8
        return arg.getBytes();
    }

    private byte[] vectorToarray(Vector<Byte> v) {
        // convert bytes vector to bytes array
        byte[] bytes = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            bytes[i] = v.get(i);
        }
        return bytes;
    }

    private byte[] appendBytes(byte[] arr1, byte[] arr2) {
        // Add your code here
        byte[] arr3 = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, arr3, 0, arr1.length);
        System.arraycopy(arr2, 0, arr3, arr1.length, arr2.length);
        return arr3;
    }
 }
