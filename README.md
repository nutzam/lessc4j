# lessc4j

Running less.js by nashorn, convert `less` to `css`.

使用JDK8+自带的nashorn引擎,实现 `less --> css` 的转换过程

## Usage 基本用法

```java
LesscService lessc = new LesscService();
lessc.init();
String lessStr = ".class { width: (1 + 3) }";
String cssStr = lessc.render(lessStr);
System.out.println(cssStr);
// output:  .class { width: 4 }
```