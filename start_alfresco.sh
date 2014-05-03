export JAVA_HOME=/usr/lib/jvm/jdk1.6.0_45/bin
CATALINA_HOME="/home/smiledev/workspaces/workspace_UdL_3.4.13/Universitat_Lleida/target/alfresco/tomcat"
/home/smiledev/workspaces/workspace_UdL_3.4.13/Universitat_Lleida/target/alfresco/tomcat/bin/catalina.sh jpda start
tail -f /home/smiledev/workspaces/workspace_UdL_3.4.13/Universitat_Lleida/target/alfresco/tomcat/logs/catalina.out
