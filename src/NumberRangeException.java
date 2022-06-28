class NumberRangeException extends Exception // This exception occurs when number typed is -ve and not in range
{
    public NumberRangeException(String str) {
        System.out.println(str);
    }
}