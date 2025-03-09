#include <iostream>

/**
 * 怎么理解值传递和引用传递
 */
namespace zhi_chuan_di {

class ZhiChuanDi {
private:
  /* data */
  int a;

public:
  ZhiChuanDi();
  ~ZhiChuanDi();
  int get_a() { return this->a; }
  void set_a(int val) { this->a = val; }
};

ZhiChuanDi::ZhiChuanDi() {}

ZhiChuanDi::~ZhiChuanDi() {}

} // namespace zhi_chuan_di

/**
 * 通过修改函数参数值传递类型，再通过终端输出，即可知道什么是值传递，什么是引用传递
 */
void chuandi(zhi_chuan_di::ZhiChuanDi& val) {
  val.set_a(2);
  std::cout << val.get_a() << std::endl;
}

int main(int argc, char **argv) {
  zhi_chuan_di::ZhiChuanDi val;
  val.set_a(1);

  chuandi(val);

  std::cout << val.get_a() << std::endl;

  return 0;
}