package princ.crystallizedbeacons;

import net.minecraft.resources.Identifier;

public class CrystallizedBeaconsConstants {
    public static final String NAMESPACE = "crystallized-beacons";

    public static Identifier withDefaultNamespace(String string) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, string);
    }

    public static String withDataKeyPrefix(String string) {
        Identifier identifier = withDefaultNamespace(string);
        return identifier.getNamespace() + ":" + identifier.getPath();
    }
}
