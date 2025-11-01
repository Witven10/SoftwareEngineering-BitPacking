public class BitPackingFactory {

    public static BitPacking create(String type, int k) {
        switch (type.toLowerCase()) { // Convertir le type en minuscules pour une comparaison insensible à la casse
            case "nooverlap":
                return new BitPackingNoOverlap(k);
            case "overlap":
                return new BitPackingOverlap(k);
            case "overflow":
                return new BitPackingOverflow(k);
            default:
                throw new IllegalArgumentException("Type de compression inconnu: " + type);
        }
    }
}
