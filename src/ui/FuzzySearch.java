package ui;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public final class FuzzySearch {
    private FuzzySearch() {
    }

    public static String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT);
    }

    @SafeVarargs
    public static <T> List<T> filter(List<T> source, String keyword, Function<T, String>... extractors) {
        List<T> result = new ArrayList<>();
        if (source == null) return result;
        String normalizedKeyword = normalize(keyword).trim();
        if (normalizedKeyword.isEmpty()) return new ArrayList<>(source);

        String[] terms = normalizedKeyword.split("\\s+");
        for (T item : source) {
            StringBuilder haystack = new StringBuilder();
            for (Function<T, String> extractor : extractors) {
                haystack.append(' ').append(normalize(extractor.apply(item)));
            }
            String value = haystack.toString();
            boolean matched = true;
            for (String term : terms) {
                if (!value.contains(term)) {
                    matched = false;
                    break;
                }
            }
            if (matched) result.add(item);
        }
        return result;
    }
}
