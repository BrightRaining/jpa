# JPA使用

## JPA基本介绍

## 1.构建

构建 persistence.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.0" xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
	<persistence-unit name="jpa01" transaction-type="RESOURCE_LOCAL">		
	<!-- 
		配置使用什么 ORM 产品来作为 JPA 的实现 
		1. 实际上配置的是  javax.persistence.spi.PersistenceProvider 接口的实现类
		2. 若 JPA 项目中只有一个 JPA 的实现产品, 则也可以不配置该节点. 
		-->
		<provider>org.hibernate.ejb.HibernatePersistence</provider>
        
        <class>com.atguigu.hellow.Customer</class>
		<class>com.atguigu.hellow.Order</class>
		<class>com.atguigu.hellow.Manager</class>

		<class>com.atguigu.hellow.Department</class>		
		<!-- 
		配置二级缓存的策略 
		ALL：所有的实体类都被缓存
		NONE：所有的实体类都不被缓存. 
		ENABLE_SELECTIVE：标识 @Cacheable(true) 注解的实体类将被缓存
		DISABLE_SELECTIVE：缓存除标识 @Cacheable(false) 以外的所有实体类
		UNSPECIFIED：默认值，JPA 产品默认值将被使用
		-->
		<shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>
	
		<properties>
		 <!-- 连接数据库  -->
			<property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver"/>
			<property name="javax.persistence.jdbc.url" value="jdbc:mysql:///jpa"/>
			<property name="javax.persistence.jdbc.user" value="root"/>
			<property name="javax.persistence.jdbc.password" value="root"/>
		 <!--  配置hibernate的基本属性 -->
		 <property name="hibernate.format_sql" value="true"/>
		  <property name="hibernate.show_sql" value="true"/>
	
		 <property name="hibernate.hbm2ddl.auto" value="update"/>
		
			<!-- 二级缓存相关 -->
			<property name="hibernate.cache.use_second_level_cache" value="true"/>
			<property name="hibernate.cache.region.factory_class" value="org.hibernate.cache.ehcache.EhCacheRegionFactory"/>
			<property name="hibernate.cache.use_query_cache" value="true"/>
		</properties>
			
	</persistence-unit>
</persistence>

```

![1561208849834](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561208849834.png)

## 2.添加对应的类

```java
指定生成的表明 JPA_CUSTOMER
@Table(name="JPA_CUSTOMER")
标注这是一个实体类
@Entity
启用二级缓存的表示配置文件中也要 开启
@Cacheable(true)
public class Customer {
  /**
	* 对应的主键生成策略
	*和生成的列名的声明都会放在get方法上
   **/
    
	private int id;
	private String name;
	private int age;
	
	private Date createdTime;
	private Date birth;
	
	private Set<Order> orders = new HashSet<>();
	
	//外键的名字
	@JoinColumn(name="CUSTOMER_ID")
    //一对多生成策略在这里可以声明是否懒加载等相关声明
	@OneToMany
	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

    //主键生成策略
	@GeneratedValue(strategy=GenerationType.AUTO)
    //声明主键
	@Id
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
    //声明列名 如果不声明就会用属性名
	@Column(name="LAST_NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	//声明这是日期并且准确到秒
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
    
    //声明这是日期并且只用到日
	@Temporal(TemporalType.DATE)
	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}
	//工具方法. 不需要映射为数据表的一列. 
//		@Transient
//		public String getInfo(){
//			return "lastName: " + name + ", email: " + age;
//		}

		@Override
		public String toString() {
			return "Customer [id=" + id + ", name=" + name + ", age=" + age + ", createdTime=" + createdTime
					+ ", birth=" + birth + "]";
		}
	

}

```

```java
package com.atguigu.hellow;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Table(name="jpa_order")
@Entity
public class Order {
	private Integer id;
	private String orderName;

	private Customer customer;

	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="ordername")
	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	
	
	//外键声明
	@JoinColumn(name="CUSTOMER_ID")
    //多对一生成策略 实行懒加载
	@ManyToOne(fetch=FetchType.LAZY)
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
}

```

## **3.执行持久化操作**

```java
package com.atguigu.hellow.text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.atguigu.hellow.Customer;

public class Main {

	public static void main(String[] args) {
		//创建EntitytmanagerFactory   
        //persistenceUnitName要与配置文件中的名字一致
		String persistenceUnitName="jpa01";
		Map<String, Object> properites = new HashMap<String, Object>();
		properites.put("hibernate.show_sql", true);
		EntityManagerFactory entityManagerFactory = 
				Persistence.createEntityManagerFactory(persistenceUnitName);
			
		//2. 创建 EntityManager. 类似于 Hibernate 的 SessionFactory
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		//3. 开启事务
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		//4. 进行持久化操作
		Customer customer = new Customer();
		customer.setAge(12);
		customer.setName("Tom");
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
	
		
		entityManager.persist(customer);
		
		//5. 提交事务
		transaction.commit();
		
		//6. 关闭 EntityManager
		entityManager.close();
		
		//7. 关闭 EntityManagerFactory
		entityManagerFactory.close();	
	}
}

```



## 4.JPA**基本注解介绍**

| @Entity         | 用于实体类声明语句之前指出该类为实体类将其映射到数据库表中   |
| :-------------- | ------------------------------------------------------------ |
| @Table          | •**当实体类与其映射的数据库表名不同名时**需要使用<br/>**@Table** 标注说明，该标注**与** **@Entity**
**标注**并列使用**，置于实体类声明语句之前，可写于单独语句行，也可与声明语句同行。<br/>•常用选项是 **name，用于指明数据库的表名<br/>两个选项 catalog<br/>和
schema
用于设置表所属的数据库目录或模式，通常为数据库名。uniqueConstraints
选项用于设置约束条件，通常不须设置<br/>![1561209776983](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561209776983.png) |
| @Id             | 用于一个实体类的属性映射为数据•数据库的**主键列**。该属性通常置于属性声明语句之前，可与声明语句同行，也可写在单独行上。<br/>@Id标注也可置于属性的**getter** 方法之前<br/> ![1561210077298](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561210077298.png) |
| @GeneratedValue | @**GeneratedValue  **用于标注主**键的生成策略**，通过strategy属性指定。**默认情况下，JPA自动**选择一个最适合底层数据库的主键生成策略：SqlServer对应identity，MySQL对应auto increment <br/>举例：<br/>**IDENTITY**：采用数据库ID自增长的方式来自增主键字段oracle不支持<br/>**AUTO**：JPA自动选择合适的策略，是默认选项<br/>–**SEQUENCE**：通过序列产生主键，通过@SequenceGenerator注解指定序列名，MySql不支持这种方式<br/>**TABLE**：通过表产生主键，框架借由表模拟序列产生主键，使用该策略可以使应用更易于数据库移植 |
| @Basic          | 一个简单的属性到数据库表的字段的映射,对于**没有任何标注**的getXxxx() 方法,默认即为@Basic<br/>•fetch: 表示该属性的读取策略,有 EAGER 和 LAZY 两种,分别表示主支抓取和延迟加载,默认为 EAGER.<br/>optional:表示该属性是否允许为null 默认为true |
| @Column         | **当实体的属性与其映射的数据库表的列不同名时需要使用**<br/>常用属性是name 还有**unique** 、**nullable**、**length**<br/>![1561210654391](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561210654391.png) |
| **@Transient**  | **表示该属性并非一个到数据库表的字段的映射**,ORM框架将忽略该属性<br/>如果一个属性并非数据库表的字段映射,就务必将其标示为@Transient,否则,ORM框架默认其注解为@Basic |
| **@**Temporal   | ![1561210765011](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561210765011.png) |

## 5. 使用table注解生成主键的详解



![1561210922674](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561210922674.png)

![1561210939491](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561210939491.png)



## 6.JPA的API

###   1. Persistence

> •**Persistence**  类是用于获取 EntityManagerFactory 实例。该类包含一个名为 **createEntityManagerFactory** 的 **静态方法** 。
>
> •createEntityManagerFactory 方法有如下两个重载版本。
>
> –带有一个参数的方法以 JPA 配置文件 persistence.xml 中的**持久化单元名**为参数
>
> –带有两个参数的方法：前一个参数含义相同，后一个参数Map类型**，**用于设置 **JPA** **的**相关属性，这时将忽略其它地方设置的属性。Map 对象的属性名必须是 JPA 实现库提供商的名字空间约定的属性名。

### 2.EntityManagerFactory

> •**EntityManagerFactory** 接口主要**用来创****建** **EntityManager** **实例**。该接口约定了如下4个方法：
>
> –**createEntityManager()**：用于创建实体管理器对象实例。
>
> –createEntityManager(Map map)：用于创建实体管理器对象实例的重载方法，Map 参数用于提供 EntityManager 的属性。
>
> –isOpen()：检查 EntityManagerFactory 是否处于打开状态。实体管理器工厂创建后一直处于打开状态，除非调用close()方法将其关闭。
>
> close()：关闭 EntityManagerFactory 。
>
>  EntityManagerFactory关闭后将释放所有资源，isOpen()方法测试将返回 false，其它方法将不能调用，否则将导致IllegalStateException异常

### 3.EntityManager

> •在 JPA 规范中, EntityManager 是完成持久化操作的核心对象。实体作为普通 Java 对象，只有在调用 EntityManager 将其持久化后才会变成持久化对象。EntityManager 对象在一组实体类与底层数据源之间进行 O/R 映射的管理。它可以用来管理和更新 Entity Bean, 根椐主键查找 Entity Bean, 还可以通过JPQL语句查询实体。
>
> • 实体的状态:
>
> –**新建状态:  新创建的对象，**尚未拥有持久性主键。
>
> –**持久化状态**：已经拥有持久性主键并**和持久化建立了上下文环境**
>
> –**游离状态**：**拥有持久化主键，但是没有与持久化建立上下文环**境
>
> –**删除状态**:  拥有持久化主键，已经和持久化建立上下文环境，但是**从数据库中**删除。
>
> •**find** **(Class<T>** **entityClass,Object** **primaryKey****)**：返回指定的 OID 对应的实体类对象，如果这个实体存在于当前的持久化环境，则返回一个被缓存的对象；否则会创建一个新的 Entity, 并加载数据库中相关信息；若 OID 不存在于数据库中，则返回一个 null。第一个参数为被查询的**实体类类型**，第二个参数为待查找实体的**主键值**。
>
> •**getReference** **(Class<T>** **entityClass,Object** **primaryKey****)**：与find()方法类似，不同的是：如果缓存中不存在指定的 Entity, EntityManager 会创建一个 Entity 类的代理，但是不会立即加载数据库中的信息，只有第一次真正使用此 Entity 的属性才加载，所以如果此 OID 在数据库不存在，getReference() 不会返回 null 值, 而是抛出EntityNotFoundException
>
> •**persist** **(Object entity)**：用于将新创建的 Entity 纳入到 EntityManager 的管理。该方法执行后，传入 persist() 方法的 Entity 对象转换成持久化状态。
>
> –如果传入 persist() 方法的 Entity 对象已经处于持久化状态，则 persist() 方法什么都不做。
>
> –如果对删除状态的 Entity 进行 persist() 操作，会转换为持久化状态。
>
> –如果对游离状态的实体执行 persist() 操作，可能会在 persist() 方法抛出 EntityExistException(也有可能是在flush或事务提交后抛出)。
>
> •**remove
> (Object entity)**：**删除实例**。如果实例是被管理的，即与数据库实体记录关联，则同时会删除关联的数据库记录。
>
> ![1561211801745](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561211801745.png)
>
> •**flush ()**：同步持久上下文环境，即将持久上下文环境的所有未保存实体的状态信息保存到数据库中。
>
> •**setFlushMode** (FlushModeType flushMode)：设置持久上下文环境的Flush模式。参数可以取2个枚举
>
> –FlushModeType.AUTO 为自动更新数据库实体，
>
> –FlushModeType.COMMIT 为直到提交事务时才更新数据库记录。
>
> •**getFlushMode** ()：获取持久上下文环境的Flush模式。返回FlushModeType类的枚举值。
>
> •**refresh** (Object entity)：用数据库实体记录的值更新实体对象的状态，即更新实例的属性值。
>
> •**clear** ()：清除持久上下文环境，断开所有关联的实体。如果这时还有未提交的更新则会被撤消。
>
> •**contains** (Object entity)：判断一个实例是否属于当前持久上下文环境管理的实体。
>
> •**isOpen** ()：判断当前的实体管理器是否是打开状态。
>
> •**getTransaction** ()：返回资源层的事务对象。EntityTransaction实例可以用于开始和提交多个事务。
>
> •**close** ()：关闭实体管理器。之后若调用实体管理器实例的方法或其派生的查询对象的方法都将抛出 IllegalstateException 异常，除了getTransaction 和 isOpen方法(返回 false)。不过，当与实体管理器关联的事务处于活动状态时，调用 close 方法后持久上下文将仍处于被管理状态，直到事务完成。
>
> •**createQuery** (String qlString)：创建一个查询对象。
>
> •**createNamedQuery** (String name)：根据命名的查询语句块创建查询对象。参数为命名的查询语句。
>
> •**createNativeQuery** (String sqlString)：使用标准 SQL语句创建查询对象。参数为标准SQL语句字符串。
>
> •**createNativeQuery** (String sqls, String resultSetMapping)：使用标准SQL语句创建查询对象，并指定返回结果集 Map的 名称。

### 4.EntityTransaction

> •EntityTransaction 接口用来管理资源层实体管理器的事务操作。通过调用实体管理器的getTransaction方法 获得其实例。
>
> •begin ()
>
> –用于启动一个事务，此后的多个数据库操作将作为整体被提交或撤消。若这时事务已启动则会抛出 IllegalStateException 异常。
>
> •commit ()
>
> –用于提交当前事务。即将事务启动以后的所有数据库更新操作持久化至数据库中。
>
> •rollback ()
>
> –撤消(回滚)当前事务。即撤消事务启动后的所有数据库更新操作，从而不对数据库产生影响。
>
> •setRollbackOnly ()
>
> –使当前事务只能被撤消。
>
> •getRollbackOnly ()
>
> –查看当前事务是否设置了只能撤消标志。
>
> •isActive ()
>
> –查看当前事务是否是活动的。如果返回true则不能调用begin方法，否则将抛出 IllegalStateException 异常；如果返回 false 则不能调用 commit、rollback、setRollbackOnly 及 getRollbackOnly 方法，否则将抛出 IllegalStateException 异常。
>
> 

## 7.映射关联关系

###  1.双向一对多及多对一映射

> •双向一对多关系中，必须存在一个关系维护端，在 JPA 规范中，要求  many 的一方作为关系的维护端(owner side), one 的一方作为被维护端(inverse side)。
>
> •**可以在** **one** **方指定** **@OneToMany** **注释****并**设置** **mappedBy** **属性**，以指定它是这一关联中的被维护端，**many** 为维护端。
>
> •在 many 方指定 @ManyToOne 注释，并使用 **@JoinColumn** 指定外键名称
>
> ![1561211995187](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561211995187.png)
>
> ![1561212004384](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561212004384.png)

### 2.双向一对一映射

> •基于外键的 1-1 关联关系：在双向的一对一关联中，需要在关系被维护端(inverse
> side)中的 @OneToOne
> 注释中指定
> **mappedBy**，以指定是这一关联中的被维护端。同时需要在关系维护端(owner
> side)建立外键列指向关系被维护端的主键列。
>
> ![1561212062984](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561212062984.png)
>
> ![1561212070957](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561212070957.png)

### 3.双向 1-1 不延迟加载的问题

> •如果延迟加载要起作用, 就必须设置一个代理对象.
>
> •Manager 其实可以不关联一个 Department
>
> •如果有 Department 关联就设置为代理对象而延迟加载, 如果不存在关联的 Department 就设置 null, **因为****外键字段是定义****在** **Department** **表****中的**,Hibernate 在不读取 Department 表的情况是无法判断是否有关联有 Deparmtment, 因此无法判断设置 null 还是代理对象, 而统一设置为代理对象,也无法满足不关联的情况, 所以无法使用延迟加载,只 有显式读取 Department.
>
> ![1561212116835](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561212116835.png)

### 4.双向多对多关联关系

> •在双向多对多关系中，我们必须指定一个关系维护端(ownerside),可以通过 @ManyToMany注释中指定mappedBy属性来标识其为关系维护端。
>
> ![1561212185243](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561212185243.png)

## 8.使用二级缓存

> •**<shared-cache-mode**> 节点：若 JPA 实现支持二级缓存，该节点可以配置在当前的持久化单元中是否启用二级缓存，可配置如下值：
>
> –ALL：所有的实体类都被缓存
>
> –NONE：所有的实体类都不被缓存. 
>
> –ENABLE_SELECTIVE：标识 **@Cacheable(true**) 注解的实体类将被缓存
>
> –DISABLE_SELECTIVE：缓存除标识 @Cacheable(false) 以外的所有实体类UNSPECIFIED：默认值，JPA 产品默认值将被使用

## 9.JPQL

### 1.Query接口的主要方法

> –int **executeUpdate**()
>
> •用于执行update或delete语句。
>
> –List **getResultList**()
>
> •用于执行select语句并返回结果集实体列表。
>
> –Object **getSingleResult**()
>
> •用于执行只返回单个结果实体的select语句。
>
> –Query **setFirstResult**(int startPosition)
>
> •用于设置从哪个实体记录开始返回查询结果。
>
> –Query **setMaxResults**(int maxResult) 
>
> •用于设置返回结果实体的最大数。与setFirstResult结合使用可实现分页查询。
>
> –Query setFlushMode(FlushModeType flushMode) 
>
> •设置查询对象的Flush模式。参数可以取2个枚举值：FlushModeType.AUTO 为自动更新数据库记录，FlushMode Type.COMMIT 为直到提交事务时才更新数据库记录。
>
> –**setHint**(String hintName, Object value)
>
> •**设置与查询对象相关的特定供应商参数或提示信息**。参数名及其取值需要参考特定 JPA 实现库提供商的文档。如果第二个参数无效将抛出IllegalArgumentException异常。
>
> –**setParameter**(int position, Object value) 
>
> •为查询语句的指定位置参数赋值。Position 指定参数序号，value 为赋给参数的值。
>
> –setParameter(int position, Date d, TemporalType type) 
>
> •为查询语句的指定位置参数赋 Date 值。Position 指定参数序号，value 为赋给参数的值，temporalType 取 TemporalType 的枚举常量，包括 DATE、TIME 及 TIMESTAMP 三个，，用于将 Java 的 Date 型值临时转换为数据库支持的日期时间类型（java.sql.Date、java.sql.Time及java.sql.Timestamp）。
>
> –setParameter(int position, Calendar c, TemporalType type) 
>
> •为查询语句的指定位置参数赋 Calenda r值。position 指定参数序号，value 为赋给参数的值，temporalType 的含义及取舍同前。
>
> –setParameter(String name, Object value) 
>
> •为查询语句的指定名称参数赋值。
>
> –setParameter(String name, Date d, TemporalType type) 
>
> •为查询语句的指定名称参数赋 Date 值。用法同前。
>
> –setParameter(String name, Calendar c, TemporalType type) 
>
> •为查询语句的指定名称参数设置Calendar值。name为参数名，其它同前。该方法调用时如果参数位置或参数名不正确，或者所赋的参数值类型不匹配，将抛出 IllegalArgumentException 异常。

### 2.相关语法

> •select语句用于执行查询。其语法可表示为：
>
> ​	select_clause 
>
> ​	form_clause 
>
> ​	[where_clause] 
>
> ​	[groupby_clause] 
>
> ​	[having_clause]
>
> ​	[orderby_clause]
>
> select-from 子句:
>
> ​	•from 子句是查询语句的必选子句。
>
> ​	–Select 用来指定查询返回的结果实体或实体的某些属性
>
> ​	–From 子句声明查询源实体类，并指定标识符变量（相当于SQL表的别名）。
>
> ​	•如果不希望返回重复实体，可使用关键字 distinct 修饰。select、from 都是 JPQL 	的关键字，通常全大写或全小写，建议不要大小写混用。
>
> 示例：
>
> ​	查询所有实体：
>
> ​		•查询所有实体的 JPQL 查询字串很简单，例如：
>
>   		select o from Order o 或  select o from Order as o
>
> ​		•关键字 as 可以省去。
>
> ​		•标识符变量的命名规范与 Java 标识符相同，且区分大小写。
>
> ​	•调用 EntityManager 的 createQuery() 方法可创建查询对象，接着调用 
>
> ​	Query 接口的 getResultList() 方法就可获得查询结果集。例如：
>
> ​		•Query query = entityManager.createQuery( "select o from Order o"); 
>
> ​		•List orders = query.getResultList();
>
> ​		•Iterator iterator = orders.iterator();
>
> ​		•while( iterator.hasNext() ) {
>
>  			 // 处理Order
>
> ​			}
>
> where子句:
>
> •where子句用于指定查询条件，where跟条件表达式。例：
>
>   select o from Orders o where o.id = 1
>
>   select o from Orders o where o.id > 3 and o.confirm = 'true'  
>
>   select o from Orders o where o.address.streetNumber >= 123
>
> •JPQL也支持包含参数的查询，例如：
>
>   select o from Orders o where o.id = :myId
>
>   select o from Orders o where o.id = :myId and o.customer = :customerName
>
> 　注意：参数名前必须冠以冒号(:)，执行查询前须使用Query.setParameter(name, value)方法给参数赋值。
>
> 
>
> •也可以不使用参数名而使用参数的序号，例如：
>
> select o from Order o where o.id = ?1 and o.customer = ?2
>
> –其中 ?1 代表第一个参数，?2 代表第一个参数。在执行查询之前需要使用重载方法Query.setParameter(pos, value) 提供参数值。
>
> **Query** **query** **=** **entityManager.createQuery**( "select o from　  **Orders o where o.id = ?1 and** **o.customer** **= ?2" );**
>
> query.setParameter( 1, 2 );
>
> query.setParameter( 2, "John" );
>
> **List orders =** **query.getResultList**();
>
> 
>
> where子句示例:
>
> // 以下语句查询 Id 介于 100 至 200 之间的订单。
>
> select o from Orders o where o.id between 100 and 200
>
> // 以下语句查询国籍为的 'US'、'CN'或'JP' 的客户。
>
> select c from Customers c where c.county in ('US','CN','JP')
>
> // 以下语句查询手机号以139开头的客户。%表示任意多个字符序列，包括0个。
>
> select c from Customers c where c.phone like '139%'
>
> // 以下语句查询名字包含4个字符，且234位为ose的客户。_表示任意单个字符。
>
> select c from Customers c where c.lname like '_ose' 
>
> // 以下语句查询电话号码未知的客户。Nul l用于测试单值是否为空。
>
> select c from Customers c where c.phone is null
>
> // 以下语句查询尚未输入订单项的订单。empty用于测试集合是否为空。
>
> select o from Orders o where o.orderItems is empty
>
> **查询部分属性**
>
> •如果只须查询实体的部分属性而不需要返回整个实体。例如：
>
> select o.id, o.customerName, o.address.streetNumber from Order o order by o.id
>
> •执行该查询返回的不再是Orders实体集合，而是一个对象数组的集合(Object[])，集合的每个成员为一个对象数组，可通过数组元素访问各个属性。

### 3.使用 Hibernate的查询缓存

> ![1561212783777](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1561212783777.png)

### 4.group by子句与聚合查询

> •group by 子句用于对查询结果分组统计，通常需要使用聚合函数。常用的聚合函数主要有 AVG、SUM、COUNT、MAX、MIN 等，它们的含义与SQL相同。例如：
>
> select max(o.id) from Orders o
>
> •没有 group by 子句的查询是基于整个实体类的，使用聚合函数将返回单个结果值，可以使用Query.getSingleResult()得到查询结果。例如：
>
> Query query = entityManager.createQuery(
>
>   "select max(o.id) from Orders o");
>
> Object result = query.getSingleResult();
>
> Long max = (Long)result;

### 5.JPQL函数

> •JPQL提供了以下一些内建函数，包括字符串处理函数、算术函数和日期函数。
>
> •**字符串处理函数**主要有：
>
> –concat(String s1, String s2)：字符串合并/连接函数。
>
> –substring(String s, int start, int length)：取字串函数。
>
> –trim([leading|trailing|both,] [char c,] String s)：从字符串中去掉首/尾指定的字符或空格。
>
> –lower(String s)：将字符串转换成小写形式。
>
> –upper(String s)：将字符串转换成大写形式。
>
> –length(String s)：求字符串的长度。
>
> –locate(String s1, String s2[, int start])：从第一个字符串中查找第二个字符串(子串)出现的位置。若未找到则返回0。
>
> •**算术函数**主要有 abs、mod、sqrt、size 等。Size 用于求集合的元素个数。
>
> •**日期函数**主要为三个，即 current_date、current_time、current_timestamp，它们不需要参数，返回服务器上的当前日期、时间和时戳。

## 7.样本实例

### 1.自己写的

```java
package com.atguigu.hellow.text;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.ejb.QueryHints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.atguigu.hellow.Customer;
import com.atguigu.hellow.Department;
import com.atguigu.hellow.Manager;
import com.atguigu.hellow.Order;

class TextClass {

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private EntityTransaction entityTransaction;
	
	@BeforeEach
	void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("jpa01");
		entityManager = entityManagerFactory.createEntityManager();
		entityTransaction=entityManager.getTransaction();
		entityTransaction.begin();
		System.out.println("初始化");
	}
	
	@AfterEach
	void after() {
		entityTransaction.commit();
		entityManager.close();
		entityManagerFactory.close();
		System.out.println("结束");
	}
	
	/**
	 * 单个查询   
	 */
	@Test
	void testFind() {	
		Customer customer = entityManager.find(Customer.class, 2);
		System.out.println(customer);
		
	}
	
	/**
	 * GetReference相当于hibernate的load方法
	 */
	@Test
	void testGetReference() {
		Customer customer = entityManager.getReference(Customer.class, 1);
		System.out.println("代理类，延迟加载"+customer.getClass().getName());
		System.out.println("======================");
		System.out.println(customer);
	}
	/**
	 *1添加
	 *2 但是如果添加的对象中有了主键那末不能添加
	 */
	@Test
	void testPersistence() {
		Customer customer = new Customer();
		customer.setAge(15);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setName("小明");
//		customer.setId(100);
		entityManager.persist(customer);
	}
	
	/**
	 * 该方法只能移除 持久化 对象. 而 hibernate 的 delete 方法实际上还可以移除 游离对象.
	 */
	@Test
	void testRemove() {
//		Customer customer = new Customer();
//		customer.setId(2);
		Customer customer = entityManager.find(Customer.class, 2);
		entityManager.remove(customer);
	}
	
	/**
	 * 开启多对一关系映射
	 */
	@Test
	public void textManeyToOne() {
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setName("kk");
		
		//创建订单
		Order order1 = new Order();
		order1.setOrderName("G-GG-1");
		
		Order order2 = new Order();
		order2.setOrderName("G-GG-2");
		

		/**
		 * 保存多对一时, 建议先保存 1 的一端, 后保存 n 的一端, 这样不会多出额外的 UPDATE 语句.
		 */
		entityManager.persist(customer);
		//		建立关系	
		order1.setCustomer(customer);
		order2.setCustomer(customer);

		entityManager.persist(order1);
		entityManager.persist(order2);
		
	}
	/**
	 * 不能直接删除 1 的一端, 因为有外键约束
	 */
	public void testManyToOneRemove() {
//		先删除订单
		Order order = entityManager.find(Order.class, 1);
		entityManager.remove(order);
//		再删除主类
		Customer customer = entityManager.find(Customer.class, 7);
		entityManager.remove(customer);
	}
	
	/**
	 * 查询 maneyToOne
	 */
	
	@Test
	public void testManyToOneFind(){
		
		Order order = entityManager.find(Order.class, 1);
		System.out.println(order.getOrderName());
		//System.out.println(order.getCustomer().getName());
	}
	
	/**
	 * 测试 一对多
	 */
	public void testOneToManey() {
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setName("kk");
		
		Order order1 = new Order();
		order1.setOrderName("O-MM-1");
		
		Order order2 = new Order();
		order2.setOrderName("O-MM-2");
		
		//建立关联关系
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行保存操作
		entityManager.persist(customer);

		entityManager.persist(order1);
		entityManager.persist(order2);
	}
	/**
	 * 查询一对多
	 */
	//默认对关联的多的一方使用懒加载的加载策略. 
		//可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略
		@Test
		public void testOneToManyFind(){
			Customer customer = entityManager.find(Customer.class, 6);
			System.out.println(customer.getName());			
			System.out.println(customer.getOrders().size());
		}
		//默认情况下, 若删除 1 的一端, 则会先把关联的 n 的一端的外键置空, 然后进行删除. 
		//可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略. 
		
		@Test
		public void testOneToManyRemove(){
			Customer customer = entityManager.find(Customer.class, 6);
			entityManager.remove(customer);
		}
		
		//双向 1-1 的关联关系, 建议先保存不维护关联关系的一方, 即没有外键的一方, 这样不会多出 UPDATE 语句.
		@Test
		public void testOneToOne() {
			
			Manager manager = new Manager();
			manager.setMgrName("管理");
			
			entityManager.persist(manager);
			
			Department department = new Department();
			department.setName("被管理");
			department.setMgr(manager);
			
			entityManager.persist(department);
		}
	//测试OneToOne查询

		//1. 默认情况下, 若获取不维护关联关系的一方, 则也会通过左外连接获取其关联的对象. 
		//可以通过 @OneToOne 的 fetch 属性来修改加载策略. 但依然会再发送 SQL 语句来初始化其关联的对象
		//这说明在不维护关联关系的一方, 不建议修改 fetch 属性. 
		@Test
		public void testOneToOneFind2(){
			Manager mgr = entityManager.find(Manager.class, 1);
			System.out.println(mgr.getMgrName());
			
			System.out.println(mgr.getDept().getClass().getName());
		}
		//1.默认情况下, 若获取维护关联关系的一方, 则会通过左外连接获取其关联的对象. 
		//但可以通过 @OntToOne 的 fetch 属性来修改加载策略.
		
		@Test
		public void testOneToOneFind(){
			Department dept = entityManager.find(Department.class, 1);
			System.out.println(dept.getName());
			System.out.println(dept.getMgr().getClass().getName());
		}
		
		/**
		 * 测试二级缓存
		 * 在类上加 @Cacheable(true)
		 */
		@Test
		public void testSecondLevelCache() {
			Customer customer1 = entityManager.find(Customer.class, 1);
			
			entityTransaction.commit();
			entityManager.close();
			
			entityManager = entityManagerFactory.createEntityManager();
			entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();
			
			Customer customer2 = entityManager.find(Customer.class, 1);
		}
		/**
		 * JPQL查询
		 */
		@Test
		public void testHelloJPQL(){
			String jpql = "FROM Customer c WHERE c.age > ?";
			List<Customer> list = entityManager.createQuery(jpql, Customer.class).setParameter(1, 12).getResultList();
			for(Customer customer:list) {
				System.out.println(customer.getName());
			}
			
		}
		
		//默认情况下, 若只查询部分属性, 则将返回 Object[] 类型的结果. 或者 Object[] 类型的 List.
		//也可以在实体类中创建对应的构造器, 然后再 JPQL 语句中利用对应的构造器返回实体类的对象.
		//如：要有对用的构造方法
		@Test
		public void testPartlyProperties(){
			String jpql = "SELECT new Customer(c.lastName, c.age) FROM Customer c WHERE c.id > ?";
			List result = entityManager.createQuery(jpql).setParameter(1, 1).getResultList();
			
			System.out.println(result);
		}
		//createNamedQuery 适用于在实体类前使用 @NamedQuery 标记的查询语句
		//如：@NamedQuery(name="testNamedQuery", query="FROM Customer c WHERE c.id = ?")
		@Test
		public void testNamedQuery(){
			Query query = entityManager.createNamedQuery("testNamedQuery").setParameter(1, 3);
			Customer customer = (Customer) query.getSingleResult();
			
			System.out.println(customer);
		}
		
		//createNativeQuery 适用于本地 SQL
		@Test
		public void testNativeQuery(){
			String sql = "SELECT age FROM jpa_cutomers WHERE id = ?";
			 Customer singleResult = (Customer)entityManager.createNativeQuery(sql,Customer.class).setParameter(1, 3).getSingleResult();
			System.out.println(singleResult.getAge());
		}
		
		//使用 hibernate 的查询缓存.  
		//createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		@Test
		public void testQueryCache(){
			String jpql = "FROM Customer c WHERE c.age > ?";
			Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
			
			//占位符的索引是从 1 开始
			query.setParameter(1, 1);
			List<Customer> customers = query.getResultList();
			System.out.println(customers.size());
			
			query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
			
			//占位符的索引是从 1 开始
			query.setParameter(1, 1);
			customers = query.getResultList();
			System.out.println(customers.size());
		}
		
		
		@Test
		public void testOrderBy(){
			String jpql = "FROM Customer c WHERE c.age > ? ORDER BY c.age DESC";
			Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
			
			//占位符的索引是从 1 开始
			query.setParameter(1, 1);
			List<Customer> customers = query.getResultList();
			System.out.println(customers.size());
		}
		
		//查询 order 数量大于 2 的那些 Customer
		@Test
		public void testGroupBy(){
			String jpql = "SELECT o.customer FROM Order o "
					+ "GROUP BY o.customer "
					+ "HAVING count(o.id) >= 2";
			List<Customer> customers = entityManager.createQuery(jpql).getResultList();
			
			System.out.println(customers);
		}

		//子查询
		@Test
		public void testSubQuery(){
			//查询所有 Customer 的 lastName 为 YY 的 Order
			String jpql = "SELECT o FROM Order o "
					+ "WHERE o.customer = (SELECT c FROM Customer c WHERE c.lastName = ?)";
			
			Query query = entityManager.createQuery(jpql).setParameter(1, "YY");
			List<Order> orders = query.getResultList();
			System.out.println(orders.size());
		}
		
		//使用 jpql 内建的函数
		@Test
		public void testJpqlFunction(){
			String jpql = "SELECT lower(c.email) FROM Customer c";
			
			List<String> emails = entityManager.createQuery(jpql).getResultList();
			System.out.println(emails);
		}
		
		//可以使用 JPQL 完成 UPDATE 和 DELETE 操作. 
		@Test
		public void testExecuteUpdate(){
			String jpql = "UPDATE Customer c SET c.lastName = ? WHERE c.id = ?";
			Query query = entityManager.createQuery(jpql).setParameter(1, "YYY").setParameter(2, 12);
			
			query.executeUpdate();
		}
		
}

```

### 2.范本

```java
package com.atguigu.jpa.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.ejb.QueryHints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atguigu.jpa.helloworld.Category;
import com.atguigu.jpa.helloworld.Customer;
import com.atguigu.jpa.helloworld.Department;
import com.atguigu.jpa.helloworld.Item;
import com.atguigu.jpa.helloworld.Manager;
import com.atguigu.jpa.helloworld.Order;

public class JPATest {

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private EntityTransaction transaction;
	
	@Before
	public void init(){
		entityManagerFactory = Persistence.createEntityManagerFactory("jpa-1");
		entityManager = entityManagerFactory.createEntityManager();
		transaction = entityManager.getTransaction();
		transaction.begin();
	}
	
	@After
	public void destroy(){
		transaction.commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	
	//可以使用 JPQL 完成 UPDATE 和 DELETE 操作. 
	@Test
	public void testExecuteUpdate(){
		String jpql = "UPDATE Customer c SET c.lastName = ? WHERE c.id = ?";
		Query query = entityManager.createQuery(jpql).setParameter(1, "YYY").setParameter(2, 12);
		
		query.executeUpdate();
	}

	//使用 jpql 内建的函数
	@Test
	public void testJpqlFunction(){
		String jpql = "SELECT lower(c.email) FROM Customer c";
		
		List<String> emails = entityManager.createQuery(jpql).getResultList();
		System.out.println(emails);
	}
	
	@Test
	public void testSubQuery(){
		//查询所有 Customer 的 lastName 为 YY 的 Order
		String jpql = "SELECT o FROM Order o "
				+ "WHERE o.customer = (SELECT c FROM Customer c WHERE c.lastName = ?)";
		
		Query query = entityManager.createQuery(jpql).setParameter(1, "YY");
		List<Order> orders = query.getResultList();
		System.out.println(orders.size());
	}
	
	/**
	 * JPQL 的关联查询同 HQL 的关联查询. 
	 */
	@Test
	public void testLeftOuterJoinFetch(){
		String jpql = "FROM Customer c LEFT OUTER JOIN FETCH c.orders WHERE c.id = ?";
		
		Customer customer = 
				(Customer) entityManager.createQuery(jpql).setParameter(1, 12).getSingleResult();
		System.out.println(customer.getLastName());
		System.out.println(customer.getOrders().size());
		
//		List<Object[]> result = entityManager.createQuery(jpql).setParameter(1, 12).getResultList();
//		System.out.println(result);
	}
	
	//查询 order 数量大于 2 的那些 Customer
	@Test
	public void testGroupBy(){
		String jpql = "SELECT o.customer FROM Order o "
				+ "GROUP BY o.customer "
				+ "HAVING count(o.id) >= 2";
		List<Customer> customers = entityManager.createQuery(jpql).getResultList();
		
		System.out.println(customers);
	}
	
	@Test
	public void testOrderBy(){
		String jpql = "FROM Customer c WHERE c.age > ? ORDER BY c.age DESC";
		Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		
		//占位符的索引是从 1 开始
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	//使用 hibernate 的查询缓存. 
	@Test
	public void testQueryCache(){
		String jpql = "FROM Customer c WHERE c.age > ?";
		Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		
		//占位符的索引是从 1 开始
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
		
		query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		
		//占位符的索引是从 1 开始
		query.setParameter(1, 1);
		customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	//createNativeQuery 适用于本地 SQL
	@Test
	public void testNativeQuery(){
		String sql = "SELECT age FROM jpa_cutomers WHERE id = ?";
		Query query = entityManager.createNativeQuery(sql).setParameter(1, 3);
		
		Object result = query.getSingleResult();
		System.out.println(result);
	}
	
	//createNamedQuery 适用于在实体类前使用 @NamedQuery 标记的查询语句
	@Test
	public void testNamedQuery(){
		Query query = entityManager.createNamedQuery("testNamedQuery").setParameter(1, 3);
		Customer customer = (Customer) query.getSingleResult();
		
		System.out.println(customer);
	}
	
	//默认情况下, 若只查询部分属性, 则将返回 Object[] 类型的结果. 或者 Object[] 类型的 List.
	//也可以在实体类中创建对应的构造器, 然后再 JPQL 语句中利用对应的构造器返回实体类的对象.
	@Test
	public void testPartlyProperties(){
		String jpql = "SELECT new Customer(c.lastName, c.age) FROM Customer c WHERE c.id > ?";
		List result = entityManager.createQuery(jpql).setParameter(1, 1).getResultList();
		
		System.out.println(result);
	}
	
	@Test
	public void testHelloJPQL(){
		String jpql = "FROM Customer c WHERE c.age > ?";
		Query query = entityManager.createQuery(jpql);
		
		//占位符的索引是从 1 开始
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	@Test
	public void testSecondLevelCache(){
		Customer customer1 = entityManager.find(Customer.class, 1);
		
		transaction.commit();
		entityManager.close();
		
		entityManager = entityManagerFactory.createEntityManager();
		transaction = entityManager.getTransaction();
		transaction.begin();
		
		Customer customer2 = entityManager.find(Customer.class, 1);
	}
	
	//对于关联的集合对象, 默认使用懒加载的策略.
	//使用维护关联关系的一方获取, 还是使用不维护关联关系的一方获取, SQL 语句相同. 
	@Test
	public void testManyToManyFind(){
//		Item item = entityManager.find(Item.class, 5);
//		System.out.println(item.getItemName());
//		
//		System.out.println(item.getCategories().size());
		
		Category category = entityManager.find(Category.class, 3);
		System.out.println(category.getCategoryName());
		System.out.println(category.getItems().size());
	}
	
	//多对所的保存
	@Test
	public void testManyToManyPersist(){
		Item i1 = new Item();
		i1.setItemName("i-1");
	
		Item i2 = new Item();
		i2.setItemName("i-2");
		
		Category c1 = new Category();
		c1.setCategoryName("C-1");
		
		Category c2 = new Category();
		c2.setCategoryName("C-2");
		
		//设置关联关系
		i1.getCategories().add(c1);
		i1.getCategories().add(c2);
		
		i2.getCategories().add(c1);
		i2.getCategories().add(c2);
		
		c1.getItems().add(i1);
		c1.getItems().add(i2);
		
		c2.getItems().add(i1);
		c2.getItems().add(i2);
		
		//执行保存
		entityManager.persist(i1);
		entityManager.persist(i2);
		entityManager.persist(c1);
		entityManager.persist(c2);
	}
	
	//1. 默认情况下, 若获取不维护关联关系的一方, 则也会通过左外连接获取其关联的对象. 
	//可以通过 @OneToOne 的 fetch 属性来修改加载策略. 但依然会再发送 SQL 语句来初始化其关联的对象
	//这说明在不维护关联关系的一方, 不建议修改 fetch 属性. 
	@Test
	public void testOneToOneFind2(){
		Manager mgr = entityManager.find(Manager.class, 1);
		System.out.println(mgr.getMgrName());
		
		System.out.println(mgr.getDept().getClass().getName());
	}
	
	//1.默认情况下, 若获取维护关联关系的一方, 则会通过左外连接获取其关联的对象. 
	//但可以通过 @OntToOne 的 fetch 属性来修改加载策略.
	@Test
	public void testOneToOneFind(){
		Department dept = entityManager.find(Department.class, 1);
		System.out.println(dept.getDeptName());
		System.out.println(dept.getMgr().getClass().getName());
	}
	
	//双向 1-1 的关联关系, 建议先保存不维护关联关系的一方, 即没有外键的一方, 这样不会多出 UPDATE 语句.
	@Test
	public void testOneToOnePersistence(){
		Manager mgr = new Manager();
		mgr.setMgrName("M-BB");
		
		Department dept = new Department();
		dept.setDeptName("D-BB");
		
		//设置关联关系
		mgr.setDept(dept);
		dept.setMgr(mgr);
		
		//执行保存操作
		entityManager.persist(mgr);
		entityManager.persist(dept);
	}
	
	@Test
	public void testUpdate(){
		Customer customer = entityManager.find(Customer.class, 10);
		
		customer.getOrders().iterator().next().setOrderName("O-XXX-10");
	}
	
	//默认情况下, 若删除 1 的一端, 则会先把关联的 n 的一端的外键置空, 然后进行删除. 
	//可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略. 
	@Test
	public void testOneToManyRemove(){
		Customer customer = entityManager.find(Customer.class, 8);
		entityManager.remove(customer);
	}
	
	//默认对关联的多的一方使用懒加载的加载策略. 
	//可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略
	@Test
	public void testOneToManyFind(){
		Customer customer = entityManager.find(Customer.class, 9);
		System.out.println(customer.getLastName());
		
		System.out.println(customer.getOrders().size());
	}
	
	//若是双向 1-n 的关联关系, 执行保存时
	//若先保存 n 的一端, 再保存 1 的一端, 默认情况下, 会多出 n 条 UPDATE 语句.
	//若先保存 1 的一端, 则会多出 n 条 UPDATE 语句
	//在进行双向 1-n 关联关系时, 建议使用 n 的一方来维护关联关系, 而 1 的一方不维护关联系, 这样会有效的减少 SQL 语句. 
	//注意: 若在 1 的一端的 @OneToMany 中使用 mappedBy 属性, 则 @OneToMany 端就不能再使用 @JoinColumn 属性了. 
	
	//单向 1-n 关联关系执行保存时, 一定会多出 UPDATE 语句.
	//因为 n 的一端在插入时不会同时插入外键列. 
	@Test
	public void testOneToManyPersist(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("mm@163.com");
		customer.setLastName("MM");
		
		Order order1 = new Order();
		order1.setOrderName("O-MM-1");
		
		Order order2 = new Order();
		order2.setOrderName("O-MM-2");
		
		//建立关联关系
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行保存操作
		entityManager.persist(customer);

		entityManager.persist(order1);
		entityManager.persist(order2);
	}
	
	/*
	@Test
	public void testManyToOneUpdate(){
		Order order = entityManager.find(Order.class, 2);
		order.getCustomer().setLastName("FFF");
	}
	
	//不能直接删除 1 的一端, 因为有外键约束. 
	@Test
	public void testManyToOneRemove(){
//		Order order = entityManager.find(Order.class, 1);
//		entityManager.remove(order);
		
		Customer customer = entityManager.find(Customer.class, 7);
		entityManager.remove(customer);
	}
	
	//默认情况下, 使用左外连接的方式来获取 n 的一端的对象和其关联的 1 的一端的对象. 
	//可使用 @ManyToOne 的 fetch 属性来修改默认的关联属性的加载策略
	@Test
	public void testManyToOneFind(){
		Order order = entityManager.find(Order.class, 1);
		System.out.println(order.getOrderName());
		
		System.out.println(order.getCustomer().getLastName());
	}
	*/
	
	/**
	 * 保存多对一时, 建议先保存 1 的一端, 后保存 n 的一端, 这样不会多出额外的 UPDATE 语句.
	 */
	/*
	@Test
	public void testManyToOnePersist(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("gg@163.com");
		customer.setLastName("GG");
		
		Order order1 = new Order();
		order1.setOrderName("G-GG-1");
		
		Order order2 = new Order();
		order2.setOrderName("G-GG-2");
		
		//设置关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行保存操作
		entityManager.persist(order1);
		entityManager.persist(order2);
		
		entityManager.persist(customer);
	}
	*/
	
	/**
	 * 同 hibernate 中 Session 的 refresh 方法. 
	 */
	@Test
	public void testRefresh(){
		Customer customer = entityManager.find(Customer.class, 1);
		customer = entityManager.find(Customer.class, 1);
		
		entityManager.refresh(customer);
	}
	
	/**
	 * 同 hibernate 中 Session 的 flush 方法. 
	 */
	@Test
	public void testFlush(){
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println(customer);
		
		customer.setLastName("AA");
		
		entityManager.flush();
	}
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中有对应的对象
	//2. JPA 会把游离对象的属性复制到查询到EntityManager 缓存中的对象中.
	//3. EntityManager 缓存中的对象执行 UPDATE. 
	@Test
	public void testMerge4(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("dd@163.com");
		customer.setLastName("DD");
		
		customer.setId(4);
		Customer customer2 = entityManager.find(Customer.class, 4);
		
		entityManager.merge(customer);
		
		System.out.println(customer == customer2); //false
	}
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中没有该对象
	//2. 若在数据库中也有对应的记录
	//3. JPA 会查询对应的记录, 然后返回该记录对一个的对象, 再然后会把游离对象的属性复制到查询到的对象中.
	//4. 对查询到的对象执行 update 操作. 
	@Test
	public void testMerge3(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("ee@163.com");
		customer.setLastName("EE");
		
		customer.setId(4);
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println(customer == customer2); //false
	}
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中没有该对象
	//2. 若在数据库中也没有对应的记录
	//3. JPA 会创建一个新的对象, 然后把当前游离对象的属性复制到新创建的对象中
	//4. 对新创建的对象执行 insert 操作. 
	@Test
	public void testMerge2(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("dd@163.com");
		customer.setLastName("DD");
		
		customer.setId(100);
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println("customer#id:" + customer.getId());
		System.out.println("customer2#id:" + customer2.getId());
	}
	
	/**
	 * 总的来说: 类似于 hibernate Session 的 saveOrUpdate 方法.
	 */
	//1. 若传入的是一个临时对象
	//会创建一个新的对象, 把临时对象的属性复制到新的对象中, 然后对新的对象执行持久化操作. 所以
	//新的对象中有 id, 但以前的临时对象中没有 id. 
	@Test
	public void testMerge1(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("cc@163.com");
		customer.setLastName("CC");
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println("customer#id:" + customer.getId());
		System.out.println("customer2#id:" + customer2.getId());
	}
	
	//类似于 hibernate 中 Session 的 delete 方法. 把对象对应的记录从数据库中移除
	//但注意: 该方法只能移除 持久化 对象. 而 hibernate 的 delete 方法实际上还可以移除 游离对象.
	@Test
	public void testRemove(){
//		Customer customer = new Customer();
//		customer.setId(2);
		
		Customer customer = entityManager.find(Customer.class, 2);
		entityManager.remove(customer);
	}
	
	//类似于 hibernate 的 save 方法. 使对象由临时状态变为持久化状态. 
	//和 hibernate 的 save 方法的不同之处: 若对象有 id, 则不能执行 insert 操作, 而会抛出异常. 
	@Test
	public void testPersistence(){
		Customer customer = new Customer();
		customer.setAge(15);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("bb@163.com");
		customer.setLastName("BB");
		customer.setId(100);
		
		entityManager.persist(customer);
		System.out.println(customer.getId());
	}
	
	//类似于 hibernate 中 Session 的 load 方法
	@Test
	public void testGetReference(){
		Customer customer = entityManager.getReference(Customer.class, 1);
		System.out.println(customer.getClass().getName());
		
		System.out.println("-------------------------------------");
//		transaction.commit();
//		entityManager.close();
		
		System.out.println(customer);
	}
	
	//类似于 hibernate 中 Session 的 get 方法. 
	@Test
	public void testFind() {
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println("-------------------------------------");
		
		System.out.println(customer);
	}

}

```

## 8.Jpa多数据源配置(1.5版本)

**由于是多数据源所以不同数据库的类和Repository放在不同的包内**



> - @Autowired 自动装配
> - @Qualifier 指定自动装配bean的name
> - @Primary 指定装配的优先级，这个指定的优先级最高。

### 8.1配置文件

```xml
spring.datasource.primary.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=true
spring.datasource.primary.username=root
spring.datasource.primary.password=root
spring.datasource.primary.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.max-active=10
spring.datasource.primary.max-idle=5
spring.datasource.primary.min-idle=0

spring.datasource.secondary.url=jdbc:mysql://localhost:3306/springcloud_db02?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=true
spring.datasource.secondary.username=root
spring.datasource.secondary.password=root
spring.datasource.secondary.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.secondary.max-active=10
spring.datasource.secondary.max-idle=5
spring.datasource.secondary.min-idle=0

#新设置访问接口，默认为8080
server.port=80

#hibernate配置
spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.show-sql= true


```

### 8.2数据源配置

```java
package com.atles.jpamaneysql.datasourceconfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Configuration
public class DataSourcesConfig {

    @Bean(name = "primaryDataSource")
    @Qualifier("primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary") //为配置文件中的数据库信息配置属性
    public DataSource primaryDataSource(){
        System.out.println("primary db built");
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "secondaryDataSource")
    @Qualifier("secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource(){
        System.out.println("secondary db built");
        return DataSourceBuilder.create().build();
    }


}

```

### 8.3 第一数据源

```java
package com.atles.jpamaneysql.entityconfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef="entityManagerFactoryPrimary",
        transactionManagerRef="transactionManagerPrimary",
        basePackages= { "com.atles.jpamaneysql.repository.cot" }
     )
public class PrimaryConfig {

    @Autowired
    @Qualifier(value = "primaryDataSource")
    private DataSource primaryDataSource;

    @Bean(name = "entityManagerPrimary")
    @Primary
    public EntityManager entityManager(EntityManagerFactoryBuilder builder){
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder){
        return builder.dataSource(primaryDataSource)
  
                .properties(getVendorProperties(primaryDataSource))
                .packages("com.atles.jpamaneysql.entity")
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }


    @Autowired(required = false)
    private JpaProperties jpaProperties;

    private Map<String,String> getVendorProperties(DataSource dataSource){
      
        return jpaProperties.getHibernateProperties(dataSource);
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }

}

```

### 8.4第二数据源

```java
package com.atles.jpamaneysql.entityconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="entityManagerFactorySecondary",
        transactionManagerRef="transactionManagerSecondary",
        basePackages= { "com.atles.jpamaneysql.repository.pro" }) //设置Repository所在位置
public class SecondaryConfig {

    @Autowired(required = false)
    private JpaProperties jpaProperties;

    @Autowired @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @Bean(name = "entityManagerSecondary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySecondary(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactorySecondary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryDataSource)
                .properties(getVendorProperties(secondaryDataSource))
                .packages("com.atles.jpamaneysql.bean") //设置实体类所在位置
                .persistenceUnit("secondaryPersistenceUnit")
                .build();
    }


    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    @Bean(name = "transactionManagerSecondary")
    PlatformTransactionManager transactionManagerSecondary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySecondary(builder).getObject());
    }


}

```



### 8.5目录结构

![1562399631102](C:\Users\user\AppData\Roaming\Typora\typora-user-images\1562399631102.png)

## 9.Jpa多数据源配置(2.0版本)

### 9.1 配置文件

**使用yml书写配置文件使用application.properties书写将会出现jdbcUrl错误**

```xml
spring:
  datasource:
    primary:
      jdbc-url: jdbc:mysql://localhost:3306/test
      username: root
      password: root
    secondary:
      jdbc-url: jdbc:mysql://localhost:3306/springcloud_db02
      username: root
      password: root
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
      
```



### 9.2 数据源配置

**注册多数据源的配置没有什么改变**

```java
package com.atles.springbootjpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Configuration
public class DataSourcesConfig {

    @Bean(name = "primaryDataSource")
    @Qualifier("primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary") //为配置文件中的数据库信息配置属性
    public DataSource primaryDataSource(){
        System.out.println("primary db built");
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "secondaryDataSource")
    @Qualifier("secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource(){
        System.out.println("secondary db built");
        return DataSourceBuilder.create().build();
    }

}

```

### 9.3 第一数据源

```java
package com.atles.springbootjpa.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef="entityManagerFactoryPrimary",
        transactionManagerRef="transactionManagerPrimary",
        basePackages= { "com.atles.springbootjpa.repository.cot" }
     )
public class PrimaryConfig {

    @Autowired
    @Qualifier(value = "primaryDataSource")
    private DataSource primaryDataSource;

    @Bean(name = "entityManagerPrimary")
    @Primary
    public EntityManager entityManager(EntityManagerFactoryBuilder builder){
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder){
        return builder.dataSource(primaryDataSource)
                .properties(getVendorProperties())
                .packages("com.atles.springbootjpa.entity")
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }


    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    private Map<String,Object> getVendorProperties(){
        return  hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }

}

```

### 9.4 第二数据源

```java
package com.atles.springbootjpa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="entityManagerFactorySecondary",
        transactionManagerRef="transactionManagerSecondary",
        basePackages= { "com.atles.springbootjpa.repository.pro" }) //设置Repository所在位置
public class SecondaryConfig {



    @Autowired @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @Bean(name = "entityManagerSecondary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySecondary(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactorySecondary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryDataSource)
                .properties(getVendorProperties())
                .packages("com.atles.springbootjpa.bean") //设置实体类所在位置
                .persistenceUnit("secondaryPersistenceUnit")
                .build();
    }
    @Autowired
    private JpaProperties jpaProperties;
    @Autowired
    private HibernateProperties hibernateProperties;

    private Map<String,Object> getVendorProperties(){
        return
                hibernateProperties
                        .determineHibernateProperties
                                (jpaProperties.getProperties(), new HibernateSettings());
    }


    @Bean(name = "transactionManagerSecondary")
    PlatformTransactionManager transactionManagerSecondary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySecondary(builder).getObject());
    }


}

```

### 9.5 注意

> repository、entity的所在位置，要和实际保存的位置一致。
>
> 主数据源的一些配置需要添加@Primary作为spring默认的首选项，其他数据源无需添加该注解。
>
> 通过查看相关源码我们看到Spring Boot中JpaProperties的代码一直在调整，这里我们将properties相关代码单独提取出作为一个单独的方法getVendorProperties展示版本间的区别。其中：
>
> springBoot 1.5.x 使用的是：
>
> ```java
> private Map<String, String> getVendorProperties() {
>   return jpaProperties.getHibernateProperties(userDataSource);
> }
> ```
>
> springBoot 2.0.x 使用的是：
>
> ```java
> private Map<String, Object> getVendorProperties() {
>   return jpaProperties.getHibernateProperties(new HibernateSettings());
> }
> ```
>
> 