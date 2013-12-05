package ClientProtocol;

public class GetList extends Protocol {
	public GetList(){
		super.content = super.CS + super.sp + "GETLIST" + super.crlf
				+ super.crlf;
	}
}
