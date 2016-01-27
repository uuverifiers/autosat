all:
	mkdir -p bin/
	mkdir -p dist/
	javac -d bin/ -cp ../Common/lib/commons-io-2.4.jar:../Common/lib/org.sat4j.core.jar:../Common/lib/commons-cli-1.2.jar:../Common/lib/log4j-core-2.1.jar:../Common/lib/log4j-api-2.1.jar:../Common/lib/java-cup-11a.jar \
	                 src/main/java/*/*.java \
	                 src/main/java/*/*/*.java \
	                 src/main/java/*/*/*/*.java \
	                 ../Common/src/main/java/*/*.java \
	                 ../Common/src/main/java/*/*/*.java \
	                 ../Common/src/main/java/*/*/*/*.java 
	cd bin/ && jar cvf ../dist/symmetryencoding.jar *
