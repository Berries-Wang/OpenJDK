#include <iostream>

class People {
private:
  /* data */
public:
  People();
  ~People();
  void say();
};

People::People(/* args */) { std::cout << "构造函数执行" << std::endl; }

People::~People() { std::cout << "析造函数执行" << std::endl; }
void People::say() { std::cout << "He say ...." << std::endl; }

int main(int argc, char **argv) {

  People he;
  he.say();
  return 0;
}

/**
 * 输出:
 *
 * <pre>
 *   构造函数执行
 *   He say ....
 *   析造函数执行
 * </pre>
 *
 */