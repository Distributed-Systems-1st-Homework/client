package hr.fer.rassus.client.constants;

public enum Constants {
    /**
     * Constant FILE_PATH (file path).
     */
    FILE_PATH("D:\\OneDrive\\FAKS\\4.godina\\RASSUS\\DZ1\\client\\src\\main\\resources\\static\\mjerenja.csv"),
    /**
     * Constant PARSER_REGEX (regex for parsing file row).
     */
    PARSER_REGEX("^([+-]?\\d*\\.?\\d*),([+-]?\\d*\\.?\\d*),([+-]?\\d*\\.?\\d*),([+-]?\\d*\\.?\\d*),([+-]?\\d*\\.?\\d*),([+-]?\\d*\\.?\\d*),$");

    private final String constant;

    private Constants(String constant) {
        this.constant = constant;
    }
    /**
     * Getter for constant.
     *
     * @return value of this constant
     */
    public String getConstant() {
        return constant;
    }

}
