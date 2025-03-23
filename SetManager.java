package setTheoryHelper;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class SetManager {
  private static final Scanner sc = new Scanner(System.in);

  public static void showMenu() {
    System.out.print("Ingresa el tamaño del conjunto universal: ");

    int universalSetSize = sc.nextInt();
    Set<Object> universalSet = createUniversalSet(universalSetSize);

    System.out.println("\nEl conjunto universal es: \n");
    System.out.println(universalSet);

    System.out.println("\nVerificación de pertenencia al conjunto universal: \n");
    checkUniversalMembership(universalSet);

    System.out.println("\nClasificación de subconjuntos: \n");
    classifySubsets(universalSet);

    System.out.println("\nEl conjunto potencia es: \n");
    Set<Set<Object>> powerSet = getPowerSet(universalSet);
    powerSet.forEach(System.out::println);
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
            System.out.println(subset + " ⊂ U"); // subconjuto propio
          } else {
            System.out.println(subset + " ⊆ U"); // subconjunto no propio
          }
        } else {
          System.out.println(subset + " ⊈ U"); // no es subconjunto
        }
      }
    });
  }

  private static void checkUniversalMembership(Set<Object> universalSet) {
    universalSet.forEach(element -> {
      System.out.println(element + " ∈ U");
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
