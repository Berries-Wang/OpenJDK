#include <iostream>

namespace wei {
class Template {
public:
  Template() { std::cout << "Template 构造函数执行了" << std::endl; }

  ~Template() { std::cout << "Template 构造函数执行了" << std::endl; }
};
} // namespace wei

/**
 * 构造函数会执行10次
 */
int main(int argc, char **argv) {

  int arr_length = 10;
  wei::Template templates_arr[arr_length];

  return 0;
}
