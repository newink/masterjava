export LB_HOME=/home/newink/study/java/liquibase
$LB_HOME/liquibase --driver=org.postgresql.Driver \
--classpath=$LB_HOME/lib \
--changeLogFile=databaseChangeLog.sql \
--url="jdbc:postgresql://localhost:5432/masterjava" \
--username=postgres \
--password=root \
migrate