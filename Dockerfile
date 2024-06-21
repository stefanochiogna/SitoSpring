FROM tomcat:10.1.24-jdk17

WORKDIR /home/sito_forze_armate

COPY raccolta_file/ /home/SitoSpring/raccolta_file/

COPY SitoForzeArmate/target/SitoForzeArmate-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD [ "catalina.sh", "run" ]