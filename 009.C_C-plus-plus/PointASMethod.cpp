// 类型指针当做函数来使用解析

#include <iostream>

class markOopDesc {};

typedef class markOopDesc *markOop;

int main(int argc, char **argv) {

  markOop a = markOop(123);

  std::cout << "Hello World" << std::endl;

  // 此时，a 的值为 0x7b ，0x7b转换为10进制就是123。即当类型指针当做方法名来调用，就是构建一个指定值得指针.

  return 0;
}
