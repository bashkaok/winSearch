package org.mikesoft.winsearch;

/**
 * Helper class for a find string prepare
 */
public class FindStringBuilder {
    private String str;

    private FindStringBuilder(String str) {
        this.str = str;
    }

    private void encloseInQuotations() {
        str = "\"" + str + "\"";
    }

    private void removeIllegals() {
        str = str.replaceAll("[\\n.!,)(]", "_");
    }

    private void replaceSpecial() {
        str = str.replaceAll("'", "''");
    }

    /**
     * Builder for a find string
     * <ul>
     *     <li>removes illegal symbols</li>
     *     <li>replaces special symbols</li>
     *     <li>encloses string in quotation marks (if needed)</li>
     * </ul>
     * @param findStr initial string
     * @param strictMatch true if a full match will be searched
     * @return result string
     */
    public static String build(String findStr, boolean strictMatch) {
        FindStringBuilder builder = new FindStringBuilder(findStr);
        builder.removeIllegals();
        builder.replaceSpecial();
        if (!strictMatch && findStr.contains(" ")) builder.encloseInQuotations();
        return builder.str;
    }

    /**
     * Check the string for quotations marks
     * @param str string
     * @return true if string is enclosed in quotation marks
     */
    public static boolean isQuotationEnclosed(String str) {
        return str.startsWith("\"") && str.endsWith("\"");
    }
}
