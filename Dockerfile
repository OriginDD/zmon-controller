FROM registry.opensource.zalan.do/stups/test:8u91-b14-1-22

EXPOSE 8443

COPY zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar /zmon-controller.jar
COPY target/scm-source.json /

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) -jar /zmon-controller.jar
