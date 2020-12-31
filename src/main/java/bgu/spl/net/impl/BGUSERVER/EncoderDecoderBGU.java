package bgu.spl.net.impl.BGUSERVER;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.rci.ACKimp;
import bgu.spl.net.impl.rci.CSCommand;
import bgu.spl.net.impl.rci.Errorimp;
import bgu.spl.net.impl.rci.MyServerError;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

public class EncoderDecoderBGU implements MessageEncoderDecoder<CSCommand> {
private short opcode= -1;
private byte[] bytes;
private byte[] objectBytes = null;
private byte[] opcodebytes=new byte[2];
Vector<Byte> vector= new Vector<>();
int index=0;
private ByteBuffer lengthBuffer = ByteBuffer.allocate(2);
int len=0;
String[] arguments= new String[2];
    public CSCommand decodeNextByte(byte nextByte){
        if (len==200) throw new Error();
        if (opcode == -1){
            lengthBuffer.put(nextByte);
            opcodebytes[len++]=nextByte;
            if (!lengthBuffer.hasRemaining()) { //we read 4 bytes and therefore can take the length
                lengthBuffer.flip();
                lengthBuffer.clear();
                opcode= bytesToShort(opcodebytes);
                len=0;
                index++;
            }}
            else{
                len++;
                vector.add(nextByte);
                if (index>=1 && nextByte=='\0'){
                    bytes = Toarray(vector);
                 arguments[index-1]= popString();
                 index++;
                 vector.clear();
                 if (index>=3) return decodeByopcode();
                }
                return null;
            }
          /*  lengthBuffer.put(nextByte);
            if (!lengthBuffer.hasRemaining()) {
                lengthBuffer.flip();
            }
            if (nextByte=='\0') return null;
        }
        else if (opcode==-1){

        }else{
*/


        return null;}
    private CSCommand decodeByopcode(){
        CSCommand output= new CSCommand(opcode);
        switch (opcode){
            case 1:
            case 2:
            case 3:
                output.SetArgument1(arguments[0]);
                output.SetArgument2(arguments[1]);
                break;
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
                output.SetArgument1(arguments[1]);
        }
        return output;
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
            zero[0]=0;
            output= appendbytes(output,zero);
        }else {
            output= shortToBytes((short) 13);
            output= appendbytes(output,shortToBytes((opcode)));
        }
        return output;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    private void pushByte(byte nextByte){
        if (len>=bytes.length)
            bytes= Arrays.copyOf(bytes,len*2);
        bytes[len++]=nextByte;
    }
    private String popString(){
        int size= bytes.length;
        String result= new String(bytes,0,bytes.length, StandardCharsets.UTF_8);
        len=0;
        return result;
    }
    private byte[] encodeString(String arg){
        return arg.getBytes();
    }
    private byte[] Toarray(Vector<Byte> v) {
        byte[] bytes = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            bytes[i] = v.get(i);
        }
        return bytes;
    }
    private byte[] appendbytes(byte[] arr1, byte[] arr2) {
        // Add your code here
        byte[] arr3 = new byte[arr1.length + arr2.length];
        for (int i = 0; i < arr1.length; i = i + 1) {
            arr3[i] = arr1[i];
        }
        for (int i = 0; i < arr2.length; i = i + 1) {
            arr3[i + arr1.length] = arr2[i];
        }
        return arr3;
    }
 }