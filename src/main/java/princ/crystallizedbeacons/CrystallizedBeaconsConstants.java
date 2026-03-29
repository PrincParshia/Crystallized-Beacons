package princ.crystallizedbeacons;

import net.minecraft.resources.ResourceLocation;

public class CrystallizedBeaconsConstants {
    public static final String NAMESPACE = "crystallized-beacons";

    public static ResourceLocation withDefaultNamespace(String string) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, string);
    }

    public static String dataKeyPrefix(String string) {
        ResourceLocation resourceLocation = withDefaultNamespace(string);
        return resourceLocation.getNamespace() + ":" + resourceLocation.getPath();
    }
}
