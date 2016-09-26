##Inventory System

![alt tag](https://raw.githubusercontent.com/trett/cis/master/cis.png)

####Spring profiles
-`inmem` - in memory authentication  
-`ldap` - LDAP server authentication  
-`test` - test properties for H2 database from test.properties.file  
-`prod` - production properties for database from database.properties  
####examples
#####run dev+mysql
`mvn jetty:run -Ddatabase=mysql -Djetty=true -Dwar=false -Dspring.profiles.active="prod,inmem"`
#####build war
`mvn package -Ddatabase=mysql -Dwar=true`

**Must be deployed in ROOT context**  
- Spring profiles for Tomcat          
    create `setenv.sh` in Tomcat's `bin` directory and put `JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod,ldap"`  
- Database settings placing in `cis/WEB-INF/resources/database.properties`  
- Indices folder must be created and given rights to write.  