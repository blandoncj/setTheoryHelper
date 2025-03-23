package setTheoryHelper;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SetManager {
  private static final Scanner sc = new Scanner(System.in);

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
    System.out.println(PURPLE + "5. Salir");
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
}
