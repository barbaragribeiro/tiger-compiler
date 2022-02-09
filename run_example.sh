echo "Exemplo 1 - Entrada correta"
cat ./entradas/teste1_correto.tig
echo
echo "---------------"
echo "Exemplo 1 - Saída do compilador"
java -cp "build:lib/cup/java-cup-11b.jar" compiler.Main < ./entradas/teste1_correto.tig

echo
echo
echo "------------------------------------------"
echo "Exemplo 2 - Entrada com Erro"
cat ./entradas/teste1_errado.tig
echo
echo "---------------"
echo "Exemplo 2 - Saída do compilador"
java -cp "build:lib/cup/java-cup-11b.jar" compiler.Main < ./entradas/teste1_errado.tig

echo
echo
echo "------------------------------------------"
echo "Exemplo 3 - Entrada correta"
cat ./entradas/teste2_correto.tig
echo
echo "---------------"
echo "Exemplo 3 - Saída do compilador"
java -cp "build:lib/cup/java-cup-11b.jar" compiler.Main < ./entradas/teste2_correto.tig

echo
echo
echo "------------------------------------------"
echo "Exemplo 4 - Entrada com Erro"
cat ./entradas/teste2_errado.tig
echo
echo "---------------"
echo "Exemplo 4 - Saída do compilador"
java -cp "build:lib/cup/java-cup-11b.jar" compiler.Main < ./entradas/teste2_errado.tig

echo
echo
echo "------------------------------------------"
echo "Exemplo 5 - Entrada correta"
cat ./entradas/teste3_correto.tig
echo
echo "---------------"
echo "Exemplo 5 - Saída do compilador"
java -cp "build:lib/cup/java-cup-11b.jar" compiler.Main < ./entradas/teste3_correto.tig

echo
echo
echo "------------------------------------------"
echo "Exemplo 6 - Entrada com Erro"
cat ./entradas/teste3_errado.tig
echo
echo "---------------"
echo "Exemplo 6 - Saída do compilador"
java -cp "build:lib/cup/java-cup-11b.jar" compiler.Main < ./entradas/teste3_errado.tig