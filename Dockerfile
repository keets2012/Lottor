FROM 192.168.1.202/common/basejava
RUN mkdir /data
VOLUME /data
ADD ./transaction-reliable-server/target/transaction-reliable-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9666 9998
ENTRYPOINT ["java","-javaagent:/data/pp-agent/pinpoint-bootstrap-1.6.0.jar","-Dpinpoint.agentId=lotor","-Dpinpoint.applicationName=lotor", "-jar", "/app.jar"]
