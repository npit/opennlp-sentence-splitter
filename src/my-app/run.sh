trfile="/home/nik/work/iit/storybot/french_sent_splitter/french.periods.txt.manualeval.txt"
java -cp target/my-app-1.0-SNAPSHOT.jar:/home/nik/.m2/repository/org/apache/opennlp/opennlp-tools/1.5.3/opennlp-tools-1.5.3.jar:/home/nik/.m2/repository/org/apache/opennlp/opennlp-maxent/3.0.3/opennlp-maxent-3.0.3.jar  com.mycompany.app.splitter "${trfile}"
