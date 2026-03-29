package princ.crystallizedbeacons;

import net.minecraft.resources.ResourceLocation;

public class CrystallizedBeaconsConstants {
    public static final String NAMESPACE = "crystallized-beacons";

    public static ResourceLocation withDefaultNamespace(String string) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, string);
    }
}
