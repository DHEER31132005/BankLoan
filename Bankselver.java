import java.util.*;

public class Bankselver {

    public static String caesarEncode(String message, int shift) {
        return caesarCipher(message, shift);
    }

    public static String caesarDecode(String message, int shift) {
        return caesarCipher(message, -shift);
    }

    private static String caesarCipher(String text, int shift) {
        StringBuilder result = new StringBuilder();
        shift = shift % 26;

        for (char ch : text.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                char c = (char) (((ch - 'A' + shift + 26) % 26) + 'A');
                result.append(c);
            } else if (Character.isLowerCase(ch)) {
                char c = (char) (((ch - 'a' + shift + 26) % 26) + 'a');
                result.append(c);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String formatIndianCurrency(double number) {
        String[] parts = String.valueOf(number).split("\\.");
        String intPart = parts[0];
        String decPart = parts.length > 1 ? "." + parts[1] : "";

        StringBuilder sb = new StringBuilder();
        int len = intPart.length();

        if (len > 3) {
            sb.append(intPart.substring(len - 3));
            intPart = intPart.substring(0, len - 3);

            while (intPart.length() > 2) {
                sb.insert(0, "," + intPart.substring(intPart.length() - 2));
                intPart = intPart.substring(0, intPart.length() - 2);
            }

            if (intPart.length() > 0) {
                sb.insert(0, intPart + ",");
            }
        } else {
            sb.append(intPart);
        }

        return sb.toString() + decPart;
    }

   
    static class Element {
        int left, right;
        List<Integer> values;

        Element(int left, int right, List<Integer> values) {
            this.left = left;
            this.right = right;
            this.values = values;
        }

        int length() {
            return right - left;
        }

        boolean overlapsMoreThanHalf(Element other) {
            int overlap = Math.max(0, Math.min(this.right, other.right) - Math.max(this.left, other.left));
            return overlap >= Math.min(this.length(), other.length()) / 2;
        }

        public String toString() {
            return "Pos:[" + left + "," + right + "] Values:" + values;
        }
    }

    public static List<Element> combineElements(List<Element> list1, List<Element> list2) {
        List<Element> combined = new ArrayList<>();
        List<Element> all = new ArrayList<>();
        all.addAll(list1);
        all.addAll(list2);

        all.sort(Comparator.comparingInt(e -> e.left));
        boolean[] used = new boolean[all.size()];

        for (int i = 0; i < all.size(); i++) {
            if (used[i]) continue;
            Element base = all.get(i);

            for (int j = i + 1; j < all.size(); j++) {
                if (!used[j] && base.overlapsMoreThanHalf(all.get(j))) {
                    base.values.addAll(all.get(j).values);
                    used[j] = true;
                }
            }
            combined.add(base);
        }
        return combined;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Caesar Cipher ===");
        System.out.print("Enter message: ");
        String message = sc.nextLine();

        System.out.print("Enter shift (e.g. 3): ");
        int shift = sc.nextInt();
        sc.nextLine(); 

        String encoded = caesarEncode(message, shift);
        String decoded = caesarDecode(encoded, shift);

        System.out.println("Encoded Message: " + encoded);
        System.out.println("Decoded Message: " + decoded);

        
        System.out.println("\n=== Indian Currency Formatter ===");
        System.out.print("Enter number: ");
        double num = sc.nextDouble();
        sc.nextLine(); 
        System.out.println("Formatted: " + formatIndianCurrency(num));

      
        System.out.println("\n=== Combine Two Element Lists ===");

        List<Element> list1 = new ArrayList<>();
        List<Element> list2 = new ArrayList<>();

        System.out.print("Enter number of elements in List 1: ");
        int n1 = sc.nextInt();
        for (int i = 0; i < n1; i++) {
            System.out.println("Element " + (i + 1));
            System.out.print("Left position: ");
            int left = sc.nextInt();
            System.out.print("Right position: ");
            int right = sc.nextInt();
            System.out.print("Number of values: ");
            int count = sc.nextInt();
            List<Integer> values = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                values.add(sc.nextInt());
            }
            list1.add(new Element(left, right, values));
        }

        System.out.print("Enter number of elements in List 2: ");
        int n2 = sc.nextInt();
        for (int i = 0; i < n2; i++) {
            System.out.println("Element " + (i + 1));
            System.out.print("Left position: ");
            int left = sc.nextInt();
            System.out.print("Right position: ");
            int right = sc.nextInt();
            System.out.print("Number of values: ");
            int count = sc.nextInt();
            List<Integer> values = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                values.add(sc.nextInt());
            }
            list2.add(new Element(left, right, values));
        }

        List<Element> result = combineElements(list1, list2);
        System.out.println("\nCombined Result:");
        for (Element e : result) {
            System.out.println(e);
        }

        sc.close();
    }
}
