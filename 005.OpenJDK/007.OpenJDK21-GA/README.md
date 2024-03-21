# OpenJDK21-GA


## 注意事项
1. 字符串存储由char[] 修改为 byte[] ,  始于OpenJDK9
   + 参考代码: [005.OpenJDK/007.OpenJDK21-GA/OpenJDK21-GA/src/java.base/share/classes/java/lang/String.java](../../005.OpenJDK/007.OpenJDK21-GA/OpenJDK21-GA/src/java.base/share/classes/java/lang/String.java)
   + 参考资料: JEP 254: Compact Strings：https://openjdk.org/jeps/254
