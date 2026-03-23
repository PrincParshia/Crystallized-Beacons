package princ.crystallizedbeacons;

import net.minecraft.resources.Identifier;

public class CrystallizedBeaconsConstants {
    public static final String NAMESPACE = "crystallized-beacons";
    public static final String RENDER_STATE_DATA_KEY_PREFIX = NAMESPACE + ":";

    public static Identifier withDefaultNamespace(String s) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, s);
    }
}
