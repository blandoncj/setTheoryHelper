package setTheoryHelper;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SetManager {
    private static final Scanner sc = new Scanner(System.in);
    private static Set<Object> setA = new HashSet<>();
    private static Set<Object> setB = new HashSet<>();
    private static Set<Object> setC = new HashSet<>();
    
    // Colores y estilos
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String BLUE = "\033[34m";
    private static final String GREEN = "\033[32m";
    private static final String CYAN = "\033[36m";
    private static final String YELLOW = "\033[33m";
    private static final String MAGENTA = "\033[35m";
    private static final String RED = "\033[31m";
    private static final String PURPLE = "\033[35m";

    public static void main(String[] args) {
        showMenu();
    }

    public static void showMenu() {
        printTitle();

        System.out.print(BOLD + BLUE + "Ingresa el tamaño del conjunto universal: " + RESET);
        int universalSetSize = sc.nextInt();
        Set<Object> universalSet = createUniversalSet(universalSetSize);
        
        while (true) {
            printMenuOptions();
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    printSection("Conjunto Universal", GREEN);
                    printTable(universalSet);
                    break;
                case 2:
                    printSection("Verificación de Pertenencia", YELLOW);
                    checkUniversalMembership(universalSet);
                    break;
                case 3:
                    printSection("Clasificación de Subconjuntos", MAGENTA);
                    classifySubsets(universalSet);
                    break;
                case 4:
                    printSection("Conjunto Potencia", RED);
                    Set<Set<Object>> powerSet = getPowerSet(universalSet);
                    printTable(powerSet);
                    break;
                case 5:
                    printSection("Operaciones con Conjuntos", BLUE);
                    performSetOperations(universalSet);
                    break;
                case 6:
                    printSection("Leyes de Conjuntos", CYAN);
                    demonstrateSetLaws(universalSet);
                    break;
                case 7:
                    System.out.println(BOLD + PURPLE + "¡Gracias por usar el programa! 👋" + RESET);
                    return;
                default:
                    System.out.println(RED + "Opción no válida. Intenta de nuevo." + RESET);
            }
        }
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
        System.out.print(BOLD + "Elige una opción: " + RESET);
    }

    private static void printTitle() {
        System.out.println(BOLD + "    📌 TEORIA DE CONJUNTOS 📌" + RESET);
        System.out.println("═════════════════════════════════════════");
    }

    private static void printSection(String title, String color) {
        System.out.println("\n" + color + BOLD + "🔹 " + title + " 🔹" + RESET);
        System.out.println("─────────────────────────────────────────");
    }

    private static void printTable(Set<?> set) {
        System.out.println(CYAN + "┌───────────────────────────┐" + RESET);
        for (Object element : set) {
            System.out.printf(CYAN + "│ %-25s │\n" + RESET, element);
        }
        System.out.println(CYAN + "└───────────────────────────┘" + RESET);
    }

    private static Set<Object> createUniversalSet(int size) {
        Set<Object> universalSet = new HashSet<>();
        sc.nextLine();

        System.out.println(
            "\nIngresa un elemento simple o si es un subconjunto, ingresa los elementos entre llaves. Ejemplo: {a, b}\n");

        for (int i = 0; i < size; i++) {
            System.out.print("Ingresa el elemento " + (i + 1) + ": ");
            String input = sc.nextLine().trim();

            if (input.startsWith("{") && input.endsWith("}")) {
                String[] elements = input.substring(1, input.length() - 1).split(",");
                Set<Object> subset = new HashSet<>();
                for (String element : elements) {
                    subset.add(element.trim());
                }
                universalSet.add(subset);
            } else {
                universalSet.add(input);
            }
        }
        return universalSet;
    }

    private static void classifySubsets(Set<Object> universalSet) {
        Set<Object> simpleElements = new HashSet<>();

        for (Object element : universalSet) {
            if (!(element instanceof Set)) {
                simpleElements.add(element);
            }
        }

        universalSet.forEach(element -> {
            if (element instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<Object> subset = (Set<Object>) element;

                if (simpleElements.containsAll(subset)) {
                    if (subset.size() < simpleElements.size()) {
                        System.out.println(CYAN + subset + " ⊂ U" + RESET); // subconjunto propio
                    } else {
                        System.out.println(CYAN + subset + " ⊆ U" + RESET); // subconjunto no propio
                    }
                } else {
                    System.out.println(RED + subset + " ⊈ U" + RESET); // no es subconjunto
                }
            }
        });
    }

    private static void checkUniversalMembership(Set<Object> universalSet) {
        universalSet.forEach(element -> {
            System.out.println(CYAN + element + " ∈ U" + RESET);
        });
    }

    private static Set<Set<Object>> getPowerSet(Set<Object> universalSet) {
        Set<Set<Object>> powerSet = new HashSet<>();
        powerSet.add(new HashSet<>());

        for (Object element : universalSet) {
            Set<Set<Object>> newSubsets = new HashSet<>();

            for (Set<Object> subset : powerSet) {
                newSubsets.add(subset);

                Set<Object> newSubset = new HashSet<>(subset);
                newSubset.add(element);
                newSubsets.add(newSubset);
            }

            powerSet.addAll(newSubsets);
        }
        return powerSet;
    }

    private static void performSetOperations(Set<Object> universalSet) {
        System.out.println("\n" + BOLD + "🔹 Configuración de Conjuntos para Operaciones 🔹" + RESET);
        System.out.println("─────────────────────────────────────────");
        
        System.out.println(YELLOW + "Configuración del Conjunto A:" + RESET);
        setA = configureSet(universalSet);
        System.out.println(YELLOW + "Configuración del Conjunto B:" + RESET);
        setB = configureSet(universalSet);
        System.out.println(YELLOW + "Configuración del Conjunto C:" + RESET);
        setC = configureSet(universalSet);

        while (true) {
            System.out.println("\n" + BOLD + "📌 OPERACIONES DISPONIBLES" + RESET);
            System.out.println("─────────────────────────────────────────");
            System.out.println("1. Unión (A ∪ B)");
            System.out.println("2. Intersección (A ∩ B)");
            System.out.println("3. Diferencia (A - B)");
            System.out.println("4. Diferencia Simétrica (A Δ B)");
            System.out.println("5. Complemento (A')");
            System.out.println("6. Producto Cartesiano (A × B)");
            System.out.println("7. Volver al menú principal");
            System.out.print(BOLD + "Elige una operación: " + RESET);
            
            int op = sc.nextInt();
            sc.nextLine();
            
            if (op == 7) break;
            
            switch (op) {
                case 1:
                    printSection("Unión A ∪ B", GREEN);
                    Set<Object> union = union(setA, setB);
                    printTable(union);
                    break;
                case 2:
                    printSection("Intersección A ∩ B", YELLOW);
                    Set<Object> intersection = intersection(setA, setB);
                    printTable(intersection);
                    break;
                case 3:
                    printSection("Diferencia A - B", BLUE);
                    Set<Object> difference = difference(setA, setB);
                    printTable(difference);
                    break;
                case 4:
                    printSection("Diferencia Simétrica A Δ B", MAGENTA);
                    Set<Object> symmetricDiff = symmetricDifference(setA, setB);
                    printTable(symmetricDiff);
                    break;
                case 5:
                    printSection("Complemento de A", RED);
                    Set<Object> complement = complement(setA, universalSet);
                    printTable(complement);
                    break;
                case 6:
                    printSection("Producto Cartesiano A × B", CYAN);
                    Set<String> cartesianProduct = cartesianProduct(setA, setB);
                    printTable(cartesianProduct);
                    break;
                default:
                    System.out.println(RED + "Opción no válida." + RESET);
            }
        }
    }

    private static Set<Object> configureSet(Set<Object> universalSet) {
        Set<Object> newSet = new HashSet<>();
        System.out.print("Ingrese tamaño del conjunto: ");
        int size = sc.nextInt();
        sc.nextLine();
        
        System.out.println("Elementos disponibles: " + universalSet);
        
        for (int i = 0; i < size; i++) {
            System.out.print("Ingrese elemento " + (i+1) + ": ");
            String element = sc.nextLine();
            if (universalSet.contains(element)) {
                newSet.add(element);
            } else {
                System.out.println(RED + "Elemento no existe en el conjunto universal. Intente de nuevo." + RESET);
                i--;
            }
        }
        return newSet;
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
        Set<Object> union = union(a, b);
        Set<Object> intersection = intersection(a, b);
        return difference(union, intersection);
    }

    private static Set<Object> complement(Set<Object> a, Set<Object> universal) {
        return difference(universal, a);
    }

    private static Set<String> cartesianProduct(Set<Object> a, Set<Object> b) {
        Set<String> result = new HashSet<>();
        for (Object elemA : a) {
            for (Object elemB : b) {
                result.add("(" + elemA + ", " + elemB + ")");
            }
        }
        return result;
    }

    private static void demonstrateSetLaws(Set<Object> universal) {
      // Configurar conjuntos si están vacíos
      if (setA.isEmpty() || setB.isEmpty() || setC.isEmpty()) {
          System.out.println(YELLOW + "Primero debe configurar los conjuntos A, B y C en Operaciones con Conjuntos." + RESET);
          return;
      }
  
      // Leyes de Conjuntos
      System.out.println("\n" + BOLD + "📚 LEYES DE LA TEORÍA DE CONJUNTOS" + RESET);
      System.out.println("─────────────────────────────────────────");
      
      // 1. Ley Conmutativa
      System.out.println(BLUE + "1. Ley Conmutativa:" + RESET);
      System.out.println("A ∪ B = B ∪ A");
      System.out.println("A ∪ B: " + union(setA, setB));
      System.out.println("B ∪ A: " + union(setB, setA));
      System.out.println("¿Son iguales? " + union(setA, setB).equals(union(setB, setA)));
      
      System.out.println("\nA ∩ B = B ∩ A");
      System.out.println("A ∩ B: " + intersection(setA, setB));
      System.out.println("B ∩ A: " + intersection(setB, setA));
      System.out.println("¿Son iguales? " + intersection(setA, setB).equals(intersection(setB, setA)));
      
      // 2. Ley Asociativa
      System.out.println("\n" + GREEN + "2. Ley Asociativa:" + RESET);
      System.out.println("(A ∪ B) ∪ C = A ∪ (B ∪ C)");
      System.out.println("(A ∪ B) ∪ C: " + union(union(setA, setB), setC));
      System.out.println("A ∪ (B ∪ C): " + union(setA, union(setB, setC)));
      System.out.println("¿Son iguales? " + union(union(setA, setB), setC).equals(union(setA, union(setB, setC))));
      
      System.out.println("\n(A ∩ B) ∩ C = A ∩ (B ∩ C)");
      System.out.println("(A ∩ B) ∩ C: " + intersection(intersection(setA, setB), setC));
      System.out.println("A ∩ (B ∩ C): " + intersection(setA, intersection(setB, setC)));
      System.out.println("¿Son iguales? " + intersection(intersection(setA, setB), setC).equals(intersection(setA, intersection(setB, setC))));
      
      // 3. Ley Distributiva
      System.out.println("\n" + YELLOW + "3. Ley Distributiva:" + RESET);
      System.out.println("A ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C)");
      System.out.println("A ∪ (B ∩ C): " + union(setA, intersection(setB, setC)));
      System.out.println("(A ∪ B) ∩ (A ∪ C): " + intersection(union(setA, setB), union(setA, setC)));
      System.out.println("¿Son iguales? " + union(setA, intersection(setB, setC)).equals(intersection(union(setA, setB), union(setA, setC))));
      
      System.out.println("\nA ∩ (B ∪ C) = (A ∩ B) ∪ (A ∩ C)");
      System.out.println("A ∩ (B ∪ C): " + intersection(setA, union(setB, setC)));
      System.out.println("(A ∩ B) ∪ (A ∩ C): " + union(intersection(setA, setB), intersection(setA, setC)));
      System.out.println("¿Son iguales? " + intersection(setA, union(setB, setC)).equals(union(intersection(setA, setB), intersection(setA, setC))));
      
      // 4. Leyes de De Morgan
      System.out.println("\n" + MAGENTA + "4. Leyes de De Morgan:" + RESET);
      System.out.println("(A ∪ B)' = A' ∩ B'");
      System.out.println("(A ∪ B)': " + complement(union(setA, setB), universal));
      System.out.println("A' ∩ B': " + intersection(complement(setA, universal), complement(setB, universal)));
      System.out.println("¿Son iguales? " + complement(union(setA, setB), universal).equals(intersection(complement(setA, universal), complement(setB, universal))));
      
      System.out.println("\n(A ∩ B)' = A' ∪ B'");
      System.out.println("(A ∩ B)': " + complement(intersection(setA, setB), universal));
      System.out.println("A' ∪ B': " + union(complement(setA, universal), complement(setB, universal)));
      System.out.println("¿Son iguales? " + complement(intersection(setA, setB), universal).equals(union(complement(setA, universal), complement(setB, universal))));
      
      // 5. Leyes de Complemento
      System.out.println("\n" + CYAN + "5. Leyes de Complemento:" + RESET);
      System.out.println("A ∪ A' = U");
      System.out.println("A ∪ A': " + union(setA, complement(setA, universal)));
      System.out.println("U: " + universal);
      System.out.println("¿Son iguales? " + union(setA, complement(setA, universal)).equals(universal));
      
      System.out.println("\nA ∩ A' = ∅");
      System.out.println("A ∩ A': " + intersection(setA, complement(setA, universal)));
      System.out.println("¿Está vacío? " + intersection(setA, complement(setA, universal)).isEmpty());
      
      // 6. Leyes de Idempotencia
      System.out.println("\n" + RED + "6. Leyes de Idempotencia:" + RESET);
      System.out.println("A ∪ A = A");
      System.out.println("A ∪ A: " + union(setA, setA));
      System.out.println("A: " + setA);
      System.out.println("¿Son iguales? " + union(setA, setA).equals(setA));
      
      System.out.println("\nA ∩ A = A");
      System.out.println("A ∩ A: " + intersection(setA, setA));
      System.out.println("A: " + setA);
      System.out.println("¿Son iguales? " + intersection(setA, setA).equals(setA));
  }
}