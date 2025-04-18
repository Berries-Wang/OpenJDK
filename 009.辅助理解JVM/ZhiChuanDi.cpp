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
  ZhiChuanDi(ZhiChuanDi &other); // 复制构造函数
  int get_a() { return this->a; }
  void set_a(int val) { this->a = val; }
};

ZhiChuanDi::ZhiChuanDi() { std::cout << "构造函数执行" << std::endl; }

ZhiChuanDi::~ZhiChuanDi() { std::cout << "析构函数执行" << std::endl; }
ZhiChuanDi::ZhiChuanDi(ZhiChuanDi &other) {
  std::cout << "复制构造函数执行" << std::endl;
}

/**
 * 通过修改函数参数值传递类型，再通过终端输出，即可知道什么是值传递，什么是引用传递
 */
// void chuan_di(zhi_chuan_di::ZhiChuanDi& val) { // 传引用
void chuan_di(zhi_chuan_di::ZhiChuanDi val) { // 传值
  val.set_a(2);
  std::cout << val.get_a() << std::endl;
}

} // namespace zhi_chuan_di

int main(int argc, char **argv) {
  zhi_chuan_di::ZhiChuanDi val;
  val.set_a(1);
  std::cout << "val.set_a(1); 执行完成" << std::endl;

  zhi_chuan_di::chuan_di(val);

  std::cout << val.get_a() << std::endl;

  return 0;
}