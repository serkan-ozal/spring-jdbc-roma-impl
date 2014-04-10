CONTENTS
=======

- [1. What is Spring-JDBC-ROMA?](#Section_1)
- [2. Features](#Section_2)
- [3. Installation](#Section_3)
- [4. Usage](#Section_4)
- [4.1. Default configurations](#Section_4_1)
- [4.2. Primitive Typed Field Features](#Section_4_2)
- [4.3. Date Typed Field Features](#Section_4_3)
- [4.4. Clob Typed Column Features](#Section_4_4)
- [4.5. Blob Typed Column Features](#Section_4_5)
- [4.6. Enum Typed Field Features](#Section_4_6)
- [4.7. Field Based Features Configuration Features](#Section_4_7)
- [4.8. Class (or Type) Based Configuration Features](#Section_4_8)
- [4.9. RXEL (ROMA Expression Language)](#Section_4_9)
- [4.10. Complex Typed Field Features](#Section_4_10)
- [4.11. Conditional Lazy Feature](#Section_4_11)
- [4.12. Conditional Lazy-Load Feature](#Section_4_12)
- [4.13. Conditional Ignore Feature](#Section_4_13)
- [4.14. Other Features](#Section_4_14)
- [5. A Simple Example](#Section_5)
- [6. Roadmap](#Section_6)

<a name="Section_1"></a>
1. What is Spring-JDBC-ROMA?
=======

**Spring-JDBC-ROMA** is a rowmapper extension for **Spring-JDBC module**. 
There is already a rowmapper named `org.springframework.jdbc.core.BeanPropertyRowMapper` for binding 
resultset attributes to object. But it is reflection based and can cause performance problems as Spring developers said.
However **Spring-JDBC-ROMA** is not reflection based and it is byte code generation (with **CGLib** and **Javassist**) 
based rowmapper. It generates rowmapper on the fly like implementing as manual so it has no performance overhead. 
It also supports object relations as lazy and eager. There are other lots of interesting features and 
these features can be customized with developer's extended classes. 

<a name="Section_2"></a>
2. Features
=======

* All primitive types, strings, enums, dates, clob, blob, collections and complex objects are supported.  

* Writing your custom class (or type) based field generator factory, object creater, object processor, table name resolver, column name resolver implementations and customizable data source, schema, table names are supported.  

* Writing your custom field based mapper, SQL based binder, expression language based binder and custom binder implementations are supported.   
 
* Lazy or eager field accessing is supported. Lazy support can be configured as conditional and conditions can be provided as expression language or as custom lazy condition provider implementations. If lazy condition is not enable, specified field is not handled as lazy and set as eager while creating root entity.

* Loading lazy fields can be enable or disable at runtime dynamically by using key based, expression based and custom implementation based approaches. If lazy-load condition is not enable, specified lazy field is not loaded no matter field is configured as lazy.

* Ignoring fields can be enable or disable at runtime dynamically by using key based, expression based and custom implementation based approaches. This feature is very useful in some use-cases. For example, this feature can be used to ignore some fields while serializing to JSON at your specified controller and this behaviour doesn't effect other controllers.

* Writing field access definitions as REXL (ROMA Expression Language) or as compilable Java code in annotation. XML and properties file configuration support will be added soon.  

<a name="Section_3"></a>
3. Installation
=======

In your **pom.xml**, you must add repository and dependency for Spring-JDBC-ROMA. 
You can change **spring.jdbc.roma.version** to any existing **spring-jdbc-roma** library version.

~~~~~ xml
...
<properties>
    ...
    <spring.jdbc.roma.version>2.0.0-SNAPSHOT</spring.jdbc.roma.version>
    ...
</properties>
...
<dependencies>
    ...
	<dependency>
		<groupId>org.springframework</groupId>
		<artifactId>spring-jdbc-roma</artifactId>
		<version>${spring.jdbc.roma.version}</version>
	</dependency>
	...
</dependencies>
...
<repositories>
	...
	<repository>
		<id>serkanozal-maven-repository</id>
		<url>https://github.com/serkan-ozal/maven-repository/raw/master/</url>
	</repository>
	...
</repositories>
...
~~~~~

And finally, in your **Spring context xml** file add following configuration to make your Spring context automatically aware of Spring-JDBC-ROMA.

~~~~~ xml
...
<import resource="classpath*:roma-context.xml"/>
...
~~~~~

<a name="Section_4"></a>
4. Usage
=======

You must get your row mapper via service class (`org.springframework.jdbc.roma.impl.service.RowMapperService`) of ROMA.

~~~~~ java
@Autowired
RowMapperService rowMapperService;
    
...

RowMapper<User> userRowMapper = rowMapperService.getRowMapper(User.class);
~~~~~

<a name="Section_4_1"></a>
4.1. Default configurations
-------

Default configurations are stored and accessed by `org.springframework.jdbc.roma.api.config.DefaultConfigs` class.

Configurable values as default are:

* **Datasource name:** Name of the datasource to connect. Initial value of default datasource name is `dataSource`. So if you have defined your datasource named as `dataSource`, you don't need to configure this value.

* **Schema name:** Name of the schema name in DB to connect. Initial value of default datasource name is `null`. So default schema of DB is used. If default schema of your connected DB is used, you don't need to configure this value.

Using default values are helpful if there are so many entities using same datasource and schema name configurations.
In addition to default values, you must configure these values based on entity and this will be mentioned in sections below. Note that initial default 

Default configurations can be configured programatically or in context xml of Spring like:

~~~~~ xml
...
<bean id="defaultConfigs" class="org.springframework.jdbc.roma.api.config.DefaultConfigs">
	<property name="defaultDataSourceName" value="myDataSource"/>
	<property name="defaultSchemaName" value="myDBSchema"/>
</bean>
...
~~~~~

<a name="Section_4_2"></a>
4.2. Primitive Typed Field Features
-------

* `boolean`
* `byte`
* `char`
* `short`
* `int`
* `float`
* `long`
* `double`
* `java.lang.Boolean`
* `java.lang.Byte`
* `java.lang.Character`
* `java.lang.Short`
* `java.lang.Integer`
* `java.lang.Float`
* `java.lang.Long`
* `java.lang.Double`
* `java.lang.String`  

typed fields are automatically mapped to result set in row mapper.

<a name="Section_4_3"></a>
4.3. Date Typed Field Features
-------

`java.util.Date` typed fields are automatically mapped to result set as **Date** in row mapper. But if mapped column in database is defined as **Timestamp** typed, you can configure this field by using **`@RowMapperTimeField`** annotation as:

~~~~~ java
@RowMapperTimeField(asTimestamp = true)
private Date date;
~~~~~

<a name="Section_4_4"></a>
4.4. Clob Typed Column Features
-------

`CLOB` typed column can be mapped to **`java.lang.String`** typed field by using **`@RowMapperClobField`** annotation like this:

~~~~~ java
@RowMapperClobField
private String address;
~~~~~

<a name="Section_4_5"></a>
4.5. Blob Typed Column Features
-------
`BLOB` typed column can be mapped to **`byte[]`** typed field by using **`@RowMapperBlobField`** annotation like this:

~~~~~ java
@RowMapperBlobField
private byte[] image;
~~~~~

<a name="Section_4_6"></a>
4.6. Enum Typed Field Features
-------

By default, enums are mapped by using numeric column value as ordinal of enum. For example, you have an enum named `UserType` like this:

~~~~~ java
public enum UserType {

	ADMIN,
	MEMBER,
	GUEST
	
}
~~~~~

If numeric value of column is `1`, value is mapped to enum field as `MEMBER`.

Enum mappings can be configured by using **`@RowMapperEnumField`** annotation.

* **constantsAndMaps:** This feature is now depracated from version 2.0. With this configuration feature, you can map numeric value of column to String value of enum.

For example:
~~~~~ java
@RowMapperEnumField(constantsAndMaps = {"1:ADMIN", "2:MEMBER", "3:GUEST"})
private UserType userType;
~~~~~

* **enumStartValue:** With this configuration feature, numeric column value is mapped to enum ordinal by using start value as first element of enum. For example, you have an enum named `Language` like this:

~~~~~ java
public enum Language {

	TURKISH,
	ENGLISH,
	GERMAN,
	...
}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(enumStartValue = 1)
private Language language;
~~~~~

If numeric value of column is `1`, value is mapped to enum field as `TURKISH`. Because `enumStartValue` is `1` and actual ordinal will be `1 - 1 = 0`. So mapped value will be `TURKISH`.

* **useStringValue:** By default, column value is mapped to enum value with its numeric value. With this configuration feature, column value is mapped to enum value with its string value by using name of enum value. For example, you have an enum named `Religion` like this:

~~~~~ java
public enum Religion {

	MUSLIM,
	JEWISH,
	CHRISTIAN_CAHTOLICS,
	CHRISTIAN_ORTHODOX,
	CHRISTIAN_PROTESTANT,
	ATHEIST,
	OTHER;
	
}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(useStringValue = true)
private Religion religion;
~~~~~

If string value of column is `"MUSLIM"`, value is mapped to enum field as `MUSLIM`. 

* **defaultIndex:** With this configuration feature, if column value is empty (null), this index value will be used as ordinal of enum value for default value of field. For example, you have an enum named `UserType` like this:

~~~~~ java
public enum UserType {

	ADMIN,
	MEMBER,
	GUEST
	
}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(defaultIndex = 2)
private UserType userType;
~~~~~

If column value is empty (null), value is mapped to enum field as `GUEST`. Because `defaultIndex` is `2` and ordinal will used as `2`. So mapped value will be `GUEST`.

* **defaultValue:** With this configuration feature, if column value is empty (null), this index value will be used as name of enum value for default value of field. For example, you have an enum named `UserType` like this:

~~~~~ java
public enum UserType {

	ADMIN,
	MEMBER,
	GUEST
	
}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(defaultValue = "GUEST")
private UserType userType;
~~~~~

If column value is empty (null), value is mapped to enum field as `GUEST`. Since `defaultValue` is `"GUEST"`, mapped value will be `GUEST`.

* **mapViaNumericMapper:** With this configuration feature by using **`@RowMapperEnumNumericMapper`** annotation in **`@RowMapperEnumField`** annotation, you can use your custom mapper implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.NumericEnumMapper`** interface for numeric column values. Instance of your implementation is created once and used as singleton. For example, you have an enum named `BloodType` like this:

~~~~~ java
public enum BloodType {

	TYPE_A_RH_POSITIVE(1),
	TYPE_A_RH_NEGATIVE(2),
	TYPE_B_RH_POSITIVE(3),
	TYPE_B_RH_NEGATIVE(4),
	TYPE_AB_RH_POSITIVE(5),
	TYPE_AB_RH_NEGATIVE(6),
	TYPE_0_RH_POSITIVE(7),
	TYPE_0_RH_NEGATIVE(8);
	
	int code;
	
	BloodType(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}
~~~~~

your custom numeric mapper implementation is declared as like:

~~~~~ java
public class BloodTypeEnumMapper implements RowMapperEnumField.NumericEnumMapper<BloodType> {

	@Override
	public BloodType map(Integer value) {
		for (BloodType bt : BloodType.values()) {
			if (bt.getCode() == value) {
				return bt;
			}
		}
		return null;
	}

}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(
	mapViaNumericMapper = 
		@RowMapperEnumNumericMapper(
			mapper = BloodTypeEnumMapper.class))
private BloodType bloodType;
~~~~~

* **mapViaStringMapper:** With this configuration feature by using **`@RowMapperEnumStringMapper`** annotation in **`@RowMapperEnumField`** annotation, you can use your custom mapper implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.StringEnumMapper`** interface for numeric column values. Instance of your implementation is created once and used as singleton. For example, you have an enum named `MaritalStatus` like this:

~~~~~ java
public enum MaritalStatus {

	SINGLE,
	ENGAGED,
	MARRIED,
	DIVORCED;

}
~~~~~

your custom numeric mapper implementation is declared as like:

~~~~~ java
public class MaritalStatusEnumMapper implements RowMapperEnumField.StringEnumMapper<MaritalStatus> {

	@Override
	public MaritalStatus map(String value) {
		for (MaritalStatus ms : MaritalStatus.values()) {
			if (ms.name().equalsIgnoreCase(value)) {
				return ms;
			}
		}
		return null;
	}

}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(
	mapViaStringMapper = 
		@RowMapperEnumStringMapper(
			mapper = MaritalStatusEnumMapper.class))
private MaritalStatus maritalStatus;
~~~~~

* **mapViaNumericValueNumericMappings:** With this configuration feature by using **`@RowMapperEnumAutoMapper`** annotation in **`@RowMapperEnumField`** annotation, you can map numeric column values with ordinals of enum. For example, you have an enum named `Occupation` like this:

~~~~~ java
public enum Occupation {

	OTHER(0),
	ARCHITECT(100),
	DOCTOR(200),
	ENGINEER(300),
	LAWYER(400),
	MUSICIAN(500),
	STUDENT(600),
	TEACHER(700);
	
	int code;
	
	Occupation(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(
	mapViaAutoMapper = 
		@RowMapperEnumAutoMapper(
			mapViaNumericValueNumericMappings = {
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 0, value = 0),
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 1, value = 100),
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 2, value = 200),
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 3, value = 300),
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 4, value = 400),
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 5, value = 500),
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 6, value = 600),
				@RowMapperEnumNumericValueNumericMapping(mappingIndex = 7, value = 700)}))
private Occupation occupation;
~~~~~

* **mapViaNumericValueStringMappings:** With this configuration feature by using **`@RowMapperEnumAutoMapper`** annotation in **`@RowMapperEnumField`** annotation, you can map numeric column values with names of enum. For example, you have an enum named `Occupation` like this:

~~~~~ java
public enum Occupation {

	OTHER(0),
	ARCHITECT(100),
	DOCTOR(200),
	ENGINEER(300),
	LAWYER(400),
	MUSICIAN(500),
	STUDENT(600),
	TEACHER(700);
	
	int code;
	
	Occupation(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(
	mapViaAutoMapper = 
		@RowMapperEnumAutoMapper(
			mapViaNumericValueStringMappings = {
				@RowMapperEnumNumericValueStringMapping(mappingValue = "OTHER", value = 0),
				@RowMapperEnumNumericValueStringMapping(mappingValue = "ARCHITECT", value = 100),
				@RowMapperEnumNumericValueStringMapping(mappingValue = "DOCTOR", value = 200),
				@RowMapperEnumNumericValueStringMapping(mappingValue = "ENGINEER", value = 300),
				@RowMapperEnumNumericValueStringMapping(mappingValue = "LAWYER", value = 400),
				@RowMapperEnumNumericValueStringMapping(mappingValue = "MUSICIAN", value = 500),
				@RowMapperEnumNumericValueStringMapping(mappingValue = "STUDENT", value = 600),
				@RowMapperEnumNumericValueStringMapping(mappingValue = "TEACHER", value = 700)}))
private Occupation occupation;
~~~~~


* **mapViaStringValueNumericMappings:** With this configuration feature by using **`@RowMapperEnumAutoMapper`** annotation in **`@RowMapperEnumField`** annotation, you can map string column values with ordinals of enum. For example, you have an enum named `Education` like this:

~~~~~ java
public enum Education {

	PRIMARY_SCHOOL,
	SECONDARY_SCHOOL,
	HIGH_SCHOOL,
	BACHELOR,
	MASTER,
	PHD,
	ASSOCIATE_PROFESSOR,
	PROFESSOR,
	OTHER;

}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(
	mapViaAutoMapper = 
		@RowMapperEnumAutoMapper(
			mapViaStringValueNumericMappings = {
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 0, value = "PRIMARY_SCHOOL"),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 1, value = "SECONDARY_SCHOOL"),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 2, value = "HIGH_SCHOOL"),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 3, value = "BACHELOR"),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 4, value = "MASTER"),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 5, value = "PHD" ),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 6, value = "ASSOCIATE_PROFESSOR"),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 7, value = "PROFESSOR"),
				@RowMapperEnumStringValueNumericMapping(mappingIndex = 8, value = "OTHER")}))
private Education education;
~~~~~

* **mapViaStringValueStringMappings:** With this configuration feature by using **`@RowMapperEnumAutoMapper`** annotation in **`@RowMapperEnumField`** annotation, you can map string column values with names of enum. For example, you have an enum named `Education` like this:

~~~~~ java
public enum Education {

	PRIMARY_SCHOOL,
	SECONDARY_SCHOOL,
	HIGH_SCHOOL,
	BACHELOR,
	MASTER,
	PHD,
	ASSOCIATE_PROFESSOR,
	PROFESSOR,
	OTHER;

}
~~~~~

and enum field is declared as like:

~~~~~ java
@RowMapperEnumField(
	mapViaAutoMapper = 
		@RowMapperEnumAutoMapper(
			mapViaStringValueStringMappings = {
				@RowMapperEnumStringValueStringMapping(mappingValue = "PRIMARY_SCHOOL", value = "PRIMARY_SCHOOL"),
				@RowMapperEnumStringValueStringMapping(mappingValue = "SECONDARY_SCHOOL", value = "SECONDARY_SCHOOL"),
				@RowMapperEnumStringValueStringMapping(mappingValue = "HIGH_SCHOOL", value = "HIGH_SCHOOL"),
				@RowMapperEnumStringValueStringMapping(mappingValue = "BACHELOR", value = "BACHELOR"),
				@RowMapperEnumStringValueStringMapping(mappingValue = "MASTER", value = "MASTER"),
				@RowMapperEnumStringValueStringMapping(mappingValue = "PHD", value = "PHD" ),
				@RowMapperEnumStringValueStringMapping(mappingValue = "ASSOCIATE_PROFESSOR", value = "ASSOCIATE_PROFESSOR"),
				@RowMapperEnumStringValueStringMapping(mappingValue = "PROFESSOR", value = "PROFESSOR"),
				@RowMapperEnumStringValueStringMapping(mappingValue = "OTHER", value = "OTHER")}))
private Education education;
~~~~~

<a name="Section_4_7"></a>
4.7. Field Based Features Configuration Features
-------
General field configurations can be done by using **`@RowMappeField`** annotation.

By default, you don't need to specify column name for field if you use similar names for field and column in database. For similarity between field name and column name, default column name resolver generates some possible column names by using field name and check them by connecting to related table at database. Generated possible column names are in

1. columnName
2. columname
3. COLUMNNAME
4. column_Name
5. colum_name
6. COLUMN_NAME

formats.

For example, if you have a field named `birthDate`, generated possible column names will be

1. birthDate
2. birthdate
3. BIRTHDATE
4. birth_Date
5. birth_date
6. BIRTH_DATE

* **columnName:** With this configuration feature, you can specify column name of field to be mapped.

For example:
~~~~~ java
@RowMappeField(columnName = "username")
private String username;
~~~~~

* **fieldGenerator:** With this configuration feature, you can use your custom field generator implementations by implementing **`org.springframework.jdbc.roma.api.generator.RowMapperFieldGenerator`** interface for generating mapping statement. Instance of your implementation is created once and used as singleton. In you implementation, you can

1. refer to created entity by using **`RowMapperFieldGenerator.GENERATED_OBJECT_NAME`** property,
2. refer to resultset by using **`RowMapperFieldGenerator.RESULT_SET_ARGUMENT`** property,
3. refer to row number by using **`RowMapperFieldGenerator.ROW_NUM_ARGUMENT`** property.

For example:
~~~~~ java
@RowMappeField(fieldGenerator = UsernameFieldGenerator.class)
private String username;
~~~~~

and your custom field generator implementation is declared as like:

~~~~~ java
public class UsernameFieldGenerator implements RowMapperFieldGenerator {

	@Override
	public String generateFieldMapping(Field f) {
		return 
			RowMapperFieldGenerator.GENERATED_OBJECT_NAME + 
				".setUsername" + 
				"(" + 
					RowMapperFieldGenerator.RESULT_SET_ARGUMENT + ".getString(\"username\")" +	
				");";
	}

}
~~~~~

* **fieldMapper:** With this configuration feature, you can use your custom field mapper implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperField.RowMapperFieldMapper`** interface for mapping specified field from resultset. Instance of your implementation is created once and used as singleton.

For example:
~~~~~ java
@RowMapperField(fieldMapper = RoleNameFieldMapper.class)
private String name;
~~~~~

and your custom field mapper implementation is declared as like:

~~~~~ java
public class RoleNameFieldMapper implements RowMapperFieldMapper<Role> {

	private static final Logger logger = Logger.getLogger(RoleNameFieldMapper.class);
	
	@Override
	public void mapField(Role role, String fieldName, ResultSet rs, int rowNum) {
		try {
			role.setName(rs.getString("name"));
		} 
		catch (SQLException e) {
			logger.error("Error occured while mapping field " + fieldName + 
					" in Role object from resultset", e);
		}
	}

}
~~~~~

<a name="Section_4_8"></a>
4.8. Class (or Type) Based Configuration Features
-------

General class (or type) configurations can be done by using **`@RowMappeClass`** annotation.

* **dataSourceName:** With this configuration feature, you can specify name of the datasource to connect for this class (or type). Initial value of default datasource name is `dataSource`. So if you have defined your datasource named as `dataSource`, you don't need to configure this value.

For example:
~~~~~ java
@RowMapperClass(dataSourceName = "myDataSource")
public class User {
	...
}
~~~~~

* **schemaName:** With this configuration feature, you can specify name of the schema in database to connect for this class (or type). Initial value of default datasource name is `null`. So default schema of DB is used. If default schema of your connected DB is used, you don't need to configure this value.

For example:
~~~~~ java
@RowMapperClass(schemaName = "Production")
public class User {
	...
}
~~~~~

* **tableName:** With this configuration feature, you can specify name of the table to map this class (or type). By default, you don't need to specify table name for class if you use similar names for class and table in database. For similarity between class name and table name, default table name resolver generates some possible table names by using class name and check them by connecting to related table at database. Generated possible table names are in

1. TableName
2. tablename
3. TABLENAME
4. Table_Name
5. table_name
6. TABLE_NAME

formats.

For example, if you have a class named `SystemLog`, generated possible table names will be

1. SystemLog
2. systemlog
3. SYSTEMLOG
4. System_Log
5. system_log
6. SYSTEM_LOG

For example:
~~~~~ java
@RowMapperClass(tableName = "USERS")
public class User {
	...
}
~~~~~

* **fieldGeneratorFactory:** With this configuration feature, you can use your custom field generator factory implementations by implementing **`org.springframework.jdbc.roma.api.factory.RowMapperGeneratorFactory`** interface for creating field generators. Instance of your implementation is created once and used as singleton. For example you have a class named `User` and you will use your custom field generator factory class.

For example:
~~~~~ java
@RowMapperClass(fieldGeneratorFactory = UserFieldGeneratorFactory.class)
public class User {
	...
}
~~~~~

and your custom field generator factory implementation is declared as like:

~~~~~ java
public class UserFieldGeneratorFactory implements RowMapperFieldGeneratorFactory<User> {

	@Override
	public RowMapperFieldGenerator<User> createRowMapperFieldGenerator(Field f) {
		if (f.getType().equals(int.class) || f.getType().equals(Integer.class)) {
			return new MyIntegerFieldGenerator(f);
		}
		...
	}

}
~~~~~

* **objectCreater:** With this configuration feature, you can use your custom object creater implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperObjectCreater`** interface for creating entities. Instance of your implementation is created once and used as singleton. For example you have a class named `Role` and you will use your custom object creater class.

For example:
~~~~~ java
@RowMapperClass(objectCreater = RoleObjectCreater.class)
public class Role {
	...
}
~~~~~

and your custom object creater implementation is declared as like:

~~~~~ java
public class RoleObjectCreater implements RowMapperObjectCreater<Role> {

	@Override
	public Role createObject(Class<Role> clazz) {
		return new Role();
	}

}
~~~~~

* **objectProcessor:** With this configuration feature, you can use your custom object processor implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperObjectProcessor`** interface for creating entities. Instance of your implementation is created once and used as singleton. Object processor class is called after all fields have been mapped from result set before object is returned from rowmapper. For example you have a class named `User` and you will use your custom object processor class. By your custom object processor class, you will set `age` field that is not present in database table of `User` object since it can be calculated by using `birthDate` field.

For example:
~~~~~ java
@RowMapperClass(objectProcessor = UserObjectProcessor.class)
public class User {
	...
}
~~~~~

and your custom object processor implementation is declared as like:

~~~~~ java
public class UserObjectProcessor implements RowMapperObjectProcessor<User> {

	@SuppressWarnings("deprecation")
	@Override
	public void processObject(User user, ResultSet rs, int rowNum) {
		if (user.getBirthDate() != null) {
			user.setAge((byte)(Calendar.getInstance().getTime().getYear() - user.getBirthDate().getYear()));
		}
	}

}
~~~~~

* **columnNameResolver:** With this configuration feature, you can use your custom column name resolver implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperColumnNameResolver`** interface for resolving column name of fields. Instance of your implementation is created once and used as singleton. For example you have a class named `User` and you will use your custom field generator factory class.

For example:
~~~~~ java
@RowMapperClass(columnNameResolver = UserColumnNameResolver.class)
public class User {
	...
}
~~~~~

and your custom column name resolver implementation is declared as like:

~~~~~ java
public class UserColumnNameResolver implements RowMapperColumnNameResolver {

	@Override
	public String resolveColumnName(Field f) {
		return f.getName().toLowerCase();
	}

}
~~~~~

* **tableNameResolver:** With this configuration feature, you can use your custom table name resolver implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperTableNameResolver`** interface for resolving table name of classes. Instance of your implementation is created once and used as singleton. For example you have a class named `User` and you will use your custom field generator factory class.

For example:
~~~~~ java
@RowMapperClass(tableNameResolver = UserTableNameResolver.class)
public class User {
	...
}
~~~~~

and your custom table name resolver implementation is declared as like:

~~~~~ java
public class UserTableNameResolver implements RowMapperTableNameResolver {

	@Override
	public String resolveTableName(Class<?> clazz) {
		return clazz.getSimpleName().toUpperCase();
	}

}
~~~~~ 

<a name="Section_4_9"></a>
4.9. RXEL (ROMA Expression Language)
-------

ROMA has a specific expression language named **ROMA Expression Language (REXL)** for customizing some implementations in short like any other expression languages such as Spring expression language.

4.9.1. Using beans in expressions
-------
With **`@`** sign you can use Spring beans in your expression language.

For example:
`@{userDao}.list()` expression is executed as calling `list` method in Spring bean with id `userDao`.

4.9.2. Using properties in expressions
-------
With **`$`** sign you can use properties of created entity in your expression language.

For example:
`@{roleDao}.list(${id})` expression is executed as calling `list` method in Spring bean with id `roleDao` by passing `id` property of created entity as argument.

4.9.3. Using attributes in expressions
-------
With **`#`** sign you can get any attribute of any object in your expression language. 

For example:
`@{userDao}.getAdmin().#{name}` expression is executed as calling `getAdmin` method in Spring bean with id `userDao` and return `name` attribute of returning object (possibly User entity) from `getAdmin` call.

4.9.4. Using result set in expressions
-------
With **`&`** sign you can use result set attributes in your expression language. But in your expression, you must indicate type of the requested attribute between **`[`** and **`]`** signs.

For example:
`&{[int]enable} == 0 ? "false" : "true"` expression is executed as calling `getInt("enable")` method in result set and checks its value to return `true` or `false`.

<a name="Section_4_10"></a>
4.10. Complex Typed Field Features
-------

* **provideViaExpressionProvider:** With this configuration feature, you can use RXEL (ROMA Expression Language) expression language for providing field value.

For example:
~~~~~ java
@RowMapperObjectField(
	provideViaExpressionProvider = 
		@RowMapperExpressionProvider(expression = "@{roleDAO}.getUserRoleList(${id})"))
private List<Role> roles;
~~~~~

* **provideViaSqlProvider:** With this configuration feature, you can specify SQL query will be executed to provide value of field by using limited supported version of RXEL. Only property (**`${...}`**) and resultset (**`&{[...]...}`**) based expressions are supported in specified SQL query. If your field is collection typed (Only List type is supported for SQL provide feature), you must specify element type by using **`entityType`** property. In addition, you can specify name of datasource where SQL query will be executed by using **`dataSourceName`** property.

For example:
~~~~~ java
RowMapperObjectField(
	provideViaSqlProvider = 
		@RowMapperSqlProvider(
	                provideSql = 
	                    "SELECT p.* FROM PERMISSION p WHERE p.ID IN " +
	                    "(" +
	                        "SELECT rp.PERMISSION_ID FROM role_permission rp WHERE rp.ROLE_ID = ${id}" +
	                    ") ORDER BY p.name",
	                entityType = Permission.class),	    
	        ...)
private List<Permission> permissions;
~~~~~ 

Also, there are a way to customizing SQL query with its parameters by using your custom SQL query info provider implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider.RowMapperSqlQueryInfoProvider`** interface for providing SQL query info. Instance of your implementation is created once and used as singleton. Your custom SQL query info provider class returns a **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider.SqlQueryInfo`** typed object and this objects contains SQL query to execute and its parameters as **`Object[]`** array. Note that **RXEL** is **not supported** in SQL query for this feature and you must use **`?`** as placeholder for parameter in query.
 
For example:
~~~~~ java
@RowMapperObjectField(
	provideViaSqlProvider = 
		@RowMapperSqlProvider(
			sqlQueryInfoProvider = UserAccountInfoSqlQueryInfoProvider.class))
private AccountInfo accountInfo;
~~~~~

and your custom SQL query info provider implementation is declared as like:

~~~~~ java
public class UserAccountInfoSqlQueryInfoProvider implements RowMapperSqlQueryInfoProvider<User> {

	@Override
	public SqlQueryInfo provideSqlQueryInfo(User user, String fieldName) {
		return 
			new SqlQueryInfo(
				"SELECT a.* FROM ACCOUNT_INFO a WHERE a.ID IN " +
	            "(" +
	                "SELECT ua.account_info_id FROM USER_ACCOUNT_INFO ua WHERE ua.user_id = ?" +
	            ")",
	            new Object[] { user.getId() });
	}

}
~~~~~ 

* **provideViaCustomProvider:** With this configuration feature, you can use your custom field value provider implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperCustomProvider.RowMapperFieldProvider`** interface for providing value of field. Instance of your implementation is created once and used as singleton. 
 
For example:
~~~~~ java
@RowMapperObjectField(
	provideViaCustomProvider = 
		@RowMapperCustomProvider(
			fieldProvider = UserPhoneNumberFieldProvider.class))
private String phoneNumber;
~~~~~

and your custom field provider implementation is declared as like:

~~~~~ java
public class UserPhoneNumberFieldProvider implements RowMapperFieldProvider<User, String> {

	private final static Logger logger = Logger.getLogger(UserPhoneNumberFieldProvider.class);
	
	@Override
	public String provideField(User user, String fieldName, ResultSet rs, int rowNum) {
		try {
			return rs.getString("phone_number");
		} 
		catch (SQLException e) {
			logger.error("Error occured while mapping field " + fieldName + " in User object from resultset", e);
			return null;
		}
	}

}
~~~~~ 

* **lazy:** With this configuration feature, you can configure this field as lazy permanently if **`lazyCondition`** is not specified. Note that specified field type must not **`final`** class.

* **lazyCondition:** With this configuration feature, you can configure conditional lazy feature mentioned at section **`4.11. Conditional Lazy Feature`**.

* **lazyLoadCondition:** With this configuration feature, you can configure conditional lazy-load feature mentioned at section **`4.12. Conditional Lazy-Load Feature`**.

* **ignoreCondition:** With this configuration feature, you can configure conditional ignore feature mentioned at section **`4.13. Conditional Ignore Feature`**.

<a name="Section_4_11"></a>
4.11. Conditional Lazy Feature
-------

Conditional lazy feature can be configured by using **`@RowMapperLazyCondition`** annotation in **`@RowMapperObjectField`** annotation with **`lazyCondition`** attribute. With conditional lazy feature, you can control lazy feature of any field as dynamic at runtime. 

* **provideViaPropertyBasedProvider:** With this configuration feature, you can attach a specific property to a lazy condition and enable/disable condition of this property is used to evaluate condition.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	lazyCondition = 
		@RowMapperLazyCondition(
			provideViaPropertyBasedProvider = 
				@RowMapperPropertyBasedLazyConditionProvider(
					propertyName = "creditCardInfoLazyCondition")))
private CreditCardInfo creditCardInfo;
~~~~~

To enable/disable lazy condition property, there are two ways:

1. You can enable/disable lazy condition property by using **`enableLazyConditionProperty`** and **`disableLazyConditionProperty`** methods of **`org.springframework.jdbc.roma.impl.service.RowMapperService`** service class.

For example you can get row mapper service class as:
~~~~~ java
@Autowired
private RowMapperService;
~~~~~

and you can enable lazy condition property as:

~~~~~ java
rowMapperService.enableLazyConditionProperty("creditCardInfoLazyCondition");
~~~~~

or you can disable lazy condition property as:

~~~~~ java
rowMapperService.disableLazyConditionProperty("creditCardInfoLazyCondition");
~~~~~

2. You can enable/disable lazy condition property automatically by using **`@RowMapperPropertyBasedLazyConditionAware`** annotation on any method of any class in Spring context before database access code is executed. To enable/disable this property on start of this method and on end of this method can be configured by **`options`** property.

For example you can setup lazy condition configuration automatically by annotation like:
~~~~~ java
@Override
@RowMapperPropertyBasedLazyConditionAware(
	propertyName = "creditCardInfoLazyCondition",
	options = 	RowMapperPropertyBasedLazyConditionAware.ENABLE_ON_START | 
				RowMapperPropertyBasedLazyConditionAware.DISABLE_ON_FINISH)
public CreditCardInfo getUserCreditCardInfo(Long userId) {
	...
}
~~~~~

* **provideViaExpressionBasedProvider:** With this configuration feature, you can use RXEL (ROMA Expression Language) expression language to evaluate lazy condition.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	lazyCondition = 
		@RowMapperLazyCondition(
			provideViaExpressionBasedProvider = 
				@RowMapperExpressionBasedLazyConditionProvider(
					expression = "${name}.equals(\"Member\")")))
private List<Permission> permissions;
~~~~~

* **provideViaCustomProvider:** With this configuration feature, you can use your custom lazy condition evaluater implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperLazyConditionProvider`** interface for evaluating lazy condition as custom. Instance of your implementation is created once and used as singleton.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	lazyCondition = 
		@RowMapperLazyCondition(
			provideViaCustomProvider = 
				@RowMapperCustomLazyConditionProvider(
					lazyConditionProvider = UserRolesLazyConditionProvider.class)))
private List<Role> roles;
~~~~~

and your custom lazy condition checker implementation is declared as like:

~~~~~ java
public class UserRolesLazyConditionProvider implements RowMapperLazyConditionProvider<User> {

	private final static Logger logger = Logger.getLogger(UserRolesLazyConditionProvider.class);
	
	@Override
	public boolean evaluateCondition(User user, String fieldName, ResultSet rs, int rowNum) {
		boolean conditionResult = user.getId() % 2 == 0;
		logger.debug("Evaluated lazy condition of field " + "\"" + fieldName + "\"" +  
			 " for user with id " + "\"" + user.getId() + "\"" + " as " + "\"" + conditionResult + "\"");
		return conditionResult;
	}

}
~~~~~

<a name="Section_4_12"></a>
4.12. Conditional Lazy-Load Feature
-------

Conditional lazy-load feature can be configured by using **`@RowMapperLazyLoadCondition`** annotation in **`@RowMapperObjectField`** annotation with **`lazyLoadCondition`** attribute. With conditional lazy-load feature, you can control real object loading behaviour of any lazy field as dynamic at runtime. 

* **provideViaPropertyBasedProvider:** With this configuration feature, you can attach a specific property to a lazy-load condition and enable/disable condition of this property is used to evaluate condition.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	lazyLoadCondition = 
		@RowMapperLazyLoadCondition(
			provideViaPropertyBasedProvider = 
				@RowMapperPropertyBasedLazyLoadConditionProvider(
					propertyName = "creditCardInfoLazyLoadCondition")))
private CreditCardInfo creditCardInfo;
~~~~~

To enable/disable lazy-load condition property, there are two ways:

1. You can enable/disable lazy-load condition property by using **`enableLazyLoadConditionProperty`** and **`disableLazyLoadConditionProperty`** methods of **`org.springframework.jdbc.roma.impl.service.RowMapperService`** service class.

For example you can get row mapper service class as:
~~~~~ java
@Autowired
private RowMapperService;
~~~~~

and you can enable lazy-load condition property as:

~~~~~ java
rowMapperService.enableLazyLoadConditionProperty("creditCardInfoLazyLoadCondition");
~~~~~

or you can disable lazy-load condition property as:

~~~~~ java
rowMapperService.disableLazyLoadConditionProperty("creditCardInfoLazyLoadCondition");
~~~~~

2. You can enable/disable lazy-load condition property automatically by using **`@RowMapperPropertyBasedLazyLoadConditionAware`** annotation on any method of any class in Spring context before database access code is executed. To enable/disable this property on start of this method and on end of this method can be configured by **`options`** property.

For example you can setup lazy-load condition configuration automatically by annotation like:
~~~~~ java
@Override
@RowMapperPropertyBasedLazyLoadConditionAware(
	propertyName = "creditCardInfoLazyLoadCondition",
	options = 	RowMapperPropertyBasedLazyLoadConditionAware.ENABLE_ON_START | 
				RowMapperPropertyBasedLazyLoadConditionAware.DISABLE_ON_FINISH)
public CreditCardInfo getUserCreditCardInfo(Long userId) {
	...
}
~~~~~

* **provideViaExpressionBasedProvider:** With this configuration feature, you can use RXEL (ROMA Expression Language) expression language to evaluate lazy-load condition.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	lazyLoadCondition = 
		@RowMapperLazyLoadCondition(
			provideViaExpressionBasedProvider = 
				@RowMapperExpressionBasedLazyLoadConditionProvider(
					expression = "${name}.equals(\"Member\")")))
private List<Permission> permissions;
~~~~~

* **provideViaCustomProvider:** With this configuration feature, you can use your custom lazy-load condition evaluater implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyLoadCondition.RowMapperLazyLoadConditionProvider`** interface for evaluating lazy-load condition as custom. Instance of your implementation is created once and used as singleton.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	lazyLoadCondition = 
		@RowMapperLazyLoadCondition(
			provideViaCustomProvider = 
				@RowMapperCustomLazyLoadConditionProvider(
					lazyLoadConditionProvider = UserRolesLazyLoadConditionProvider.class)))
private List<Role> roles;
~~~~~

and your custom lazy-load condition checker implementation is declared as like:

~~~~~ java
public class UserRolesLazyLoadConditionProvider implements RowMapperLazyLoadConditionProvider<User> {

	private final static Logger logger = Logger.getLogger(UserRolesLazyLoadConditionProvider.class);
	
	@Override
	public boolean evaluateCondition(User user, String fieldName) {
		boolean conditionResult = user.getId() % 2 == 0;
		logger.debug("Evaluated lazy-load condition of field " + "\"" + fieldName + "\"" +  
			 " for user with id " + "\"" + user.getId() + "\"" + " as " + "\"" + conditionResult + "\"");
		return conditionResult;
	}

}
~~~~~

<a name="Section_4_13"></a>
4.13. Conditional Ignore Feature
-------

Conditional ignore feature can be configured by using **`@RowMapperIgnoreCondition`** annotation in **`@RowMapperObjectField`** annotation with **`ignoreCondition`** attribute. With conditional ignore feature, you can control turning on/off of mapping behaviour of any field as dynamic at runtime. 

* **provideViaPropertyBasedProvider:** With this configuration feature, you can attach a specific property to a ignore condition and enable/disable condition of this property is used to evaluate condition.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	ignoreCondition = 
		@RowMapperIgnoreCondition(
			provideViaPropertyBasedProvider = 
				@RowMapperPropertyBasedIgnoreConditionProvider(
					propertyName = "creditCardInfoIgnoreCondition")))
private CreditCardInfo creditCardInfo;
~~~~~

To enable/disable ignore condition property, there are two ways:

1. You can enable/disable ignore condition property by using **`enableIgnoreConditionProperty`** and **`disableIgnoreConditionProperty`** methods of **`org.springframework.jdbc.roma.impl.service.RowMapperService`** service class.

For example you can get row mapper service class as:
~~~~~ java
@Autowired
private RowMapperService;
~~~~~

and you can enable ignore condition property as:

~~~~~ java
rowMapperService.enableIgnoreConditionProperty("creditCardInfoIgnoreCondition");
~~~~~

or you can disable ignore condition property as:

~~~~~ java
rowMapperService.disableIgnoreConditionProperty("creditCardInfoIgnoreCondition");
~~~~~

2. You can enable/disable ignore condition property automatically by using **`@RowMapperPropertyBasedIgnoreConditionAware`** annotation on any method of any class in Spring context before database access code is executed. To enable/disable this property on start of this method and on end of this method can be configured by **`options`** property.

For example you can setup ignore condition configuration automatically by annotation like:
~~~~~ java
@Override
@RowMapperPropertyBasedIgnoreConditionAware(
	propertyName = "creditCardInfoIgnoreCondition",
	options = 	RowMapperPropertyBasedIgnoreConditionAware.ENABLE_ON_START | 
				RowMapperPropertyBasedIgnoreConditionAware.DISABLE_ON_FINISH)
public CreditCardInfo getUserCreditCardInfo(Long userId) {
	...
}
~~~~~

* **provideViaExpressionBasedProvider:** With this configuration feature, you can use RXEL (ROMA Expression Language) expression language to evaluate ignore condition.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	ignoreCondition = 
		@RowMapperIgnoreCondition(
			provideViaExpressionBasedProvider = 
				@RowMapperExpressionBasedIgnoreConditionProvider(
					expression = "${name}.equals(\"Member\")")))
private List<Permission> permissions;
~~~~~

* **provideViaCustomProvider:** With this configuration feature, you can use your custom ignore condition evaluater implementations by implementing **`org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition.RowMapperIgnoreConditionProvider`** interface for evaluating ignore condition as custom. Instance of your implementation is created once and used as singleton.

For example:
~~~~~ java
@RowMapperObjectField(
	...
	ignoreCondition = 
		@RowMapperIgnoreCondition(
			provideViaCustomProvider = 
				@RowMapperCustomIgnoreConditionProvider(
					ignoreConditionProvider = UserRolesIgnoreConditionProvider.class)))
private List<Role> roles;
~~~~~

and your custom ignore condition checker implementation is declared as like:

~~~~~ java
public class UserRolesIgnoreConditionProvider implements RowMapperIgnoreConditionProvider<User> {

	private final static Logger logger = Logger.getLogger(UserRolesIgnoreConditionProvider.class);
	
	@Override
	public boolean evaluateCondition(User user, String fieldName) {
		boolean conditionResult = user.getId() % 2 == 0;
		logger.debug("Evaluated ignore condition of field " + "\"" + fieldName + "\"" +  
			 " for user with id " + "\"" + user.getId() + "\"" + " as " + "\"" + conditionResult + "\"");
		return conditionResult;
	}

}
~~~~~

<a name="Section_4_14"></a>
4.14. Other Features
-------

* **Ignoring field:** To hide field from row mapper, you can declare this field as **`transient`** or annotate it with **`@RowMapperIgnoreField`** annotation.

<a name="Section_5"></a>
5. A Simple Example
=======

Here is `User` class:  

~~~~~ java
@RowMapperClass(objectProcessor = UserObjectProcessor.class)
public class User {

	private Long id;
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	@RowMapperObjectField(
		provideViaCustomProvider = 
			@RowMapperCustomProvider(
					fieldProvider = UserPhoneNumberFieldProvider.class))
	private String phoneNumber;
	@RowMapperObjectField(
		provideViaExpressionProvider = 
			@RowMapperExpressionProvider(
					expression = "new Address(&{[string]city}, &{[string]country})",
					usedClasses = {Address.class}))
	private Address address;
	private boolean enabled = true;
	private Gender gender;
	private Date birthDate;
	@RowMapperEnumField(enumStartValue = 1)
	private Language language;
	@RowMapperEnumField(
			mapViaAutoMapper = 
				@RowMapperEnumAutoMapper(
						mapViaNumericValueNumericMappings = {
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 0, value = 0),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 1, value = 100),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 2, value = 200),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 3, value = 300),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 4, value = 400),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 5, value = 500),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 6, value = 600),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 7, value = 700)
						}))
	private Occupation occupation;
	@RowMapperEnumField(
			mapViaAutoMapper = 
				@RowMapperEnumAutoMapper(
						mapViaStringValueNumericMappings = {
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 0, value = "PRIMARY_SCHOOL"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 1, value = "SECONDARY_SCHOOL"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 2, value = "HIGH_SCHOOL"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 3, value = "BACHELOR"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 4, value = "MASTER"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 5, value = "PHD"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 6, value = "ASSOCIATE_PROFESSOR"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 7, value = "PROFESSOR"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 8, value = "OTHER")
						}))
	private Education education;
	@RowMapperEnumField(
			mapViaNumericMapper = 
				@RowMapperEnumNumericMapper(
							mapper = BloodTypeEnumMapper.class))
	private BloodType bloodType;
	@RowMapperEnumField(
			mapViaStringMapper = 
				@RowMapperEnumStringMapper(
							mapper = MaritalStatusEnumMapper.class))
	private MaritalStatus maritalStatus;
	@RowMapperEnumField(useStringValue = true)
	private Religion religion;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
						expression = "@{roleDAO}.getUserRoleList(${id})"),		
			lazy = true,
			lazyCondition = 
			@RowMapperLazyCondition(
					provideViaCustomProvider = 
						@RowMapperCustomLazyConditionProvider(
								lazyConditionProvider = UserRolesLazyConditionProvider.class)))
	private List<Role> roles;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
					expression = "@{creditCardInfoDAO}.getUserCreditCardInfo(${id})"),		
			lazy = true,
			lazyCondition = 
				@RowMapperLazyCondition(
					provideViaPropertyBasedProvider = 
						@RowMapperPropertyBasedLazyConditionProvider(
								propertyName = "creditCardInfoLazyCondition")))
	private CreditCardInfo creditCardInfo;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
					expression = "@{creditCardInfoDAO}.getUserSecondaryCreditCardInfo(${id})"),		
			lazy = true,
			lazyCondition = 
			@RowMapperLazyCondition(
					provideViaPropertyBasedProvider = 
						@RowMapperPropertyBasedLazyConditionProvider(
								propertyName = "creditCardInfoLazyCondition")))
	private CreditCardInfo secondaryCreditCardInfo;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
					expression = "@{creditCardInfoDAO}.getUserCreditCardInfo(${id})"),		
			ignoreCondition = 
				@RowMapperIgnoreCondition(
					provideViaPropertyBasedProvider = 
						@RowMapperPropertyBasedIgnoreConditionProvider(
								propertyName = "creditCardInfoIgnoreCondition")))
	private CreditCardInfo previousCreditCardInfo;
	
	@RowMapperIgnoreField // Or define field as transient
	private byte age;
~~~~~

Here is `Role` class:    

~~~~~ java
@RowMapperClass(objectCreater = RoleObjectCreater.class)
public class Role {

	private Long id;
	@RowMapperField(fieldMapper = RoleNameFieldMapper.class)
	private String name;
	@RowMapperObjectField(
	        provideViaSqlProvider = 
	            @RowMapperSqlProvider(
	                provideSql = 
	                    "SELECT p.* FROM PERMISSION p WHERE p.ID IN " +
	                    "(" +
	                        "SELECT rp.PERMISSION_ID FROM role_permission rp WHERE rp.ROLE_ID = ${id}" +
	                    ") ORDER BY p.name",
	                entityType = Permission.class),	    
	        lazy = true,
	        lazyCondition = 
			@RowMapperLazyCondition(
				provideViaExpressionBasedProvider = 
					@RowMapperExpressionBasedLazyConditionProvider(
						expression = "${name}.equals(\"Member\")")))
	private List<Permission> permissions;

    ...
    
}
~~~~~

Here is `BloodTypeEnumMapper` class:  

~~~~~ java
public class BloodTypeEnumMapper implements RowMapperEnumField.NumericEnumMapper<BloodType> {

	@Override
	public BloodType map(Integer value) {
		for (BloodType bt : BloodType.values()) {
			if (bt.getCode() == value) {
				return bt;
			}
		}
		return null;
	}

}	
~~~~~

Here is `MarialStatusEnumMapper` class:  

~~~~~ java
public class MaritalStatusEnumMapper implements RowMapperEnumField.StringEnumMapper<MaritalStatus> {

	@Override
	public MaritalStatus map(String value) {
		for (MaritalStatus ms : MaritalStatus.values()) {
			if (ms.name().equalsIgnoreCase(value)) {
				return ms;
			}
		}
		return null;
	}

}	
~~~~~

Here is `UserPhoneNumberFieldProvider` class:  

~~~~~ java
public class UserPhoneNumberFieldProvider implements RowMapperFieldProvider<User, String> {

	private final static Logger logger = Logger.getLogger(UserPhoneNumberFieldProvider.class);
	
	@Override
	public String provideField(User user, String fieldName, ResultSet rs, int rowNum) {
		try {
			return rs.getString("phone_number");
		} 
		catch (SQLException e) {
			logger.error("Error occured while mapping field " + fieldName + " in User object from resultset", e);
			return null;
		}
	}

}
~~~~~

Here is `UserRolesLazyConditionProvider` class:  

~~~~~ java
public class UserRolesLazyConditionProvider implements RowMapperLazyConditionProvider<User> {

	private final static Logger logger = Logger.getLogger(UserRolesLazyConditionProvider.class);
	
	@Override
	public boolean evaluateCondition(User user, String fieldName, ResultSet rs, int rowNum) {
		boolean conditionResult = user.getId() % 2 == 0;
		logger.debug("Evaluated lazy condition of field " + "\"" + fieldName + "\"" +  
			 " for user with id " + "\"" + user.getId() + "\"" + " as " + "\"" + conditionResult + "\"");
		return conditionResult;
	}

}	
~~~~~

Here is `UserObjectProcessor` class:  

~~~~~ java
public class UserObjectProcessor implements RowMapperObjectProcessor<User> {

	@SuppressWarnings("deprecation")
	@Override
	public void processObject(User user, ResultSet rs, int rowNum) {
		if (user.getBirthDate() != null) {
			user.setAge((byte)(Calendar.getInstance().getTime().getYear() - user.getBirthDate().getYear()));
		}
	}

}
~~~~~

Here is `RoleObjectCreater` class:  

~~~~~ java
public class RoleObjectCreater implements RowMapperObjectCreater<Role> {

	@Override
	public Role createObject(Class<Role> clazz) {
		return new Role();
	}

}
~~~~~

Here is `RoleNameFieldMapper` class:  

~~~~~ java
public class RoleNameFieldMapper implements RowMapperFieldMapper<Role> {

	private static final Logger logger = Logger.getLogger(RoleNameFieldMapper.class);
	
	@Override
	public void mapField(Role role, String fieldName, ResultSet rs, int rowNum) {
		try {
			role.setName(rs.getString("name"));
		} 
		catch (SQLException e) {
			logger.error("Error occured while mapping field " + fieldName + 
					" in Role object from resultset", e);
		}
	}

}
~~~~~

Here is `UserAccountInfoSqlQueryInfoProvider` class:  

~~~~~ java
public class UserAccountInfoSqlQueryInfoProvider implements RowMapperSqlQueryInfoProvider<User> {

	@Override
	public SqlQueryInfo provideSqlQueryInfo(User user, String fieldName) {
		return 
			new SqlQueryInfo(
				"SELECT a.* FROM ACCOUNT_INFO a WHERE a.ID IN " +
	            "(" +
	                "SELECT ua.account_info_id FROM USER_ACCOUNT_INFO ua WHERE ua.user_id = ?" +
	            ")",
	            new Object[] { user.getId() });
	}

}
~~~~~ 

Here is `CreditCardInfoJdbcDAO` class:  

~~~~~ java
@Repository(value="creditCardInfoDAO_v2")
public class CreditCardInfoJdbcDAO extends BaseJdbcDAO implements CreditCardInfoDAO {

	...
	
	@Override
	@RowMapperPropertyBasedLazyConditionAware(
		propertyName = "creditCardInfoLazyCondition",
		options = 	RowMapperPropertyBasedLazyConditionAware.ENABLE_ON_START | 
				RowMapperPropertyBasedLazyConditionAware.DISABLE_ON_FINISH)
	public CreditCardInfo getUserSecondaryCreditCardInfo(Long userId) {
		logger.debug("Getting secondary credit card info for user with id " + userId);
		return 
			jdbcTemplate.queryForObject(
				"SELECT c.* FROM CREDIT_CARD_INFO c WHERE c.id = " +
				"(" +
					"SELECT uc.credit_card_info_id FROM USER_SECONDARY_CREDIT_CARD_INFO uc WHERE uc.user_id = " + userId +
				")", 
				creditCardInfoRowMapper);
	}

}
~~~~~

You can get `User` entity rowmapper as follows:

~~~~~ java
@Autowired
RowMapperService rowMapperService;
    
...

RowMapper<User> userRowMapper = rowMapperService.getRowMapper(User.class);
~~~~~

In this example, we can get related `Role` entites of `User` entity with **`@RowMapperObjectField`** annotion automatically. 
We use **`@RowMapperObjectField`** annotation for accessing related `Role` entites of `User` entity with `id` attribute of User. 
We have **`lazy=true`** configuration, since `roles` field are initialized while we are accessing it first time. 
If we don't access it, it will not be set. 

~~~~~

You can find all demo codes (including these samples above) at [https://github.com/serkan-ozal/spring-jdbc-roma-demo](https://github.com/serkan-ozal/spring-jdbc-roma-demo)
 
 
<a name="Section_6"></a> 
6. Roadmap
=======

* In addition to Annotation based configuration **XML** and **Properties** file based configuration support will be added.

* Spring context xml specific XSD will be defined and configuration can be done in Spring context xml by using **<roma>** tags.

* Integration with [https://github.com/nurkiewicz/spring-data-jdbc-repository](https://github.com/nurkiewicz/spring-data-jdbc-repository) which is Spring Data JDBC generic DAO implementation framework by [Tomasz Nurkiewicz](https://github.com/nurkiewicz).

* Generic DAO implementaion will be added for CRUD operations such as **get**, **list**, **add**, **update** and **delete** by considering object relations and different DBMS vendors (Oracle, MySQL, PostgreSQL, ...)
