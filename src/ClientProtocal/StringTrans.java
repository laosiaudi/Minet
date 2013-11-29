package ClientProtocal;


public class StringTrans {
	/**
     * Êý×Ö×Ö·û´®×ªASCIIÂë×Ö·û´®
     * 
     * @param String
     *            ×Ö·û´®
     * @return ASCII×Ö·û´®
     */
	 public static String StringToAsciiString(String content) {
	        String result = "";
	        int max = content.length();
	        for (int i = 0; i < max; i++) {
	            char c = content.charAt(i);
	            String b = Integer.toHexString(c);
	            result = result + b;
	        }
	        return result;
	    }
	 /**
	     * Ê®Áù½øÖÆ×Ö·û´®×°Ê®½øÖÆ
	     * 
	     * @param hex
	     *            Ê®Áù½øÖÆ×Ö·û´®
	     * @return Ê®½øÖÆÊýÖµ
	     */
	    public static int hexStringToAlgorism(String hex) {
	        hex = hex.toUpperCase();
	        int max = hex.length();
	        int result = 0;
	        for (int i = max; i > 0; i--) {
	            char c = hex.charAt(i - 1);
	            int algorism = 0;
	            if (c >= '0' && c <= '9') {
	                algorism = c - '0';
	            } else {
	                algorism = c - 55;
	            }
	            result += Math.pow(16, max - i) * algorism;
	        }
	        return result;
	    }
	    
	 
	 /**
	     * ASCIIÂë×Ö·û´®×ªÊý×Ö×Ö·û´®
	     * 
	     * @param String
	     *            ASCII×Ö·û´®
	     * @return ×Ö·û´®
	     */
	    public static String AsciiStringToString(String content) {
	        String result = "";
	        int length = content.length() / 2;
	        for (int i = 0; i < length; i++) {
	            String c = content.substring(i * 2, i * 2 + 2);
	            int a = hexStringToAlgorism(c);
	            char b = (char) a;
	            String d = String.valueOf(b);
	            result += d;
	        }
	        return result;
	    }
}
