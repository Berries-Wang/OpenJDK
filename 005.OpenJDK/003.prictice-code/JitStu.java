public class JitStu {
    public static void main(String[] args) {
        loop(100);
    }

    public static void loop(int count) {
        int sum = 0;
        for (int i = 0; i <= 1000000; i++) {
            sum += i;
        }
        System.out.print("------------->" + sum + "\n");
    }
}

// wei@Wang:~/WorkSpace/open_source/OpenJdk/005.OpenJDK/003.prictice-code$ java -XX:+PrintCompilation JitStu 
//      53    2       3       java.lang.String::hashCode (55 bytes)
//      54    1       3       java.lang.String::equals (81 bytes)
//      54    3       3       java.lang.String::charAt (29 bytes)
//      56    5       3       java.lang.Object::<init> (1 bytes)
//      56    4       1       java.lang.ref.Reference::get (5 bytes)
//      56    6     n 0       java.lang.System::arraycopy (native)   (static)
//      58    9       3       java.lang.String::indexOf (70 bytes)
//      58    8       3       java.lang.Math::min (11 bytes)
//      59   10       3       java.lang.String::length (6 bytes)
//      59    7       3       java.util.Arrays::copyOfRange (63 bytes)
//      61   12       1       java.io.File::getPath (5 bytes)
//      61   11       1       java.lang.ThreadLocal::access$400 (5 bytes)
//      61   13 %     3       JitStu::loop @ 4 (51 bytes)
//      62   14       3       JitStu::loop (51 bytes)
//      62   15 %     4       JitStu::loop @ 4 (51 bytes)
//      64   13 %     3       JitStu::loop @ -2 (51 bytes)   made not entrant
//      64   15 %     4       JitStu::loop @ -2 (51 bytes)   made not entrant
// ------------->1784293664