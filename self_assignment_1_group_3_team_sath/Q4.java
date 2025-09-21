public class Q4 {
    public static String[] alphBubbleSort(String[] words) {
        int n = words.length;
        boolean swapped;
        
        do {
            swapped = false;
            for (int i = 0; i < n - 1; i++) {
                if (words[i].compareToIgnoreCase(words[i + 1]) > 0) {
                    String temp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
        
        return words;
    }

    public static void main(String[] args) {
        // Soliloquy text
        String text = "To be or not to be, that is the question;"
                + " Whether `tis nobler in the mind to suffer"
                + " the slings and arrows of outrageous fortune,"
                + " or to take arms against a sea of troubles,"
                + " and by opposing end them?";
        String[] words = text.split("[\\s\\p{Punct}]+");
        String[] sortedWords = alphBubbleSort(words);
        System.out.println("Sorted Words:");
        for (String w : sortedWords) {
            System.out.println(w);
        }
    }
}
