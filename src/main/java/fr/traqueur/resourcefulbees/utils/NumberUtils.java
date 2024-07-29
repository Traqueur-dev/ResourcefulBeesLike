package fr.traqueur.resourcefulbees.utils;

public class NumberUtils {

    public static double castDouble(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else if (object instanceof String) {
            try {
                return Double.parseDouble((String) object);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

}
