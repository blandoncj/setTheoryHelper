package setTheoryHelper;

import java.util.*;
import java.util.stream.Collectors;

public class SetManager {
    private static final Scanner sc = new Scanner(System.in);
    private static Set<Object> universalSet = new HashSet<>();
    private static List<Set<Object>> subsets = new ArrayList<>();
    
    // Colores ANSI mejorados
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String ORANGE = "\u001B[38;5;208m";

    public static void main(String[] args) {
        showMenu();
    }

    public static void showMenu() {
        printTitle();
        initializeUniversalSet();

        while (true) {
            printMenuOptions();
            int option = getIntInput("Elige una opción: ", 1, 7);
            
            switch (option) {
                case 1: showUniversalSet(); break;
                case 2: verifyMembership(); break;
                case 3: classifySubsets(); break;
                case 4: showPowerSet(); break;
                case 5: performSetOperations(); break;
                case 6: demonstrateSetLaws(); break;
                case 7: exitProgram(); return;
            }
        }
    }

    private static void initializeUniversalSet() {
        System.out.print(BOLD + BLUE + "Ingresa el tamaño del conjunto universal: " + RESET);
        int size = getIntInput("", 1, Integer.MAX_VALUE);
        universalSet = createUniversalSet(size);
    }

    private static Set<Object> createUniversalSet(int size) {
        Set<Object> universal = new HashSet<>();
        System.out.println("\n" + YELLOW + "Ingresa elementos (ejemplo: 'a' o '{a, b}'):" + RESET);

        for (int i = 0; i < size; i++) {
            System.out.print("Elemento " + (i + 1) + ": ");
            String input = sc.nextLine().trim();

            if (input.startsWith("{") && input.endsWith("}")) {
                handleSubsetInput(input, universal);
            } else {
                handleElementInput(input, universal);
            }
        }
        return universal;
    }

    private static void handleSubsetInput(String input, Set<Object> universal) {
        Set<Object> subset = Arrays.stream(input.substring(1, input.length() - 1).split(","))
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .collect(Collectors.toSet());
        
        if (subset.isEmpty()) {
            System.out.println(RED + "Subconjunto vacío no permitido. Intenta de nuevo." + RESET);
        } else {
            universal.add(subset);
            subsets.add(subset);
        }
    }

    private static void handleElementInput(String input, Set<Object> universal) {
        if (input.isEmpty()) {
            System.out.println(RED + "Elemento vacío no permitido. Intenta de nuevo." + RESET);
        } else {
            universal.add(input);
        }
    }

    private static void showUniversalSet() {
        printSection("Conjunto Universal", GREEN);
        printTable(universalSet, "U");
    }

    private static void verifyMembership() {
        printSection("Verificación de Pertenencia", YELLOW);
        System.out.println("Elementos y subconjuntos en el conjunto universal:");
        universalSet.forEach(element -> 
            System.out.println(CYAN + "• " + element + " ∈ U" + RESET));
    }

    private static void classifySubsets() {
        printSection("Clasificación de Subconjuntos", MAGENTA);
        
        if (subsets.isEmpty()) {
            System.out.println(YELLOW + "No hay subconjuntos definidos para clasificar." + RESET);
            return;
        }
    
        subsets.forEach(subset -> {
            if (universalSet.containsAll(subset)) {
                String type = subset.size() < universalSet.size() ? 
                    "⊂ U (Subconjunto propio)" : "⊆ U (Subconjunto impropio)";
                System.out.println(GREEN + "• " + subset + " " + type + RESET);
            } else {
                System.out.println(RED + "• " + subset + " ⊈ U (No es subconjunto)" + RESET);
            }
        });
    }
    // private static Set<Object> getSimpleElements() {
    //     return universalSet.stream()
    //             .filter(e -> !(e instanceof Set))
    //             .collect(Collectors.toSet());
    // }

    private static void classifySubset(Set<Object> subset, Set<Object> simpleElements) {
        if (simpleElements.containsAll(subset)) {
            String type = subset.size() < simpleElements.size() ? 
                "⊂ U (Subconjunto propio)" : "⊆ U (Subconjunto impropio)";
            System.out.println(GREEN + "• " + subset + " " + type + RESET);
        } else {
            System.out.println(RED + "• " + subset + " ⊈ U (No es subconjunto)" + RESET);
        }
    }

    private static void showPowerSet() {
        printSection("Conjunto Potencia", RED);
        System.out.println("Elementos del conjunto universal (cada subconjunto es un elemento atómico):");
        universalSet.forEach(e -> System.out.println("• " + e));
        
        Set<Set<Object>> powerSet = getPowerSet(universalSet);
        System.out.println("\n" + BOLD + "Conjunto Potencia P(U):" + RESET);
        powerSet.forEach(subset -> System.out.println("• " + subset));
    }

    private static Set<Set<Object>> getPowerSet(Set<Object> originalSet) {
        Set<Set<Object>> powerSet = new HashSet<>();
        powerSet.add(new HashSet<>());
    
        for (Object element : originalSet) {
            Set<Set<Object>> newSubsets = new HashSet<>();
            for (Set<Object> subset : powerSet) {
                Set<Object> newSubset = new HashSet<>(subset);
                newSubset.add(element);
                newSubsets.add(newSubset);
            }
            powerSet.addAll(newSubsets);
        }
        return powerSet;
    }

    private static void performSetOperations() {
        printSection("Operaciones con Conjuntos", BLUE);
        
        if (subsets.isEmpty()) {
            System.out.println(RED + "No hay subconjuntos definidos para operar." + RESET);
            return;
        }

        List<Set<Object>> selectedSets = selectSetsForOperation();
        if (selectedSets.isEmpty()) return;

        int operation = selectOperation();
        executeOperation(selectedSets, operation);
    }

    private static List<Set<Object>> selectSetsForOperation() {
        System.out.println("\nSubconjuntos disponibles:");
        for (int i = 0; i < subsets.size(); i++) {
            System.out.println((i + 1) + ". " + formatSet(subsets.get(i)));
        }

        System.out.print("\nSelecciona los subconjuntos (índices separados por espacios): ");
        String[] indices = sc.nextLine().split(" ");
        List<Set<Object>> selectedSets = new ArrayList<>();
        
        for (String indexStr : indices) {
            try {
                int index = Integer.parseInt(indexStr) - 1;
                if (index >= 0 && index < subsets.size()) {
                    selectedSets.add(subsets.get(index));
                } else {
                    System.out.println(RED + "Índice fuera de rango: " + (index + 1) + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Índice no válido: " + indexStr + RESET);
            }
        }

        if (selectedSets.isEmpty()) {
            System.out.println(RED + "No se seleccionaron subconjuntos válidos." + RESET);
        }
        return selectedSets;
    }

    private static int selectOperation() {
        System.out.println("\nOperaciones disponibles:");
        System.out.println("1. Unión (A ∪ B ∪ C...)");
        System.out.println("2. Intersección (A ∩ B ∩ C...)");
        System.out.println("3. Diferencia (A - B - C...)");
        System.out.println("4. Diferencia Simétrica (A Δ B Δ C...)");
        System.out.println("5. Complemento (A')");
        System.out.println("6. Producto Cartesiano (A × B × C...)");
        return getIntInput("Elige una operación: ", 1, 6);
    }

    private static void executeOperation(List<Set<Object>> sets, int operation) {
        switch (operation) {
            case 1: performUnion(sets); break;
            case 2: performIntersection(sets); break;
            case 3: performDifference(sets); break;
            case 4: performSymmetricDifference(sets); break;
            case 5: performComplement(sets); break;
            case 6: performCartesianProduct(sets); break;
        }
    }

    private static void performUnion(List<Set<Object>> sets) {
        Set<Object> result = new HashSet<>();
        sets.forEach(result::addAll);
        printOperationResult("Unión", "∪", sets, result);
    }

    private static void performIntersection(List<Set<Object>> sets) {
        if (sets.size() < 2) {
            System.out.println(RED + "Se necesitan al menos 2 conjuntos." + RESET);
            return;
        }
        
        Set<Object> result = new HashSet<>(sets.get(0));
        for (int i = 1; i < sets.size(); i++) {
            result.retainAll(sets.get(i));
        }
        printOperationResult("Intersección", "∩", sets, result);
    }

    private static void performDifference(List<Set<Object>> sets) {
        if (sets.size() < 2) {
            System.out.println(RED + "Se necesitan al menos 2 conjuntos." + RESET);
            return;
        }
        
        Set<Object> result = new HashSet<>(sets.get(0));
        for (int i = 1; i < sets.size(); i++) {
            result.removeAll(sets.get(i));
        }
        printOperationResult("Diferencia", "-", sets, result);
    }

    private static void performSymmetricDifference(List<Set<Object>> sets) {
        if (sets.size() < 2) {
            System.out.println(RED + "Se necesitan al menos 2 conjuntos." + RESET);
            return;
        }
        
        Set<Object> result = symmetricDifference(sets.get(0), sets.get(1));
        for (int i = 2; i < sets.size(); i++) {
            result = symmetricDifference(result, sets.get(i));
        }
        printOperationResult("Diferencia Simétrica", "Δ", sets, result);
    }

    private static void performComplement(List<Set<Object>> sets) {
        Set<Object> result = complement(sets.get(0), universalSet);
        System.out.println("\n" + BOLD + "🔹 Complemento" + RESET);
        System.out.println("Conjunto Universal (U): " + formatSet(universalSet));
        System.out.println("Conjunto A: " + formatSet(sets.get(0)));
        System.out.println("Operación: A' = U - A");
        System.out.println("Resultado (A'):");
        printTable(result, "");
    }
    
    // Se puede eliminar el método printOperationResult para el complemento o dejarlo para otras operaciones

    private static void performCartesianProduct(List<Set<Object>> sets) {
        if (sets.size() < 2) {
            System.out.println(RED + "Se necesitan al menos 2 conjuntos." + RESET);
            return;
        }
        
        Set<String> result = cartesianProduct(sets);
        printOperationResult("Producto Cartesiano", "×", sets, result);
    }

    private static void demonstrateSetLaws() {
        printSection("Leyes de Conjuntos", CYAN);
        
        if (subsets.size() < 2) {
            System.out.println(YELLOW + "Se necesitan al menos 2 subconjuntos." + RESET);
            return;
        }

        List<Set<Object>> selectedSets = selectSetsForLaws();
        if (selectedSets.size() < 2) return;

        Map<String, Set<Object>> namedSets = nameSelectedSets(selectedSets);
        demonstrateAllLaws(namedSets);
    }

    private static List<Set<Object>> selectSetsForLaws() {
        System.out.println("\nSubconjuntos disponibles:");
        for (int i = 0; i < subsets.size(); i++) {
            System.out.println((i + 1) + ". " + formatSet(subsets.get(i)));
        }

        System.out.print("\nSelecciona los subconjuntos (índices separados por espacios, mínimo 2): ");
        String[] indices = sc.nextLine().split(" ");
        List<Set<Object>> selectedSets = new ArrayList<>();
        
        for (String indexStr : indices) {
            try {
                int index = Integer.parseInt(indexStr) - 1;
                if (index >= 0 && index < subsets.size()) {
                    selectedSets.add(subsets.get(index));
                } else {
                    System.out.println(RED + "Índice fuera de rango: " + (index + 1) + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Índice no válido: " + indexStr + RESET);
            }
        }

        if (selectedSets.size() < 2) {
            System.out.println(RED + "Se necesitan al menos 2 subconjuntos." + RESET);
            return Collections.emptyList();
        }
        return selectedSets;
    }

    private static Map<String, Set<Object>> nameSelectedSets(List<Set<Object>> sets) {
        Map<String, Set<Object>> namedSets = new LinkedHashMap<>();
        char currentName = 'A';
        for (Set<Object> set : sets) {
            namedSets.put(String.valueOf(currentName++), set);
        }
        return namedSets;
    }

    private static void demonstrateAllLaws(Map<String, Set<Object>> namedSets) {
        System.out.println("\n" + BOLD + "📚 LEYES DE CONJUNTOS" + RESET);
        System.out.println("Conjuntos seleccionados:");
        namedSets.forEach((name, set) -> 
            System.out.println(name + " = " + formatSet(set)));

        demonstrateCommutativeLaw(namedSets);
        demonstrateAssociativeLaw(namedSets);
        demonstrateDistributiveLaw(namedSets);
        demonstrateDeMorganLaws(namedSets);
        demonstrateComplementLaws(namedSets);
        demonstrateIdempotentLaws(namedSets);
    }

    private static void demonstrateCommutativeLaw(Map<String, Set<Object>> namedSets) {
        printSection("\n1. Ley Conmutativa", BLUE);
        
        if (namedSets.size() >= 2) {
            Set<Object> A = namedSets.get("A");
            Set<Object> B = namedSets.get("B");
            
            // Unión
            Set<Object> unionAB = union(A, B);
            Set<Object> unionBA = union(B, A);
            System.out.println("A ∪ B = " + formatSet(unionAB));
            System.out.println("B ∪ A = " + formatSet(unionBA));
            System.out.println("¿Son iguales? " + (unionAB.equals(unionBA) ? "✅ Sí" : "❌ No"));
            
            // Intersección
            Set<Object> interAB = intersection(A, B);
            Set<Object> interBA = intersection(B, A);
            System.out.println("\nA ∩ B = " + formatSet(interAB));
            System.out.println("B ∩ A = " + formatSet(interBA));
            System.out.println("¿Son iguales? " + (interAB.equals(interBA) ? "✅ Sí" : "❌ No"));
        } else {
            System.out.println(YELLOW + "Se necesitan 2 conjuntos para esta ley." + RESET);
        }
    }

    private static void demonstrateAssociativeLaw(Map<String, Set<Object>> namedSets) {
        printSection("\n2. Ley Asociativa", GREEN);
        
        if (namedSets.size() >= 3) {
            Set<Object> A = namedSets.get("A");
            Set<Object> B = namedSets.get("B");
            Set<Object> C = namedSets.get("C");
            
            // Unión
            Set<Object> unionAB_C = union(union(A, B), C);
            Set<Object> unionA_BC = union(A, union(B, C));
            System.out.println("(A ∪ B) ∪ C = " + formatSet(unionAB_C));
            System.out.println("A ∪ (B ∪ C) = " + formatSet(unionA_BC));
            System.out.println("¿Son iguales? " + (unionAB_C.equals(unionA_BC) ? "✅ Sí" : "❌ No"));
            
            // Intersección
            Set<Object> interAB_C = intersection(intersection(A, B), C);
            Set<Object> interA_BC = intersection(A, intersection(B, C));
            System.out.println("\n(A ∩ B) ∩ C = " + formatSet(interAB_C));
            System.out.println("A ∩ (B ∩ C) = " + formatSet(interA_BC));
            System.out.println("¿Son iguales? " + (interAB_C.equals(interA_BC) ? "✅ Sí" : "❌ No"));
        } else {
            System.out.println(YELLOW + "Se necesitan 3 conjuntos para esta ley." + RESET);
        }
    }

    private static void demonstrateDistributiveLaw(Map<String, Set<Object>> namedSets) {
        printSection("\n3. Ley Distributiva", YELLOW);
        
        if (namedSets.size() >= 3) {
            Set<Object> A = namedSets.get("A");
            Set<Object> B = namedSets.get("B");
            Set<Object> C = namedSets.get("C");
            
            // Distributiva de unión sobre intersección
            Set<Object> leftSide1 = union(A, intersection(B, C));
            Set<Object> rightSide1 = intersection(union(A, B), union(A, C));
            System.out.println("A ∪ (B ∩ C) = " + formatSet(leftSide1));
            System.out.println("(A ∪ B) ∩ (A ∪ C) = " + formatSet(rightSide1));
            System.out.println("¿Son iguales? " + (leftSide1.equals(rightSide1) ? "✅ Sí" : "❌ No"));
            
            // Distributiva de intersección sobre unión
            Set<Object> leftSide2 = intersection(A, union(B, C));
            Set<Object> rightSide2 = union(intersection(A, B), intersection(A, C));
            System.out.println("\nA ∩ (B ∪ C) = " + formatSet(leftSide2));
            System.out.println("(A ∩ B) ∪ (A ∩ C) = " + formatSet(rightSide2));
            System.out.println("¿Son iguales? " + (leftSide2.equals(rightSide2) ? "✅ Sí" : "❌ No"));
        } else {
            System.out.println(YELLOW + "Se necesitan 3 conjuntos para esta ley." + RESET);
        }
    }

    private static void demonstrateDeMorganLaws(Map<String, Set<Object>> namedSets) {
        printSection("\n4. Leyes de De Morgan", MAGENTA);
        
        if (namedSets.size() >= 2) {
            Set<Object> A = namedSets.get("A");
            Set<Object> B = namedSets.get("B");
            
            // Primera ley
            Set<Object> complementUnion = complement(union(A, B), universalSet);
            Set<Object> intersectionComplements = intersection(complement(A, universalSet), complement(B, universalSet));
            System.out.println("(A ∪ B)' = " + formatSet(complementUnion));
            System.out.println("A' ∩ B' = " + formatSet(intersectionComplements));
            System.out.println("¿Son iguales? " + (complementUnion.equals(intersectionComplements) ? "✅ Sí" : "❌ No"));
            
            // Segunda ley
            Set<Object> complementIntersection = complement(intersection(A, B), universalSet);
            Set<Object> unionComplements = union(complement(A, universalSet), complement(B, universalSet));
            System.out.println("\n(A ∩ B)' = " + formatSet(complementIntersection));
            System.out.println("A' ∪ B' = " + formatSet(unionComplements));
            System.out.println("¿Son iguales? " + (complementIntersection.equals(unionComplements) ? "✅ Sí" : "❌ No"));
        } else {
            System.out.println(YELLOW + "Se necesitan 2 conjuntos para esta ley." + RESET);
        }
    }

    private static void demonstrateComplementLaws(Map<String, Set<Object>> namedSets) {
        printSection("\n5. Leyes de Complemento", CYAN);
        
        if (!namedSets.isEmpty()) {
            Set<Object> A = namedSets.get("A");
            Set<Object> complementA = complement(A, universalSet);
            
            // A ∪ A' = U
            Set<Object> unionWithComplement = union(A, complementA);
            System.out.println("A ∪ A' = " + formatSet(unionWithComplement));
            System.out.println("U = " + formatSet(universalSet));
            System.out.println("¿Son iguales? " + (unionWithComplement.equals(universalSet) ? "✅ Sí" : "❌ No"));
            
            // A ∩ A' = ∅
            Set<Object> intersectionWithComplement = intersection(A, complementA);
            System.out.println("\nA ∩ A' = " + formatSet(intersectionWithComplement));
            System.out.println("¿Está vacío? " + (intersectionWithComplement.isEmpty() ? "✅ Sí" : "❌ No"));
        }
    }

    private static void demonstrateIdempotentLaws(Map<String, Set<Object>> namedSets) {
        printSection("\n6. Leyes de Idempotencia", RED);
        
        if (!namedSets.isEmpty()) {
            Set<Object> A = namedSets.get("A");
            
            // A ∪ A = A
            Set<Object> unionAA = union(A, A);
            System.out.println("A ∪ A = " + formatSet(unionAA));
            System.out.println("A = " + formatSet(A));
            System.out.println("¿Son iguales? " + (unionAA.equals(A) ? "✅ Sí" : "❌ No"));
            
            // A ∩ A = A
            Set<Object> intersectionAA = intersection(A, A);
            System.out.println("\nA ∩ A = " + formatSet(intersectionAA));
            System.out.println("A = " + formatSet(A));
            System.out.println("¿Son iguales? " + (intersectionAA.equals(A) ? "✅ Sí" : "❌ No"));
        }
    }

    private static void printOperationResult(String operationName, String symbol, List<Set<Object>> sets, Set<?> result) {
        System.out.println("\n" + BOLD + "🔹 " + operationName + RESET);
        
        // Construir la expresión
        StringBuilder expression = new StringBuilder();
        char setName = 'A';
        for (int i = 0; i < sets.size(); i++) {
            if (i > 0) expression.append(" ").append(symbol).append(" ");
            expression.append((char)(setName + i));
        }
        
        System.out.println("Operación: " + expression);
        System.out.println("Resultado:");
        printTable(result, "");
    }

    private static String formatSet(Set<?> set) {
        if (set.isEmpty()) return "∅";
        return set.toString();
    }

    private static void printTable(Set<?> set, String title) {
        String border = "┌───────────────────────────┐";
        String empty = "│        CONJUNTO VACÍO      │";
        
        System.out.println(CYAN + border + RESET);
        if (set.isEmpty()) {
            System.out.println(CYAN + empty + RESET);
        } else {
            for (Object element : set) {
                System.out.printf(CYAN + "│ %-25s │\n" + RESET, element);
            }
        }
        if (!title.isEmpty()) {
            System.out.printf(CYAN + "│ %-25s │\n" + RESET, "(" + title + ")");
        }
        System.out.println(CYAN + "└───────────────────────────┘" + RESET);
    }

    private static void printSection(String title, String color) {
        System.out.println("\n" + color + BOLD + "🔹 " + title + " 🔹" + RESET);
        System.out.println("─────────────────────────────────────────");
    }

    private static void printTitle() {
        System.out.println(BOLD + "    📌 TEORIA DE CONJUNTOS 📌" + RESET);
        System.out.println("═════════════════════════════════════════");
    }

    private static void printMenuOptions() {
        System.out.println("\n" + BOLD + "📋 MENÚ DE OPCIONES" + RESET);
        System.out.println("─────────────────────────────────────────");
        System.out.println(BLUE + "1. Mostrar Conjunto Universal");
        System.out.println(YELLOW + "2. Verificar Pertenencia");
        System.out.println(MAGENTA + "3. Clasificar Subconjuntos");
        System.out.println(RED + "4. Mostrar Conjunto Potencia");
        System.out.println(GREEN + "5. Operaciones con Conjuntos");
        System.out.println(CYAN + "6. Leyes de Conjuntos");
        System.out.println(PURPLE + "7. Salir");
    }

    private static int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(BOLD + prompt + RESET);
            try {
                int input = sc.nextInt();
                sc.nextLine();
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println(RED + "Ingresa un número entre " + min + " y " + max + RESET);
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println(RED + "Entrada inválida. Ingresa un número." + RESET);
            }
        }
    }

    private static void exitProgram() {
        System.out.println(BOLD + PURPLE + "\n¡Gracias por usar el programa! 👋" + RESET);
        System.out.println("═════════════════════════════════════════");
    }

    // Operaciones básicas de conjuntos
    private static Set<Object> union(Set<Object> a, Set<Object> b) {
        Set<Object> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }

    private static Set<Object> intersection(Set<Object> a, Set<Object> b) {
        Set<Object> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }

    private static Set<Object> difference(Set<Object> a, Set<Object> b) {
        Set<Object> result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }

    private static Set<Object> symmetricDifference(Set<Object> a, Set<Object> b) {
        return union(difference(a, b), difference(b, a));
    }

    private static Set<Object> complement(Set<Object> set, Set<Object> universal) {
        return difference(universal, set);
    }

    private static Set<String> cartesianProduct(List<Set<Object>> sets) {
        Set<String> result = new LinkedHashSet<>();
        cartesianProductHelper(sets, 0, new ArrayList<>(), result);
        return result;
    }

    private static void cartesianProductHelper(List<Set<Object>> sets, int index, List<Object> current, Set<String> result) {
        if (index == sets.size()) {
            result.add("(" + current.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + ")");
            return;
        }
        
        for (Object element : sets.get(index)) {
            current.add(element);
            cartesianProductHelper(sets, index + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}