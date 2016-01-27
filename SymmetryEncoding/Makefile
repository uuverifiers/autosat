all:
	mkdir -p bin/
	mkdir -p dist/
	javac -d bin/ -cp lib/commons-io-2.4.jar:lib/org.sat4j.core.jar:lib/commons-cli-1.2.jar:lib/log4j-core-2.1.jar:lib/log4j-api-2.1.jar:lib/java-cup-11a.jar \
	                 src/main/java/*/*.java \
	                 src/main/java/*/*/*.java \
	                 src/main/java/*/*/*/*.java \
	                 ../Common/src/main/java/*/*.java \
	                 ../Common/src/main/java/*/*/*.java \
	                 ../Common/src/main/java/*/*/*/*.java 
	cd bin/ && jar cvf ../dist/symmetryencoding.jar *
