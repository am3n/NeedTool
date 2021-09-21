// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

class Base64
{
    private static final char[] intToBase64;
    private static final char[] intToAltBase64;
    private static final byte[] base64ToInt;
    private static final byte[] altBase64ToInt;
    
    static String byteArrayToBase64(final byte[] array) {
        return byteArrayToBase64(array, false);
    }
    
    static String byteArrayToAltBase64(final byte[] array) {
        return byteArrayToBase64(array, true);
    }
    
    private static String byteArrayToBase64(final byte[] array, final boolean b) {
        final int length = array.length;
        final int n = length / 3;
        final int n2 = length - 3 * n;
        final StringBuffer sb = new StringBuffer(4 * ((length + 2) / 3));
        final char[] array2 = b ? Base64.intToAltBase64 : Base64.intToBase64;
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            final int n4 = array[n3++] & 0xFF;
            final int n5 = array[n3++] & 0xFF;
            final int n6 = array[n3++] & 0xFF;
            sb.append(array2[n4 >> 2]);
            sb.append(array2[(n4 << 4 & 0x3F) | n5 >> 4]);
            sb.append(array2[(n5 << 2 & 0x3F) | n6 >> 6]);
            sb.append(array2[n6 & 0x3F]);
        }
        if (n2 != 0) {
            final int n7 = array[n3++] & 0xFF;
            sb.append(array2[n7 >> 2]);
            if (n2 == 1) {
                sb.append(array2[n7 << 4 & 0x3F]);
                sb.append("==");
            }
            else {
                final int n8 = array[n3++] & 0xFF;
                sb.append(array2[(n7 << 4 & 0x3F) | n8 >> 4]);
                sb.append(array2[n8 << 2 & 0x3F]);
                sb.append('=');
            }
        }
        return sb.toString();
    }
    
    static byte[] base64ToByteArray(final String s) {
        return base64ToByteArray(s, false);
    }
    
    static byte[] altBase64ToByteArray(final String s) {
        return base64ToByteArray(s, true);
    }
    
    private static byte[] base64ToByteArray(final String s, final boolean b) {
        final byte[] array = b ? Base64.altBase64ToInt : Base64.base64ToInt;
        final int length = s.length();
        final int n = length / 4;
        if (4 * n != length) {
            throw new IllegalArgumentException("String length must be a multiple of four.");
        }
        int n2 = 0;
        int n3 = n;
        if (length != 0) {
            if (s.charAt(length - 1) == '=') {
                ++n2;
                --n3;
            }
            if (s.charAt(length - 2) == '=') {
                ++n2;
            }
        }
        final byte[] array2 = new byte[3 * n - n2];
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i < n3; ++i) {
            final int base64toInt = base64toInt(s.charAt(n4++), array);
            final int base64toInt2 = base64toInt(s.charAt(n4++), array);
            final int base64toInt3 = base64toInt(s.charAt(n4++), array);
            final int base64toInt4 = base64toInt(s.charAt(n4++), array);
            array2[n5++] = (byte)(base64toInt << 2 | base64toInt2 >> 4);
            array2[n5++] = (byte)(base64toInt2 << 4 | base64toInt3 >> 2);
            array2[n5++] = (byte)(base64toInt3 << 6 | base64toInt4);
        }
        if (n2 != 0) {
            final int base64toInt5 = base64toInt(s.charAt(n4++), array);
            final int base64toInt6 = base64toInt(s.charAt(n4++), array);
            array2[n5++] = (byte)(base64toInt5 << 2 | base64toInt6 >> 4);
            if (n2 == 1) {
                array2[n5++] = (byte)(base64toInt6 << 4 | base64toInt(s.charAt(n4++), array) >> 2);
            }
        }
        return array2;
    }
    
    private static int base64toInt(final char c, final byte[] array) {
        final byte b = array[c];
        if (b < 0) {
            throw new IllegalArgumentException("Illegal character " + c);
        }
        return b;
    }
    
    static {
        intToBase64 = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        intToAltBase64 = new char[] { '!', '\"', '#', '$', '%', '&', '\'', '(', ')', ',', '-', '.', ':', ';', '<', '>', '@', '[', ']', '^', '`', '_', '{', '|', '}', '~', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '?' };
        base64ToInt = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
        altBase64ToInt = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, -1, 62, 9, 10, 11, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 12, 13, 14, -1, 15, 63, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, -1, 18, 19, 21, 20, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 22, 23, 24, 25 };
    }
}
