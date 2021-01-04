package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.rci.ACKimp;
import bgu.spl.net.impl.rci.CSCommand;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class EncoderDecoderBGU implements MessageEncoderDecoder<CSCommand> {
private short opcode= -1;
Vector<Byte> vectorBuffer = new Vector<>();
int index=0;
private final ByteBuffer lengthBuffer = ByteBuffer.allocate(2); // using for reading short
String[] arguments= new String[2];


    public CSCommand decodeNextByte(byte nextByte) {
        if (opcode == -1) {
            lengthBuffer.put(nextByte);
            if (!lengthBuffer.hasRemaining()) {
                lengthBuffer.flip();
                opcode= lengthBuffer.getShort();
                lengthBuffer.clear();
                if (opcode==4|opcode==11) return decodeByopcode(nextByte);
            }

        } else {
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
                if (nextByte=='\0'){
                arguments[index++]= popString(VectorToarray(vectorBuffer));
                vectorBuffer.clear();
                if(index>=2) {
                    output = new CSCommand(opcode);
                    output.SetArgument1(arguments[0]);
                    output.SetArgument2(arguments[1]);
                    reset();
                }
                }else{
                    vectorBuffer.add(nextByte);
                }
                break;
            case 4:
            case 11:
                output=new CSCommand(opcode);
                reset();
                break;
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
                lengthBuffer.put(nextByte);
                if (!lengthBuffer.hasRemaining()){
                    lengthBuffer.flip();
                    short course = lengthBuffer.getShort();
                    lengthBuffer.clear();
                    output = new CSCommand(opcode);
                    output.SetArgument1(""+ course);
                    reset();
                }
                break;
            case 8:
                if (nextByte=='\0'){
                    arguments[index++]= popString(VectorToarray(vectorBuffer));
                    vectorBuffer.clear();
                    output = new CSCommand(opcode);
                    output.SetArgument1(arguments[0]);
                    reset();
                }else{
                    vectorBuffer.add(nextByte);
                }
                break;
        }
        return output;
    }
    private void reset(){
        index=0;
        opcode=-1;
        arguments= new String[2];
    }
    public byte[] encode(CSCommand message){
        byte[] output;
        String[] CmdArgs= message.getArgs();
        short opcode = message.getOpcode();
        if (message instanceof ACKimp){
            output= shortToBytes((short) 12);
            output= appendbytes(output,shortToBytes((opcode)));
            switch (opcode){
                case 6:
                case 7:
                case 8:
                case 9:
                case 11:
                    output= appendbytes(output,encodeString(CmdArgs[1]));
            }
            byte[] zero= new byte[1];
            output= appendbytes(output,zero);
        }else {
            output= shortToBytes((short) 13);
            output= appendbytes(output,shortToBytes((opcode)));
        }
        return output;
    }
    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    private String popString(byte[] bytes){
        return new String(bytes,0,bytes.length, StandardCharsets.UTF_8);
    }
    private byte[] encodeString(String arg){
        return arg.getBytes();
    }

    private byte[] VectorToarray(Vector<Byte> v) {
        byte[] bytes = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            bytes[i] = v.get(i);
        }
        return bytes;
    }

    private byte[] appendbytes(byte[] arr1, byte[] arr2) {
        // Add your code here
        byte[] arr3 = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, arr3, 0, arr1.length);
        System.arraycopy(arr2, 0, arr3, arr1.length, arr2.length);
        return arr3;
    }
 }