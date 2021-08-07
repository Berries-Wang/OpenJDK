#include <iostream>

class A {
private:
  /* data */
public:
  A(/* args */);

  virtual void method1();
  virtual void method2();
  virtual void method3();

  ~A();
};

A ::A(/* args */) {}

A ::~A() {}

void A::method1() { std::cout << "method1 in A" << std::endl; }
void A::method2() { std::cout << "method2 in A" << std::endl; }
void A::method3() { std::cout << "method3 in A" << std::endl; }

//----------------------------
class B : public A {
private:
  /* data */
public:
  B(/* args */);

  void method2() override;
  virtual void method4();
  void method5();

  ~B();
};

B::B(/* args */) {}
B ::~B() {}

void B::method2() { std::cout << "method2 in B" << std::endl; }
void B::method4() { std::cout << "method4 in B" << std::endl; }
void B::method5() { std::cout << "method5 in B" << std::endl; }

int main(int argc, char **argv) {
  A *a = new A();
  a->method2(); //  method2 in A

  a = new B();
  a->method2(); // method2 in B

  return 0;
}