package ClientProtocol;

public class Login extends Protocol{
        String userName;
        int Port;
        
        public Login(String un, int p){
                userName = un;
                Port = p;
                super.content = "CS1.0" + super.sp + "LOGIN" + super.sp
                                + userName + super.crlf + "Port" + super.sp + Port + super.crlf;
        }
}