mkdir build
java -cp ./lib/ JLex.Main ./src/Main.lex
java -jar ./lib/cup/java-cup-11b.jar -expect 1 ./src/Main.cup
mv sym.java ./src/sym.java
mv parser.java ./src/parser.java
javac -cp "./src:./lib/cup/java-cup-11b-runtime.jar" -d build ./src/Main.lex.java ./src/parser.java ./src/sym.java ./src/Main.java
