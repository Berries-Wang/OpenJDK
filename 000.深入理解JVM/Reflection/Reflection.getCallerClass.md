## Reflection.getCallerClass()
### 调用权限
#### Reflection.getCallerClass()此方法的调用者必须有权限，需要什么样的权限呢？
1. 由bootstrap class loader加载的类可以调用
2. 由extension class loader加载的类可以调用
3. 都知道用户路径的类加载都是由 application class loader进行加载的，换句话说就是用户自定义的一些类中无法调用此方法
### 作用
1. Reflection.getCallerClass()方法调用所在的方法必须用@CallerSensitive进行注解，通过此方法获取class时会跳过链路上所有的有@CallerSensitive注解的方法的类，直到遇到第一个未使用该注解的类，避免了用Reflection.getCallerClass(int n) 这个过时方法来自己做判断。