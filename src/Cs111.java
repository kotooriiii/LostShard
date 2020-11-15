public class Cs111 {
    public static void main(String[] args) {

        int[] encryptedArray = new int[]
                {
                        53, 58, 37, 37, 69, 62, 7,
                        30, 7, 11, 37, 38, 7, 75,
                        37, 22, 7, 41, 37, 23, 62,
                        7, 30, 64, 26, 7, 75, 37,
                        22, 7, 52, 10, 41, 41, 7,
                        64, 62, 23, 62, 61, 7, 58,
                        30, 23, 62, 7, 21, 37, 7,
                        52, 37, 61, 12, 7, 30, 7,
                        26, 30, 75, 7, 10, 64, 7,
                        75, 37, 22, 61, 7, 41, 10,
                        35, 62
                };

        char[] decryptedArray = new char[encryptedArray.length];

        for (int i = 0; i < encryptedArray.length; i++) {
            int m = decode(encryptedArray[i], 37, 77);
            char character = getMap(m);
            decryptedArray[i] = character;
        }

        System.out.println(arrayToString(decryptedArray));
    }

    public static String arrayToString(char[] chars) {
        String plaintext = "";

        for (char c : chars) {
            plaintext += c;
        }

        return plaintext;
    }

    public static char getMap(int m) {
        switch (m) {
            case 2:
                return 'A';
            case 3:
                return 'B';
            case 4:
                return 'C';
            case 5:
                return 'D';
            case 6:
                return 'E';
            case 7:
                return 'F';
            case 8:
                return 'G';
            case 9:
                return 'H';
            case 10:
                return 'I';
            case 11:
                return 'J';
            case 12:
                return 'K';
            case 13:
                return 'L';
            case 14:
                return 'M';
            case 15:
                return 'N';
            case 16:
                return 'O';
            case 17:
                return 'P';
            case 18:
                return 'Q';
            case 19:
                return 'R';
            case 20:
                return 'S';
            case 21:
                return 'T';
            case 22:
                return 'U';
            case 23:
                return 'V';
            case 24:
                return 'W';
            case 25:
                return 'X';
            case 26:
                return 'Y';
            case 27:
                return 'Z';
            case 28:
                return ' ';
            default:
                return '*'; //null character
        }
    }

    static int decode(int base, int exponent, int modulus)
    {
        //set ans to 1 and not 0 because x*0 = 0
        int ans = 1;

        //exponent of 0 = 1, so condition will be when exponent is bigger than 0
        while (0 < exponent)
        {

            //There are two cases odd or even.

            //If odd, multiply the base with the answer. This will make the odd exponent -> even.
            if(exponent%2==1) {
                exponent--;
                ans = (ans * base) % modulus;
            }

            //If even, which at this point WILL be because odds are converted to even from last case,
            //then divide by two and update base
            exponent = exponent/2; //because exponent is being divided by 2 every time, this method should be O(log(exponent))
            base = (base * base) % modulus;
        }
        return ans%modulus;
    }


}
