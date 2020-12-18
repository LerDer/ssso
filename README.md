### 基础架构
`SpringBoot（2.2.6.RELEASE）` + `Redis`

`SpringBoot`是基础，至于`Redis`，因为由于`Session`的管理比较麻烦，牵扯到`Session`的同步，或者使用`Hash`对请求进行负载均衡。所以现在好的方法是使用`Redis`来替代`Session`。

先看一下pom文件吧

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.72</version>
</dependency>

<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.8.1</version>
</dependency>

<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.14</version>
</dependency>

<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

### 配置文件
```java
@ConfigurationProperties(prefix = "sbt.sso")
public class ConfEnv {

/**
 * 当前环境 local,daily,gray,online
 */
private String env = "local";

/**
 * 是否单点登录 默认 true 是
 */
private Boolean singleLogin = true;

/**
 * 系统名称，用于多系统区分，不传 不区分多系统 如果多系统，黑名单也区分多系统(可配置是否区分)。用户在Redis中的存储也区分多系统
 */
private String systemName = "";

/**
 * 如果设置了多系统，黑名单 是否多系统，默认false
 */
private Boolean mulBlack = false;

/**
 * 带星号的当做正则编译处理，需要登陆才能访问 多个用 , 号分隔
 */
private String urlPattern = "";

/**
 * 不需要登陆就可以访问，支持正则，多个用 , 号分隔
 */
private String notPattern = "";

/**
 * Domain 名称，不配置，从浏览器url自动获取
 */
private String domainName;

/**
 * 所有请求都拦截 默认true 全部拦截，但是notPattern 中的不拦截，
 * 如果是false，则只拦截 urlPattern 中，notPattern 中的不拦截
 */
private Boolean allHandle = true;

/**
 * BlowFish 加密解密秘钥，不配使用默认值，各系统可单独配置
 */
private String encryptKey;

/**
 * IP检测，两次请求的IP不一致，下线用户
 */
private Boolean ipCheck = false;

/**
 * 是否自动续约，即自动续订Cookie有效时长，默认false
 */
private Boolean autoRenewal = false;
}
```

- 支持指定环境，不同环境`Cookie`隔离。
- 支持单点登录，默认 true 为单点登录模式
- 支持黑名单，限制指定用户Id或IP
- 支持服务器端踢出用户
- 支持多系统区分，如果配置多系统，各系统间用户隔离，黑名单是否隔离可单独配置
- 支持登录拦截，还可配置不需要登陆就可以访问的路径
- 支持配置`Cookie`的`Domain`名称，不配置，自动从`url`中获取
- `Cookie`加密，支持自定义秘钥，且各系统可单独配置
- 支持在任何地方，获取当前登录的用户
- 已添加跨域配置，应用该项目的系统无需再配置跨域
- 可配置IP检测，两次登录IP不一致，强制下线，默认关闭
- 无侵入，简单易用

### `properties`文件自动提示

![image](https://www.yunask.cn/images/sso/1.jpg)

### 使用方式
新建一个项目，然后添加下面依赖

#### 添加依赖

```xml
 <dependency>
    <groupId>com.sbt</groupId>
    <artifactId>ssso</artifactId>
    <version>1.0.2</version>
</dependency>
```

#### 全部依赖

```xml
 <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
    <scope>compile</scope>
</dependency>

<dependency>
    <groupId>com.sbt</groupId>
    <artifactId>ssso</artifactId>
    <version>1.0.2</version>
</dependency>
```
#### 配置

```java
#系统名称，用于多系统区分，不传 不区分多系统 如果多系统，黑名单也区分多系统(黑名单可配不区分)。用户在Redis中的存储也区分多系统
sbt.sso.system-name=demo
#所有请求都拦截 默认true 全部拦截，但是notPattern 中的不拦截，如果是false，则只拦截 urlPattern 中，notPattern 中的不拦截
sbt.sso.all-handle=true
#是否单点登录 默认 true 是
sbt.sso.single-login=false
#带星号的当做正则编译处理，需要登陆才能访问 多个用 , 号分隔
#sbt.sso.url-pattern=/user/.*
#不需要登陆就可以访问，支持正则，多个用 , 号分隔
sbt.sso.not-pattern=/user/login,/user/logout,/error
#当前环境 local,daily,gray,online
sbt.sso.env=local
#IP检测，两次请求的IP不一致，下线用户
sbt.sso.ip-check=false
#如果设置了多系统，黑名单 是否多系统，默认false
sbt.sso.mul-black=false
#BlowFish 加密解密秘钥，不配使用默认值，各系统可单独配置
sbt.sso.encrypt-key=asdjasjdklajssdad
#Domain 名称，不配置，从浏览器url自动获取
#sbt.sso.domain-name=localhost

#redis
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0
```

### 登录

```java
@ApiOperation("登录")
@PostMapping(value = "/login", name = "登录")
public HttpResult login() {
    SsoUser user = new SsoUser();
    user.setUserId(12L);
    user.setMemberId(123L);
    user.setEcUserId(1234L);
    user.setName("haha");
    user.setMobile("13912345678");
    user.setEmail("lerder@foxmail.com");
    user.setIdcardNo("1237593745098432");
    user.setStatus(1);
    SessionUtil.setUser(user, true);
    return HttpResult.success();
}
```

**只需要`SessionUtil.setUser(user, true);`，即可创建`Cookie`，且已加密。**这个true是：是否记住我

```java
public static void setUser(SsoUser user, Boolean rememberMe) {
    setUser(user, rememberMe, false);
}
```

![image](https://www.yunask.cn/images/sso/2.jpg)

- `demo`为系统名称，`_dk`表示环境为日常开发环境，`Cookie`加密存储

![image](https://www.yunask.cn/images/sso/3.jpg)

- `Domian`从`url`中获取

- 设置为单点登录模式，模拟重复登录。先在一个浏览器中登录，获取用户正常。
然后打开另一个浏览器登录，再回到前一个浏览器获取用户。

![image](https://www.yunask.cn/images/sso/4.jpg)

### 登出

```java
@ApiOperation("登出")
@GetMapping(value = "/logout", name = "登出")
public HttpResult logout() {
    SessionUtil.loginOut();
    return HttpResult.success();
}
```

- 登出后，Cookie删除

![image](https://www.yunask.cn/images/sso/5.jpg)


### 获取当前登录用户

```java
@ApiOperation("获取用户")
@GetMapping(value = "/user", name = "获取用户")
public HttpResult user() {
    SsoUser user = SessionUtil.getUser();
    Long userId = SessionUtil.getUserId();
    String name = SessionUtil.getName();
    String mobile = SessionUtil.getMobile();
    return HttpResult.success(user);
}
```

![image](https://www.yunask.cn/images/sso/6.jpg)

Demo项目地址 https://github.com/LerDer/ssso-demo

欢迎大家关注我的公众号，共同学习，一起进步。加油🤣

<img src="https://www.yunask.cn/images/minap/q.jpg"  width=50%>